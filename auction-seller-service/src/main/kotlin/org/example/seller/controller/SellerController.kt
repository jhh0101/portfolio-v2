package org.example.seller.controller

import auction.auctionuserapi.auth.annotation.LoginUser
import auction.auctionuserapi.auth.dto.DetailsUser
import jakarta.validation.Valid
import org.example.common.global.response.ApiResponse
import org.example.seller.application.dto.RejectReasonResponse
import org.example.seller.application.dto.SellerApplyRequest
import org.example.seller.application.dto.SellerResponse
import org.example.seller.application.service.SellerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/user")
class SellerController(
    private val sellerService: SellerService
) {

    @PostMapping("/seller/apply")
    fun sellerApply(
        @LoginUser user: DetailsUser,
        @RequestBody request: @Valid SellerApplyRequest
    ): ResponseEntity<ApiResponse<SellerResponse>> {
        val response: SellerResponse = sellerService.sellerApply(user.id, request)
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 신청", response))
    }

    @GetMapping("/seller/apply")
    fun sellerDetails(@LoginUser user: DetailsUser): ResponseEntity<ApiResponse<SellerResponse>> {
        val response: SellerResponse = sellerService.sellerDetails(user.id)
        return ResponseEntity.ok(ApiResponse.success("판매자 신청 내용 조회", response))
    }

    @PatchMapping("/{sellerId}/modify")
    fun applyModify(
        @LoginUser user: DetailsUser,
        @PathVariable sellerId: Long,
        @RequestBody request: @Valid SellerApplyRequest
    ): ResponseEntity<ApiResponse<SellerResponse>> {
        val response: SellerResponse = sellerService.applyModify(user.id, sellerId, request)
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 수정", response))
    }

    @PatchMapping("/{sellerId}/cancel")
    fun sellerCancel(
        @LoginUser user: DetailsUser,
        @PathVariable sellerId: Long
    ): ResponseEntity<ApiResponse<SellerResponse>> {
        val response: SellerResponse = sellerService.sellerCancel(sellerId, user.id)
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 취소", response))
    }

    @GetMapping("/reject/reason")
    fun rejectReason(@LoginUser user: DetailsUser): ResponseEntity<ApiResponse<RejectReasonResponse>> {
        val response: RejectReasonResponse = sellerService.rejectReason(user.id)
        return ResponseEntity.ok(ApiResponse.success("신청 거절 사유 조회", response))
    }
}
