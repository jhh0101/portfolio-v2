package org.example.bid.application.service.module

import auction.auctionbidapi.client.BidClient
import auction.auctionbidapi.status.BidStatus
import auction.auctionproductapi.auction.client.AuctionClient
import auction.auctionproductapi.auction.status.AuctionStatus
import org.example.bid.domain.bid.repository.BidRepository
import org.springframework.stereotype.Service

@Service
class BidCommonService(
    private val bidRepository: BidRepository,
    private val auctionClient: AuctionClient
) : BidClient{
    override fun bidCount(userId: Long) : Long {
        val auctionIds = auctionClient.getAuctionIdsByStatus(AuctionStatus.PROCEEDING)
        if (auctionIds.isEmpty()) return 0L
        return bidRepository.bidCount(userId, auctionIds)
    }

    override fun existsByStatusAndAuction(
        bidStatus: BidStatus,
        auction: Long?
    ): Boolean {
        TODO("Not yet implemented")
    }
}