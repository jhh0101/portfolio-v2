package org.example.product.domain.product.dto

import com.fasterxml.jackson.annotation.JsonFormat
import org.example.product.domain.product.entity.Product
import auction.auctionproductapi.product.status.ProductStatus
import java.time.LocalDateTime

data class ProductResponse(
    val productId: Long,
    val seller: String,
    val category: String,
    val title: String,
    val productStatus: ProductStatus,
    val mainImageUrl: String,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime? = null
)

fun Product.toProductResponse(): ProductResponse {

    val mainUrl = this.image
        .firstOrNull { it.imageOrder == 1 }
        ?.imageUrl ?: "https://picsum.photos/400/300"

    return ProductResponse(
        productId = this.productId ?: 0L,
        seller = this.seller.nickname,
        category = this.category.category,
        title = this.title,
        productStatus = this.productStatus,
        mainImageUrl = mainUrl,
        createdAt = this.createdAt
    )
}
