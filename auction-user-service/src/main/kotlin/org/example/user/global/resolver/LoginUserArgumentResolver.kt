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

    // 1. 이 리졸버가 해당 파라미터를 처리할 수 있는지 확인
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(LoginUser::class.java) &&
                parameter.parameterType == DetailsUser::class.java
    }

    // 2. 실제로 어떤 값을 넣어줄지 결정 (변환 로직)
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        // SecurityContextHolder에서 인증 객체 꺼내기
        val authentication = SecurityContextHolder.getContext().authentication ?: return null
        val principal = authentication.principal

        // SecurityUser(인증 객체)를 CurrentUser(순수 DTO)로 변환해서 반환
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