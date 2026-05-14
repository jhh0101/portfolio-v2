package org.example.product.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ProductRequest(
    val categoryId: Long,
    @field:NotBlank(message = "상품 제목을 입력해주세요.")
    @field:Size(
        min = 2,
        max = 20,
        message = "제목은 2~20자 입니다."
    )
    val title: String,
    val description: String
)
