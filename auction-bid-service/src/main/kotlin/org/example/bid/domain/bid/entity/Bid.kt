package org.example.bid.domain.bid.entity

import auction.auctionbidapi.status.BidStatus
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.example.bid.global.base.BaseCreatedAt

@Entity
@Table(
    name = "bids",
    indexes = [Index(
        name = "idx_bid_auction",
        columnList = "auction_id"
    ), Index(name = "idx_bid_bidder", columnList = "bidder_id")]
)
@AttributeOverride(name = "createdAt", column = Column(name = "bid_time", updatable = false))
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    var bidId: Long? = null,

    @Column(name = "auction_id")
    val auctionId: Long,

    @Column(name = "bidder_id")
    val bidderId: Long,

    @Column(name = "bid_price", nullable = false)
    var bidPrice: Long,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: BidStatus? = null,
) : BaseCreatedAt() {

    fun cancelBid() {
        this.status = BidStatus.CANCELED
    }

    fun invalidBid() {
        this.status = BidStatus.INVALID
    }
}
