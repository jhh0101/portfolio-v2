package auction.auctionbidapi.client

import auction.auctionbidapi.dto.BidCommonResponse

interface BidAuctionClient {
    fun findTopByStatusAndAuctionOrderByBidIdDesc(auctionId: Long) : BidCommonResponse
}