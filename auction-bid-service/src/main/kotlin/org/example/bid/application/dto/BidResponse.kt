package org.example.bid.application.dto

import auction.auctionbidapi.status.BidStatus
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class BidResponse(
    val bidId: Long,
    val auctionId: Long,
    val bidPrice: Long,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val bidTime: LocalDateTime,
    val status: BidStatus,
    val nickname: String,
) {}
