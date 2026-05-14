package org.example.auction.application.service

import org.redisson.api.RScoredSortedSet
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class AuctionScheduler(
    private val auctionService: AuctionService,
    private val redissonClient: RedissonClient

) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = 1000)
    fun checkExpiredAuctions() {
        val closingQueue: RScoredSortedSet<Long?> =
            redissonClient.getScoredSortedSet<Long>("auction:closing")

        val now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()

        val expiredAuctionIds: Collection<Long?>? = closingQueue.pollFirst(
            closingQueue.count(Double.NEGATIVE_INFINITY, true, now.toDouble(), true)
        )

        if (expiredAuctionIds != null) {
            if (expiredAuctionIds.isEmpty()) {
                return
            }
        }

        log.info("{}개의 경매가 종료되었습니다. 처리를 시작합니다.", expiredAuctionIds?.size)

        if (expiredAuctionIds != null) {
            for (auctionId in expiredAuctionIds) {
                try {
                    if (auctionId != null) {
                        auctionService.finishAuction(auctionId)
                    }
                } catch (e: java.lang.Exception) {
                    log.error("경매 종료 처리 중 오류 발생 - ID: {}, 사유: {}", auctionId, e.message)
                }
            }
        }
    }
}
