package org.example.user.global.resolver

import auction.auctionuserapi.auth.annotation.LoginUser
import auction.auctionuserapi.auth.dto.DetailsUser
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
        val authentication = SecurityContextHolder.getContext().authentication ?: return null
        val principal = authentication.principal

        return if (principal is SecurityUser) {
            DetailsUser(
                id = principal.userId,
                email = principal.username,
                nickname = principal.nickname,
                role = principal.role
            )
        } else {
            null
        }
    }
}