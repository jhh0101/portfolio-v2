package org.example.common.global.auth.filter

import jakarta.servlet.ServletException
import org.example.common.global.auth.service.JwtService
import org.example.common.global.error.CustomException
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(javaClass)

    @Throws(ServletException::class, java.io.IOException::class)
    override fun doFilterInternal(
        request: jakarta.servlet.http.HttpServletRequest,
        response: jakarta.servlet.http.HttpServletResponse,
        filterChain: jakarta.servlet.FilterChain
    ) {
        try {
            val token = getTokenFromRequest(request)

            if (!token.isNullOrBlank()){
                if (jwtService.validateToken(token)) {
                    val userId: Long = jwtService.getUserIdFromToken(token)
                    val email: String = jwtService.getEmailFromToken(token)
                    val role: String = jwtService.getRoleFromToken(token)
                    val nickname: String = jwtService.getNicknameFromToken(token)

                    val userDetails: UserDetails = userDetailsService.loadUserByUsername(email)

                    val authentication =
                        UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            listOf(SimpleGrantedAuthority("ROLE_$role"))
                        )
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                    SecurityContextHolder.getContext().authentication = authentication
                    log.debug(
                        "Set authentication for user: userId={}, email={}, nickname={} role={}",
                        userId,
                        email,
                        nickname,
                        role
                    )
                }
            }
        } catch (e: CustomException) {
            log.error("JWT authentication error: {}", e.message)
            // 예외를 그냥 넘겨서 GlobalExceptionHandler가 처리하도록 함
        }

        filterChain.doFilter(request, response)
    }

    private fun getTokenFromRequest(request: jakarta.servlet.http.HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (StringUtils.hasText(bearerToken) && bearerToken!!.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }
}
