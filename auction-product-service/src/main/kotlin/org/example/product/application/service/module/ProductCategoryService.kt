package org.example.product.application.service.module

import auction.auctionproductapi.product.client.ProductCategoryClient
import org.example.product.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service
import kotlin.Long

@Service
class ProductCategoryService(
    private val productRepository: ProductRepository,
) : ProductCategoryClient {

    override fun existsByCategoryId(categoryId: Long): Boolean {
        return productRepository.existsByCategoryId(categoryId)
    }

}