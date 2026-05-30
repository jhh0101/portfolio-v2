package org.example.redis.zset

import org.redisson.api.RScoredSortedSet
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.collections.remove

@Component
class RedisZSetHelper(
    private val redissonClient: RedissonClient,
) {
    fun <V> queueAdd(key: String, score: Long, value: V) {
        val queue = redissonClient.getScoredSortedSet<V>(key)
        queue.add(score.toDouble(), value)
    }

    fun <V> queueRemove(key: String, index: Long?) : Boolean {
        val queue = redissonClient.getScoredSortedSet<V>(key)
        return queue.remove(index)
    }

    fun <V> queuePollFirst(key: String, maxScore: Long) : Collection<V> {
        val queue = redissonClient.getScoredSortedSet<V>(key)

        val items = queue.valueRange(Double.NEGATIVE_INFINITY, true, maxScore.toDouble(), true)

        if (items.isEmpty()) return emptyList()

        queue.removeAll(items)

        return items
    }
}