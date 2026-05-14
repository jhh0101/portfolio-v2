package auction.auctionbidapi.client

import auction.auctionbidapi.status.BidStatus

interface BidClient {
    fun bidCount(userId: Long) : Long
    fun existsByStatusAndAuction(bidStatus: BidStatus, auction: Long?) : Boolean
}