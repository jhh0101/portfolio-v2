package org.example.auction.seller.feign

import auction.auctionsellerapi.status.SellerStatus
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "auction-seller-service", url = "http://localhost:8081", contextId = "sellerFeignClient")
interface SellerFeignClient {
    @GetMapping("/internal/seller/{id}/status")
    fun getSellerStatus(@PathVariable("id") userId: Long) : SellerStatus
}