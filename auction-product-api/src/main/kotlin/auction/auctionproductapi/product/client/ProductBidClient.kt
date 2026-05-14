package auction.auctionproductapi.product.client

import auction.auctionproductapi.auction.status.AuctionStatus
import auction.auctionproductapi.product.dto.ProductCommonResponse
import auction.auctionproductapi.product.status.ProductStatus

interface ProductBidClient {
    fun findAuctionIdsBySellerId(userId: Long, auctionStatus: AuctionStatus, productStatus: ProductStatus) : List<Long>

}