package org.example.category

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "org.example"
    ]
)
class CategoryApplication

fun main(args: Array<String>) {
    runApplication<CategoryApplication>(*args)
}