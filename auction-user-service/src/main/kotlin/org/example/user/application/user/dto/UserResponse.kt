package org.example.user.application.user.dto

import com.fasterxml.jackson.annotation.JsonFormat
import org.example.common.global.util.MaskingUtil
import auction.auctionuserapi.user.type.Role
import org.example.user.domain.user.entity.User
import auction.auctionuserapi.user.type.UserStatus
import java.time.LocalDateTime
import kotlin.Long

data class UserResponse(
    var userId: Long? = null,
    var email: String,
    var username: String,
    var phone: String,
    var nickname: String,
    var baseAddress: String?,
    var detailAddress: String? = null,
    var role: Role,
    var userStatus: UserStatus,
    var point: Long,
    var avgRating: Double,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime? = null,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val updatedAt: LocalDateTime? = null
)

fun User.toDto() : UserResponse {
    val formatPhone = MaskingUtil.formatPhone(phone)

    return UserResponse(
        userId = this.userId,
        email = this.email,
        username = this.username,
        phone = formatPhone.toString(),
        nickname = this.nickname,
        baseAddress = this.baseAddress,
        detailAddress = this.detailAddress,
        role = this.role,
        userStatus = this.status,
        point = this.point,
        avgRating = this.avgRating,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}