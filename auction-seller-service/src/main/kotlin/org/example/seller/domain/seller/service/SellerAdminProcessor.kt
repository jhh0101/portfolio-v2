package org.example.seller.domain.seller.service

import auction.auctionsellerapi.status.SellerStatus
import auction.auctionuserapi.user.dto.UserCommonResponse
import auction.auctionuserapi.user.error.UserErrorCode
import auction.auctionuserapi.user.type.Role
import jakarta.transaction.Transactional
import org.example.auction.user.feign.UserFeignClient
import org.example.common.global.error.CustomException
import org.example.common.global.error.GlobalErrorCode
import org.example.seller.application.dto.SellerRejectRequest
import org.example.seller.domain.seller.entity.Seller
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
@Transactional
class SellerAdminProcessor(
    private val userFeignClient: UserFeignClient,
) {

    fun sellerList(sellers: Page<Seller>) : Map<Long, UserCommonResponse> {
        val userIds = sellers.map { it.userId }.toSet().toList()

        return userFeignClient.getUsersByIds(userIds)
    }

    fun approveSeller(seller: Seller) : UserCommonResponse{
        validateUserProtection(seller.userId)

        if (seller.status !== SellerStatus.PENDING) {
            throw CustomException(
                GlobalErrorCode.BAD_REQUEST,
                "현재 상태(" + seller.status + ")에서는 승인할 수 없습니다."
            )
        }

        val userDto = userFeignClient.userModuleDto(seller.userId)

        userFeignClient.userUpdateRole(seller.userId, Role.SELLER)

        seller.approveSeller()

        return userDto
    }

    fun rejectSeller(seller: Seller, request: SellerRejectRequest) : UserCommonResponse{
        if (seller.status !== SellerStatus.PENDING) {
            throw CustomException(
                GlobalErrorCode.BAD_REQUEST,
                "현재 상태(" + seller.status + ")에서는 거절할 수 없습니다."
            )
        }

        val userDto = userFeignClient.userModuleDto(seller.userId)

        seller.rejectSeller(request.rejectReason)

        return userDto
    }

    private fun validateUserProtection(userId: Long) {
        if (PROTECTED_USER_IDS.contains(userId)) {
            throw CustomException(UserErrorCode.PROTECT_DEFAULT_USERS)
        }
    }

    companion object {
        private val PROTECTED_USER_IDS = mutableSetOf<Long>(1L, 2L, 3L, 4L)
    }
}