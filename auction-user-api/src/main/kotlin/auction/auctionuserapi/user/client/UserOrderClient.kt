package auction.auctionuserapi.user.client

interface UserOrderClient {

    fun updateUserRating(userId: Long, ratingAvg: Double)
}