package org.example.seller.controller

import jakarta.validation.Valid
import org.example.common.global.response.ApiResponse
import org.example.seller.application.dto.SellerApplyListResponse
import org.example.seller.application.dto.SellerRejectRequest
import org.example.seller.application.dto.SellerResponse
import org.example.seller.application.service.SellerAdminService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
class SellerAdminController(
    private val sellerAdminService: SellerAdminService,
) {

    @GetMapping("/apply/list")
    fun sellerList(
        @PageableDefault(
            size = 10,
            sort = ["sellerId"],
            direction = Sort.Direction.ASC
        ) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<SellerApplyListResponse>>> {
        val response: Page<SellerApplyListResponse> = sellerAdminService.sellerList(pageable)
        return ResponseEntity.ok(ApiResponse.success("판매 신청자 리스트 조회", response))
    }

    @PatchMapping("/{sellerId}/approve")
    fun sellerApprove(@PathVariable sellerId: Long): ResponseEntity<ApiResponse<SellerResponse>> {
        val response: SellerResponse = sellerAdminService.approveSeller(sellerId)
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 승인", response))
    }

    @PatchMapping("/{sellerId}/reject")
    fun sellerReject(
        @PathVariable sellerId: Long,
        @RequestBody request: @Valid SellerRejectRequest
    ): ResponseEntity<ApiResponse<SellerResponse>> {
        val response: SellerResponse = sellerAdminService.rejectSeller(sellerId, request)
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 거절", response))
    }

    @GetMapping("/seller/{sellerId}/apply")
    fun sellerDetails(@PathVariable sellerId: Long): ResponseEntity<ApiResponse<SellerResponse>> {
        val response: SellerResponse = sellerAdminService.sellerDetails(sellerId)
        return ResponseEntity.ok(ApiResponse.success("판매자 신청 내용 조회", response))
    }
}
