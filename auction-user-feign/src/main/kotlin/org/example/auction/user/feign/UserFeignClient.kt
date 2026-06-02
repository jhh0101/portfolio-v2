package org.example.auction.user.feign

import auction.auctionuserapi.user.client.UserBidClient
import auction.auctionuserapi.user.client.UserClient
import auction.auctionuserapi.user.client.UserOrderClient
import auction.auctionuserapi.user.client.UserSellerClient
import auction.auctionuserapi.user.dto.UserCommonResponse
import auction.auctionuserapi.user.type.Role
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "auction-user-service", url = "http://localhost:8087")
@RequestMapping("/internal/user")
interface UserFeignClient : UserClient, UserBidClient, UserOrderClient, UserSellerClient {

    @GetMapping("/{id}")
    override fun userModuleDto(@PathVariable("id") userId: Long) : UserCommonResponse

    @GetMapping("/list")
    override fun userListModuleDto(@RequestParam("userIds") userIds: List<Long>) : List<UserCommonResponse>

    @GetMapping("/map")
    override fun getUsersByIds(@RequestParam("userIds") userIds: List<Long>) : Map<Long, UserCommonResponse>

    @GetMapping("/{id}/exists")
    override fun validateUserExists(@PathVariable("id") userId: Long)

    @PostMapping("/{id}/addPoint")
    override fun userAddPoint(@PathVariable("id") userId: Long, @RequestParam("bidPrice") bidPrice: Long)

    @PostMapping("/{id}/subPoint")
    override fun userSubPoint(@PathVariable("id") userId: Long, @RequestParam("bidPrice") bidPrice: Long)

    @GetMapping("/{userId}/validCheck/{bidderId}")
    override fun userValidCheck(@PathVariable("userId") userId: Long, @PathVariable("bidderId") bidderId: Long)

    @PatchMapping("/{id}/update-rating")
    override fun updateUserRating(@PathVariable("id") userId: Long, @RequestParam("ratingAvg") ratingAvg: Double)

    @PatchMapping("/{id}/update-role")
    override fun userUpdateRole(@PathVariable("id") userId:Long, @RequestParam("role") role: Role)
}