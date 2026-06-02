package org.example.order.controller.internal.query

import auction.auctionorderapi.dto.OrderCommonResponse
import org.example.order.appilcation.service.module.OrderCommonService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/order")
class InternalOrderQueryController(
    private val orderCommonService: OrderCommonService,
) {
    @GetMapping("/{id}")
    fun orderModuleDto(@PathVariable("id") orderId: Long): OrderCommonResponse {
        return orderCommonService.orderModuleDto(orderId)
    }

    @GetMapping("/list")
    fun orderListModuleDto(@RequestParam("ids")orderIds: List<Long>): List<OrderCommonResponse> {
        return orderCommonService.orderListModuleDto(orderIds)
    }

}