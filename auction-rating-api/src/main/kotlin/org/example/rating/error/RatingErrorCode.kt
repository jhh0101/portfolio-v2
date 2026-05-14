package org.example.rating.error

import org.example.common.global.error.ErrorCode

enum class RatingErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    RATING_NOT_FOUND("RATING001", "평가를 찾을 수 없습니다"),
}