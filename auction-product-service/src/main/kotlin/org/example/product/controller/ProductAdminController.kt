package org.example.product.controller

import org.example.common.global.response.ApiResponse
import org.example.product.application.service.ProductAdminService
import org.example.product.domain.product.dto.ProductAndAuctionResponse
import org.example.product.domain.product.dto.ProductListCondition
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
class ProductAdminController(
    private val productAdminService: ProductAdminService,
) {

    @GetMapping("/{userId}/product")
    fun userProductList(
        @PathVariable userId: Long,
        condition: ProductListCondition,
        @PageableDefault(size = 5) pageable: Pageable
    ): ResponseEntity<ApiResponse<Slice<ProductAndAuctionResponse>>> {
        val responses: Slice<ProductAndAuctionResponse> =
            productAdminService.userProductList(userId, condition, pageable)
        return ResponseEntity.ok(ApiResponse.success("사용자의 상품 리스트 조회", responses))
    }
}
