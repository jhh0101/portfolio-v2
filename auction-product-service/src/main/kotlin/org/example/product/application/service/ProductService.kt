package org.example.product.application.service

import auction.auctionbidapi.client.BidClient
import auction.auctionbidapi.status.BidStatus
import auction.auctioncategoryapi.client.CategoryClient
import auction.auctioncategoryapi.client.CategoryProductClient
import auction.auctioncategoryapi.dto.CategoryCommonResponse
import auction.auctionproductapi.auction.status.AuctionStatus
import auction.auctionproductapi.product.status.ProductStatus
import auction.auctionuserapi.user.client.UserClient
import org.example.auction.application.dto.AuctionRequest
import org.example.auction.domain.auction.entity.Auction
import org.example.auction.domain.auction.repository.AuctionRepository
import org.example.common.global.error.CustomException
import org.example.common.global.error.GlobalErrorCode
import org.example.global.s3.service.S3Service
import org.example.product.application.dto.*
import org.example.product.domain.product.dto.*
import org.example.product.domain.product.entity.Product
import org.example.product.domain.product.entity.ProductImage
import auction.auctionproductapi.product.error.ProductErrorCode
import org.example.product.domain.product.repository.ProductImageRepository
import org.example.product.domain.product.repository.ProductQueryRepository
import org.example.product.domain.product.repository.ProductRepository
import org.example.product.domain.product.service.ProductProcessor
import org.redisson.api.RScoredSortedSet
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.ZoneId

@Service
class ProductService(
    private val productImageRepository: ProductImageRepository,
    private val productRepository: ProductRepository,
    private val auctionRepository: AuctionRepository,
    private val productQueryRepository: ProductQueryRepository,
    private val productProcessor: ProductProcessor,
    private val userClient: UserClient,
    private val categoryClient: CategoryClient,
    private val categoryProductClient: CategoryProductClient,
    private val bidClient: BidClient,
    private val redissonClient: RedissonClient,
    private val s3Service: S3Service,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // 상품 메서드
    @Transactional
    fun addProduct(userId: Long, productRequest: ProductRequest, auctionRequest: AuctionRequest): ProductResponse {
        val userDto = userClient.userModuleDto(userId)
        val categoryDto = categoryClient.categoryModuleDto(productRequest.categoryId)

        val product = Product(
            sellerId = userDto.userId,
            categoryId = categoryDto.categoryId,
            title = productRequest.title,
            description = productRequest.description,
            viewCount = 0,
            productStatus = ProductStatus.ACTIVE,
            image = mutableListOf(),
            auction = null
        )

        val productSave = productRepository.save(product)

        val auction = Auction(
            product = productSave,
            startPrice = auctionRequest.startPrice,
            currentPrice = auctionRequest.startPrice,
            startTime = auctionRequest.startTime,
            endTime = auctionRequest.endTime,
            status = AuctionStatus.PROCEEDING
        )

        val auctionSave: Auction = auctionRepository.save(auction)

        val closingQueue: RScoredSortedSet<Long> = redissonClient.getScoredSortedSet("auction:closing")

        val closingTimestamp: Long = auctionSave.endTime
            .atZone(ZoneId.systemDefault())
            .toEpochSecond()

        closingQueue.add(closingTimestamp.toDouble(), auctionSave.auctionId )

        return productSave.toProductResponse(userDto, categoryDto)
    }


    @Transactional(readOnly = true)
    fun productList(
        condition: ProductListCondition,
        pageable: Pageable
    ): Page<ProductAndAuctionResponse> {
        val filterCategoryIds = condition.path
            ?.takeIf { it.isNotBlank() } // 비어있지 않을 때만 다음 단계 진행
            ?.let { path -> categoryProductClient.categoryDtoByPath(path).map { it.categoryId } }

        val auctions: Page<Product> = productQueryRepository.productList(null, condition, filterCategoryIds, pageable)

        if (auctions.isEmpty) return auctions.map { it.toProductAndAuctionDto(null, null) }

        val fetchedCategoryIds: List<Long> = auctions.map { it.categoryId }.distinct().toList()

        val displayCategoryDtos = categoryProductClient.categoryDtoByIds(fetchedCategoryIds)

        val categoryMap = displayCategoryDtos.associateBy { it.categoryId }

        return auctions.map { product ->
            val matchedCategoryDto = categoryMap[product.categoryId]
                ?: CategoryCommonResponse(product.categoryId, "알 수 없는 카테고리")

            product.toProductAndAuctionDto(null, matchedCategoryDto)
        }

    }

    fun findProductDetail(
        productId: Long,
        userId: Long,
        viewedCookieValue: String // Request 안 받음!
    ): ProductDetailResult { // 응답용 DTO를 하나 만듭니다 (기존 응답 + 새 쿠키값)

        val product = productRepository.findWithAuctionById(productId)
            ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND)

        val userDto = userClient.userModuleDto(product.sellerId)
        val isSeller = userDto.userId == userId

        val categoryDto = categoryClient.categoryModuleDto(product.categoryId)

        val (shouldIncrease, newCookieValue) = productProcessor.validateFindProductDetail(
            productId, isSeller, viewedCookieValue
        )

        if (shouldIncrease) {
            productRepository.viewCount(productId)
        }

        val responseData = product.toProductDetailAndAuctionDto(userDto, categoryDto)

        return ProductDetailResult(responseData, newCookieValue)
    }

    @Transactional(readOnly = true)
    fun myProductList(
        userId: Long,
        condition: ProductListCondition,
        pageable: Pageable
    ): Page<ProductAndAuctionResponse> {
        val userDto = userClient.userModuleDto(userId)

        val filterCategoryIds = condition.path
            ?.takeIf { it.isNotBlank() } // 비어있지 않을 때만 다음 단계 진행
            ?.let { path -> categoryProductClient.categoryDtoByPath(path).map { it.categoryId } }

        val auctions: Page<Product> = productQueryRepository.productList(userId, condition, filterCategoryIds, pageable)

        if (auctions.isEmpty) return auctions.map { it.toProductAndAuctionDto(null, null) }

        val fetchedCategoryIds: List<Long> = auctions.map { it.categoryId }.distinct().toList()

        val displayCategoryDtos = categoryProductClient.categoryDtoByIds(fetchedCategoryIds)

        val categoryMap = displayCategoryDtos.associateBy { it.categoryId }

        return auctions.map { product ->
            val matchedCategoryDto = categoryMap[product.categoryId]
                ?: CategoryCommonResponse(product.categoryId, "알 수 없는 카테고리")

            product.toProductAndAuctionDto(userDto, matchedCategoryDto)
        }
    }

    @Transactional
    fun updateProductDetail(
        userId: Long,
        productId: Long,
        productRequest: ProductRequest,
        auctionRequest: AuctionRequest
    ): ProductDetailAndAuctionResponse {
        val product: Product = productRepository.findWithAuctionById(productId)
            ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다.")

        if (userId != product.sellerId) {
            throw CustomException(GlobalErrorCode.BAD_REQUEST, "사용자가 일치하지 않습니다.")
        }

        if (bidClient.existsByStatusAndAuction(BidStatus.ACTIVE, product.auction?.auctionId)) {
            throw CustomException(ProductErrorCode.CANNOT_MODIFY_AFTER_BID, "입찰한 상품은 수정할 수 없습니다.")
        }

        val userDto = userClient.userModuleDto(userId)
        val categoryDto = categoryClient.categoryModuleDto(productRequest.categoryId)

        product.updateProduct(
            categoryDto.categoryId,
            productRequest.title,
            productRequest.description,
            auctionRequest.startPrice,
            auctionRequest.startTime,
            auctionRequest.endTime
        )

        val closingQueue = redissonClient.getScoredSortedSet<Long?>("auction:closing")

        val newScore: Long = auctionRequest.endTime.atZone(ZoneId.systemDefault()).toEpochSecond()

        closingQueue.add(newScore.toDouble(), product.auction?.auctionId)

        return product.toProductDetailAndAuctionDto(userDto, categoryDto)
    }

    // 상품 삭제
    @Transactional
    fun deleteProduct(userId: Long, productId: Long) {
        val product: Product = productRepository.findWithAuctionById(productId)
            ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다.")

        if (userId != product.sellerId) {
            throw CustomException(GlobalErrorCode.BAD_REQUEST, "사용자가 일치하지 않습니다.")
        }

        if (bidClient.existsByStatusAndAuction(BidStatus.ACTIVE, product.auction?.auctionId)) {
            throw CustomException(ProductErrorCode.CANNOT_MODIFY_AFTER_BID, "입찰한 상품은 수정할 수 없습니다.")
        }
        for (img in product.image) {
            s3Service.deleteFile(img.imageUrl)
        }

        product.auction?.changeStatus(AuctionStatus.CANCELED)
        productRepository.delete(product)

        val auctionId: Long? = product.auction?.auctionId
        val closingQueue = redissonClient.getScoredSortedSet<Long>("auction:closing")
        val removed = closingQueue.remove(auctionId)

        if (removed) {
            log.info("경매 삭제로 인한 Redis 스케줄 제거 완료 - Auction ID: {}", auctionId)
        }
    }

    // 이미지 메서드
    @Transactional
    fun uploadImages(productId: Long, files: MutableList<MultipartFile>): MutableList<ProductImageResponse> {
        val responses: MutableList<ProductImageResponse> = ArrayList()

        val product = productRepository.findByIdOrNull(productId)
            ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다.")

        val lastOrder = productImageRepository.findMaxOrderByProductId(productId)

        for (i in files.indices) {
            val order = lastOrder + i + 1 // 0번 인덱스는 1번, 1번 인덱스는 2번...
            val url: String = s3Service.uploadFile(files[i], "products")

            val img = ProductImage(
                product = product,
                imageUrl = url,
                imageOrder = order
            )

            val savedImg = productImageRepository.save(img)

            // 생성된 정보를 DTO에 담아 리스트에 추가
            responses.add(savedImg.toProductImageDto())
        }
        return responses
    }

    @Transactional(readOnly = true)
    fun loadImage(productId: Long): List<ProductImageResponse> {
        val productImages: List<ProductImage> =
            productImageRepository.findByProduct_ProductIdOrderByImageOrderAsc(productId)
        return productImages.stream().map {it.toProductImageDto()}.toList()
    }

    @Transactional
    fun moveToMain(imageId: Long) {
        val image = productImageRepository.findByIdOrNull(imageId)
            ?: throw CustomException(ProductErrorCode.IMAGE_NOT_FOUND)

        val oldOrder: Int = image.imageOrder
        val newOrder = 1

        productImageRepository.shiftOrders(image.product.productId, newOrder, oldOrder)

        image.updateOrder(newOrder)
    }

    @Transactional
    fun deleteImage(imageId: Long) {
        val image = productImageRepository.findByIdOrNull(imageId)
            ?: throw CustomException(ProductErrorCode.IMAGE_NOT_FOUND)

        s3Service.deleteFile(image.imageUrl)
        productImageRepository.delete(image)

        val remainingImages: List<ProductImage> =
            productImageRepository.findByProduct_ProductIdOrderByImageOrderAsc(
                image.product.productId ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND)
            )

        for (i in remainingImages.indices) {
            remainingImages[i].updateOrder(i + 1) // 엔티티에 updateOrder 메서드 필요
        }
    }
}
