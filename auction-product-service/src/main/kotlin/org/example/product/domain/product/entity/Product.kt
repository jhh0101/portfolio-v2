package org.example.product.domain.product.entity

import auction.auctionproductapi.product.status.ProductStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.example.auction.domain.auction.entity.Auction
import org.example.common.global.base.BaseCreatedAt
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@SQLRestriction("status != 'DELETED'")
@SQLDelete(sql = "UPDATE products SET status = 'DELETED' WHERE product_id = ?")
@Table(
    name = "products",
    indexes = [Index(
        name = "idx_product_seller",
        columnList = "seller_id"
    ), Index(name = "idx_product_category", columnList = "category_id")]
)

class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    var productId: Long? = null,

    @Column(name = "seller_id", nullable = false)
    val sellerId: Long,

    @Column(name = "category_id")
    var categoryId: Long,

    @Column(name = "title")
    var title: String,

    @Column(name = "description")
    var description: String,

    @Column(name = "view_count")
    var viewCount: Int,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var productStatus: ProductStatus,

    @OneToOne(
        mappedBy = "product",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.REMOVE]
    )
    var auction: Auction?,

    @BatchSize(size = 50)
    @OneToMany(
        mappedBy = "product",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val image: MutableList<ProductImage> = mutableListOf()
) : BaseCreatedAt() {

    fun updateProduct(
        categoryId: Long,
        title: String,
        description: String,
        startPrice: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        this.categoryId = categoryId
        this.title = title
        this.description = description

        this.auction?.updateAuction(startPrice, startTime, endTime)
    }

    fun changeStatus(status: ProductStatus) {
        this.productStatus = status
    }
}
