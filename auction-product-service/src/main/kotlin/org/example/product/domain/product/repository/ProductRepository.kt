package org.example.product.domain.product.repository

import auction.auctionproductapi.auction.status.AuctionStatus
import jakarta.persistence.QueryHint
import org.example.product.domain.product.entity.Product
import auction.auctionproductapi.product.status.ProductStatus
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.stream.Stream

@Repository
interface ProductRepository : JpaRepository<Product, Long> {

    fun existsByCategoryId(categoryId: Long) : Boolean

    @Query(
        ("SELECT p FROM Product p " +
                "JOIN FETCH p.auction a " +
                "WHERE p.productId = :productId")
    )
    fun findWithAuctionByProductId(@Param("productId") productId: Long): Product?

    @Modifying
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.productId = :productId")
    fun viewCount(@Param("productId") productId: Long): Int

    @Query(
        ("SELECT COUNT(p) " +
                "FROM Product p " +
                "JOIN p.auction a " +
                "WHERE a.status = 'PROCEEDING' " +
                "AND p.sellerId = :sellerId")
    )
    fun productCount(@Param("sellerId") sellerId: Long): Long

    @EntityGraph(attributePaths = ["auction"])
    fun findAllBySellerId(userId: Long): List<Product>

    fun deleteAllBySellerId(userId: Long)

    fun findAuctionIdsBySellerIdAndAuctionStatusAndProductStatus(sellerId: Long, auctionStatus: AuctionStatus, productStatus: ProductStatus) : List<Long>

//    @QueryHints(value = [
//            QueryHint(name = "org.hibernate.fetchSize", value = "100"),
//            QueryHint(name = "org.hibernate.readOnly", value = "true")
//    ])
//    @Query(
//        ("SELECT p FROM Product p " +
//                "JOIN FETCH p.auction a " +
//                "WHERE p.productStatus = :status")
//    )
//    fun findAllByProductStatus(@Param("status") status: ProductStatus): Stream<Product>
//
//    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @Query(
//        ("UPDATE Product p SET p.productStatus = :status " +
//                "WHERE p.productStatus = :activeStatus " +
//                "AND p.auction.status = :auctionStatus AND p.auction.endTime <= :now " +
//                "AND EXISTS (SELECT 1 FROM Bid b WHERE b.auction.auctionId = p.auction.auctionId)")
//    )
//    fun updateProductStatusSold(
//        @Param("now") now: LocalDateTime,
//        @Param("status") status: ProductStatus,
//        @Param("activeStatus") activeStatus: ProductStatus,
//        @Param("auctionStatus") auctionStatus: AuctionStatus
//    )
//
//    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @Query(
//        ("UPDATE Product p SET p.productStatus = :status " +
//                "WHERE p.productStatus = :activeStatus " +
//                "AND p.auction.status = :auctionStatus AND p.auction.endTime <= :now " +
//                "AND NOT EXISTS (SELECT 1 FROM Bid b WHERE b.auction.auctionId = p.auction.auctionId)")
//    )
//    fun updateProductStatusFailed(
//        @Param("now") now: LocalDateTime,
//        @Param("status") status: ProductStatus,
//        @Param("activeStatus") activeStatus: ProductStatus,
//        @Param("auctionStatus") auctionStatus: AuctionStatus
//    )

    @EntityGraph(attributePaths = ["auction"])
    fun findAllByProductIdIn(productIds: List<Long>): List<Product>

    @EntityGraph(attributePaths = ["auction", "images"])
    fun findWithDetailsByProductId(productId: Long): Product?
}
