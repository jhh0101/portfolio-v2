package auction.auctionuserapi.user.error

import org.example.common.global.error.ErrorCode

enum class UserErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    USER_NOT_FOUND("USER001", "사용자를 찾을 수 없습니다"),
    DUPLICATE_EMAIL("USER002", "이미 사용 중인 이메일입니다"),
    DUPLICATE_NICKNAME("USER003", "이미 사용 중인 사용자 이름입니다"),
    USER_VERIFICATION_FAILED("USER004", "사용자 인증에 실패했습니다"),
    PASSWORD_MISMATCH("USER005", "비밀번호가 일치하지 않습니다"),
    SOCIAL_USER_CANNOT_CHANGE_PASSWORD("USER006", "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다"),
    SUSPENDED_USER("USER007", "정지된 사용자입니다"),
    WITHDRAWN_USER("USER008", "탈퇴된 사용자입니다"),
    CANNOT_WITHDRAW_WHILE_TRADING("USER009", "진행 중인 거래가 있습니다"),

    // Bid
    NOT_ENOUGH_POINTS("B001", "입찰 내역을 찾을 수 없습니다"),
}