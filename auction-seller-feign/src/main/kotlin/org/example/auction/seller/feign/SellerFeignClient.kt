package org.example.auction.seller.feign

import auction.auctionsellerapi.client.SellerClient
import auction.auctionsellerapi.status.SellerStatus
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@FeignClient(name = "auction-seller-service", url = "http://localhost:8081")
@RequestMapping("/internal/seller")
interface SellerFeignClient : SellerClient {
    @GetMapping("/{id}/status")
    override fun getSellerStatus(@PathVariable("id") userId: Long) : SellerStatus
}