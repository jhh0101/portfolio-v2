package org.example.product.application.service

import auction.auctioncategoryapi.client.CategoryClient
import auction.auctioncategoryapi.client.CategoryProductClient
import auction.auctioncategoryapi.dto.CategoryCommonResponse
import org.example.product.domain.product.dto.ProductAndAuctionResponse
import org.example.product.domain.product.dto.ProductListCondition
import org.example.product.domain.product.dto.toProductAndAuctionDto
import org.example.product.domain.product.entity.Product
import org.example.auction.user.feign.UserFeignClient
import org.example.product.domain.product.repository.ProductQueryRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductAdminService(
    private val productQueryRepository: ProductQueryRepository,
    private val userFeignClient: UserFeignClient,
    private val categoryProductClient: CategoryProductClient,
    private val categoryClient: CategoryClient,
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

        val userDto = userFeignClient.userModuleDto(userId)

        val displayCategoryDtos = categoryClient.categoryListModuleDto(fetchedCategoryIds)

        val categoryMap = displayCategoryDtos.associateBy { it.categoryId }

        return auctions.map { product ->
            val matchedCategoryDto = categoryMap[product.categoryId]
                ?: CategoryCommonResponse(product.categoryId, "알 수 없는 카테고리")

            product.toProductAndAuctionDto(userDto, matchedCategoryDto)
        }
    }
}
