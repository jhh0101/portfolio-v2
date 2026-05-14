package org.example.order.domain.repository

import org.example.order.domain.entity.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = ["auction", "auction.product", "auction.product.image"])
    fun findAllByBuyerId(userId: Long, pageable: Pageable): Page<Order>
    fun findSliceByBuyerId(userId: Long, pageable: Pageable): Slice<Order>

    fun findByAuctionId(auctionAuctionId: Long): Order?
}
