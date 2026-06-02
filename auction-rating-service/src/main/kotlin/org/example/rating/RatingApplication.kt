package org.example.rating

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
        "org.example.auction.user.feign",
        "org.example.auction.product.feign",
    ]
)
class RatingApplication

fun main(args: Array<String>) {
    runApplication<RatingApplication>(*args)
}