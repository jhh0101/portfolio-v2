package org.example.user.application.auth.service

import org.example.common.global.error.CustomException
import org.example.common.global.error.GlobalErrorCode
import org.example.user.application.auth.dto.LoginRequest
import org.example.user.application.auth.dto.SecurityUser
import org.example.user.application.auth.dto.TokenResponse
import org.example.user.domain.user.entity.User
import auction.auctionuserapi.user.error.UserErrorCode
import org.example.common.global.auth.service.JwtService
import org.example.user.domain.user.processor.AuthProcessor
import org.example.user.domain.user.repository.UserRepository
import org.example.common.global.config.JwtProperties
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.LockedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AuthService(
    private val jwtService: JwtService,
    private val jwtProperties: JwtProperties,
    private val authProcessor: AuthProcessor,
    private val userRepository: UserRepository,
    private val refreshTokenService: RefreshTokenService,
    private val authenticationManager: AuthenticationManager,
    private val redisTemplate: RedisTemplate<String, String>,
) {


    @Transactional
    fun login(request: LoginRequest): TokenResponse {
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    request.email,
                    request.password
                )
            )

            val securityUser = authentication.principal as SecurityUser

            val accessToken = jwtService.generateAccessToken(
                userId = securityUser.userId,
                email = securityUser.username,
                nickname = securityUser.nickname,
                role = securityUser.user.role.name
            )
            val refreshToken = jwtService.generateRefreshToken(securityUser.userId)

            refreshTokenService.saveRefreshTokenBidirectional(securityUser.userId, refreshToken)

            return TokenResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = jwtProperties.accessTokenExpiration / 1000L
            )
        } catch (e: LockedException) {
            throw CustomException(UserErrorCode.SUSPENDED_USER, "정지된 사용자 입니다.")
        }
    }

    @Transactional
    fun refreshAccessToken(refreshToken: String): TokenResponse {
        jwtService.validateToken(refreshToken)

        val userId = refreshTokenService.getUserIdByToken(refreshToken)
                ?: throw CustomException(GlobalErrorCode.TOKEN_NOT_FOUND)

        val redisRefreshToken = redisTemplate.opsForValue().get("refresh:user:$userId")

        authProcessor.validateRedisRefreshToken(redisRefreshToken, refreshToken)

        if (redisRefreshToken == null || redisRefreshToken != refreshToken) {
            throw CustomException(GlobalErrorCode.INVALID_TOKEN)
        }

        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        val newAccessToken = jwtService.generateAccessToken(userId, user.email, user.nickname, user.role.name)
        val newRefreshToken = jwtService.generateRefreshToken(user.userId)

        refreshTokenService.saveRefreshTokenBidirectional(userId, newRefreshToken)

        return TokenResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
            expiresIn = jwtProperties.accessTokenExpiration / 1000L
        )
    }

    fun logout(userId: Long) {
        refreshTokenService.deleteRefreshToken(userId)
    }
}
