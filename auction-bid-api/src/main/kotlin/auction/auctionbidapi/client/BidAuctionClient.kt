package auction.auctionbidapi.client

import auction.auctionbidapi.dto.BidAuctionResponse
import java.util.Optional

interface BidAuctionClient {
    fun findTopByStatusAndAuctionOrderByBidIdDesc(auctionId: Long) : BidAuctionResponse
}