package org.example.product.controller.internal.query

import auction.auctionproductapi.auction.status.AuctionStatus
import auction.auctionproductapi.product.dto.ProductCommonResponse
import auction.auctionproductapi.product.dto.ProductDetailResponse
import auction.auctionproductapi.product.status.ProductStatus
import auction.auctionuserapi.user.dto.UserCommonResponse
import org.example.product.application.service.module.ProductBidService
import org.example.product.application.service.module.ProductCategoryService
import org.example.product.application.service.module.ProductCommonService
import org.example.product.application.service.module.ProductDetailService
import org.example.product.application.service.module.ProductUserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/product")
class InternalProductQueryController(
    private val productCommonService: ProductCommonService,
    private val productBidService: ProductBidService,
    private val productCategoryService: ProductCategoryService,
    private val productDetailService: ProductDetailService,
    private val productUserService: ProductUserService,
) {

    @GetMapping("/{id}")
    fun productModuleDto(@PathVariable("id") productId: Long) : ProductCommonResponse {
        return productCommonService.productModuleDto(productId)
    }

    @GetMapping("/list")
    fun productListModuleDto(@RequestParam("productIds") productIds: List<Long>) : List<ProductCommonResponse> {
        return productCommonService.productListModuleDto(productIds)
    }

    @GetMapping("/{id}/count")
    fun productCount(@PathVariable("id") userId: Long) : Long {
        return productCommonService.productCount(userId)
    }

    @GetMapping("/{id}/auctions/ids")
    fun findAuctionIdsBySellerId(@PathVariable("id") userId: Long,
                                 @RequestParam("auctionStatus") auctionStatus: AuctionStatus,
                                 @RequestParam("productStatus") productStatus: ProductStatus
    ) : List<Long> {
        return productBidService.findAuctionIdsBySellerId(userId, auctionStatus, productStatus)
    }

    @GetMapping("/{id}/exists/category")
    fun existsByCategoryId(@PathVariable("id") categoryId: Long) : Boolean {
        return productCategoryService.existsByCategoryId(categoryId)
    }

    @GetMapping("/{id}/detail")
    fun productDetailResponse(@PathVariable("id") productId: Long) : ProductDetailResponse {
        return productDetailService.productDetailResponse(productId)
    }

    @GetMapping("/{productIds}/detail/list")
    fun productDetailResponses(@RequestParam("productIds") productIds: List<Long>) : List<ProductDetailResponse> {
        return productDetailService.productDetailResponses(productIds)
    }

    @GetMapping("/{id}/list")
    fun findAllByUserId(@PathVariable("id") userId: Long) : List<ProductCommonResponse> {
        return productUserService.findAllByUserId(userId)
    }
}