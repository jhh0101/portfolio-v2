package org.example.product.application.dto

data class ProductDetailResult(
    val response: ProductDetailAndAuctionResponse,
    val newCookieValue: String?
)