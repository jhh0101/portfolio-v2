package org.example.auction.product.feign.product

import auction.auctionproductapi.auction.status.AuctionStatus
import auction.auctionproductapi.product.client.ProductBidClient
import auction.auctionproductapi.product.client.ProductCategoryClient
import auction.auctionproductapi.product.client.ProductClient
import auction.auctionproductapi.product.client.ProductDetailClient
import auction.auctionproductapi.product.client.ProductUserClient
import auction.auctionproductapi.product.command.ProductUserCommandClient
import auction.auctionproductapi.product.dto.ProductCommonResponse
import auction.auctionproductapi.product.dto.ProductDetailResponse
import auction.auctionproductapi.product.status.ProductStatus
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "auction-product-service", url = "http://localhost:8082")
@RequestMapping("/internal/product")
interface ProductFeignClient : ProductBidClient,
    ProductCategoryClient, ProductClient,
    ProductDetailClient, ProductUserClient,
    ProductUserCommandClient {

    @GetMapping("/{id}")
    override fun productModuleDto(@PathVariable("id") productId: Long) : ProductCommonResponse

    @GetMapping("/list")
    override fun productListModuleDto(@RequestParam("productIds") productIds: List<Long>) : List<ProductCommonResponse>

    @GetMapping("/{id}/count")
    override fun productCount(@PathVariable("id") userId: Long) : Long

    @GetMapping("/{id}/auctions/ids")
    override fun findAuctionIdsBySellerId(@PathVariable("id") userId: Long,
                                          @RequestParam("auctionStatus") auctionStatus: AuctionStatus,
                                          @RequestParam("productStatus") productStatus: ProductStatus
    ) : List<Long>

    @GetMapping("/{id}/exists/category")
    override fun existsByCategoryId(@PathVariable("id") categoryId: Long) : Boolean

    @GetMapping("/{id}/detail")
    override fun productDetailResponse(@PathVariable("id") productId: Long) : ProductDetailResponse

    @GetMapping("/{productIds}/detail/list")
    override fun productDetailResponses(@PathVariable("productIds") productIds: List<Long>) : List<ProductDetailResponse>

    @GetMapping("/{id}/list")
    override fun findAllByUserId(@PathVariable("id") userId: Long) : List<ProductCommonResponse>

    @DeleteMapping("/{id}/delete")
    override fun deleteProductsBySuspendedUser(@PathVariable("id") userId: Long)
}