package org.example.product.domain.product.dto

import auction.auctioncategoryapi.dto.CategoryCommonResponse
import auction.auctionproductapi.auction.error.AuctionErrorCode
import auction.auctionuserapi.user.dto.UserCommonResponse
import org.example.auction.domain.auction.dto.AuctionResponse
import org.example.auction.domain.auction.dto.toAuctionResponse
import org.example.common.global.error.CustomException
import org.example.product.domain.product.entity.Product


data class ProductAndAuctionResponse(
    private val productResponse: ProductResponse,
    private val auctionResponse: AuctionResponse
)

fun Product.toProductAndAuctionDto(userDto: UserCommonResponse?, categoryDto: CategoryCommonResponse?): ProductAndAuctionResponse {
    return ProductAndAuctionResponse(
        productResponse = this.toProductResponse(userDto, categoryDto),
        auctionResponse = this.auction?.toAuctionResponse() ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND)
    )
}