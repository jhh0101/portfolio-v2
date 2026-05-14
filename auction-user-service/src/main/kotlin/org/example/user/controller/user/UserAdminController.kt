package org.example.user.controller.user

import org.example.common.global.response.ApiResponse
import org.example.user.application.user.dto.*
import org.example.user.application.user.service.UserAdminService
import org.example.user.application.user.service.UserService
import org.example.user.domain.user.dto.UserSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
class UserAdminController(
    private val userAdminService: UserAdminService,
    private val userService: UserService,
) {
    @PostMapping("/{userId}/suspend")
    fun suspend(
        @PathVariable userId: Long,
        @RequestBody request: UserSuspensionRequest
    ): ResponseEntity<ApiResponse<UserDeleteResponse>> {
        val response: UserDeleteResponse = userAdminService.suspend(userId, request)
        return ResponseEntity.ok(ApiResponse.success("회원 정지", response))
    }

    @GetMapping("/list")
    fun userList(
        condition: UserSearchCondition,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<UserResponse>>> {
        val responses: Page<UserResponse> = userAdminService.userList(condition, pageable)
        return ResponseEntity.ok(ApiResponse.success("회원 리스트 조회", responses))
    }

    @GetMapping("/suspension-status/{userId}")
    fun suspensionStatus(@PathVariable userId: Long): ResponseEntity<ApiResponse<WithdrawalStatusResponse>> {
        val response: WithdrawalStatusResponse = userService.withdrawalStatus(userId)
        return ResponseEntity.ok(ApiResponse.success("정지 회원의 상태 조회", response))
    }

    @GetMapping("/suspension-reason/{userId}")
    fun suspensionReason(@PathVariable userId: Long): ResponseEntity<ApiResponse<UserSuspendReasonResponse>> {
        val response: UserSuspendReasonResponse = userAdminService.suspendReason(userId)
        return ResponseEntity.ok(ApiResponse.success("정지 회원의 정지 사유 조회", response))
    }
}
