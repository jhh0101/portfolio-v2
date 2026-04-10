package org.example.bid.domain.bid.dto

import com.fasterxml.jackson.annotation.JsonFormat
import auction.auctionbidapi.status.BidStatus
import java.time.LocalDateTime

class BidInfoResponse(
    val bidId: Long,
    val auctionId: Long,
    val nickname: String,
    val bidPrice: Long,
    val status: BidStatus,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val bidTime: LocalDateTime? = null
)

fun BidInfo.toDto(): BidInfoResponse {
    return BidInfoResponse(
        bidId = this.bidId,
        auctionId = this.auctionId,
        nickname = this.nickname,
        bidPrice = this.bidPrice,
        status = this.status,
        bidTime = this.bidTime
    )
}
