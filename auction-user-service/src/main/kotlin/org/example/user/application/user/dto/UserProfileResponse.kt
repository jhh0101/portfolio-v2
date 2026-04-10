package org.example.user.application.user.dto

import auction.auctionsellerapi.status.SellerStatus
import com.fasterxml.jackson.annotation.JsonFormat
import org.example.common.global.util.MaskingUtil
import auction.auctionuserapi.user.type.Role
import org.example.user.domain.user.entity.User
import java.time.LocalDateTime

data class UserProfileResponse(
    val userId: Long,
    val email: String,
    val username: String,
    val phone: String,
    val nickname: String,
    val baseAddress: String?,
    val detailAddress: String,
    val role: Role,
    val point: Long,
    val avgRating: String,
    val sellerStatus: SellerStatus,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime? = null,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val updatedAt: LocalDateTime? = null,
)

fun User.toProfileDto(sellerStatus: SellerStatus) : UserProfileResponse{
    val formatPhone: String? = MaskingUtil.formatPhone(phone)
    val detailAddress: String? = if (detailAddress != null) detailAddress else ""
    val formatRating: String = String.format("%.1f", avgRating)

    return UserProfileResponse(
        userId = this.userId ?: 0L,
        email = this.email,
        username = this.username,
        phone = formatPhone.toString(),
        nickname = this.nickname,
        baseAddress = this.baseAddress,
        detailAddress = detailAddress.toString(),
        role = this.role,
        point = this.point,
        avgRating = formatRating,
        sellerStatus = sellerStatus,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt

    )
}