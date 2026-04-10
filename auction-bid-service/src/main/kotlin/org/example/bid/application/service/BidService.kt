package org.example.bid.application.service

import auction.auctionproductapi.auction.client.AuctionBidClient
import auction.auctionproductapi.auction.client.AuctionClient
import auction.auctionproductapi.auction.dto.AuctionCommonResponse
import auction.auctionproductapi.product.client.ProductClient
import auction.auctionproductapi.product.dto.ProductCommonResponse
import auction.auctionuserapi.user.client.UserClient
import auction.auctionuserapi.user.dto.UserCommonResponse
import org.example.bid.application.dto.BidRequest
import org.example.bid.application.dto.BidResultResponse
import org.example.bid.application.dto.toDto
import org.example.bid.domain.bid.dto.BidHistoryResponse
import org.example.bid.domain.bid.dto.BidInfoResponse
import org.example.bid.domain.bid.dto.toDto
import org.example.bid.domain.bid.entity.Bid
import auction.auctionbidapi.status.BidStatus
import org.example.bid.domain.bid.error.BidErrorCode
import org.example.bid.domain.bid.repository.BidQueryRepository
import org.example.bid.domain.bid.repository.BidRepository
import org.example.bid.domain.bid.service.BidProcessor
import org.example.common.global.error.CustomException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BidService(
    private val bidRepository: BidRepository,
    private val bidQueryRepository: BidQueryRepository,
    private val userCommonClient: UserClient,
    private val auctionClient: AuctionClient,
    private val auctionBidClient: AuctionBidClient,
    private val productClient: ProductClient,
    private val bidProcessor: BidProcessor,
) {

    @Transactional
    fun addBid(userId: Long, auctionId: Long, request: BidRequest): BidResultResponse {
        val userDto: UserCommonResponse = userCommonClient.userModuleDto(userId)

        val auctionDto: AuctionCommonResponse = auctionBidClient.auctionLockModuleDto(auctionId)

        val productDto = productClient.productModuleDto(auctionDto.productId)

        val lastBid: Bid? = bidRepository.findTopByStatusAndAuctionIdOrderByBidIdDesc(BidStatus.ACTIVE, auctionDto.auctionId)

        val bid = bidProcessor.addBid(
            userDto,
            auctionDto,
            productDto,
            request.bidPrice,
            lastBid
        )

        val bidSave: Bid = bidRepository.save(bid)

        return bidSave.toDto(productDto.title, userDto.userPoint)
    }

    @Transactional(readOnly = true)
    fun findBid(auctionId: Long, pageable: Pageable): Page<BidInfoResponse> {
        return bidRepository.findAllByAuctionId(auctionId, pageable).map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun findBidHistory(userId: Long, pageable: Pageable): Page<BidHistoryResponse> {
        val bidPage: Page<Bid> = bidRepository.findAllByBidderId(userId, pageable)

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

        return PageImpl(responseContent, pageable, bidPage.totalElements)
    }

    @Transactional
    fun cancelBid(userId: Long, bidId: Long, auctionId: Long): BidResultResponse {
        val auctionDto = auctionBidClient.auctionLockModuleDto(auctionId)
        val productDto = productClient.productModuleDto(auctionDto.productId)
        val userDto = userCommonClient.userModuleDto(userId)

        val bid: Bid = bidRepository.findByIdOrNull(bidId)
            ?: throw CustomException(BidErrorCode.BID_NOT_FOUND)

        val currentTopBid: Bid =
            bidRepository.findTopByStatusAndAuctionIdOrderByBidPriceDesc(BidStatus.ACTIVE, auctionDto.auctionId)
                ?: throw CustomException(
            BidErrorCode.BID_NOT_FOUND, "활성화된 입찰 내역이 없습니다.")

        val bidList: List<Bid> =
            bidRepository.findAllByStatusAndAuctionIdOrderByBidPriceDesc(BidStatus.ACTIVE, auctionDto.auctionId)

        bidProcessor.cancelBid(
            auctionDto,
            userId,
            bid,
            currentTopBid,
            bidList
        )

        return bid.toDto(productDto.title, userDto.userPoint)
    }
}
