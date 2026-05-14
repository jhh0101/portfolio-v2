package org.example.bid.application.service.command

import auction.auctionbidapi.command.BidUserCommandClient
import auction.auctionbidapi.dto.BidCommonResponse
import auction.auctionbidapi.error.BidErrorCode
import auction.auctionbidapi.status.BidStatus
import auction.auctionproductapi.auction.client.AuctionClient
import auction.auctionproductapi.auction.dto.AuctionCommonResponse
import auction.auctionproductapi.auction.status.AuctionStatus
import auction.auctionuserapi.user.client.UserClient
import org.example.bid.application.service.BidService
import org.example.bid.domain.bid.entity.Bid
import org.example.bid.domain.bid.repository.BidRepository
import org.example.common.global.error.CustomException
import org.slf4j.LoggerFactory

class BidUserCommandService(
    private val bidRepository: BidRepository,
    private val bidService: BidService,
    private val auctionClient: AuctionClient,
    private val userClient: UserClient,
) : BidUserCommandClient {
    private val log = LoggerFactory.getLogger(javaClass)
    override fun cancelActiveBidsAndGetRefundTargets(userId: Long) : List<BidCommonResponse>{
        val bidList: List<Bid> = bidRepository.findTopBidsPerAuctionBySellerId(userId)
        val userBids: List<Bid> = bidRepository.findLatestBidsByUserId(userId)
        val userDto = userClient.userModuleDto(userId)

        val auctionCache = mutableMapOf<Long, AuctionCommonResponse>()

        for (userBid in userBids) {
            val auctionId = userBid.auctionId
            val auctionDto = auctionCache.getOrPut(auctionId) {
                auctionClient.auctionModuleDto(auctionId)
            }
            if (auctionDto.status === AuctionStatus.PROCEEDING.name) {
                val currentTopBid: Bid =
                    bidRepository.findTopByStatusAndAuctionIdOrderByBidPriceDesc(BidStatus.ACTIVE, auctionDto.auctionId)
                        ?: throw CustomException(
                            BidErrorCode.BID_NOT_FOUND, "활성화된 입찰 내역이 없습니다.")

                if (currentTopBid.bidderId == userId) {
                    bidService.cancelBid(
                        userDto.userId,
                        userBid.bidId ?: throw CustomException(BidErrorCode.BID_NOT_FOUND),
                        auctionDto.auctionId
                    )
                } else {
                    log.info("사용자가 상위 입찰자가 아니므로 취소 패스 - AuctionId: {}", auctionDto.auctionId)
                }
            }
        }
        return bidList.map { bid ->
            BidCommonResponse(
                bidId = bid.bidId ?: throw CustomException(BidErrorCode.BID_NOT_FOUND),
                bidderId = bid.bidderId,
                auctionId = bid.auctionId,
                bidPrice = bid.bidPrice,
                status = bid.status
            )
        }
    }
}
