package org.example.seller.controller.internal.query

import auction.auctionsellerapi.status.SellerStatus
import org.example.seller.application.service.module.SellerCommonService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/seller")
class InternalSellerQueryController(
    private val sellerCommonService: SellerCommonService,
) {
    @GetMapping("/{id}/status")
    fun getSellerStatus(@PathVariable("id") userId: Long) : SellerStatus {
        return sellerCommonService.getSellerStatus(userId)
    }

}