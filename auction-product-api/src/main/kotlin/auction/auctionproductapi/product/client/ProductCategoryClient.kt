package auction.auctionproductapi.product.client

interface ProductCategoryClient {
    fun existsByCategoryId(categoryId: Long) : Boolean
}