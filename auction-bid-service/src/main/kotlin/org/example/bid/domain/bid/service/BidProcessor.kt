package org.example.bid.domain.bid.service

import auction.auctionproductapi.auction.client.AuctionBidClient
import auction.auctionproductapi.auction.client.AuctionClient
import auction.auctionproductapi.auction.dto.AuctionCommonResponse
import auction.auctionproductapi.product.dto.ProductCommonResponse
import auction.auctionuserapi.user.client.UserBidClient
import auction.auctionuserapi.user.client.UserClient
import auction.auctionuserapi.user.dto.UserCommonResponse
import org.example.bid.domain.bid.entity.Bid
import auction.auctionbidapi.status.BidStatus
import org.example.bid.domain.bid.error.BidErrorCode
import org.example.common.global.error.CustomException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BidProcessor(
    private val userClient: UserClient,
    private val userBidClient: UserBidClient,
    private val auctionClient: AuctionClient,
    private val auctionBidClient: AuctionBidClient,
) {

    fun addBid(
        userDto: UserCommonResponse,
        auctionDto: AuctionCommonResponse,
        productDto: ProductCommonResponse,
        bidPrice: Long,
        lastBid: Bid?
    ): Bid {
        if (userDto.userPoint < bidPrice) throw CustomException(BidErrorCode.NOT_ENOUGH_POINTS)

        if (productDto.productSellerId == userDto.userId) throw CustomException(BidErrorCode.SELF_BID_NOT_ALLOWED)

        validateBidPrice(lastBid, auctionDto, bidPrice, userDto)

        if (lastBid != null) {
            userClient.userAddPoint(lastBid.bidderId, lastBid.bidPrice)
        }

        userClient.userSubPoint(userDto.userId, bidPrice)
        auctionBidClient.updateCurrentPrice(auctionDto.auctionId, bidPrice)

        return Bid(auctionId = auctionDto.auctionId, bidderId = userDto.userId, bidPrice = bidPrice, status = BidStatus.ACTIVE)
    }

    private fun validateBidPrice(
        lastBid: Bid?,
        auctionDto: AuctionCommonResponse,
        bidPrice: Long,
        userDto: UserCommonResponse
    ) {
        if (lastBid == null) {
            if (auctionDto.startPrice > bidPrice) throw CustomException(BidErrorCode.BID_PRICE_TOO_LOW);
        } else {
            if (lastBid.bidderId == userDto.userId) throw CustomException(BidErrorCode.ALREADY_HIGHEST_BIDDER)
            if (auctionDto.currentPrice >= bidPrice) throw CustomException(BidErrorCode.BID_PRICE_TOO_LOW);
        }
    }


    fun cancelBid(
        auctionDto: AuctionCommonResponse,
        userId: Long,
        bid: Bid,
        currentTopBid: Bid,
        bidList: List<Bid>
    ) {
        val bidAuctionDto = auctionClient.auctionModuleDto(bid.auctionId)

        auctionBidClient.auctionValidStatus(auctionDto, bid.auctionId)

        userBidClient.userValidCheck(userId, bid.bidderId)

        if (currentTopBid.bidderId != userId) {
            throw CustomException(BidErrorCode.BID_NOT_FOUND)
        }

        val now = LocalDateTime.now()

        if (now.isAfter(bid.createdAt?.plusMinutes(10)) || now.isAfter(auctionDto.endTime?.minusMinutes(10))) {
            throw CustomException(BidErrorCode.BID_CANCEL_RESTRICTED)
        }

        userClient.userAddPoint(bid.bidderId, bid.bidPrice)

        bid.cancelBid();

        processRollbackToNextBidder(userId, auctionDto, bidAuctionDto, bidList)
    }

    private fun processRollbackToNextBidder(
                  userId: Long,
                  auctionDto: AuctionCommonResponse,
                  bidAuctionDto: AuctionCommonResponse,
                  bidList: List<Bid>){
        var bidFound = false

        for (lastBidder in bidList) {
            val bidderId = lastBidder.bidderId
            if (bidderId == userId) {
                continue;
            }
            val userDto = userClient.userModuleDto(bidderId)
            val bidderPoint = userDto.userPoint
            if (bidderPoint >= lastBidder.bidPrice) {
                auctionBidClient.updateCurrentPrice(auctionDto.auctionId, lastBidder.bidPrice)
                userClient.userSubPoint(userDto.userId, lastBidder.bidPrice)
                bidFound = true
                break
            } else {
                lastBidder.invalidBid()
            }
        }

        if (!bidFound) {
            auctionBidClient.updateCurrentPrice(auctionDto.auctionId, bidAuctionDto.startPrice)
        }
    }
}
