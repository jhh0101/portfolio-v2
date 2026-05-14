package org.example.rating.domain.repository

import org.example.rating.domain.entity.Rating
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RatingRepository : JpaRepository<Rating, Long> {
    fun existsByOrderId(orderId: Long): Boolean

    @EntityGraph(attributePaths = ["toUserId", "fromUserId", "orderId"])
    fun findAllByToUserId(toUserUserId: Long, pageable: Pageable): Page<Rating>

    fun findByOrderId(orderId: Long): Rating?
}
