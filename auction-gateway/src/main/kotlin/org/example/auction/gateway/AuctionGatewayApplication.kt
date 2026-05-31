package org.example.auction.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuctionGatewayApplication

fun main(args: Array<String>) {
    runApplication<AuctionGatewayApplication>(*args)
}
