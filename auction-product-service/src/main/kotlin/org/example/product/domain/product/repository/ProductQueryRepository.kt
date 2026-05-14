package org.example.product.domain.product.repository

import auction.auctionproductapi.auction.status.AuctionStatus
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.product.domain.product.dto.ProductListCondition
import org.example.product.domain.product.entity.Product
import auction.auctionproductapi.product.status.ProductStatus
import org.example.product.domain.product.entity.QProduct.product
import org.example.auction.domain.auction.entity.QAuction.auction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.util.function.LongSupplier

@Repository
class ProductQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {

    fun productList(
        userId: Long?,
        condition: ProductListCondition,
        targetCategoryIds: List<Long>?,
        pageable: Pageable
    ): Page<Product> {
        val content = jpaQueryFactory
            .selectFrom(product)
            .innerJoin(product.auction, auction).fetchJoin()
            .where(
                isMyAuction(userId),
                titleContain(condition.title),
                categoryIdIn(targetCategoryIds),
                statusFilter(userId),
                auction.status.eq(AuctionStatus.PROCEEDING),
                product.productStatus.eq(ProductStatus.ACTIVE)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(orderSpecifier(condition.sort))
            .fetch()

        val count: JPAQuery<Long> = jpaQueryFactory
            .select(product.count())
            .from(product)
            .innerJoin(product.auction, auction)
            .where(
                isMyAuction(userId),
                titleContain(condition.title),
                categoryIdIn(targetCategoryIds),
                statusFilter(userId),
                auction.status.eq(AuctionStatus.PROCEEDING),
                product.productStatus.eq(ProductStatus.ACTIVE)
            )

        return PageableExecutionUtils.getPage(content, pageable) { count.fetchOne() ?: 0L }
    }

    fun adminProductList(
        userId: Long?,
        condition: ProductListCondition,
        targetCategoryIds: List<Long>?,
        pageable: Pageable
    ): Slice<Product> {
        val pageSize = pageable.pageSize

        val content = jpaQueryFactory
            .selectFrom(product)
            .innerJoin(product.auction, auction).fetchJoin()
            .where(
                isMyAuction(userId),
                titleContain(condition.title),
                categoryIdIn(targetCategoryIds),
                statusFilter(userId)
            )
            .offset(pageable.offset)
            .limit(pageSize.toLong() + 1)
            .orderBy(orderSpecifier(condition.sort))
            .fetch()

        var hasNext = false
        if (content.size > pageSize) {
            content.removeAt(pageSize)
            hasNext = true
        }

        return SliceImpl(content, pageable, hasNext)
    }

    private fun isMyAuction(userId: Long?): BooleanExpression? {
        return userId.let { product.sellerId.eq(it) }
    }

    private fun titleContain(title: String?): BooleanExpression? {
        return if (!title.isNullOrBlank()) product.title.contains(title) else null
    }

    private fun categoryIdIn(targetCategoryIds: List<Long>?): BooleanExpression? {
        return if (!targetCategoryIds.isNullOrEmpty()) {
            product.categoryId.`in`(targetCategoryIds)
        } else null
    }

    private fun statusFilter(userId: Long?): BooleanExpression? {
        return if (userId != null) {
            product.productStatus.`in`(ProductStatus.ACTIVE, ProductStatus.SOLD, ProductStatus.FAILED)
        } else null
    }

    private fun orderSpecifier(sort: String?): OrderSpecifier<*> {
        return when (sort) {
            "endingSoon" -> product.auction.endTime.asc()
            "priceHigh" -> product.auction.currentPrice.desc()
            "priceLow" -> product.auction.currentPrice.asc()
            else -> product.productId.desc()
        }
    }
}
