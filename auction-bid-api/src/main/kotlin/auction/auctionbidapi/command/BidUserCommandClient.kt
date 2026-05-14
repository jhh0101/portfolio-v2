package auction.auctionbidapi.command

import auction.auctionbidapi.dto.BidCommonResponse

interface BidUserCommandClient {
    fun cancelActiveBidsAndGetRefundTargets(userId: Long) : List<BidCommonResponse>
}
