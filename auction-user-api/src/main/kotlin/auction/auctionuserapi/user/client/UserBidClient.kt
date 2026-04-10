package auction.auctionuserapi.user.client

interface UserBidClient {

    fun userValidCheck(userId: Long, bidderId: Long)
}