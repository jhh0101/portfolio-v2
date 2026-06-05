package org.example.auction.gateway

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.config.GatewayProperties
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@SpringBootApplication(
    scanBasePackages = [
        "org.example"
    ],
    exclude = [
        DataSourceAutoConfiguration::class,
        HibernateJpaAutoConfiguration::class
    ]
)
class AuctionGatewayApplication{
    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeExchange { it.anyExchange().permitAll() }
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<AuctionGatewayApplication>(*args)
}
