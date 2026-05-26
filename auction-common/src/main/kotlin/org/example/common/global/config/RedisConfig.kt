package org.example.common.global.config

import io.lettuce.core.ClientOptions
import io.lettuce.core.protocol.ProtocolVersion
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableRedisRepositories
class RedisConfig(
    @Value("\${spring.data.redis.host}") private val host: String,
    @Value("\${spring.data.redis.port}") private val port: Int,
    @Value("\${spring.data.redis.password}") private val password: String
) {

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        // [핵심] 최신 통신 규약(RESP3) 대신 RESP2를 사용하여 인증 문제 회피
        val clientOptions = ClientOptions.builder()
            .protocolVersion(ProtocolVersion.RESP2)
            .build()

        val clientConfig = LettuceClientConfiguration.builder()
            .clientOptions(clientOptions)
            .build()

        val serverConfig = RedisStandaloneConfiguration(host, port)
        serverConfig.setPassword(password)

        return LettuceConnectionFactory(serverConfig, clientConfig)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, String> {
        return RedisTemplate<String, String>().apply {
            connectionFactory = redisConnectionFactory()
            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
        }
    }

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config().apply {
            useSingleServer()
                .setAddress("redis://$host:$port")
                .setPassword(password)
        }
        return Redisson.create(config)
    }
}