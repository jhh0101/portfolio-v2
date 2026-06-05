package org.example.auction.bid.feign

import auction.auctionbidapi.dto.BidCommonResponse
import auction.auctionbidapi.status.BidStatus
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(name = "auction-bid-service", url = "http://localhost:8084", contextId = "bidFeignClient")
interface BidFeignClient{
    @GetMapping("/internal/bid/{id}/count")
    fun bidCount(@PathVariable("id") userId: Long) : Long

    @GetMapping("/internal/bid/{id}/exists")
    fun existsByStatusAndAuction(bidStatus: BidStatus, @PathVariable("id") auctionId: Long?) : Boolean

    @GetMapping("/internal/bid/{id}/top")
    fun findTopByStatusAndAuctionOrderByBidIdDesc(@PathVariable("id") auctionId: Long) : BidCommonResponse

    @PostMapping("/internal/bid/{id}/cancel")
    fun cancelActiveBidsAndGetRefundTargets(@PathVariable("id") userId: Long) : List<BidCommonResponse>
}