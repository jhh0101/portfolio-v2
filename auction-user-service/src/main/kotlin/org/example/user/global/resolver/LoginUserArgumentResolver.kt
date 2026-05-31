package org.example.user.global.resolver

import auction.auctionuserapi.auth.annotation.LoginUser
import auction.auctionuserapi.auth.dto.DetailsUser
import jakarta.servlet.http.HttpServletRequest
import org.example.user.application.auth.dto.SecurityUser
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import kotlin.jvm.java

@Component
class LoginUserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(LoginUser::class.java) &&
                parameter.parameterType == DetailsUser::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val request = webRequest.nativeRequest as HttpServletRequest

        val userIdStr = request.getHeader("X-User-Id")
        val email = request.getHeader("X-User-Email")
        val role = request.getHeader("X-User-Role")
        val nickname = request.getHeader("X-User-Nickname")

        if (userIdStr.isNullOrBlank()) {
            return null
        }

        return DetailsUser(
            id = userIdStr.toLong(),
            email = email,
            nickname = nickname,
            role = role
        )
    }
}