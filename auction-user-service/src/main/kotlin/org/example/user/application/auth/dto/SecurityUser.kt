package org.example.user.application.auth.dto

import org.example.user.domain.user.entity.User
import auction.auctionuserapi.user.type.UserStatus
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class SecurityUser(
    val user: User
) : UserDetails {

    val userId: Long
        get() = user.userId ?: 0L

    val nickname: String
        get() = user.nickname

    val status: UserStatus
        get() = user.status

    val role: String
        get() = user.role.name

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_" + user.role))
    }

    override fun getPassword(): String? {
        return user.password
    }

    override fun getUsername(): String {
        return user.email
    }

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = user.status != UserStatus.SUSPENDED

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

}
