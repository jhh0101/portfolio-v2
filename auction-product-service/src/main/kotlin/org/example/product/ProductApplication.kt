package org.example.product

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
        "org.example.auction.order.feign",
        "org.example.auction.category.feign",
        "org.example.auction.bid.feign"
    ]
)
class ProductApplication

fun main(args: Array<String>) {
    runApplication<ProductApplication>(*args)
}