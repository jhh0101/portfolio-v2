package auction.auctionuserapi.user.dto

data class UserCommonResponse(
    val userId: Long,
    val userPoint: Long,
    val userNickname: String,
    val userEmail: String,
    val avgRating: Double,
    val role: String,
) {
}