package org.example.seller.application.dto

import jakarta.validation.constraints.NotBlank

data class SellerRejectRequest(
    @field:NotBlank(message = "거절 사유를 입력해주세요.")
    val rejectReason: String
) {

}
