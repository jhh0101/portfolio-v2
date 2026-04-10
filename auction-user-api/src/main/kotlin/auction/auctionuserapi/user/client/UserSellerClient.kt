package auction.auctionuserapi.user.client

import auction.auctionuserapi.user.type.Role

interface UserSellerClient {
    fun userUpdateRole(userId:Long, role: Role)
}
