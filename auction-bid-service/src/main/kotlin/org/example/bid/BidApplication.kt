package org.example.bid

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "org.example"
    ]
)
class BidApplication

fun main(args: Array<String>) {
    runApplication<BidApplication>(*args)
}