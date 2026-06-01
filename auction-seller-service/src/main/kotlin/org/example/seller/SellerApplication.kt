package org.example.seller

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
        "org.example.auction.user.feign"
    ]
)
class SellerApplication

fun main(args: Array<String>) {
    runApplication<SellerApplication>(*args)
}