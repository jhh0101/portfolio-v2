package org.example.product

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "org.example"
    ]
)
class ProductApplication

fun main(args: Array<String>) {
    runApplication<ProductApplication>(*args)
}