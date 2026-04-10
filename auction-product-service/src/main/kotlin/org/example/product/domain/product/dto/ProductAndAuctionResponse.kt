package org.example.product.domain.product.dto

import org.example.auction.domain.auction.dto.AuctionResponse
import org.example.auction.domain.auction.dto.toAuctionResponse
import org.example.product.domain.product.entity.Product


data class ProductAndAuctionResponse(
    private val productResponse: ProductResponse? = null,
    private val auctionResponse: AuctionResponse? = null
)

fun Product.toProductAndAuctionDto(): ProductAndAuctionResponse {
    return ProductAndAuctionResponse(
        productResponse = this.toProductResponse(),
        auctionResponse = this.auction.toAuctionResponse()
    )
}