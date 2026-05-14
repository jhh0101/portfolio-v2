package org.example.bid.controller

import auction.auctionuserapi.auth.annotation.LoginUser
import auction.auctionuserapi.auth.dto.DetailsUser
import jakarta.validation.Valid
import org.example.bid.application.dto.BidRequest
import org.example.bid.application.dto.BidResultResponse
import org.example.bid.application.service.BidService
import org.example.bid.domain.bid.dto.BidHistoryResponse
import org.example.bid.domain.bid.dto.BidInfoResponse
import org.example.common.global.response.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auction")
class BidController(
    private val bidService: BidService
) {

    // User
    @PostMapping("/{auctionId}")
    fun addBid(
        @Valid @RequestBody request: BidRequest,
        @PathVariable auctionId: Long,
        @LoginUser user: DetailsUser
    ): ResponseEntity<ApiResponse<BidResultResponse>> {
        val response: BidResultResponse = bidService.addBid(user.id, auctionId, request)
        return ResponseEntity.ok(ApiResponse.success("입찰 성공", response))
    }

    @GetMapping("/{auctionId}/bid")
    fun findBidList(
        @PathVariable auctionId: Long,
        @PageableDefault(
            sort = ["bidId"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<BidInfoResponse>>> {
        val responses: Page<BidInfoResponse> = bidService.findBid(auctionId, pageable)
        return ResponseEntity.ok(ApiResponse.success("입찰인 리스트 조회", responses))
    }

    @PostMapping("/{auctionId}/bid/{bidId}")
    fun cancelBid(
        @LoginUser user: DetailsUser,
        @PathVariable bidId: Long,
        @PathVariable auctionId: Long
    ): ResponseEntity<ApiResponse<BidResultResponse>> {
        val response: BidResultResponse = bidService.cancelBid(user.id, bidId, auctionId)
        return ResponseEntity.ok(ApiResponse.success("입찰 취소", response))
    }

    @GetMapping("/my/bid")
    fun findBidHistory(
        @LoginUser user: DetailsUser,
        @PageableDefault(size = 5) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<BidHistoryResponse>>> {
        val responses: Page<BidHistoryResponse> =
            bidService.findBidHistoryPage(user.id, pageable)
        return ResponseEntity.ok(ApiResponse.success("입찰 상품 리스트 출력", responses))
    }
}

