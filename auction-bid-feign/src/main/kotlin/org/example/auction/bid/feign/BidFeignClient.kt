package org.example.auction.bid.feign

import auction.auctionbidapi.client.BidAuctionClient
import auction.auctionbidapi.client.BidClient
import auction.auctionbidapi.command.BidUserCommandClient
import auction.auctionbidapi.dto.BidCommonResponse
import auction.auctionbidapi.status.BidStatus
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@FeignClient(name = "auction-bid-service", url = "http://localhost:8084")
@RequestMapping("/internal/bid")
interface BidFeignClient : BidAuctionClient, BidClient, BidUserCommandClient {
    @GetMapping("/{id}/count")
    override fun bidCount(@PathVariable("id") userId: Long) : Long

    @GetMapping("/{id}/exists")
    override fun existsByStatusAndAuction(bidStatus: BidStatus, @PathVariable("id") auctionId: Long?) : Boolean

    @GetMapping("/{id}/top")
    override fun findTopByStatusAndAuctionOrderByBidIdDesc(@PathVariable("id") auctionId: Long) : BidCommonResponse

    @PostMapping("/{id}/cancel")
    override fun cancelActiveBidsAndGetRefundTargets(@PathVariable("id") userId: Long) : List<BidCommonResponse>
}