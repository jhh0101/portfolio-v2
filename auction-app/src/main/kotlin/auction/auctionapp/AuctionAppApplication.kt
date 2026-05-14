package auction.auctionapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "auction.auctionapp",
        "org.example"
    ]
)
class AuctionAppApplication

fun main(args: Array<String>) {
    runApplication<AuctionAppApplication>(*args)
}
