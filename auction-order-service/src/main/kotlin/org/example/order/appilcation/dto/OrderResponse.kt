package org.example.order.appilcation.dto

import auction.auctionorderapi.error.OrderErrorCode
import auction.auctionproductapi.auction.dto.AuctionCommonResponse
import auction.auctionproductapi.product.dto.ProductDetailResponse
import auction.auctionuserapi.user.dto.UserCommonResponse
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.example.common.global.error.CustomException
import org.example.order.domain.entity.Order
import java.time.LocalDateTime

data class OrderResponse(
    val orderId: Long,
    val nickname: String,
    val finalPrice: Long,
    @field:JsonUnwrapped
    val productDetailResponse: ProductDetailResponse,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val endTime: LocalDateTime
){}

fun Order.toDto(userDto: UserCommonResponse?, productDto: ProductDetailResponse?, auctionDto: AuctionCommonResponse?) : OrderResponse{
    return OrderResponse(
        orderId = this.orderId ?: throw CustomException(OrderErrorCode.ORDER_NOT_FOUND),
        nickname = checkNotNull(userDto?.userNickname) {"사용자의 닉네임을 찾을 수 없습니다."},
        finalPrice = checkNotNull(this.finalPrice) {"낙찰가를 찾을 수 없습니다."},
        productDetailResponse = checkNotNull(productDto) {"낙찰된 상품 정보를 찾을 수 없습니다."},
        endTime = auctionDto?.endTime ?: LocalDateTime.now()
    )
}
