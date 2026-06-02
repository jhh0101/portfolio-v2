package org.example.bid.controller.internal.command

import auction.auctionbidapi.dto.BidCommonResponse
import org.example.bid.application.service.command.BidUserCommandService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/bid")
class InternalBidCommandController(
    private val bidUserCommandService: BidUserCommandService,
) {
    @PostMapping("/{id}/cancel")
    fun cancelActiveBidsAndGetRefundTargets(@PathVariable("id") userId: Long) : List<BidCommonResponse> {
        return bidUserCommandService.cancelActiveBidsAndGetRefundTargets(userId)
    }
}