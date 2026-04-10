package org.example.user.application.user.service.module

import auction.auctionuserapi.user.client.UserSellerClient
import auction.auctionuserapi.user.type.Role
import org.example.user.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserSellerService(
    private val userRepository: UserRepository,
) : UserSellerClient{
    override fun userUpdateRole(userId:Long, role: Role) {
        val user = userRepository.findByUserId(userId)
        user.updateRole(role)
    }


}