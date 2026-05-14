package org.example.user.application.user.dto

import org.example.user.domain.user.entity.User

data class UserSuspendReasonResponse(
    val suspendReason: String
) {
}

fun User.toSuspendReasonDto() : UserSuspendReasonResponse {
    return UserSuspendReasonResponse(
        suspendReason = checkNotNull(this.suspensionReason) {"회원 정지 사유를 찾지 못했습니다."}
    )
}
