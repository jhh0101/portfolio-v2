package org.example.order.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.example.common.global.base.BaseCreatedAt

@Entity
@Table(
    name = "orders",
    indexes = [Index(name = "idx_order_buyer", columnList = "buyer_id"), Index(
        name = "idx_order_auction",
        columnList = "auction_id"
    )]
)
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    var orderId: Long? = null,

    @Column(name = "auction_id")
    val auctionId: Long,

    @Column(name = "buyer_id")
    val buyerId: Long,

    @Column(name = "final_price")
    var finalPrice: Long? = 0L,

    ) : BaseCreatedAt() {

}
