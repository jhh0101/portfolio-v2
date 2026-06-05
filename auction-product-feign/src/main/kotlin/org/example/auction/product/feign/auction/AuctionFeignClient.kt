package org.example.auction.product.feign.auction

import auction.auctionproductapi.auction.dto.AuctionCommonResponse
import auction.auctionproductapi.auction.status.AuctionStatus
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "auction-product-service", url = "http://localhost:8082", contextId = "auctionFeignClient")
interface AuctionFeignClient {

    @GetMapping("/internal/auction/{id}")
    fun auctionModuleDto(@PathVariable("id") auctionId: Long) : AuctionCommonResponse

    @GetMapping("/internal/auction/{id}/lock")
    fun auctionLockModuleDto(@PathVariable("id") auctionId: Long) : AuctionCommonResponse

    @GetMapping("/internal/auction/list")
    fun auctionListModuleDto(@RequestParam("auctionIds") auctionIds: List<Long>) : List<AuctionCommonResponse>

    @GetMapping("/internal/auction/auctions/ids")
    fun getAuctionIdsByStatus(status: AuctionStatus) : List<Long>

    @GetMapping("/internal/auction/{id}/valid/time")
    fun validateBiddingTime(@PathVariable("id") auctionId: Long)

    @GetMapping("/internal/auction/{id}/valid/status")
    fun auctionValidStatus(auctionDto: AuctionCommonResponse,@PathVariable("id") auctionId: Long)

    @PatchMapping("/internal/auction/{id}/update/price")
    fun updateCurrentPrice(@PathVariable("id") auctionId: Long, @RequestParam("bidPrice") bidPrice: Long)
}