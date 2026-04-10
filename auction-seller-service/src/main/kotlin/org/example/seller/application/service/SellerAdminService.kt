package org.example.seller.application.service

import auction.auctionsellerapi.status.SellerStatus
import auction.auctionuserapi.user.client.UserClient
import auction.auctionuserapi.user.error.UserErrorCode
import org.example.common.global.error.CustomException
import org.example.seller.application.dto.SellerApplyListResponse
import org.example.seller.application.dto.SellerRejectRequest
import org.example.seller.application.dto.SellerResponse
import org.example.seller.application.dto.toApplyDto
import org.example.seller.application.dto.toDto
import org.example.seller.domain.error.SellerErrorCode
import org.example.seller.domain.seller.entity.Seller
import org.example.seller.domain.seller.repository.SellerRepository
import org.example.seller.domain.seller.service.SellerAdminProcessor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SellerAdminService(
    private val sellerRepository: SellerRepository,
    private val sellerAdminProcessor: SellerAdminProcessor,
    private val userClient: UserClient,
) {
    @Transactional(readOnly = true)
    fun sellerList(pageable: Pageable): Page<SellerApplyListResponse> {
        val sellers: Page<Seller> = sellerRepository.findAllByStatus(SellerStatus.PENDING, pageable)

        val userMap = sellerAdminProcessor.sellerList(sellers)

        return sellers.map { seller ->
            val userInfo = userMap[seller.userId]
                ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

            seller.toApplyDto(userInfo.userEmail, userInfo.userNickname)
        }
    }

    fun approveSeller(sellerId: Long): SellerResponse {
        val seller: Seller = sellerRepository.findByIdOrNull(sellerId)
            ?: throw CustomException(SellerErrorCode.SELLER_NOT_FOUND, "판매자를 찾을 수 없습니다.")

        val userDto = sellerAdminProcessor.approveSeller(seller)

        return seller.toDto(userDto.userNickname)
    }

    fun rejectSeller(sellerId: Long, request: SellerRejectRequest): SellerResponse {
        val seller: Seller = sellerRepository.findByIdOrNull(sellerId)
            ?: throw CustomException(SellerErrorCode.SELLER_NOT_FOUND, "판매자를 찾을 수 없습니다.")

        val userDto = sellerAdminProcessor.rejectSeller(seller, request)

        return seller.toDto(userDto.userNickname)
    }

    @Transactional(readOnly = true)
    fun sellerDetails(sellerId: Long): SellerResponse {
        val seller: Seller = sellerRepository.findById(sellerId)
            .orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }

        val userDto = userClient.userModuleDto(seller.userId)

        return seller.toDto(userDto.userNickname)
    }

}
