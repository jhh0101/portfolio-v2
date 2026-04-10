package org.example.seller.domain.seller.repository

import auction.auctionsellerapi.status.SellerStatus
import org.example.seller.domain.seller.entity.Seller
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SellerRepository : JpaRepository<Seller, Long> {
    fun findByUserId(userId: Long): Optional<Seller>

    fun findAllByStatus(status: SellerStatus, pageable: Pageable): Page<Seller>

    @Query("SELECT s.status FROM Seller s WHERE s.userId = :userId")
    fun findStatusByUserId(userId: Long): SellerStatus?
}
