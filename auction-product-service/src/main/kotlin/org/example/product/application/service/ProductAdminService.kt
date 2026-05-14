package org.example.product.application.service

import auction.auctionbidapi.client.BidClient
import auction.auctionbidapi.status.BidStatus
import auction.auctioncategoryapi.client.CategoryClient
import auction.auctioncategoryapi.client.CategoryProductClient
import auction.auctioncategoryapi.dto.CategoryCommonResponse
import auction.auctionproductapi.auction.status.AuctionStatus
import auction.auctionuserapi.user.client.UserClient
import org.example.common.global.error.CustomException
import org.example.common.global.error.GlobalErrorCode
import org.example.product.application.dto.ProductImageResponse
import org.example.product.application.dto.toProductImageDto
import org.example.product.domain.product.dto.ProductAndAuctionResponse
import org.example.product.domain.product.dto.ProductListCondition
import org.example.product.domain.product.dto.toProductAndAuctionDto
import org.example.product.domain.product.entity.Product
import org.example.product.domain.product.entity.ProductImage
import auction.auctionproductapi.product.error.ProductErrorCode
import org.example.common.global.s3.service.S3Service
import org.example.product.domain.product.repository.ProductImageRepository
import org.example.product.domain.product.repository.ProductQueryRepository
import org.example.product.domain.product.repository.ProductRepository
import org.redisson.api.RScoredSortedSet
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class ProductAdminService(
    private val productImageRepository: ProductImageRepository,
    private val productRepository: ProductRepository,
    private val productQueryRepository: ProductQueryRepository,
    private val userClient: UserClient,
    private val categoryClient: CategoryClient,
    private val categoryProductClient: CategoryProductClient,
    private val bidClient: BidClient,
    private val s3Service: S3Service,
    private val redissonClient: RedissonClient,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    fun userProductList(
        userId: Long,
        condition: ProductListCondition,
        pageable: Pageable
    ): Slice<ProductAndAuctionResponse> {
        val filterCategoryIds: List<Long>? = if (!condition.path.isNullOrBlank()) {
            categoryProductClient.categoryDtoByPath(condition.path).map { it.categoryId }
        } else null

        val auctions: Slice<Product> =
            productQueryRepository.adminProductList(userId, condition, filterCategoryIds, pageable)

        if (auctions.isEmpty) return auctions.map { it.toProductAndAuctionDto(null, null) }

        val fetchedCategoryIds: List<Long> = auctions.map { it.categoryId }.distinct().toList()

        val userDto = userClient.userModuleDto(userId)

        val displayCategoryDtos = categoryProductClient.categoryDtoByIds(fetchedCategoryIds)

        val categoryMap = displayCategoryDtos.associateBy { it.categoryId }

        return auctions.map { product ->
            val matchedCategoryDto = categoryMap[product.categoryId]
                ?: CategoryCommonResponse(product.categoryId, "알 수 없는 카테고리")

            product.toProductAndAuctionDto(userDto, matchedCategoryDto)
        }
    }

    @Transactional
    fun deleteProduct(userId: Long, productId: Long) {
        val product = productRepository.findWithAuctionByProductId(productId)
            ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다.")
        val auctionId: Long? = product.auction?.auctionId

        if (userId != product.sellerId) {
            throw CustomException(GlobalErrorCode.BAD_REQUEST, "사용자가 일치하지 않습니다.")
        }

        if (!bidClient.existsByStatusAndAuction(BidStatus.ACTIVE, auctionId)) {
            throw CustomException(ProductErrorCode.CANNOT_DELETE_AFTER_BID, "입찰한 상품은 삭제할 수 없습니다.")
        }

        for (img in product.image) {
            s3Service.deleteFile(img.imageUrl)
        }

        product.auction?.changeStatus(AuctionStatus.CANCELED)
        productRepository.delete(product)

        val closingQueue: RScoredSortedSet<Long> =
            redissonClient.getScoredSortedSet("auction:closing")
        val removed: Boolean = closingQueue.remove(auctionId)

        if (removed) {
            log.info("경매 삭제로 인한 Redis 스케줄 제거 완료 - Auction ID: {}", auctionId)
        }
    }

    @Transactional
    fun uploadImages(
        productId: Long,
        files: MutableList<MultipartFile>
    ): MutableList<ProductImageResponse> {
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

            val savedImg: ProductImage = productImageRepository.save(img)

            // 생성된 정보를 DTO에 담아 리스트에 추가
            responses.add(savedImg.toProductImageDto())
        }
        return responses
    }

    @Transactional(readOnly = true)
    fun loadImage(productId: Long): List<ProductImageResponse> {
        val productImages = productImageRepository.findByProduct_ProductIdOrderByImageOrderAsc(productId)
        return productImages.stream().map { it.toProductImageDto() }.toList()
    }

    @Transactional
    fun moveToMain(imageId: Long) {
        val image: ProductImage = productImageRepository.findByIdOrNull(imageId)
            ?: throw CustomException(ProductErrorCode.IMAGE_NOT_FOUND)

        val oldOrder: Int = image.imageOrder
        val newOrder = 1

        productImageRepository.shiftOrders(image.product.productId, newOrder, oldOrder)

        image.updateOrder(newOrder)
    }

    @Transactional
    fun deleteImage(imageId: Long) {
        val image: ProductImage = productImageRepository.findByIdOrNull(imageId)
            ?: throw CustomException(ProductErrorCode.IMAGE_NOT_FOUND)

        s3Service.deleteFile(image.imageUrl)
        productImageRepository.delete(image)

        val remainingImages = productImageRepository.findByProduct_ProductIdOrderByImageOrderAsc(
            image.product.productId ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND)
        )


        for (i in remainingImages.indices) {
            remainingImages[i].updateOrder(i + 1) // 엔티티에 updateOrder 메서드 필요
        }
    }
}
