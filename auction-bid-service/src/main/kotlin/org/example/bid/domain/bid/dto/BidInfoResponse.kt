package org.example.bid.domain.bid.dto

import auction.auctionbidapi.error.BidErrorCode
import com.fasterxml.jackson.annotation.JsonFormat
import auction.auctionbidapi.status.BidStatus
import auction.auctionuserapi.user.dto.UserCommonResponse
import org.example.bid.domain.bid.entity.Bid
import org.example.common.global.error.CustomException
import java.time.LocalDateTime

class BidInfoResponse(
    val bidId: Long,
    val auctionId: Long,
    val nickname: String,
    val bidPrice: Long,
    val status: BidStatus,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val bidTime: LocalDateTime? = null
)

fun Bid.toDto(userDto: UserCommonResponse): BidInfoResponse {
    return BidInfoResponse(
        bidId = this.bidId ?: throw CustomException(BidErrorCode.BID_NOT_FOUND),
        auctionId = this.auctionId,
        nickname = userDto.userNickname,
        bidPrice = this.bidPrice,
        status = this.status,
        bidTime = this.createdAt
    )
}
