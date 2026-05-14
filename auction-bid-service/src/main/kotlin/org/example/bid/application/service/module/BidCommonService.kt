package org.example.bid.application.service.module

import auction.auctionbidapi.client.BidClient
import auction.auctionbidapi.status.BidStatus
import org.example.bid.domain.bid.repository.BidRepository
import org.springframework.stereotype.Service

@Service
class BidCommonService(
    private val bidRepository: BidRepository,
) : BidClient{
    override fun bidCount(userId: Long) : Long {
        return bidRepository.bidCount(userId)
    }

    override fun existsByStatusAndAuction(
        bidStatus: BidStatus,
        auction: Long?
    ): Boolean {
        TODO("Not yet implemented")
    }
}