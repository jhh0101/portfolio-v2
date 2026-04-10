package auction.auctionsellerapi.client

import auction.auctionsellerapi.status.SellerStatus

interface SellerClient {
    fun getSellerStatus(userId: Long) : SellerStatus
}