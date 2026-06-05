package org.example.auction.product.feign.product

import auction.auctionproductapi.auction.status.AuctionStatus
import auction.auctionproductapi.product.dto.ProductCommonResponse
import auction.auctionproductapi.product.dto.ProductDetailResponse
import auction.auctionproductapi.product.status.ProductStatus
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "auction-product-service", url = "http://localhost:8082", contextId = "productFeignClient")
interface ProductFeignClient {

    @GetMapping("/internal/product/{id}")
    fun productModuleDto(@PathVariable("id") productId: Long) : ProductCommonResponse

    @GetMapping("/internal/product/list")
    fun productListModuleDto(@RequestParam("productIds") productIds: List<Long>) : List<ProductCommonResponse>

    @GetMapping("/internal/product/{id}/count")
    fun productCount(@PathVariable("id") userId: Long) : Long

    @GetMapping("/internal/product/{id}/auctions/ids")
    fun findAuctionIdsBySellerId(@PathVariable("id") userId: Long,
                                          @RequestParam("auctionStatus") auctionStatus: AuctionStatus,
                                          @RequestParam("productStatus") productStatus: ProductStatus
    ) : List<Long>

    @GetMapping("/internal/product/{id}/exists/category")
    fun existsByCategoryId(@PathVariable("id") categoryId: Long) : Boolean

    @GetMapping("/internal/product/{id}/detail")
    fun productDetailResponse(@PathVariable("id") productId: Long) : ProductDetailResponse

    @GetMapping("/internal/product/{productIds}/detail/list")
    fun productDetailResponses(@RequestParam("productIds") productIds: List<Long>) : List<ProductDetailResponse>

    @GetMapping("/internal/product/{id}/list")
    fun findAllByUserId(@PathVariable("id") userId: Long) : List<ProductCommonResponse>

    @DeleteMapping("/internal/product/{id}/delete")
    fun deleteProductsBySuspendedUser(@PathVariable("id") userId: Long)
}