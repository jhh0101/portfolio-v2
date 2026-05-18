package org.example.product.application.dto

import com.fasterxml.jackson.annotation.JsonUnwrapped

data class ProductDetailResult(
    @field:JsonUnwrapped
    val response: ProductDetailAndAuctionResponse,
    val newCookieValue: String?
)