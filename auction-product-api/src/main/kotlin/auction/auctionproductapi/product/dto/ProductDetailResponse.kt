package auction.auctionproductapi.product.dto

import auction.auctionproductapi.product.status.ProductStatus
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ProductDetailResponse(
    val productId: Long,
    val seller: String,
    val category: String,
    val title: String,
    val productStatus: ProductStatus,
    val mainImageUrl: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime? = null,
) {}

