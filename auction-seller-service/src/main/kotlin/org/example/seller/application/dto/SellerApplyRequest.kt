package org.example.seller.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SellerApplyRequest(
    @field:NotBlank(message = "상호명을 입력해주세요.")
    @field:Size(max = 50)
    val storeName: String,

    @field:NotBlank(message = "은행 이름을 입력해주세요.")
    @field:Size(max = 20)
    val bankName: String,

    @field:NotBlank(message = "계좌번호을 입력해주세요.")
    @field:Pattern(regexp = "^[0-9]{10,16}$", message = "계좌번호는 하이픈(-) 없이 10~16자리의 숫자로 입력해주세요.")
    val accountNumber: String,

    @field:NotBlank(message = "예금주 이름을 입력해주세요.")
    @field:Size(max = 20)
    val accountHolder: String,
)
