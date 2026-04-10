package org.example.bid.domain.bid.dto

import auction.auctionproductapi.auction.dto.AuctionCommonResponse
import auction.auctionproductapi.product.dto.ProductCommonResponse

class BidHistoryResponse(
    val productDto: ProductCommonResponse? = null,
    val auctionDto: AuctionCommonResponse? = null,
    val myMaxBidPrice: Long? = null
) {

}
