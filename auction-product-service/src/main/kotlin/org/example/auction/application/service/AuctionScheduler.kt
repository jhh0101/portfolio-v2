package org.example.auction.application.service

import org.example.redis.zset.RedisZSetHelper
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class AuctionScheduler(
    private val auctionService: AuctionService,
    private val redisZSetHelper: RedisZSetHelper

) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = 1000)
    fun checkExpiredAuctions() {
        val now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()

        val expiredAuctionIds = redisZSetHelper.queuePollFirst<Long>("auction:closing", now)

        if (expiredAuctionIds.isEmpty()) {
            return
        }

        log.info("{}개의 경매가 종료되었습니다. 처리를 시작합니다.", expiredAuctionIds?.size)

        for (auctionId in expiredAuctionIds) {
            try {
                auctionService.finishAuction(auctionId)
            } catch (e: Exception) {
                log.error("경매 종료 처리 중 오류 발생 - ID: {}, 사유: {}", auctionId, e.message)
            }
        }
    }
}
