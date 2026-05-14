package org.example.bid.application.service

import auction.auctionbidapi.error.BidErrorCode
import auction.auctionproductapi.auction.client.AuctionBidClient
import auction.auctionproductapi.auction.client.AuctionClient
import auction.auctionproductapi.auction.dto.AuctionCommonResponse
import auction.auctionproductapi.product.client.ProductClient
import auction.auctionproductapi.product.dto.ProductCommonResponse
import auction.auctionuserapi.user.client.UserClient
import org.example.bid.application.dto.BidResponse
import org.example.bid.domain.bid.dto.BidHistoryResponse
import org.example.bid.domain.bid.entity.Bid
import org.example.bid.domain.bid.repository.BidQueryRepository
import org.example.bid.domain.bid.repository.BidRepository
import org.example.bid.domain.bid.service.BidProcessor
import org.example.common.global.error.CustomException
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class BidAdminService(
    private val bidRepository: BidRepository,
    private val auctionClient: AuctionClient,
    private val productClient: ProductClient,
    private val userClient: UserClient,
) {
    @Transactional(readOnly = true)
    fun findBidHistorySlice(userId: Long, pageable: Pageable): Slice<BidHistoryResponse> {
        val bidPage: Slice<Bid> = bidRepository.findSliceByBidderId(userId, pageable)

        val auctionIds: List<Long> = bidPage.content.map { it.auctionId }
        val auctions: List<AuctionCommonResponse> = auctionClient.auctionListModuleDto(auctionIds)
        val auctionMap: Map<Long, AuctionCommonResponse> = auctions.associateBy { it.auctionId }

        val productIds: List<Long> = auctions.map { it.productId }
        val products: List<ProductCommonResponse> = productClient.productListModuleDto(productIds)
        val productMap: Map<Long, ProductCommonResponse> = products.associateBy { it.productId }

        val responseContent = bidPage.content.map { bid ->
            val auction = auctionMap[bid.auctionId] ?: throw CustomException(BidErrorCode.AUCTION_NOT_FOUND)
            val product = productMap[auction.productId] ?: throw CustomException(BidErrorCode.PRODUCT_NOT_FOUND)

            BidHistoryResponse(
                auctionDto = auction,
                productDto = product,
                myMaxBidPrice = bid.bidPrice
            )
        }

        return SliceImpl(responseContent, pageable, bidPage.hasNext())
    }

    @Transactional(readOnly = true)
    fun findUserBidList(userId: Long, auctionId: Long, pageable: Pageable): Slice<BidResponse> {
        val bidSlice: Slice<Bid> =
            bidRepository.findSliceByAuctionIdAndBidderIdOrderByCreatedAtDesc(
                auctionId = auctionId,
                bidderId = userId,
                pageable = pageable
            )

        if (bidSlice.isEmpty) {
            return SliceImpl(emptyList(), pageable, false)
        }

        val userDto = userClient.userModuleDto(userId)

        return bidSlice.map { bid ->
            BidResponse(
                bidId = bid.bidId ?: throw CustomException(BidErrorCode.BID_NOT_FOUND, "입찰자의 ID를 찾을 수 없습니다."),
                auctionId = bid.auctionId,
                bidPrice = bid.bidPrice,
                bidTime = bid.createdAt ?: LocalDateTime.now(),
                status = checkNotNull(bid.status) { "입찰(ID: ${bid.bidId})의 상태값이 Null입니다." } ,
                nickname = userDto.userNickname
            )
        }
    }
}
