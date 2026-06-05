package org.example.auction.user.feign

import auction.auctionuserapi.user.dto.UserCommonResponse
import auction.auctionuserapi.user.type.Role
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "auction-user-service", url = "http://localhost:8087", contextId = "userFeignClient")
interface UserFeignClient {

    @GetMapping("/internal/user/{id}")
    fun userModuleDto(@PathVariable("id") userId: Long) : UserCommonResponse

    @GetMapping("/internal/user/list")
    fun userListModuleDto(@RequestParam("userIds") userIds: List<Long>) : List<UserCommonResponse>

    @GetMapping("/internal/user/map")
    fun getUsersByIds(@RequestParam("userIds") userIds: List<Long>) : Map<Long, UserCommonResponse>

    @GetMapping("/internal/user/{id}/exists")
    fun validateUserExists(@PathVariable("id") userId: Long)

    @PostMapping("/internal/user/{id}/addPoint")
    fun userAddPoint(@PathVariable("id") userId: Long, @RequestParam("bidPrice") bidPrice: Long)

    @PostMapping("/internal/user/{id}/subPoint")
    fun userSubPoint(@PathVariable("id") userId: Long, @RequestParam("bidPrice") bidPrice: Long)

    @GetMapping("/internal/user/{userId}/validCheck/{bidderId}")
    fun userValidCheck(@PathVariable("userId") userId: Long, @PathVariable("bidderId") bidderId: Long)

    @PatchMapping("/internal/user/{id}/update-rating")
    fun updateUserRating(@PathVariable("id") userId: Long, @RequestParam("ratingAvg") ratingAvg: Double)

    @PatchMapping("/internal/user/{id}/update-role")
    fun userUpdateRole(@PathVariable("id") userId:Long, @RequestParam("role") role: Role)
}