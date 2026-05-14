package org.example.order.appilcation.service

import auction.auctionproductapi.auction.client.AuctionClient
import auction.auctionproductapi.product.client.ProductDetailClient
import auction.auctionuserapi.user.client.UserClient
import org.example.order.appilcation.dto.OrderResponse
import org.example.order.appilcation.dto.toDto
import org.example.order.domain.repository.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderAdminService(
    private val orderRepository: OrderRepository,
    private val userClient: UserClient,
    private val productDetailClient: ProductDetailClient,
    private val auctionClient: AuctionClient,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    fun findOrder(userId: Long, pageable: Pageable): Slice<OrderResponse> {
        log.info("사용자 {}의 낙찰 내역 조회 요청", userId)
        val orders = orderRepository.findSliceByBuyerId(userId, pageable)

        if (!orders.hasContent()) {
            return orders.map { it.toDto(null, null, null) }
        }

        val userDto = userClient.userModuleDto(userId)

        val auctionIds = orders.content.map { it.auctionId }
        val auctionDtos = auctionClient.auctionListModuleDto(auctionIds)
        val auctionMap = auctionDtos.associateBy { it.auctionId }

        val productIds = auctionDtos.map { it.productId }.distinct()
        val productDtos = productDetailClient.productDetailResponses(productIds)
        val productMap = productDtos.associateBy { it.productId }

        // 4. 데이터 최종 조립
        return orders.map { order ->
            val auctionDto = auctionMap[order.auctionId]
                ?: throw IllegalArgumentException("Auction 정보를 찾을 수 없습니다. (ID: ${order.auctionId})")

            val productDto = productMap[auctionDto.productId]
                ?: throw IllegalArgumentException("Product 정보를 찾을 수 없습니다. (ID: ${auctionDto.productId})")

            order.toDto(userDto, productDto, auctionDto)
        }
    }

}
