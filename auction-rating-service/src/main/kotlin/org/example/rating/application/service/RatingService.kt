package org.example.rating.application.service

import auction.auctionorderapi.client.OrderClient
import auction.auctionorderapi.error.OrderErrorCode
import auction.auctionproductapi.auction.client.AuctionClient
import auction.auctionproductapi.auction.error.AuctionErrorCode
import auction.auctionproductapi.product.client.ProductClient
import auction.auctionproductapi.product.error.ProductErrorCode
import auction.auctionuserapi.user.client.UserClient
import auction.auctionuserapi.user.client.UserOrderClient
import auction.auctionuserapi.user.dto.UserCommonResponse
import auction.auctionuserapi.user.error.UserErrorCode
import auction.auctionuserapi.user.type.Role
import org.example.common.global.error.CustomException
import org.example.common.global.error.GlobalErrorCode
import org.example.rating.application.dto.RatingDeleteResponse
import org.example.rating.application.dto.RatingRequest
import org.example.rating.application.dto.RatingResponse
import org.example.rating.application.dto.toDeleteDto
import org.example.rating.application.dto.toDto
import org.example.rating.domain.entity.Rating
import org.example.rating.domain.entity.RatingStatus
import org.example.rating.domain.repository.RatingQueryRepository
import org.example.rating.domain.repository.RatingRepository
import org.example.rating.error.RatingErrorCode
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.function.ServerResponse.async

@Service
class RatingService(
    val ratingRepository: RatingRepository,
    val ratingQueryRepository: RatingQueryRepository,
    val orderClient: OrderClient,
    val userClient: UserClient,
    val userOrderClient: UserOrderClient,
    val productClient: ProductClient,
    val auctionClient: AuctionClient,
) {

    @Transactional
    fun createRating(userId: Long, orderId: Long, request: RatingRequest): RatingResponse {
        val orderDto = orderClient.orderModuleDto(orderId)

        if (orderDto.buyerId != userId) {
            throw CustomException(OrderErrorCode.BUYER_MISMATCH)
        }

        if (ratingRepository.existsByOrderId(orderId)) {
            throw CustomException(GlobalErrorCode.BAD_REQUEST, "이미 주문에 대한 평가가 존재합니다.")
        }

        val toUserDto = userClient.userModuleDto(userId)
        val fromUserDto = userClient.userModuleDto(orderDto.buyerId)

        val rating = Rating(
            orderId = orderId,
            toUserId = toUserDto.userId,
            fromUserId = fromUserDto.userId,
            score = request.score,
            comment = request.comment,
            status = RatingStatus.NORMAL
        )

        val ratingSave = ratingRepository.save(rating)

        val ratingAvg: Double = ratingQueryRepository.avgRating(ratingSave.toUserId)

        userOrderClient.updateUserRating(ratingSave.toUserId, ratingAvg)

        val auctionDto = auctionClient.auctionModuleDto(orderDto.auctionId)
        val productDto = productClient.productModuleDto(auctionDto.productId)

        return ratingSave.toDto(toUserDto, fromUserDto, productDto)
    }

    @Transactional(readOnly = true)
    fun findRating(orderId: Long): RatingResponse {
        val rating = ratingRepository.findByOrderId(orderId)
            ?: throw CustomException(RatingErrorCode.RATING_NOT_FOUND)
        val toUserDto = userClient.userModuleDto(rating.toUserId)
        val fromUserDto = userClient.userModuleDto(rating.fromUserId)
        val orderDto = orderClient.orderModuleDto(orderId)
        val auctionDto = auctionClient.auctionModuleDto(orderDto.auctionId)
        val productDto = productClient.productModuleDto(auctionDto.productId)

        return rating.toDto(toUserDto, fromUserDto, productDto)
    }

    @Transactional
    fun updateRating(userId: Long, orderId: Long, ratingId: Long, request: RatingRequest): RatingResponse {
        val rating: Rating = ratingRepository.findByIdOrNull(ratingId)
            ?: throw CustomException(RatingErrorCode.RATING_NOT_FOUND, "평가를 찾을 수 없습니다.")

        if (rating.fromUserId != userId) {
            throw CustomException(GlobalErrorCode.BAD_REQUEST, "작성자가 일치하지 않습니다.")
        }

        if (rating.orderId != orderId) {
            throw CustomException(GlobalErrorCode.BAD_REQUEST, "잘못된 주문 경로입니다.")
        }

        rating.updateRating(request.score, request.comment)

        val ratingAvg: Double = ratingQueryRepository.avgRating(rating.toUserId)

        userOrderClient.updateUserRating(rating.toUserId, ratingAvg)

        val orderDto = orderClient.orderModuleDto(orderId)

        val toUserDto = userClient.userModuleDto(userId)
        val fromUserDto = userClient.userModuleDto(rating.fromUserId)

        val auctionDto = auctionClient.auctionModuleDto(orderDto.auctionId)
        val productDto = productClient.productModuleDto(auctionDto.productId)

        return rating.toDto(toUserDto, fromUserDto, productDto)
    }

    @Transactional
    fun deleteRating(userId: Long, ratingId: Long): RatingDeleteResponse {
        val rating: Rating = ratingRepository.findByIdOrNull(ratingId)
            ?: throw CustomException(RatingErrorCode.RATING_NOT_FOUND, "평가를 찾을 수 없습니다.")

        val toUserDto = userClient.userModuleDto(userId)

        if (rating.fromUserId != userId && toUserDto.role != Role.ADMIN.name) {
            throw CustomException(GlobalErrorCode.BAD_REQUEST, "삭제 권한이 없습니다.")
        }

        ratingRepository.delete(rating)

        val ratingAvg: Double = ratingQueryRepository.avgRating(rating.toUserId)

        userOrderClient.updateUserRating(rating.toUserId, ratingAvg)

        return rating.toDeleteDto(toUserDto)
    }

    @Transactional(readOnly = true)
    fun findRatingList(toUserId: Long, pageable: Pageable): Page<RatingResponse> {
        val ratings: Page<Rating> = ratingRepository.findAllByToUserId(toUserId, pageable)

        if (ratings.isEmpty) return Page.empty()

        val toUserDto = userClient.userModuleDto(toUserId)

        val fromUserIds = ratings.map { it.fromUserId }.toSet().toList()
        val fromUserDtos = userClient.userListModuleDto(fromUserIds)
        val fromUserMap = fromUserDtos.associateBy { it.userId }

        val orderIds = ratings.map { it.orderId }.toSet().toList()
        val orderDtos = orderClient.orderListModuleDto(orderIds)
        val orderMap = orderDtos.associateBy { it.orderId }

        val auctionIds = orderDtos.map { it.auctionId }.toSet().toList()
        val auctionDtos = auctionClient.auctionListModuleDto(auctionIds)
        val auctionMap = auctionDtos.associateBy { it.auctionId }

        val productIds = auctionDtos.map { it.productId }.toSet().toList()
        val productDtos = productClient.productListModuleDto(productIds)
        val productMap = productDtos.associateBy { it.productId }

        return ratings.map { rating ->
            val matchedFromUserDto = fromUserMap[rating.fromUserId]
                ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

            val matchedOrder = orderMap[rating.orderId]
                ?: throw CustomException(OrderErrorCode.ORDER_NOT_FOUND)

            val matchedAuction = auctionMap[matchedOrder.auctionId]
                ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND)

            val matchedProductDto = productMap[matchedAuction.productId]
                ?: throw CustomException(ProductErrorCode.PRODUCT_NOT_FOUND)

            rating.toDto(toUserDto, matchedFromUserDto, matchedProductDto)
        }
    }
}
