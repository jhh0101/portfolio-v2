package auction.auctionproductapi.product.client

import auction.auctionproductapi.product.dto.ProductCommonResponse

interface ProductClient {
    fun productModuleDto(productId: Long) : ProductCommonResponse

    fun productListModuleDto(productIds: List<Long>) : List<ProductCommonResponse>

    fun productCount(userId: Long) : Long

}