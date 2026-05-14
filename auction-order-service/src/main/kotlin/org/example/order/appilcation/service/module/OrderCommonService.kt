package org.example.order.appilcation.service.module

import auction.auctionorderapi.client.OrderClient
import auction.auctionorderapi.dto.OrderCommonResponse
import auction.auctionorderapi.error.OrderErrorCode
import org.example.common.global.error.CustomException
import org.example.order.domain.entity.Order
import org.example.order.domain.repository.OrderRepository
import org.springframework.data.repository.findByIdOrNull
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

    override fun orderModuleDto(orderId: Long): OrderCommonResponse {
        val order = orderRepository.findByIdOrNull(orderId)
            ?: throw CustomException(OrderErrorCode.ORDER_NOT_FOUND)

        return OrderCommonResponse(
            orderId = orderId,
            auctionId = order.auctionId,
            buyerId = order.buyerId,
            finalPrice = checkNotNull(order.finalPrice) {"낙찰 금액을 찾을 수 없습니다."}
        )
    }

    override fun orderListModuleDto(orderIds: List<Long>): List<OrderCommonResponse> {
        val orders = orderRepository.findAllById(orderIds)

        return orders.map { order ->
            OrderCommonResponse(
                orderId = order.orderId ?: throw CustomException(OrderErrorCode.ORDER_NOT_FOUND),
                auctionId = order.auctionId,
                buyerId = order.buyerId,
                finalPrice = checkNotNull(order.finalPrice) {"낙찰 금액을 찾을 수 없습니다."}
            )
        }

    }
}