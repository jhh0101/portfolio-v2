package auction.auctionorderapi.client

interface OrderClient {
    fun saveOrder(auctionId: Long, buyerId: Long, finalPrice: Long)
}