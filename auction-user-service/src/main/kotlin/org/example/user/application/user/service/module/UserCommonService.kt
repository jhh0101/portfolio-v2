package org.example.user.application.user.service.module

import auction.auctionuserapi.user.client.UserClient
import auction.auctionuserapi.user.dto.UserCommonResponse
import org.example.common.global.error.CustomException
import org.example.user.domain.user.entity.User
import auction.auctionuserapi.user.error.UserErrorCode
import org.example.user.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserCommonService(
    private val userRepository: UserRepository
) : UserClient {

    override fun userModuleDto(userId: Long) : UserCommonResponse {
        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        return UserCommonResponse(
            userId = user.userId ?: 0L,
            userPoint = user.point,
            userNickname = user.nickname,
            userEmail = user.email,
            avgRating = user.avgRating,
        )
    }
    override fun getUsersByIds(userIds: List<Long>) : Map<Long, UserCommonResponse> {
        val users: List<User> = userRepository.findAllByUserIdIn(userIds)

        return users.associate { user ->
            val id = user.userId ?: 0L

            // id를 Key로, UserCommonResponse를 Value로 만듭니다.
            id to UserCommonResponse(
                userId = id,
                userPoint = user.point,
                userEmail = user.email,
                userNickname = user.nickname,
                avgRating = user.avgRating,
            )
        }
    }

    override fun validateUserExists(userId: Long) {
        if (!userRepository.existsById(userId)) {
            throw CustomException(UserErrorCode.USER_NOT_FOUND)
        }
    }

    override fun userAddPoint(userId: Long, bidPrice: Long) {
        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        user.addPoint(bidPrice)
    }

    override fun userSubPoint(userId: Long, bidPrice: Long) {
        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        user.subPoint(bidPrice)
    }

}