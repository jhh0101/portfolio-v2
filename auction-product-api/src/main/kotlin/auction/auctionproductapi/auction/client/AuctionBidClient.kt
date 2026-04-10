package auction.auctionproductapi.auction.client

import auction.auctionproductapi.auction.dto.AuctionCommonResponse

interface AuctionBidClient {
    fun auctionLockModuleDto(auctionId: Long) : AuctionCommonResponse

    fun validateBiddingTime(auctionId: Long)

    fun updateCurrentPrice(auctionId: Long, bidPrice: Long)

    fun auctionValidStatus(auctionDto: AuctionCommonResponse, auctionId: Long)
}