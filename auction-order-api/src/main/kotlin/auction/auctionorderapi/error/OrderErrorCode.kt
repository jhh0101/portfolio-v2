package auction.auctionorderapi.error

import org.example.common.global.error.ErrorCode

enum class OrderErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    ORDER_NOT_FOUND("ORDER001", "주문 내역을 찾을 수 없습니다"),
    BUYER_MISMATCH("ORDER002", "구매자가 일치하지 않습니다."),
}