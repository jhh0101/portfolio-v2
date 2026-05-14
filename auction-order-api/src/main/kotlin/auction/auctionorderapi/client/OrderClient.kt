package auction.auctionorderapi.client

import auction.auctionorderapi.dto.OrderCommonResponse

interface OrderClient {
    fun saveOrder(auctionId: Long, buyerId: Long, finalPrice: Long)

    fun orderModuleDto(orderId: Long) : OrderCommonResponse

    fun orderListModuleDto(orderIds: List<Long>) : List<OrderCommonResponse>
}