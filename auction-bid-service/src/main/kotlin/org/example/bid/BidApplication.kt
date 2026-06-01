package org.example.bid

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
class BidApplication

fun main(args: Array<String>) {
    runApplication<BidApplication>(*args)
}