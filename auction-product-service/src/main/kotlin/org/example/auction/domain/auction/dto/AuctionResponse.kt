package org.example.auction.domain.auction.dto

import com.fasterxml.jackson.annotation.JsonFormat
import org.example.auction.domain.auction.entity.Auction
import auction.auctionproductapi.auction.status.AuctionStatus
import java.time.LocalDateTime

data class AuctionResponse(
    val auctionId: Long,
    val startPrice: Long,
    val currentPrice: Long,
    val status: AuctionStatus,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val startTime: LocalDateTime,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val endTime: LocalDateTime
)

fun Auction.toAuctionResponse(): AuctionResponse {
    return AuctionResponse(
        auctionId = this.auctionId ?: 0L,
        startPrice = this.startPrice ?: 0L,
        currentPrice = this.currentPrice ?: 0L,
        startTime = this.startTime ?: LocalDateTime.now(),
        endTime = this.endTime ?: LocalDateTime.now(),
        status = this.status ?: AuctionStatus.PROCEEDING,
    )
}