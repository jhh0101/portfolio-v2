package org.example.auction.user.feign

import auction.auctionuserapi.user.client.UserClient
import auction.auctionuserapi.user.dto.UserCommonResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "auction-user-service", url = "http://localhost:8087")
@RequestMapping("/internal/user")
interface UserFeignClient : UserClient {

    @GetMapping("/{id}")
    override fun userModuleDto(@PathVariable("id") userId: Long) : UserCommonResponse

    @GetMapping("/list")
    override fun userListModuleDto(@RequestParam("userIds") userIds: List<Long>) : List<UserCommonResponse>

    @GetMapping("/map")
    override fun getUsersByIds(@RequestParam("userIds") userIds: List<Long>) : Map<Long, UserCommonResponse>

    @GetMapping("/{id}/exists")
    override fun validateUserExists(@PathVariable("id") userId: Long)

    @GetMapping("/{id}/addPoint")
    override fun userAddPoint(@PathVariable("id") userId: Long, bidPrice: Long)

    @GetMapping("/{id}/subPoint")
    override fun userSubPoint(@PathVariable("id") userId: Long, bidPrice: Long)
}