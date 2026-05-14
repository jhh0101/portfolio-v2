package org.example.bid.domain.bid.repository

import org.example.bid.domain.bid.dto.BidInfo
import org.example.bid.domain.bid.entity.Bid
import auction.auctionbidapi.status.BidStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BidRepository : JpaRepository<Bid, Long> {
    fun existsByStatusAndAuctionId(status: BidStatus, auctionId: Long): Boolean

    fun findTopByStatusAndAuctionIdOrderByBidIdDesc(status: BidStatus, auctionId: Long): Bid?

    fun findAllByStatusAndAuctionIdOrderByBidPriceDesc(
        status: BidStatus,
        auctionId: Long
    ): List<Bid>

    @Query(
        ("SELECT b " +
                "FROM Bid b " +
                "JOIN FETCH b.bidderId " +
                "JOIN b.auction a " +
                "JOIN a.product p " +
                "JOIN p.seller s " +
                "WHERE s.userId = :sellerId " +
                "AND b.bidPrice = (" +
                "SELECT MAX(b2.bidPrice) " +
                "FROM Bid b2 " +
                "WHERE b2.auctionId = b.auctionId " +
                "AND b2.status = 'ACTIVE') " +
                "AND a.status = 'PROCEEDING' " +
                "AND b.status = 'ACTIVE'")
    )
    fun findTopBidsPerAuctionBySellerId(@Param("sellerId") sellerId: Long): List<Bid>

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

    @Query(
        ("SELECT COUNT(b) " +
                "FROM Bid b " +
                "JOIN b.auction a " +
                "WHERE a.currentPrice = b.bidPrice " +
                "AND a.status = 'PROCEEDING' " +
                "AND b.bidder.userId = :bidderId " +
                "AND b.status = 'ACTIVE'")
    )
    fun bidCount(@Param("bidderId") bidderId: Long): Long

    fun findAllByAuctionId(auctionId: Long, pageable: Pageable): Page<Bid>

    @Query(
        value = ("SELECT b.bid_id AS bidId, " +
                "b.auction_id AS auctionId, " +
                "u.nickname AS nickname, " +
                "b.bid_price AS bidPrice, " +
                "b.bid_time AS bidTime, " +
                "b.status AS status " +
                "FROM bids b " +
                "LEFT JOIN users u ON b.bidder_id = u.user_id " +
                "WHERE b.auction_id = :auctionId " +
                "AND b.bidder_id = :userId"), nativeQuery = true
    )
    fun findAllByAuctionIdToAdmin(
        @Param("userId") userId: Long,
        @Param("auctionId") auctionId: Long,
        pageable: Pageable
    ): Slice<BidInfo>

    fun findPageByBidderId(bidderId: Long, pageable: Pageable): Page<Bid>
    fun findSliceByBidderId(bidderId: Long, pageable: Pageable): Slice<Bid>

    fun findSliceByAuctionIdAndBidderIdOrderByCreatedAtDesc(
        auctionId: Long,
        bidderId: Long,
        pageable: Pageable
    ): Slice<Bid>
}
