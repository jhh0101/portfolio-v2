package org.example.product.application.service.module

import auction.auctionproductapi.auction.status.AuctionStatus
import auction.auctionproductapi.product.client.ProductBidClient
import auction.auctionproductapi.product.status.ProductStatus
import org.example.product.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service
import kotlin.Long

@Service
class ProductBidService(
    private val productRepository: ProductRepository,
) : ProductBidClient {

    override fun findAuctionIdsBySellerId(
        userId: Long,
        auctionStatus: AuctionStatus,
        productStatus: ProductStatus
    ): List<Long> {
        return productRepository.findAuctionIdsBySellerIdAndAuctionStatusAndProductStatus(userId,auctionStatus,productStatus)
    }

}