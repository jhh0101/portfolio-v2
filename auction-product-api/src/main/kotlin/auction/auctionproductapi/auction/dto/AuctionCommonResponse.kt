package auction.auctionproductapi.auction.dto

import java.time.LocalDateTime

data class AuctionCommonResponse(
    val auctionId: Long,
    val productId: Long,
    val startPrice: Long,
    val currentPrice: Long,
    val status: String?,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
)