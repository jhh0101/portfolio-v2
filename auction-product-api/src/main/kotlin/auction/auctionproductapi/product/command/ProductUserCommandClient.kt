package auction.auctionproductapi.product.command

interface ProductUserCommandClient {
    fun deleteProductsBySuspendedUser(userId: Long)
}