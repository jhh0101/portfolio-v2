package org.example.order.appilcation.service

import auction.auctionorderapi.error.OrderErrorCode
import auction.auctionproductapi.auction.client.AuctionClient
import org.example.auction.product.feign.product.ProductFeignClient
import org.example.auction.user.feign.UserFeignClient
import org.example.common.global.error.CustomException
import org.example.order.appilcation.dto.OrderResponse
import org.example.order.appilcation.dto.toDto
import org.example.order.domain.entity.Order
import org.example.order.domain.repository.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val userFeignClient: UserFeignClient,
    private val productFeignClient: ProductFeignClient,
    private val auctionClient: AuctionClient,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    fun findOrder(userId: Long, pageable: Pageable): Page<OrderResponse> {
        log.info("사용자 {}의 낙찰 내역 조회 요청", userId)
        val orders = orderRepository.findAllByBuyerId(userId, pageable)

        if (orders.isEmpty) {
            return Page.empty(pageable)
        }

        val userDto = userFeignClient.userModuleDto(userId)

        val auctionIds = orders.content.map { it.auctionId }
        val auctionDtos = auctionClient.auctionListModuleDto(auctionIds)
        val auctionMap = auctionDtos.associateBy { it.auctionId }

        val productIds = auctionDtos.map { it.productId }.distinct()
        val productDtos = productFeignClient.productDetailResponses(productIds)
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

    @Transactional(readOnly = true)
    fun auctionOrder(auctionId: Long): OrderResponse {
        val order: Order = orderRepository.findByAuctionId(auctionId)
            ?: throw CustomException(OrderErrorCode.ORDER_NOT_FOUND)

        val userDto = userFeignClient.userModuleDto(order.buyerId)

        val auctionDto = auctionClient.auctionModuleDto(order.auctionId)

        val productDto = productFeignClient.productDetailResponse(auctionDto.productId)

        log.info("옥션 {}의 낙찰자 조회", auctionId)
        return order.toDto(userDto, productDto, auctionDto)
    }
}
