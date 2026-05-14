package org.example.auction.application.service.module

import auction.auctionproductapi.auction.client.AuctionBidClient
import auction.auctionproductapi.auction.dto.AuctionCommonResponse
import auction.auctionproductapi.auction.status.AuctionStatus
import org.example.auction.domain.auction.entity.Auction
import auction.auctionproductapi.auction.error.AuctionErrorCode
import auction.auctionproductapi.product.error.ProductErrorCode
import org.example.auction.domain.auction.repository.AuctionRepository
import org.example.common.global.error.CustomException
import org.springframework.stereotype.Service

@Service
class AuctionBidService(
    private val auctionRepository: AuctionRepository
) : AuctionBidClient {

    override fun auctionLockModuleDto(auctionId: Long) : AuctionCommonResponse {
        val auction: Auction = auctionRepository.findByIdWithPessimisticLock(auctionId)
            ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND)

        auction.validateBiddingTime()

        return AuctionCommonResponse(
            auctionId = auction.auctionId ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND),
            productId = auction.product.productId ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND),
            startPrice = auction.startPrice ?: 0L,
            currentPrice = auction.currentPrice ?: 0L,
            status = auction.status?.name,
            startTime = auction.startTime,
            endTime = auction.endTime,
        )
    }

    override fun validateBiddingTime(auctionId: Long) {
        val auction: Auction = auctionRepository.findByIdWithPessimisticLock(auctionId)
            ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND)

        auction.validateBiddingTime()
    }

    override fun updateCurrentPrice(auctionId: Long, bidPrice: Long) {
        val auction: Auction = auctionRepository.findByIdWithPessimisticLock(auctionId)
            ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND)

        auction.updateCurrentPrice(bidPrice)
    }

    override fun auctionValidStatus(auctionDto: AuctionCommonResponse, auctionId: Long) {
        if (auctionDto.status != AuctionStatus.PROCEEDING.name) {
            throw CustomException(AuctionErrorCode.AUCTION_ENDED)
        }

        if (auctionId != auctionDto.auctionId) {
            throw CustomException(AuctionErrorCode.INVALID_AUCTION_INFO, "경매 정보가 올바르지 않습니다.")
        }

    }

}