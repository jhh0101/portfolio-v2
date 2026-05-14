package auction.auctionproductapi.product.client

import auction.auctionproductapi.product.dto.ProductDetailResponse

interface ProductDetailClient {
    fun productDetailResponse(productId: Long) : ProductDetailResponse
    fun productDetailResponses(productIds: List<Long>) : List<ProductDetailResponse>
}