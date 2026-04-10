package org.example.bid.application.service.module

import auction.auctionbidapi.client.BidAuctionClient
import auction.auctionbidapi.dto.BidAuctionResponse
import auction.auctionbidapi.status.BidStatus
import org.example.bid.domain.bid.repository.BidRepository

class BidAuctionService(
    private val bidRepository: BidRepository
) : BidAuctionClient {
    override fun findTopByStatusAndAuctionOrderByBidIdDesc(auctionId: Long) : BidAuctionResponse {
        val bid = bidRepository.findTopByStatusAndAuctionIdOrderByBidIdDesc(BidStatus.ACTIVE, auctionId)
        return BidAuctionResponse(bidId = bid?.bidId ?: 0L)
    }
}