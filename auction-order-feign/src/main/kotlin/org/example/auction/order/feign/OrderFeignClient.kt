package org.example.auction.order.feign

import auction.auctionorderapi.dto.OrderCommonResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "auction-order-service", url = "http://localhost:8083", contextId = "orderFeignClient")
interface OrderFeignClient {
    
    @PostMapping("/internal/order/{auctionId}/save/{buyerId}")
    fun saveOrder(@PathVariable("auctionId") auctionId: Long,
                           @PathVariable("buyerId") buyerId: Long,
                           @RequestParam("finalPrice") finalPrice: Long
    )

    @GetMapping("/internal/order/{id}")
    fun orderModuleDto(@PathVariable("id") orderId: Long): OrderCommonResponse

    @GetMapping("/internal/order/list")
    fun orderListModuleDto(@RequestParam("ids")orderIds: List<Long>): List<OrderCommonResponse>

}