package org.example.bid.domain.bid.dto

import auction.auctionbidapi.status.BidStatus
import java.time.LocalDateTime

interface BidInfo {
    val bidId: Long
    val auctionId: Long
    val nickname: String
    val bidPrice: Long
    val bidTime: LocalDateTime
    val status: BidStatus
}