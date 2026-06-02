package org.example.auction.product.feign.auction

import auction.auctionproductapi.auction.client.AuctionBidClient
import auction.auctionproductapi.auction.client.AuctionClient
import auction.auctionproductapi.auction.dto.AuctionCommonResponse
import auction.auctionproductapi.auction.status.AuctionStatus
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "auction-product-service", url = "http://localhost:8082")
@RequestMapping("/internal/auction")
interface AuctionFeignClient : AuctionBidClient, AuctionClient {

    @GetMapping("/{id}")
    override fun auctionModuleDto(@PathVariable("id") auctionId: Long) : AuctionCommonResponse

    @GetMapping("/{id}/lock")
    override fun auctionLockModuleDto(@PathVariable("id") auctionId: Long) : AuctionCommonResponse

    @GetMapping("/list")
    override fun auctionListModuleDto(@RequestParam("auctionIds") auctionIds: List<Long>) : List<AuctionCommonResponse>

    @GetMapping("/auctions/ids")
    override fun getAuctionIdsByStatus(status: AuctionStatus) : List<Long>

    @GetMapping("/{id}/valid/time")
    override fun validateBiddingTime(@PathVariable("id") auctionId: Long)

    @GetMapping("/{id}/valid/status")
    override fun auctionValidStatus(auctionDto: AuctionCommonResponse,@PathVariable("id") auctionId: Long)

    @PatchMapping("/{id}/update/price")
    override fun updateCurrentPrice(@PathVariable("id") auctionId: Long, bidPrice: Long)
}