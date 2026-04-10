package org.example.auction.application.service.module

import auction.auctionproductapi.auction.client.AuctionClient
import auction.auctionproductapi.auction.dto.AuctionCommonResponse
import org.example.auction.domain.auction.entity.Auction
import org.example.auction.domain.auction.error.AuctionErrorCode
import org.example.auction.domain.auction.repository.AuctionRepository
import org.example.common.global.error.CustomException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

import kotlin.collections.List


@Service
class AuctionCommonService(
    private val auctionRepository: AuctionRepository
) : AuctionClient {

    override fun auctionModuleDto(auctionId: Long) : AuctionCommonResponse {
        val auction: Auction = auctionRepository.findByIdOrNull(auctionId)
            ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND)

        return AuctionCommonResponse(
            auctionId = auction.auctionId ?: 0L,
            productId = auction.product.productId,
            startPrice = auction.startPrice ?: 0L,
            currentPrice = auction.currentPrice ?: 0L,
            status = auction.status?.name,
            startTime = auction.startTime,
            endTime = auction.endTime
        )
    }

    override fun auctionListModuleDto(auctionIds: List<Long>) : List<AuctionCommonResponse> {
        val auctions: List<Auction> = auctionRepository.findAllByAuctionIdIn(auctionIds)
        return auctions.map { auction ->
            AuctionCommonResponse(
                auctionId = auction.auctionId ?: 0L,
                productId = auction.product.productId,
                startPrice = auction.startPrice ?: 0L,
                currentPrice = auction.currentPrice ?: 0L,
                status = auction.status?.name,
                startTime = auction.startTime,
                endTime = auction.endTime
            )
        }
    }
}