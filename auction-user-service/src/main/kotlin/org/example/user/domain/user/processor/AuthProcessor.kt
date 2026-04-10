package org.example.user.domain.user.processor

import org.example.common.global.error.CustomException
import org.example.common.global.error.GlobalErrorCode
import org.springframework.stereotype.Service

@Service
class AuthProcessor {

    fun validateRedisRefreshToken(redisRefreshToken: String?, refreshToken: String){
        if (redisRefreshToken != refreshToken) {
            throw CustomException(GlobalErrorCode.INVALID_TOKEN)
        }
    }

}