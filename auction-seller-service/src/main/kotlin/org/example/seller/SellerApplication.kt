package org.example.seller

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "org.example"
    ]
)
class SellerApplication

fun main(args: Array<String>) {
    runApplication<SellerApplication>(*args)
}