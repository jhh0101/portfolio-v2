package org.example.bid.application.service.module

import auction.auctionbidapi.client.BidAuctionClient
import auction.auctionbidapi.dto.BidCommonResponse
import auction.auctionbidapi.error.BidErrorCode
import auction.auctionbidapi.status.BidStatus
import org.example.bid.domain.bid.repository.BidRepository
import org.example.common.global.error.CustomException

class BidAuctionService(
    private val bidRepository: BidRepository
) : BidAuctionClient {
    override fun findTopByStatusAndAuctionOrderByBidIdDesc(auctionId: Long) : BidCommonResponse {
        val bid = bidRepository.findTopByStatusAndAuctionIdOrderByBidIdDesc(BidStatus.ACTIVE, auctionId)
        return BidCommonResponse(
            bidId = bid?.bidId ?: throw CustomException(BidErrorCode.BID_NOT_FOUND),
            bidderId = bid.bidderId,
            auctionId = bid.auctionId,
            bidPrice = bid.bidPrice,
            status = bid.status
        )
    }
}