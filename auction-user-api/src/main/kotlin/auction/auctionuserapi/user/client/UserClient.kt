package auction.auctionuserapi.user.client

import auction.auctionuserapi.user.dto.UserCommonResponse

interface UserClient {

    fun userModuleDto(userId: Long) : UserCommonResponse

    fun getUsersByIds(userIds: List<Long>) : Map<Long, UserCommonResponse>

    fun validateUserExists(userId: Long)

    fun userAddPoint(userId: Long, bidPrice: Long)

    fun userSubPoint(userId: Long, bidPrice: Long)
}