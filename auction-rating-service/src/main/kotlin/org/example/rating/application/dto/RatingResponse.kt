package org.example.rating.application.dto

import auction.auctionproductapi.product.dto.ProductCommonResponse
import auction.auctionuserapi.user.dto.UserCommonResponse
import org.example.common.global.error.CustomException
import org.example.rating.domain.entity.Rating
import org.example.rating.error.RatingErrorCode

class RatingResponse(
    val ratingId: Long,
    val toNickname: String,
    val fromNickname: String,
    val title: String,
    val score: Int,
    val comment: String?,
) {  }

fun Rating.toDto(toUserDto: UserCommonResponse, fromUserDto: UserCommonResponse, productDto: ProductCommonResponse) : RatingResponse {
    return RatingResponse(
        ratingId = this.ratingId ?: throw CustomException(RatingErrorCode.RATING_NOT_FOUND),
        toNickname = toUserDto.userNickname,
        fromNickname = fromUserDto.userNickname,
        title = productDto.title,
        score = this.score,
        comment = this.comment
    )
}