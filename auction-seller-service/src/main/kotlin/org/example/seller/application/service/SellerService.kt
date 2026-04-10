package org.example.seller.application.service

import auction.auctionuserapi.user.client.UserClient
import auction.auctionuserapi.user.client.UserSellerClient
import auction.auctionuserapi.user.error.UserErrorCode
import org.example.common.global.error.CustomException
import org.example.seller.application.dto.RejectReasonResponse
import org.example.seller.application.dto.SellerApplyRequest
import org.example.seller.application.dto.SellerResponse
import org.example.seller.application.dto.toDto
import org.example.seller.application.dto.toRejectReasonDto
import org.example.seller.domain.error.SellerErrorCode
import org.example.seller.domain.seller.entity.Seller
import org.example.seller.domain.seller.repository.SellerRepository
import org.example.seller.domain.seller.service.SellerProcessor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SellerService(
    private val sellerRepository: SellerRepository,
    private val sellerProcessor: SellerProcessor,
    private val userClient: UserClient,
    private val userSellerClient: UserSellerClient,
) {


    @Transactional
    fun sellerApply(userId: Long, request: SellerApplyRequest): SellerResponse {
        val (seller, userDto) = sellerProcessor.sellerApply(userId, request)

        sellerRepository.save(seller)

        return seller.toDto(userDto.userNickname)
    }

    @Transactional(readOnly = true)
    fun sellerDetails(userId: Long): SellerResponse {
        val seller: Seller = sellerRepository.findByUserId(userId)
            .orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }

        val userDto = userClient.userModuleDto(userId)

        return seller.toDto(userDto.userNickname)
    }

    @Transactional
    fun applyModify(userId: Long, sellerId: Long, request: SellerApplyRequest): SellerResponse {
        val seller: Seller = sellerRepository.findByIdOrNull(sellerId)
            ?: throw CustomException(SellerErrorCode.SELLER_NOT_FOUND)

        val userDto = sellerProcessor.applyModify(userId, seller, request)

        return seller.toDto(userDto.userNickname)
    }

    @Transactional
    fun sellerCancel(sellerId: Long, userId: Long): SellerResponse {
        val seller: Seller = sellerRepository.findByIdOrNull(sellerId)
            ?: throw CustomException(SellerErrorCode.SELLER_NOT_FOUND)

        val userDto = sellerProcessor.sellerCancel(userId, seller)

        return seller.toDto(userDto.userNickname)
    }

    @Transactional(readOnly = true)
    fun rejectReason(userId: Long): RejectReasonResponse {
        val seller: Seller = sellerRepository.findByUserId(userId)
            .orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }

        return seller.toRejectReasonDto()
    }
}
