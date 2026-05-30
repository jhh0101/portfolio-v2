package org.example.bid.domain.bid.dto

import auction.auctionproductapi.auction.dto.AuctionCommonResponse
import auction.auctionproductapi.product.dto.ProductCommonResponse

data class BidHistoryResponse(
    val productResponse: ProductCommonResponse,
    val auctionResponse: AuctionCommonResponse,
    val myMaxBidPrice: Long? = null
) {

}
