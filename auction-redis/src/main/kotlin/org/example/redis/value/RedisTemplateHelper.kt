package org.example.redis.value

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisTemplateHelper(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    fun <T> set(key: String, value: T, duration: Duration? = null) {
        val stringValue = if (value is String) {
            value
        } else {
            objectMapper.writeValueAsString(value)
        }

        if (duration != null) {
            redisTemplate.opsForValue().set(key, stringValue, duration)
        } else {
            redisTemplate.opsForValue().set(key, stringValue)
        }
    }

    fun <T> get(key: String, clazz: Class<T>) : T? {
        val stringValue = redisTemplate.opsForValue().get(key) ?: return null

        if (clazz == String::class.java) {
            @Suppress("UNCHECKED_CAST")
            return stringValue as T
        }
        return objectMapper.readValue(stringValue, clazz)
    }

    fun delete(key: String) : Boolean {
        return redisTemplate.delete(key)
    }
}