package org.example.user.application.auth.service

import org.example.redis.value.RedisTemplateHelper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RefreshTokenService(
    private val redisTemplateHelper: RedisTemplateHelper,
    @field:Value($$"${jwt.refresh-token-expiration}")
    private val refreshTokenExpiration: Long = 604800000L
) {

    fun saveRefreshTokenBidirectional(userId: Long, token: String) {
        val userKey = REFRESH_TOKEN_PREFIX + "user:" + userId
        val tokenKey = REFRESH_TOKEN_PREFIX + "token:" + token
        val duration = Duration.ofMillis(refreshTokenExpiration)

        redisTemplateHelper.get(userKey, String::class.java)?.let { oldToken ->
            redisTemplateHelper.delete(REFRESH_TOKEN_PREFIX + "token:" + oldToken)
        }

        redisTemplateHelper.set(userKey, token, duration)
        redisTemplateHelper.set(tokenKey, userId.toString(), duration)
    }

    fun getUserIdByToken(token: String): Long? {
        val key = REFRESH_TOKEN_PREFIX + "token:" + token
        return redisTemplateHelper.get(key, Long::class.javaObjectType)
    }

    fun deleteRefreshToken(userId: Long) {
        val userKey = REFRESH_TOKEN_PREFIX + "user:" + userId

        redisTemplateHelper.get(userKey, String::class.java)?.let { token ->
            redisTemplateHelper.delete(REFRESH_TOKEN_PREFIX + "token:" + token)
        }
        redisTemplateHelper.delete(userKey)
    }

    companion object {
        private const val REFRESH_TOKEN_PREFIX = "refresh:"
    }
}