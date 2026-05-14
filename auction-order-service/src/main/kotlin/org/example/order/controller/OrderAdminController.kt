package org.example.order.controller

import org.example.common.global.response.ApiResponse
import org.example.order.appilcation.dto.OrderResponse
import org.example.order.appilcation.service.OrderAdminService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
class OrderAdminController(
    private val orderAdminService: OrderAdminService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/{userId}/order")
    fun findOrder(
        @PathVariable userId: Long,
        @PageableDefault(size = 10, sort = ["orderId"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<ApiResponse<Slice<OrderResponse>>> {
        val responses: Slice<OrderResponse> = orderAdminService.findOrder(userId, pageable)
        log.info("[Order] 사용자 {}의 낙찰 리스트 조회 요청 (page: {})", userId, pageable.pageNumber)
        return ResponseEntity.ok(ApiResponse.success("낙찰 리스트 조회", responses))
    }
}
