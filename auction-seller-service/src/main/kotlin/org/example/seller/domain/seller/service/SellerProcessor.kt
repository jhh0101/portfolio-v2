package org.example.seller.domain.seller.service

import auction.auctionsellerapi.status.SellerStatus
import auction.auctionuserapi.user.client.UserClient
import auction.auctionuserapi.user.client.UserSellerClient
import auction.auctionuserapi.user.dto.UserCommonResponse
import auction.auctionuserapi.user.type.Role
import jakarta.transaction.Transactional
import org.example.common.global.error.CustomException
import org.example.common.global.error.GlobalErrorCode
import org.example.seller.application.dto.SellerApplyRequest
import org.example.seller.domain.error.SellerErrorCode
import org.example.seller.domain.seller.entity.Seller
import org.example.seller.domain.seller.repository.SellerRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
@Transactional
class SellerProcessor(
    private val sellerRepository: SellerRepository,
    private val userClient: UserClient,
    private val userSellerClient: UserSellerClient,
) {

    fun sellerApply(userId: Long, request: SellerApplyRequest) : Pair<Seller, UserCommonResponse> {
        validateUserProtection(userId)

        val userDto = userClient.userModuleDto(userId)

        val optionalSeller: Optional<Seller> = sellerRepository.findByUserId(userId)
        val seller: Seller?

        if (optionalSeller.isPresent) {
            seller = optionalSeller.get()
            if (seller.status !== SellerStatus.REJECTED && seller.status !== SellerStatus.CANCELED) {
                throw CustomException(SellerErrorCode.DUPLICATE_SELLER, "이미 신청 중...")
            }
            seller.updateApply(request)
        } else {
            seller = Seller(
                userId = userDto.userId,
                storeName = request.storeName,
                bankName = request.bankName,
                accountNumber = request.accountNumber,
                accountHolder = request.accountHolder,
                status = SellerStatus.PENDING
            )
        }

        return seller to userDto
    }

    private fun validateUserProtection(userId: Long) {
        if (PROTECTED_USER_IDS.contains(userId)) {
            throw CustomException(GlobalErrorCode.PROTECT_DEFAULT_USERS)
        }
    }

    fun applyModify(userId: Long, seller: Seller, request: SellerApplyRequest) : UserCommonResponse {
        if (userId != seller.userId) {
            throw CustomException(GlobalErrorCode.BAD_REQUEST, "사용자 정보가 일치하지 않습니다.")
        }

        val userDto = userClient.userModuleDto(userId)

        seller.updateApply(request)

        return userDto
    }

    fun sellerCancel(userId: Long, seller: Seller) : UserCommonResponse {
        if (userId != seller.userId) {
            throw CustomException(GlobalErrorCode.BAD_REQUEST, "사용자 정보가 일치하지 않습니다.")
        }

        if (seller.status === SellerStatus.CANCELED || seller.status === SellerStatus.REJECTED) {
            throw CustomException(GlobalErrorCode.BAD_REQUEST, "취소할 수 없는 상태입니다. (이미 취소 또는 반려됨)")
        }

        val userDto = userClient.userModuleDto(userId)
        userSellerClient.userUpdateRole(userId, Role.USER)

        seller.cancelSeller()

        return userDto
    }

    companion object {
        private val PROTECTED_USER_IDS = mutableSetOf<Long>(1L, 2L, 3L, 4L)
    }
}