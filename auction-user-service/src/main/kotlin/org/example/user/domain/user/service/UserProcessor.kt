package org.example.user.domain.user.service

import org.example.user.domain.user.dto.UserUpdateCommand
import org.example.user.domain.user.entity.User
import auction.auctionuserapi.user.type.UserStatus
import auction.auctionuserapi.user.error.UserErrorCode
import org.example.common.global.error.CustomException
import org.example.common.global.util.MaskingUtil
import org.springframework.stereotype.Service

@Service
class UserProcessor {

    fun validateSignup(existingUser: User?, isDuplicateNickname: Boolean) {
        if (existingUser != null) {
            if (existingUser.status == UserStatus.SUSPENDED) {
                throw CustomException(UserErrorCode.SUSPENDED_USER, "정지된 사용자입니다.")
            }
            throw CustomException(UserErrorCode.DUPLICATE_EMAIL, "이미 사용 중인 이메일입니다.")
        }

        if (isDuplicateNickname) {
            throw CustomException(UserErrorCode.DUPLICATE_NICKNAME, "이미 사용 중인 닉네임입니다.")
        }
    }

    fun validateWithdrawn(matchesPassword: Boolean, user: User, bidCount: Long, productCount: Long) {
        validateUserProtection(user.userId)

        if (!matchesPassword) {
            throw CustomException(UserErrorCode.PASSWORD_MISMATCH, "비밀번호가 일치하지 않습니다.")
        }

        if (bidCount + productCount > 0) {
            throw CustomException(UserErrorCode.CANNOT_WITHDRAW_WHILE_TRADING, "현재 진행 중인 거래가 있습니다.")
        }

        val formatPhone: String? = MaskingUtil.formatPhone(user.phone)
        val maskEmail: String = MaskingUtil.maskEmail(user.email).toString()
        val maskUsername: String = MaskingUtil.maskUsername(user.username).toString()
        val maskPhone: String = MaskingUtil.maskPhone(formatPhone).toString()

        user.withdraw(maskEmail, maskUsername, maskPhone, user.userId)
    }

    fun validateUpdateUser(user: User, isDuplicateNickname: Boolean, command: UserUpdateCommand) {
        if (user.nickname != command.nickname) {
            if (isDuplicateNickname) {
                throw CustomException(UserErrorCode.DUPLICATE_NICKNAME, "이미 사용 중인 닉네임입니다.")
            }
        }

        user.updateUser(command)
    }

    fun validateUpdatePassword(user: User, newPassword: String, confirmPassword: String, matchesPassword: Boolean, encodedPassword: String) {
        validateUserProtection(user.userId)

        if (!matchesPassword){
            throw CustomException(UserErrorCode.PASSWORD_MISMATCH, "현재 비밀번호가 일치하지 않습니다.")
        }

        if (newPassword != confirmPassword) {
            throw CustomException(UserErrorCode.PASSWORD_MISMATCH, "변경할 비밀번호와 일치하지 않습니다.")
        }

        user.updatePassword(encodedPassword)
    }

    fun loadUserByUsername(user: User) {
        if (user.status != UserStatus.NORMAL) {
            throw CustomException(UserErrorCode.SUSPENDED_USER, "정지된 사용자입니다.")
        }
    }

    private fun validateUserProtection(userId: Long?) {
        if (PROTECTED_USER_IDS.contains(userId)) {
            throw CustomException(UserErrorCode.PROTECT_DEFAULT_USERS)
        }
    }

    companion object {
        private val PROTECTED_USER_IDS: Set<Long> = setOf(1L, 2L, 3L, 4L)
    }
}




