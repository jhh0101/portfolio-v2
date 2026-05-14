//package org.example.order.domain.order.repository
//
//import com.querydsl.jpa.impl.JPAQueryFactory
//import org.example.order.domain.auction.entity.QAuction.auction
//import org.example.order.domain.order.entity.Order
//import org.example.order.domain.order.entity.QOrder.order
//import org.example.order.domain.product.entity.QProduct.product
//import org.example.order.domain.user.entity.QUser.user
//import org.springframework.stereotype.Repository
//
//@Repository
//class OrderQueryRepository(
//    private val jpaQueryFactory: JPAQueryFactory
//) {
//
//    fun findOrderWithProductAndSeller(orderId: Long): Order? {
//        return jpaQueryFactory.selectFrom(order)
//                .innerJoin(order.auction, auction).fetchJoin()
//                .innerJoin(auction.product, product).fetchJoin()
//                .innerJoin(product.seller, user).fetchJoin()
//                .where(order.orderId.eq(orderId))
//                .fetchOne()
//
//    }
//}
