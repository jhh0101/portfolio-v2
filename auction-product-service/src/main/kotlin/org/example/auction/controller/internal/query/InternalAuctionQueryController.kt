package org.example.auction.controller.internal.query

import auction.auctionproductapi.auction.dto.AuctionCommonResponse
import auction.auctionproductapi.auction.status.AuctionStatus
import auction.auctionproductapi.product.dto.ProductCommonResponse
import auction.auctionproductapi.product.dto.ProductDetailResponse
import auction.auctionproductapi.product.status.ProductStatus
import auction.auctionuserapi.user.dto.UserCommonResponse
import org.example.auction.application.service.module.AuctionBidService
import org.example.auction.application.service.module.AuctionCommonService
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
@RequestMapping("/internal/auction")
class InternalAuctionQueryController(
    private val auctionCommonService: AuctionCommonService,
    private val auctionBidService: AuctionBidService,
) {
    @GetMapping("/{id}")
    fun auctionModuleDto(@PathVariable("id") auctionId: Long) : AuctionCommonResponse {
        return auctionCommonService.auctionModuleDto(auctionId)
    }

    @GetMapping("/{id}/lock")
    fun auctionLockModuleDto(@PathVariable("id") auctionId: Long) : AuctionCommonResponse {
        return auctionBidService.auctionLockModuleDto(auctionId)
    }

    @GetMapping("/list")
    fun auctionListModuleDto(@RequestParam("auctionIds") auctionIds: List<Long>) : List<AuctionCommonResponse> {
        return auctionCommonService.auctionListModuleDto(auctionIds)
    }

    @GetMapping("/auctions/ids")
    fun getAuctionIdsByStatus(status: AuctionStatus) : List<Long> {
        return auctionCommonService.getAuctionIdsByStatus(status)
    }

    @GetMapping("/{id}/valid/time")
    fun validateBiddingTime(@PathVariable("id") auctionId: Long) {
        auctionBidService.validateBiddingTime(auctionId)
    }

    @GetMapping("/{id}/valid/status")
    fun auctionValidStatus(auctionDto: AuctionCommonResponse,@PathVariable("id") auctionId: Long) {
        auctionBidService.auctionValidStatus(auctionDto, auctionId)
    }
}