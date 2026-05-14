package org.example.rating.controller

import auction.auctionuserapi.auth.annotation.LoginUser
import auction.auctionuserapi.auth.dto.DetailsUser
import org.example.common.global.response.ApiResponse
import org.example.rating.application.dto.RatingDeleteResponse
import org.example.rating.application.dto.RatingRequest
import org.example.rating.application.dto.RatingResponse
import org.example.rating.application.service.RatingService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/rating")
class RatingController(
    private val ratingService: RatingService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/{orderId}")
    fun createRating(
        @LoginUser user: DetailsUser,
        @PathVariable orderId: Long,
        @RequestBody request: RatingRequest
    ): ResponseEntity<ApiResponse<RatingResponse>> {
        val response: RatingResponse = ratingService.createRating(user.id, orderId, request)
        log.info("평가 등록 시도 - 사용자ID: {}, 점수: {}, 코멘트: {}", user.id, request.score, request.comment)
        return ResponseEntity.ok(ApiResponse.success("판매자 평가 등록", response))
    }

    @GetMapping("/{orderId}/one")
    fun findRating(@PathVariable orderId: Long): ResponseEntity<ApiResponse<RatingResponse>> {
        val response: RatingResponse = ratingService.findRating(orderId)
        return ResponseEntity.ok(ApiResponse.success("판매자 평가 조회", response))
    }

    @PatchMapping("/{orderId}/update/{ratingId}")
    fun updateRating(
        @LoginUser user: DetailsUser,
        @PathVariable orderId: Long,
        @PathVariable ratingId: Long,
        @RequestBody request: RatingRequest
    ): ResponseEntity<ApiResponse<RatingResponse>> {
        val response: RatingResponse = ratingService.updateRating(user.id, orderId, ratingId, request)
        log.info("평가 수정 시도 - 사용자ID: {}, 점수: {}, 코멘트: {}", user.id, request.score, request.comment)
        return ResponseEntity.ok(ApiResponse.success("판매자 평가 수정", response))
    }

    @DeleteMapping("/delete/{ratingId}")
    fun deleteRating(
        @LoginUser user: DetailsUser,
        @PathVariable ratingId: Long
    ): ResponseEntity<ApiResponse<RatingDeleteResponse>> {
        val response: RatingDeleteResponse? = ratingService.deleteRating(user.id, ratingId)
        log.info("평가 수정 시도 - 사용자ID: {}, 평가ID: {}", user.id, ratingId)
        return ResponseEntity.ok(ApiResponse.success("판매자 평가 삭제", response))
    }

    @GetMapping("/{toUserId}/list")
    fun findRatingList(
        @PathVariable toUserId: Long,
        @PageableDefault(
            size = 10,
            sort = ["order.orderId"],
            direction = Sort.Direction.ASC
        ) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<RatingResponse>>> {
        val responses: Page<RatingResponse> =
            ratingService.findRatingList(toUserId, pageable)
        return ResponseEntity.ok(ApiResponse.success("판매자 평가 리스트 조회", responses))
    }
}
