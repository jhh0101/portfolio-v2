package org.example.auction.gateway.auth.filter

import org.example.common.global.auth.service.JwtService
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import org.springframework.http.HttpHeaders

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config>(Config::class.java) {

    private val log = LoggerFactory.getLogger(javaClass)

    class Config {
        // application.yml에서 필터로 넘길 파라미터가 있다면 정의
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request

            // 1 & 2. 헤더 추출 및 검증을 한 번에 처리!
            val authorizationHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)

            if (authorizationHeader.isNullOrBlank() || !authorizationHeader.startsWith("Bearer ")) {
                return@GatewayFilter onError(exchange, "Authorization 헤더가 없거나 잘못된 형식입니다.", HttpStatus.UNAUTHORIZED)
            }

            // "Bearer " 부분을 잘라내고 token만 가져옴
            val token = authorizationHeader.substring(7)

            try {
                // 3. 토큰 유효성 검증
                if (jwtService.validateToken(token)) {
                    val userId = jwtService.getUserIdFromToken(token)
                    val email = jwtService.getEmailFromToken(token)
                    val role = jwtService.getRoleFromToken(token)
                    val nickname = jwtService.getNicknameFromToken(token)

                    // 4. 추출한 정보를 헤더에 넣음
                    val mutatedRequest = request.mutate()
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)
                        .header("X-User-Nickname", nickname)
                        .build()

                    log.debug("게이트웨이 인증 통과: userId={}, role={}", userId, role)

                    return@GatewayFilter chain.filter(exchange.mutate().request(mutatedRequest).build())
                }
            } catch (e: Exception) {
                log.error("JWT 검증 실패: {}", e.message)
                return@GatewayFilter onError(exchange, "JWT 검증에 실패했습니다.", HttpStatus.UNAUTHORIZED)
            }

            return@GatewayFilter onError(exchange, "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED)
        }
    }

    // 에러 발생 시 처리 (WebFlux 방식)
    private fun onError(exchange: ServerWebExchange, err: String, httpStatus: HttpStatus): Mono<Void> {
        val response = exchange.response
        response.statusCode = httpStatus
        log.warn("게이트웨이 인가 에러: {}", err)
        return response.setComplete()
    }
}