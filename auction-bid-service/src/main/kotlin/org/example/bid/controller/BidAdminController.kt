package org.example.bid.controller

import org.example.bid.application.dto.BidResponse
import org.example.bid.application.service.BidAdminService
import org.example.bid.domain.bid.dto.BidHistoryResponse
import org.example.common.global.response.ApiResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
class BidAdminController(
    private val bidAdminService: BidAdminService
) {


    @GetMapping("/{userId}/bid")
    fun findBidHistory(
        @PathVariable userId: Long,
        @PageableDefault(size = 10) pageable: Pageable
    ): ResponseEntity<ApiResponse<Slice<BidHistoryResponse>>> {
        val responses: Slice<BidHistoryResponse> =
            bidAdminService.findBidHistorySlice(userId, pageable)
        return ResponseEntity.ok(ApiResponse.success("사용자의 입찰 상품 리스트 출력", responses))
    }

    @GetMapping("/{auctionId}/bid-list/{userId}")
    fun findUserBidList(
        @PathVariable userId: Long,
        @PathVariable auctionId: Long,
        @PageableDefault(size = 10) pageable: Pageable
    ): ResponseEntity<ApiResponse<Slice<BidResponse>>> {
        val responses: Slice<BidResponse> =
            bidAdminService.findUserBidList(userId, auctionId, pageable)
        return ResponseEntity.ok(ApiResponse.success("사용자의 입찰 리스트 출력", responses))
    }
}
