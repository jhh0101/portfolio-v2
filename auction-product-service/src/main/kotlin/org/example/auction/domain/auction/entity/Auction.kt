package org.example.auction.domain.auction.entity

import auction.auctionproductapi.auction.status.AuctionStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import auction.auctionproductapi.auction.error.AuctionErrorCode
import org.example.common.global.error.CustomException
import org.example.product.domain.product.entity.Product
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@SQLDelete(sql = "UPDATE auctions SET status = 'CANCELED' WHERE auction_id = ?")
@SQLRestriction("status != 'CANCELED'")
@Table(name = "auctions")
class Auction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_id")
    var auctionId: Long? = null,

    @OneToOne
    @JoinColumn(name = "product_id")
    val product: Product,

    @Column(name = "start_price")
    var startPrice: Long,

    @Column(name = "current_price")
    var currentPrice: Long,

    @Column(name = "start_time")
    var startTime: LocalDateTime,

    @Column(name = "end_time")
    var endTime: LocalDateTime,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: AuctionStatus,

    ) {

    fun updateCurrentPrice(currentPrice: Long) {
        this.currentPrice = currentPrice
    }

    fun updateAuction(
        startPrice: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        this.startPrice = startPrice
        this.currentPrice = startPrice
        this.startTime = startTime
        this.endTime = endTime
    }

    fun validateBiddingTime() {
        val now = LocalDateTime.now()

        if ((AuctionStatus.PROCEEDING != this.status) ||
            now.isAfter(this.endTime) ||
            now.isBefore(this.startTime)
        ) {
            throw CustomException(AuctionErrorCode.INVALID_AUCTION_TIME)
        }
    }

    fun changeStatus(status: AuctionStatus) {
        this.status = status
    }
}
