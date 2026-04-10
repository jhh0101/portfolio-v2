package org.example.seller.application.dto

import com.fasterxml.jackson.annotation.JsonFormat
import org.example.seller.domain.seller.entity.Seller
import java.time.LocalDateTime

data class SellerApplyListResponse(
    val sellerId: Long,
    val email: String,
    val nickname: String,
    val storeName: String,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val appliedAt: LocalDateTime? = null,
)

fun Seller.toApplyDto(email: String, nickname: String) : SellerApplyListResponse {
    return SellerApplyListResponse(
        sellerId = this.sellerId ?: 0L,
        email = email,
        nickname = nickname,
        storeName = this.storeName ?: "",
        appliedAt = this.createdAt

    )
}