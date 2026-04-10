package auction.auctionproductapi.product.dto

data class ProductCommonResponse(
    val productId: Long,
    val productSellerId: Long,
    val categoryId: Long,
    val title: String,
    val description: String,
    val viewCount: Int,
    val productStatus: String,
)