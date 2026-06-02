package org.example.bid.controller.internal.query

import auction.auctionbidapi.dto.BidCommonResponse
import auction.auctionbidapi.status.BidStatus
import org.example.bid.application.service.module.BidAuctionService
import org.example.bid.application.service.module.BidCommonService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/bid")
class InternalBidQueryController(
    private val bidCommonService: BidCommonService,
    private val bidAuctionService: BidAuctionService,
) {
    @GetMapping("/{id}/count")
    fun bidCount(@PathVariable("id") userId: Long) : Long {
        return bidCommonService.bidCount(userId)
    }

    @GetMapping("/{id}/exists")
    fun existsByStatusAndAuction(bidStatus: BidStatus, @PathVariable("id") auctionId: Long?) : Boolean {
        return bidCommonService.existsByStatusAndAuction(bidStatus, auctionId)
    }

    @GetMapping("/{id}/top")
    fun findTopByStatusAndAuctionOrderByBidIdDesc(@PathVariable("id") auctionId: Long) : BidCommonResponse {
        return bidAuctionService.findTopByStatusAndAuctionOrderByBidIdDesc(auctionId)
    }
}