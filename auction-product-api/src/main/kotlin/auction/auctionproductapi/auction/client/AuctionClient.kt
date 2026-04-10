package auction.auctionproductapi.auction.client

import auction.auctionproductapi.auction.dto.AuctionCommonResponse

interface AuctionClient {
    fun auctionModuleDto(auctionId: Long) : AuctionCommonResponse

    fun auctionListModuleDto(auctionIds: List<Long>) : List<AuctionCommonResponse>
}