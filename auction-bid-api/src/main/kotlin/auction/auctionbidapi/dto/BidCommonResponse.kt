package auction.auctionbidapi.dto

import auction.auctionbidapi.status.BidStatus

data class BidCommonResponse(
    val bidId: Long,
    val auctionId: Long,
    val bidderId: Long,
    val bidPrice: Long,
    val status: BidStatus,
)