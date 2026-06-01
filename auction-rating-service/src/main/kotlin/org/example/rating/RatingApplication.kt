package org.example.rating

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "org.example"
    ]
)
class RatingApplication

fun main(args: Array<String>) {
    runApplication<RatingApplication>(*args)
}