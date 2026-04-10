package org.example.product.application.dto

import auction.auctioncategoryapi.dto.CategoryCommonResponse
import auction.auctionuserapi.user.dto.UserCommonResponse
import org.example.auction.domain.auction.dto.AuctionResponse
import org.example.auction.domain.auction.dto.toAuctionResponse
import org.example.product.domain.product.entity.Product

data class ProductDetailAndAuctionResponse(
    val productDetailResponse: ProductDetailResponse,
    val auctionResponse: AuctionResponse
)
fun Product.toProductDetailAndAuctionDto(userDto: UserCommonResponse, categoryDto: CategoryCommonResponse) : ProductDetailAndAuctionResponse{
    return ProductDetailAndAuctionResponse(
        productDetailResponse = this.toProductDetailDto(userDto, categoryDto),
        auctionResponse = this.auction.toAuctionResponse()
    )
}