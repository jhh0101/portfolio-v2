package org.example.bid.domain.bid.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.bid.application.dto.BidResponse
import org.example.bid.domain.bid.entity.QBid.bid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository

@Repository
class BidQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {
    fun findBidSliceByAuctionIdAndUserId(
        userId: Long,
        auctionId: Long,
        pageable: Pageable
    ): Slice<BidResponse> {

        val content = jpaQueryFactory
            .select(
                Projections.constructor(
                    BidResponse::class.java,
                    bid.bidId,
                    bid.auctionId,
                    bid.bidPrice,
                    bid.createdAt,
                    bid.status
                )
            )
            .from(bid)
            .where(
                bid.auctionId.eq(auctionId),
                bid.bidderId.eq(userId)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong() + 1) // Slice의 핵심: +1
            .fetch()
            .toMutableList()

        val hasNext = content.size > pageable.pageSize
        if (hasNext) content.removeAt(pageable.pageSize)

        return SliceImpl(content, pageable, hasNext)
    }
}
