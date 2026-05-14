package org.example.user.application.user.service

import auction.auctionbidapi.client.BidClient
import auction.auctionproductapi.product.client.ProductClient
import auction.auctionsellerapi.client.SellerClient
import auction.auctionsellerapi.status.SellerStatus
import org.example.user.application.auth.dto.SecurityUser
import org.example.common.global.error.CustomException
import org.example.user.application.user.dto.UserDeleteResponse
import org.example.user.application.user.dto.UserNewPasswordRequest
import org.example.user.application.user.dto.UserProfileResponse
import org.example.user.application.user.dto.UserResponse
import org.example.user.application.user.dto.UserSingupRequest
import org.example.user.application.user.dto.UserUpdateRequest
import org.example.user.application.user.dto.UserWithdrawnRequest
import org.example.user.application.user.dto.WithdrawalStatusResponse
import org.example.user.application.user.dto.toDeleteDto
import org.example.user.application.user.dto.toDto
import org.example.user.application.user.dto.toProfileDto
import org.example.user.application.user.dto.toUpdateCommand
import auction.auctionuserapi.user.type.Role
import org.example.user.domain.user.entity.User
import auction.auctionuserapi.user.type.UserStatus
import auction.auctionuserapi.user.error.UserErrorCode
import org.example.user.application.user.dto.toWithdrawalStatusDto
import org.example.user.domain.user.repository.UserRepository
import org.example.user.domain.user.service.UserProcessor
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val productClient: ProductClient,
    private val sellerClient: SellerClient,
    private val passwordEncoder: PasswordEncoder,
    private val userProcessor: UserProcessor,
    private val userRepository: UserRepository,
    private val bidClient: BidClient,
) : UserDetailsService {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun signup(request: UserSingupRequest): UserResponse {
        val existingUser: User? = userRepository.findByEmail(request.email)
        val isDuplicateNickname: Boolean = userRepository.existsByNickname(request.nickname)

        userProcessor.validateSignup(existingUser, isDuplicateNickname)

        val newUser = User(
            email = request.email,
            username = request.username,
            nickname = request.nickname,
            baseAddress = request.baseAddress,
            detailAddress = request.detailAddress,
            phone = request.phone,
            password = passwordEncoder.encode(request.password),
            point = 0L,
            avgRating = 0.0,
            status = UserStatus.NORMAL,
            role = Role.USER
        )

        val saveUser = userRepository.save(newUser)
        log.info(
            "User created: userId={}, email={}, nickname={}",
            saveUser.userId,
            saveUser.email,
            saveUser.nickname
        )

        return saveUser.toDto()
    }

    @Transactional
    fun withdrawn(userId: Long, request: UserWithdrawnRequest): UserDeleteResponse {
        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")

        val matchesPassword: Boolean = passwordEncoder.matches(request.password, user.password)

        val bidCount = bidClient.bidCount(userId)
        val productCount = productClient.productCount(userId)

        userProcessor.validateWithdrawn(matchesPassword, user, bidCount, productCount)

        return user.toDeleteDto()
    }

    fun profile(userId: Long): UserProfileResponse {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")

        val sellerStatus: SellerStatus = sellerClient.getSellerStatus(userId)

        return user.toProfileDto(sellerStatus)
    }

    @Transactional
    fun updateUser(userId: Long, request: UserUpdateRequest): UserProfileResponse {
        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")

        val isDuplicateNickname: Boolean = userRepository.existsByNickname(request.nickname)

        val command = request.toUpdateCommand()

        userProcessor.validateUpdateUser(user, isDuplicateNickname, command)

        val sellerStatus: SellerStatus = sellerClient.getSellerStatus(userId)

        return user.toProfileDto(sellerStatus)
    }

    @Transactional
    fun updatePassword(userId: Long, request: UserNewPasswordRequest) {

        val user = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")

        val matchesPassword: Boolean = passwordEncoder.matches(request.currentPassword, user.password)
        val encodedPassword: String = passwordEncoder.encode(request.newPassword).toString()

        userProcessor.validateUpdatePassword(user, request.newPassword, request.confirmPassword, matchesPassword, encodedPassword)
    }

    fun withdrawalStatus(userId: Long): WithdrawalStatusResponse {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")

        val bidCount = bidClient.bidCount(userId)
        val productCount = productClient.productCount(userId)

        return user.toWithdrawalStatusDto(bidCount, productCount)
    }


    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val user: User = userRepository.findByEmail(email)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.")

        userProcessor.loadUserByUsername(user)

        return SecurityUser(user)
    }
}