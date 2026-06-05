package org.example.auction.domain.auction.service

import org.example.auction.domain.auction.entity.Auction
import auction.auctionbidapi.dto.BidCommonResponse
import auction.auctionorderapi.client.OrderClient
import auction.auctionproductapi.auction.status.AuctionStatus
import auction.auctionproductapi.product.status.ProductStatus
import auction.auctionuserapi.user.dto.UserCommonResponse
import org.example.auction.order.feign.OrderFeignClient
import org.example.auction.user.feign.UserFeignClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.Optional

@Service
class AuctionProcessor {
    private val log = LoggerFactory.getLogger(javaClass)

    fun validateFinishAuction(auction: Auction, topBid: Optional<BidCommonResponse>, user: UserFeignClient, userDto: UserCommonResponse, order: OrderFeignClient) : OrderFeignClient? {
        if (auction.status != AuctionStatus.PROCEEDING) {
            return null
        }
        auction.changeStatus(AuctionStatus.ENDED)

        if (topBid.isPresent) {
            val winnerBid: BidCommonResponse = topBid.get()
            order.saveOrder(
                auctionId = auction.auctionId ?: 0L,
                buyerId = winnerBid.bidId,
                finalPrice = winnerBid.bidPrice
            )

            user.userAddPoint(userDto.userId, auction.currentPrice)
            auction.product.changeStatus(ProductStatus.SOLD)
            log.info("경매 낙찰 완료 - ID: {}", auction.auctionId)
            return order
        } else {
            auction.product.changeStatus(ProductStatus.FAILED)
            log.info("경매 유찰 완료 (입찰자 없음) - ID: {}", auction.auctionId)
        }
        return null
    }
}





