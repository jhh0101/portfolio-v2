package auction.auctionproductapi.product.dto

import auction.auctionproductapi.auction.status.AuctionStatus
import java.time.LocalDateTime

data class ProductCommonResponse(
    val productId: Long,
    val productSellerId: Long,
    val categoryId: Long,
    val title: String,
    val description: String,
    val viewCount: Int,
    val productStatus: String,
    var auctionId: Long,
    var startPrice: Long,
    var currentPrice: Long,
    var startTime: LocalDateTime,
    var endTime: LocalDateTime,
    var status: AuctionStatus,
    var imageUrl: List<String>,
    )