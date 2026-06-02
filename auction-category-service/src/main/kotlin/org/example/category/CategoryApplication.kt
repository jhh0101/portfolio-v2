package org.example.category

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
        "org.example.auction.product.feign"
    ]
)
class CategoryApplication

fun main(args: Array<String>) {
    runApplication<CategoryApplication>(*args)
}