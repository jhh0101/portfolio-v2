package auction.auctionproductapi.product.client

import auction.auctionproductapi.product.dto.ProductCommonResponse

interface ProductUserClient {
    fun findAllByUserId(userId: Long) : List<ProductCommonResponse>
}