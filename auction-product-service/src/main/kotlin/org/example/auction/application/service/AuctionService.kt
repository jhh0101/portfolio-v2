package org.example.auction.application.service

import auction.auctionbidapi.client.BidAuctionClient
import auction.auctionbidapi.dto.BidAuctionResponse
import auction.auctionorderapi.client.OrderClient
import org.example.auction.domain.auction.entity.Auction
import auction.auctionproductapi.auction.status.AuctionStatus
import auction.auctionuserapi.user.client.UserClient
import org.example.auction.domain.auction.error.AuctionErrorCode
import org.example.auction.domain.auction.repository.AuctionRepository
import org.example.auction.domain.auction.service.AuctionProcessor
import org.example.common.global.error.CustomException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AuctionService(
    private val auctionRepository: AuctionRepository,
    private val auctionProcessor: AuctionProcessor,
    private val bidAuctionClient: BidAuctionClient,
    private val orderClient: OrderClient,
    private val userClient: UserClient
) {

    @Transactional
    fun finishAuction(auctionId: Long?) {
        val auction: Auction = auctionRepository.findByIdWithPessimisticLock(auctionId)
            ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND, "옥션을 찾을 수 없습니다.")

        val userDto = userClient.userModuleDto(auction.product.sellerId)

        auction.changeStatus(AuctionStatus.ENDED)

        val topBid: Optional<BidAuctionResponse> = Optional.of(bidAuctionClient.findTopByStatusAndAuctionOrderByBidIdDesc(auctionId))

        auctionProcessor.validateFinishAuction(auction, topBid, userClient, userDto, orderClient)

    }
}
