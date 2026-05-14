package auction.auctionorderapi.dto

data class OrderCommonResponse(
    var orderId: Long,
    val auctionId: Long,
    val buyerId: Long,
    var finalPrice: Long,
) {
}