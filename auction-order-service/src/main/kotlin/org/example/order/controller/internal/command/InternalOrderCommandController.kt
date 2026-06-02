package org.example.order.controller.internal.command

import org.example.order.appilcation.service.module.OrderCommonService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/order")
class InternalOrderCommandController(
    private val orderCommonService: OrderCommonService,
) {
    @PostMapping("/{auctionId}/save/{buyerId}")
    fun saveOrder(@PathVariable("auctionId") auctionId: Long, @PathVariable("buyerId") buyerId: Long, finalPrice: Long) {
        orderCommonService.saveOrder(auctionId, buyerId, finalPrice)
    }

}