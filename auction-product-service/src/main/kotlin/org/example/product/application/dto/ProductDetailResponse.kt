package org.example.product.application.dto

import auction.auctioncategoryapi.dto.CategoryCommonResponse
import com.fasterxml.jackson.annotation.JsonFormat
import org.example.product.domain.product.entity.Product
import auction.auctionproductapi.product.status.ProductStatus
import auction.auctionuserapi.user.dto.UserCommonResponse
import java.time.LocalDateTime

data class ProductDetailResponse(
    val productId: Long,
    val seller: String,
    val sellerId: Long,
    val category: String,
    val categoryId: Long,
    val title: String,
    val description: String,
    val viewCount: Int,
    val ratingScore: Double,
    val productStatus: ProductStatus,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private val createdAt: LocalDateTime? = null
)
fun Product.toProductDetailDto(userDto: UserCommonResponse, categoryDto: CategoryCommonResponse) : ProductDetailResponse {
    return ProductDetailResponse(
        productId = this.productId ?: 0L,
        seller = userDto.userNickname,
        sellerId = userDto.userId,
        category = categoryDto.categoryName,
        categoryId = categoryDto.categoryId,
        title = this.title,
        description = this.description,
        viewCount = this.viewCount,
        productStatus = this.productStatus,
        createdAt = this.createdAt,
        ratingScore = userDto.avgRating
    )
}