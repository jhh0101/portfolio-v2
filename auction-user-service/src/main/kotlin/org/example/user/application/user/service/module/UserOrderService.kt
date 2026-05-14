package org.example.user.application.user.service.module

import auction.auctionuserapi.user.client.UserOrderClient
import auction.auctionuserapi.user.error.UserErrorCode
import org.example.common.global.error.CustomException
import org.example.user.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserOrderService(
    private val userRepository: UserRepository,
): UserOrderClient {
    override fun updateUserRating(userId: Long, ratingAvg: Double) {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        user.updateRating(ratingAvg)
    }
}