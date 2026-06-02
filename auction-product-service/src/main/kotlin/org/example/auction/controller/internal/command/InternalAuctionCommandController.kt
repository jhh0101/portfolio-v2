package org.example.auction.controller.internal.command

import org.example.auction.application.service.module.AuctionBidService
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/auction")
class InternalAuctionCommandController(
    private val auctionBidService: AuctionBidService,
) {
    @PatchMapping("/{id}/update/price")
    fun updateCurrentPrice(@PathVariable("id") auctionId: Long, @RequestParam("bidPrice") bidPrice: Long) {
        auctionBidService.updateCurrentPrice(auctionId, bidPrice)
    }
}