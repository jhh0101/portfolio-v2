package org.example.order.domain.order.service.module

import auction.auctionorderapi.client.OrderClient
import org.example.order.domain.order.entity.Order
import org.example.order.domain.order.repository.OrderRepository
import org.springframework.stereotype.Service

@Service
class OrderCommonService(
    private val orderRepository: OrderRepository,
) : OrderClient {
    override fun saveOrder(auctionId: Long, buyerId: Long, finalPrice: Long) {
        val saveOrder = Order(
            auctionId = auctionId,
            buyerId = buyerId,
            finalPrice = finalPrice
        )
        orderRepository.save(saveOrder)
    }
}