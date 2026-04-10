package org.example.user.application.auth.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RefreshTokenService(
    private val redisTemplate: RedisTemplate<String, String>,
    @field:Value($$"${jwt.refresh-token-expiration}")
    private val refreshTokenExpiration: Long = 604800000L
) {

    fun saveRefreshTokenBidirectional(userId: Long, token: String) {
        val userKey = REFRESH_TOKEN_PREFIX + "user:" + userId
        val tokenKey = REFRESH_TOKEN_PREFIX + "token:" + token

        redisTemplate.opsForValue().get(userKey)?.let { oldToken ->
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + "token:" + oldToken)
        }

        redisTemplate.opsForValue()
            .set(userKey, token, refreshTokenExpiration, TimeUnit.MILLISECONDS)
        redisTemplate.opsForValue()
            .set(tokenKey, userId.toString(), refreshTokenExpiration, TimeUnit.MILLISECONDS)
    }

    fun getUserIdByToken(token: String): Long? {
        val key = REFRESH_TOKEN_PREFIX + "token:" + token
        val userId = redisTemplate.opsForValue().get(key)
        return userId?.toLongOrNull()
    }

    fun deleteRefreshToken(userId: Long) {
        val userKey = REFRESH_TOKEN_PREFIX + "user:" + userId
        val token = redisTemplate.opsForValue().get(userKey)

        redisTemplate.delete(REFRESH_TOKEN_PREFIX + "token:" + token)
        redisTemplate.delete(userKey)
    }

    companion object {
        private const val REFRESH_TOKEN_PREFIX = "refresh:"
    }
}