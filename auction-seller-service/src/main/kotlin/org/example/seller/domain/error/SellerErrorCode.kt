package org.example.seller.domain.error

import org.example.common.global.error.ErrorCode

enum class SellerErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    SELLER_NOT_FOUND("SELLER001", "판매자를 찾을 수 없습니다."),
    DUPLICATE_SELLER("SELLER002", "이미 신청 중인 사용자입니다."),
}