package auction.auctionapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "auction.auctionapp",
        "org.example.user.global"
    ]
)
class AuctionAppApplication

fun main(args: Array<String>) {
    runApplication<AuctionAppApplication>(*args)
}
