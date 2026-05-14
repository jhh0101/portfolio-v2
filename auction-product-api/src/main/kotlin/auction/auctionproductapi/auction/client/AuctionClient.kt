package auction.auctionproductapi.auction.client

import auction.auctionproductapi.auction.dto.AuctionCommonResponse
import auction.auctionproductapi.auction.status.AuctionStatus

interface AuctionClient {
    fun auctionModuleDto(auctionId: Long) : AuctionCommonResponse

    fun auctionListModuleDto(auctionIds: List<Long>) : List<AuctionCommonResponse>

    fun getAuctionIdsByStatus(status: AuctionStatus) : List<Long>
}