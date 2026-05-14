package org.example.product.application.service.module

import auction.auctionproductapi.auction.error.AuctionErrorCode
import auction.auctionproductapi.product.client.ProductClient
import auction.auctionproductapi.product.dto.ProductCommonResponse
import org.example.common.global.error.CustomException
import org.example.product.domain.product.entity.Product
import auction.auctionproductapi.product.error.ProductErrorCode
import org.example.product.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service
import kotlin.Long
import kotlin.collections.map

@Service
class ProductCommonService(
    private val productRepository: ProductRepository,
) : ProductClient {

    override fun productModuleDto(productId: Long) : ProductCommonResponse {
        val product: Product = productRepository.findWithDetailsByProductId(productId)
            ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND)

        val auction = checkNotNull(product.auction) { "상품에 연결된 경매 정보가 없습니다. (ID: $productId)" }
        val images = checkNotNull(product.image) { "상품에 연결된 이미지 정보가 없습니다. (ID: $productId)" }

        return ProductCommonResponse(
            productId = product.productId ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND, "상품ID를 찾을 수 없습니다."),
            productSellerId = product.sellerId,
            title = product.title,
            description = product.description,
            viewCount = product.viewCount,
            productStatus = product.productStatus.name,
            categoryId = product.categoryId,
            auctionId = auction.auctionId ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND, "경매ID를 찾을 수 없습니다."),
            startPrice = auction.startPrice,
            currentPrice = auction.currentPrice,
            startTime = auction.startTime,
            endTime = auction.endTime,
            status = auction.status,
            imageUrl = images.map { it.imageUrl }
        )
    }

    override fun productListModuleDto(productIds: List<Long>): List<ProductCommonResponse> {
        val products: List<Product> = productRepository.findAllByProductIdIn(productIds)

        return products.map { product ->
            val auction = product.auction ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND, "상품과 연결된 경매 정보를 찾을 수 없습니다.")
            val images = product.image
            ProductCommonResponse(
                productId = product.productId ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND, "상품ID를 찾을 수 없습니다."),
                productSellerId = product.sellerId,
                title = product.title,
                description = product.description,
                viewCount = product.viewCount,
                productStatus = product.productStatus.name,
                categoryId = product.categoryId,
                auctionId = auction.auctionId ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND, "경매ID를 찾을 수 없습니다."),
                startPrice = auction.startPrice,
                currentPrice = auction.currentPrice,
                startTime = auction.startTime,
                endTime = auction.endTime,
                status = auction.status,
                imageUrl = images.map { it.imageUrl }
            )
        }
    }

    override fun productCount(userId: Long) : Long {
        return productRepository.productCount(userId)
    }

}