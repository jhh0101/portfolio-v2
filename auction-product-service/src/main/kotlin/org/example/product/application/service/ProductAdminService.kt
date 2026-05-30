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
    private val productQueryRepository: ProductQueryRepository,
    private val userClient: UserClient,
    private val categoryProductClient: CategoryProductClient,
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
}
