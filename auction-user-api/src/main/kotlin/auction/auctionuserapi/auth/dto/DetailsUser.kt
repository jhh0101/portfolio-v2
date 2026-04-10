package auction.auctionuserapi.auth.dto

data class DetailsUser(
    val id: Long,
    val email: String,
    val nickname: String,
    val role: String
)