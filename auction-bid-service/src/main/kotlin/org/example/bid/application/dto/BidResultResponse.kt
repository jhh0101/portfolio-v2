package org.example.bid.application.dto

import org.example.bid.domain.bid.entity.Bid

data class BidResultResponse(
    val auctionId: Long,
    val title: String,
    val price: Long,
    val currentPoint: Long,
)

fun Bid.toDto(productTitle: String, bidderPoint: Long): BidResultResponse {
    return BidResultResponse(
        auctionId = this.auctionId,
        title = productTitle,
        price = this.bidPrice,
        currentPoint = bidderPoint
    )
}