package org.example.common.global.auth.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import org.example.common.global.config.JwtProperties
import org.example.common.global.error.CustomException
import org.example.common.global.error.GlobalErrorCode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val signingKey: SecretKey =
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))

    fun generateAccessToken(userId: Long, email: String, nickname: String, role: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtProperties.accessTokenExpiration)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("role", role)
            .claim("nickname", nickname)
            .claim("type", "access")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(this.signingKey)
            .compact()
    }

    fun generateRefreshToken(userId: Long?): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtProperties.refreshTokenExpiration)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("type", "refresh")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(this.signingKey)
            .compact()
    }

    fun getUserIdFromToken(token: String): Long {
        val claims: Claims = parseToken(token)
        return claims.subject.toLong()
    }

    fun getEmailFromToken(token: String): String {
        val claims: Claims = parseToken(token)
        return claims.get("email", String::class.java)
    }

    fun getRoleFromToken(token: String): String {
        val claims: Claims = parseToken(token)
        return claims.get("role", String::class.java)
    }

    fun getNicknameFromToken(token: String): String {
        val claims: Claims = parseToken(token)
        return claims.get("nickname", String::class.java)
    }

    fun validateToken(token: String): Boolean {
        try {
            parseToken(token)
            return true
        } catch (e: SecurityException) {
            log.error("Invalid JWT signature: {}", e.message)
            throw CustomException(GlobalErrorCode.INVALID_TOKEN)
        } catch (e: MalformedJwtException) {
            log.error("Invalid JWT signature: {}", e.message)
            throw CustomException(GlobalErrorCode.INVALID_TOKEN)
        } catch (e: ExpiredJwtException) {
            log.error("Expired JWT token: {}", e.message)
            throw CustomException(GlobalErrorCode.EXPIRED_TOKEN)
        } catch (e: UnsupportedJwtException) {
            log.error("Unsupported JWT token: {}", e.message)
            throw CustomException(GlobalErrorCode.INVALID_TOKEN)
        } catch (e: IllegalArgumentException) {
            log.error("JWT claims string is empty: {}", e.message)
            throw CustomException(GlobalErrorCode.INVALID_TOKEN)
        }
    }

    private fun parseToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(this.signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
    }

    fun generateTemporaryToken(userId: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + 600000) // 10분

        return Jwts.builder()
            .subject(userId.toString())
            .claim("type", "temporary")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(this.signingKey)
            .compact()
    }
}