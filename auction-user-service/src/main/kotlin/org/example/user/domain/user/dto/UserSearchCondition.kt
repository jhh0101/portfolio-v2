package org.example.user.domain.user.dto

import auction.auctionuserapi.user.type.Role
import auction.auctionuserapi.user.type.UserStatus

class UserSearchCondition(
    val email: String? = null,
    val nickname: String? = null,
    val status: UserStatus? = null,
    val role: Role? = null,
)