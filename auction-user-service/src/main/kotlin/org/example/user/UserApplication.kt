package org.example.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "org.example"
    ]
)
class UserApplication

fun main(args: Array<String>) {
    runApplication<UserApplication>(*args)
}