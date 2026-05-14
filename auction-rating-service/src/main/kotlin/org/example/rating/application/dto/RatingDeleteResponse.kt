package org.example.rating.application.dto

import auction.auctionuserapi.user.dto.UserCommonResponse
import org.example.rating.domain.entity.Rating
import org.example.rating.domain.entity.RatingStatus

data class RatingDeleteResponse(
    val nickname: String,
    val status: RatingStatus,
) {}

fun Rating.toDeleteDto(toUserDto: UserCommonResponse) : RatingDeleteResponse {
    return RatingDeleteResponse(
        nickname = toUserDto.userNickname,
        status = this.status
    )
}
