package org.example.order

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
class OrderApplication

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}