package org.example.user.application.user.service

import auction.auctionbidapi.command.BidUserCommandClient
import auction.auctionproductapi.product.command.ProductUserCommandClient
import auction.auctionuserapi.user.error.UserErrorCode
import org.example.common.global.error.CustomException
import org.example.user.application.auth.service.RefreshTokenService
import org.example.user.application.user.dto.UserDeleteResponse
import org.example.user.application.user.dto.UserResponse
import org.example.user.application.user.dto.UserSuspendReasonResponse
import org.example.user.application.user.dto.UserSuspensionRequest
import org.example.user.application.user.dto.toDeleteDto
import org.example.user.application.user.dto.toDto
import org.example.user.application.user.dto.toSuspendReasonDto
import org.example.user.domain.user.dto.UserSearchCondition
import org.example.user.domain.user.entity.User
import org.example.user.domain.user.repository.UserQueryRepository
import org.example.user.domain.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserAdminService(
    private val userRepository: UserRepository,
    private val userQueryRepository: UserQueryRepository,
    private val refreshTokenService: RefreshTokenService,
    private val bidUserCommandClient: BidUserCommandClient,
    private val productUserCommandClient: ProductUserCommandClient,
) {
    @Transactional
    fun suspend(userId: Long, request: UserSuspensionRequest): UserDeleteResponse {
        validateUserProtection(userId)

        val user = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")

        val bidList = bidUserCommandClient.cancelActiveBidsAndGetRefundTargets(userId)

        val bidderIds = bidList.map { it.bidderId }.toSet()

        val usersMap = userRepository.findAllById(bidderIds).associateBy { it.userId }

        for (bid in bidList) {
            usersMap[bid.bidderId]?.addPoint(bid.bidPrice)
        }

        productUserCommandClient.deleteProductsBySuspendedUser(userId)

        refreshTokenService.deleteRefreshToken(userId)

        user.suspend(userId, request.suspensionReason)

        return user.toDeleteDto()
    }

    @Transactional(readOnly = true)
    fun userList(condition: UserSearchCondition, pageable: Pageable): Page<UserResponse> {
        val users: Page<User> = userQueryRepository.userList(condition, pageable)
        return users.map {it.toDto()}
    }

    @Transactional(readOnly = true)
    fun suspendReason(userId: Long): UserSuspendReasonResponse {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")

        return user.toSuspendReasonDto()
    }

    private fun validateUserProtection(userId: Long?) {
        if (PROTECTED_USER_IDS.contains(userId)) {
            throw CustomException(UserErrorCode.PROTECT_DEFAULT_USERS)
        }
    }

    companion object {
        private val PROTECTED_USER_IDS = mutableSetOf<Long?>(1L, 2L, 3L, 4L)
    }
}