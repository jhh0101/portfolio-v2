package org.example.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication(
    scanBasePackages = [
        "org.example"
    ]
)
@EnableFeignClients(
    basePackages = [
        "org.example.auction.product.feign",
        "org.example.auction.seller.feign",
        "org.example.auction.bid.feign"
    ]
)
class UserApplication

fun main(args: Array<String>) {
    runApplication<UserApplication>(*args)
}