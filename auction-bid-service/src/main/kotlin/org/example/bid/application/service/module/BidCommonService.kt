package org.example.bid.application.service.module

import auction.auctionbidapi.client.BidClient
import auction.auctionbidapi.status.BidStatus
import auction.auctionproductapi.auction.status.AuctionStatus
import org.example.auction.product.feign.auction.AuctionFeignClient
import org.example.bid.domain.bid.repository.BidRepository
import org.springframework.stereotype.Service

@Service
class BidCommonService(
    private val bidRepository: BidRepository,
    private val auctionFeignClient: AuctionFeignClient,
) : BidClient{
    override fun bidCount(userId: Long) : Long {
        val auctionIds = auctionFeignClient.getAuctionIdsByStatus(AuctionStatus.PROCEEDING)
        if (auctionIds.isEmpty()) return 0L
        return bidRepository.bidCount(userId, auctionIds)
    }

    override fun existsByStatusAndAuction(
        bidStatus: BidStatus,
        auctionId: Long?
    ): Boolean {
        return bidRepository.existsByStatusAndAuctionId(bidStatus, checkNotNull(auctionId) {"경매 정보를 찾을 수 없습니다."})
    }
}