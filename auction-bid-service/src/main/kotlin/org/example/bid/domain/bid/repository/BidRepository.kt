package org.example.bid.domain.bid.repository

import org.example.bid.domain.bid.dto.BidInfo
import org.example.bid.domain.bid.entity.Bid
import auction.auctionbidapi.status.BidStatus
import auction.auctionproductapi.auction.status.AuctionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BidRepository : JpaRepository<Bid, Long> {
    fun findTopByStatusAndAuctionIdOrderByBidIdDesc(status: BidStatus, auctionId: Long): Bid?

    fun findAllByStatusAndAuctionIdOrderByBidPriceDesc(
        status: BidStatus,
        auctionId: Long
    ): List<Bid>

    @Query("""
    SELECT b FROM Bid b
    WHERE b.auctionId IN :auctionIds
      AND b.status = 'ACTIVE'
      AND b.bidPrice = (
          SELECT MAX(b2.bidPrice)
          FROM Bid b2
          WHERE b2.auctionId = b.auctionId
            AND b2.status = 'ACTIVE'
      )
    """)
    fun findTopBidsByAuctionIds(@Param("auctionIds") auctionIds: List<Long>): List<Bid>

    @Query(
        ("SELECT b FROM Bid b " +
                "WHERE b.bidderId = :userId " +
                "AND b.bidId IN (" +
                "   SELECT MAX(b2.bidId) FROM Bid b2 " +
                "   WHERE b2.bidderId = :userId " +
                "   AND b2.status = 'ACTIVE' " +
                "   GROUP BY b2.auctionId" +
                ") " +
                "AND b.status = 'ACTIVE'")
    )
    fun findLatestBidsByUserId(@Param("userId") userId: Long?): List<Bid>

    fun findTopByStatusAndAuctionIdOrderByBidPriceDesc(status: BidStatus, auctionId: Long): Bid?

    @Query("""
        SELECT COUNT(b) 
        FROM Bid b 
        WHERE b.bidderId = :bidderId 
          AND b.auctionId IN :auctionIds 
          AND b.status = 'ACTIVE'
        """)
    fun bidCount(
        @Param("bidderId") bidderId: Long,
        @Param("auctionIds") auctionIds: List<Long>
    ): Long

    fun findAllByAuctionId(auctionId: Long, pageable: Pageable): Page<Bid>

    fun findPageByBidderId(bidderId: Long, pageable: Pageable): Page<Bid>
    fun findSliceByBidderId(bidderId: Long, pageable: Pageable): Slice<Bid>

    fun findSliceByAuctionIdAndBidderIdOrderByCreatedAtDesc(
        auctionId: Long,
        bidderId: Long,
        pageable: Pageable
    ): Slice<Bid>
}
