package org.example.seller.domain.seller.entity

import auction.auctionsellerapi.status.SellerStatus
import auction.auctionuserapi.user.type.Role
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.seller.application.dto.SellerApplyRequest
import org.example.seller.global.base.BaseCreatedAt

@Entity
@Table(name = "sellers")
@AttributeOverride(name = "createdAt", column = Column(name = "applied_at", updatable = false))
class Seller(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id")
    var sellerId: Long? = null,

    @Column(name = "user_id", unique = true, nullable = false)
    var userId: Long,

    @Column(name = "store_name", length = 50)
    var storeName: String? = null,

    @Column(name = "bank_name")
    var bankName: String? = null,

    @Column(name = "account_number", length = 20)
    var accountNumber: String? = null,

    @Column(name = "account_holder")
    var accountHolder: String? = null,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: SellerStatus? = null,

    @Column(name = "reject_reason")
    var rejectReason: String? = null

) : BaseCreatedAt() {

    fun updateApply(request: SellerApplyRequest) {
        this.storeName = request.storeName
        this.bankName = request.bankName
        this.accountNumber = request.accountNumber
        this.accountHolder = request.accountHolder
        this.status = SellerStatus.PENDING
        this.rejectReason = null
    }

    fun cancelSeller() {
        this.storeName = ""
        this.bankName = ""
        this.accountNumber = ""
        this.accountHolder = ""
        this.status = SellerStatus.CANCELED
        this.rejectReason = "사용자에 의한 신청 취소"
    }

    fun approveSeller() {
        this.status = SellerStatus.APPROVED
        this.rejectReason = null
    }

    fun rejectSeller(rejectReason: String?) {
        this.status = SellerStatus.REJECTED
        this.rejectReason = rejectReason
    }
}
