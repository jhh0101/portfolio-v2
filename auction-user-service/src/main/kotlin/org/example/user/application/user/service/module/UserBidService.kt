package org.example.user.application.user.service.module

import auction.auctionuserapi.user.client.UserBidClient
import org.example.common.global.error.CustomException
import auction.auctionuserapi.user.error.UserErrorCode
import org.example.user.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserBidService(
    private val userRepository: UserRepository
) : UserBidClient{

    override fun userValidCheck(userId: Long, bidderId: Long) {
        if (userId != bidderId) {
            throw CustomException(UserErrorCode.USER_VERIFICATION_FAILED, "사용자 정보가 일치하지 않습니다.")
        }
    }

}