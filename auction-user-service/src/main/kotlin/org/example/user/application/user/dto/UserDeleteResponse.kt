package org.example.user.application.user.dto

import org.example.user.domain.user.entity.User

data class UserDeleteResponse(
    val email: String
)

fun User.toDeleteDto() : UserDeleteResponse {
    return UserDeleteResponse(
        email = this.email
    )
}