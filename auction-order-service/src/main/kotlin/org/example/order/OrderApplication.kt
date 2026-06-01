package org.example.order

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "org.example"
    ]
)
class OrderApplication

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}