package org.example.rating.domain.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.rating.domain.entity.QRating.rating
import org.springframework.stereotype.Repository

@Repository
class RatingQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {

    fun avgRating(userId: Long): Double {
        return jpaQueryFactory
            .select(rating.score.avg())
            .from(rating)
            .where(rating.toUserId.eq(userId))
            .fetchOne() ?: 0.0
    }
}
