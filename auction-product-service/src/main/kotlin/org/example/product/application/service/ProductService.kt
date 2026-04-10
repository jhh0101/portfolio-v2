package org.example.product.application.service

import org.example.product.application.dto.ProductDetailResult
import org.example.product.application.dto.ProductImageResponse
import org.example.product.application.dto.toProductDetailAndAuctionDto
import org.example.product.application.dto.toProductImageDto
import org.example.common.global.error.CustomException
import org.example.product.domain.product.dto.ProductAndAuctionResponse
import org.example.product.domain.product.dto.ProductListCondition
import org.example.product.domain.product.dto.toProductAndAuctionDto
import org.example.product.domain.product.entity.Product
import org.example.product.domain.product.entity.ProductImage
import org.example.product.domain.product.error.ProductErrorCode
import org.example.product.domain.product.repository.ProductImageRepository
import org.example.product.domain.product.repository.ProductQueryRepository
import org.example.product.domain.product.repository.ProductRepository
import org.example.product.domain.product.service.ProductProcessor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productImageRepository: ProductImageRepository,
    private val productRepository: ProductRepository,
    private val productQueryRepository: ProductQueryRepository,
    private val productProcessor: ProductProcessor,
) {


    @Transactional(readOnly = true)
    fun productList(
        condition: ProductListCondition,
        pageable: Pageable
    ): Page<ProductAndAuctionResponse> {
        val auctions: Page<Product> =
            productQueryRepository.productList(null, condition, pageable)

        return auctions.map { it.toProductAndAuctionDto() }
    }

    fun findProductDetail(
        productId: Long,
        userId: Long?,
        viewedCookieValue: String? // Request 안 받음!
    ): ProductDetailResult { // 응답용 DTO를 하나 만듭니다 (기존 응답 + 새 쿠키값)

        val product = productRepository.findWithAuctionById(productId)
            ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND)

        val isSeller = product.sellerId == userId

        val (shouldIncrease, newCookieValue) = productProcessor.validateFindProductDetail(
            productId, isSeller, viewedCookieValue
        )

        if (shouldIncrease) {
            productRepository.viewCount(productId)
        }

        val responseData = product.toProductDetailAndAuctionDto(product.sellerId)

        return ProductDetailResult(responseData, newCookieValue)
    }

    @Transactional(readOnly = true)
    fun loadImage(productId: Long): List<ProductImageResponse> {
        val productImages: List<ProductImage> =
            productImageRepository.findByProduct_ProductIdOrderByImageOrderAsc(productId)
        return productImages.stream().map {it.toProductImageDto()}.toList()
    }

}
