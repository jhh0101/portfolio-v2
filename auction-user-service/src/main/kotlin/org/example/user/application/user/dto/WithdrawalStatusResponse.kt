package org.example.user.application.user.dto

import org.example.user.domain.user.entity.User

data class WithdrawalStatusResponse(
    val nickname: String,
    val currentPoint: Long,
    val bidCount: Long,
    val productCount: Long,
)

fun User.toWithdrawalStatusDto(bidCount: Long, productCount: Long) : WithdrawalStatusResponse{
    return WithdrawalStatusResponse(
        nickname = this.nickname,
        currentPoint = this.point,
        bidCount = bidCount,
        productCount = productCount
    )
}
