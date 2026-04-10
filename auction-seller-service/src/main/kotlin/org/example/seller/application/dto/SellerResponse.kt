package org.example.seller.application.dto

import auction.auctionsellerapi.status.SellerStatus
import org.example.seller.domain.seller.entity.Seller

data class SellerResponse(
    val sellerId: Long,
    val nickname: String,
    val storeName: String,
    val bankName: String,
    val accountNumber: String,
    val accountHolder: String,
    val status: SellerStatus,
    val rejectReason: String,
)

fun Seller.toDto(nickname: String): SellerResponse {
    return SellerResponse(
        sellerId = this.sellerId ?: 0L,
        nickname = nickname,
        storeName = this.storeName ?: "",
        bankName = this.bankName ?: "",
        accountHolder = this.accountHolder ?: "",
        accountNumber = this.accountNumber ?: "",
        status = this.status ?: SellerStatus.NONE,
        rejectReason = this.rejectReason ?: ""
    )
}