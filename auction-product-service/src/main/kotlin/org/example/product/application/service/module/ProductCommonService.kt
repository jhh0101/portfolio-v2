package org.example.product.application.service.module

import auction.auctionproductapi.product.client.ProductClient
import auction.auctionproductapi.product.dto.ProductCommonResponse
import org.example.common.global.error.CustomException
import org.example.product.domain.product.entity.Product
import org.example.product.domain.product.error.ProductErrorCode
import org.example.product.domain.product.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.Long

@Service
class ProductCommonService(
    private val productRepository: ProductRepository,
) : ProductClient {

    override fun productModuleDto(productId: Long) : ProductCommonResponse {
        val product: Product = productRepository.findByIdOrNull(productId)
            ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND)

        return ProductCommonResponse(
            productId = product.productId ?: 0L,
            productSellerId = product.sellerId,
            title = product.title,
            description = product.description,
            viewCount = product.viewCount,
            productStatus = product.productStatus.name,
            categoryId = product.categoryId,
        )
    }

    override fun productListModuleDto(productIds: List<Long>): List<ProductCommonResponse> {
        val products: List<Product> = productRepository.findAllByProductIdIn(productIds)

        return products.map { product ->
            ProductCommonResponse(
                productId = product.productId ?: 0L,
                productSellerId = product.sellerId,
                title = product.title,
                description = product.description,
                viewCount = product.viewCount,
                productStatus = product.productStatus.name,
                categoryId = product.categoryId,
            )
        }
    }

    override fun productCount(userId: Long) : Long {
        return productRepository.productCount(userId)
    }

}