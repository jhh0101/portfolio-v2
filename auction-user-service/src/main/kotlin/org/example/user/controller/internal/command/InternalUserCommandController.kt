package org.example.user.controller.internal.command

import auction.auctionuserapi.user.type.Role
import org.example.user.application.user.service.module.UserCommonService
import org.example.user.application.user.service.module.UserOrderService
import org.example.user.application.user.service.module.UserSellerService
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/user")
class InternalUserCommandController(
    private val userCommonService: UserCommonService,
    private val userOrderService: UserOrderService,
    private val userSellerService: UserSellerService,
) {
    @PostMapping("/{id}/addPoint")
    fun userAddPoint(@PathVariable("id") userId: Long, bidPrice: Long) {
        userCommonService.userAddPoint(userId, bidPrice)
    }

    @PostMapping("/{id}/subPoint")
    fun userSubPoint(@PathVariable("id") userId: Long, bidPrice: Long) {
        userCommonService.userSubPoint(userId, bidPrice)
    }

    @PatchMapping("/{id}/update-rating")
    fun updateUserRating(@PathVariable("id") userId: Long, ratingAvg: Double) {
        userOrderService.updateUserRating(userId, ratingAvg)
    }

    @PatchMapping("/{id}/update-role")
    fun userUpdateRole(@PathVariable("id") userId:Long, role: Role) {
        userSellerService.userUpdateRole(userId, role)
    }
}