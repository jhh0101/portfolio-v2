package org.example.category.domain.categories.repository

import auction.auctioncategoryapi.dto.CategoryCommonResponse
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import org.example.category.domain.categories.entity.QCategory.category1

@Repository
class CategoryQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {
    fun categoryDto(path: String?): List<CategoryCommonResponse> {
        if (path.isNullOrBlank()) return emptyList()
        // path == "1" 이거나 path가 "1/" 로 시작하는 것들 모두 검색
        return jpaQueryFactory.select(
            Projections.constructor(
                CategoryCommonResponse::class.java,
                category1.categoryId,
                category1.category
            )
        )
            .from(category1)
            .where(
                category1.path.eq(path).or(category1.path.startsWith("$path/"))
            )
            .fetch()
    }
}