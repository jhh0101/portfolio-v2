package org.example.product.application.service.module

import auction.auctioncategoryapi.client.CategoryClient
import auction.auctioncategoryapi.error.CategoryErrorCode
import auction.auctionproductapi.product.client.ProductDetailClient
import auction.auctionproductapi.product.dto.ProductDetailResponse
import auction.auctionproductapi.product.error.ProductErrorCode
import auction.auctionuserapi.user.client.UserClient
import auction.auctionuserapi.user.error.UserErrorCode
import org.example.common.global.error.CustomException
import org.example.product.domain.product.entity.ProductImage
import org.example.product.domain.product.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull

class ProductDetailService(
    private val productRepository: ProductRepository,
    private val userClient: UserClient,
    private val categoryClient: CategoryClient,
) : ProductDetailClient{
    override fun productDetailResponse(productId: Long): ProductDetailResponse {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND)
        val userDto = userClient.userModuleDto(product.sellerId)
        val categoryDto = categoryClient.categoryModuleDto(product.categoryId)

        val mainUrl: String = product.image.stream()
            .filter({ img -> img.imageOrder == 1 })
            .map(ProductImage::imageUrl)
            .findFirst()
            .orElse("https://picsum.photos/400/300")

        return ProductDetailResponse(
            productId = product.productId ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND),
            seller = userDto.userNickname,
            category = categoryDto.categoryName,
            title = product.title,
            productStatus = product.productStatus,
            mainImageUrl = mainUrl,
            createdAt = product.createdAt
        )
    }

    override fun productDetailResponses(productIds: List<Long>): List<ProductDetailResponse> {

        val products = productRepository.findAllById(productIds)
        if (products.isEmpty()) return emptyList()

        val sellerIds = products.map { it.sellerId }.distinct()
        val categoryIds = products.map { it.categoryId }.distinct()

        val userMap = userClient.userListModuleDto(sellerIds).associateBy { it.userId }
        val categoryMap = categoryClient.categoryListModuleDto(categoryIds).associateBy { it.categoryId }

        // 4. 최종 조립 및 반환
        return products.map { product ->
            val userDto = userMap[product.sellerId]
                ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)
            val categoryDto = categoryMap[product.categoryId]
                ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

            val mainUrl = product.image
                .firstOrNull { it.imageOrder == 1 }?.imageUrl
                ?: "https://picsum.photos/400/300"

            ProductDetailResponse(
                productId = product.productId ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND),
                seller = userDto.userNickname,
                category = categoryDto.categoryName,
                title = product.title,
                productStatus = product.productStatus,
                mainImageUrl = mainUrl,
                createdAt = product.createdAt
            )
        }
    }

}
