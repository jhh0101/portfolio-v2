package org.example.user.controller.internal.query

import auction.auctionuserapi.user.dto.UserCommonResponse
import org.example.user.application.user.service.module.UserBidService
import org.example.user.application.user.service.module.UserCommonService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/user")
class InternalUserQueryController(
    private val userCommonService: UserCommonService,
    private val userBidService: UserBidService,
) {
    @GetMapping("/{id}")
    fun userModuleDto(@PathVariable("id") userId: Long) : UserCommonResponse {
        return userCommonService.userModuleDto(userId)
    }

    @GetMapping("/list")
    fun userListModuleDto(@RequestParam("userIds") userIds: List<Long>) : List<UserCommonResponse> {
        return userCommonService.userListModuleDto(userIds)
    }

    @GetMapping("/map")
    fun getUsersByIds(@RequestParam("userIds") userIds: List<Long>) : Map<Long, UserCommonResponse> {
        return userCommonService.getUsersByIds(userIds)
    }

    @GetMapping("/{id}/exists")
    fun validateUserExists(@PathVariable("id") userId: Long) {
        userCommonService.validateUserExists(userId)
    }

    @GetMapping("/{userId}/validCheck/{bidderId}")
    fun userValidCheck(@PathVariable("userId") userId: Long, @PathVariable("bidderId") bidderId: Long) {
        userBidService.userValidCheck(userId, bidderId)
    }
}