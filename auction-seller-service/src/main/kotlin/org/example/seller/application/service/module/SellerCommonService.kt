package org.example.seller.application.service.module

import auction.auctionsellerapi.client.SellerClient
import auction.auctionsellerapi.status.SellerStatus
import org.example.seller.domain.seller.repository.SellerRepository
import org.springframework.stereotype.Service

@Service
class SellerCommonService(
    private val sellerRepository: SellerRepository
) : SellerClient {
    override fun getSellerStatus(userId: Long): SellerStatus {
        return sellerRepository.findStatusByUserId(userId)
            ?: SellerStatus.NONE
    }
}