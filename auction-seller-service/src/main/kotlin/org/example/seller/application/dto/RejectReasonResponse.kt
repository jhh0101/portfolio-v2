package org.example.seller.application.dto

import org.example.seller.domain.seller.entity.Seller

data class RejectReasonResponse(
    val rejectReason: String?
)

fun Seller.toRejectReasonDto(): RejectReasonResponse {
    return RejectReasonResponse(
        rejectReason = this.rejectReason
    )
}