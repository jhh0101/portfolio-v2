package org.example.auction.order.feign

import auction.auctionorderapi.client.OrderClient
import auction.auctionorderapi.dto.OrderCommonResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "auction-order-service", url = "http://localhost:8083")
@RequestMapping("/internal/order")
interface OrderFeignClient : OrderClient {
    
    @PostMapping("/{auctionId}/save/{buyerId}")
    override fun saveOrder(@PathVariable("auctionId") auctionId: Long, @PathVariable("buyerId") buyerId: Long, finalPrice: Long)

    @GetMapping("/{id}")
    override fun orderModuleDto(@PathVariable("id") orderId: Long): OrderCommonResponse

    @GetMapping("/list")
    override fun orderListModuleDto(@RequestParam("ids")orderIds: List<Long>): List<OrderCommonResponse>

}