package org.example.product.application.dto

import jakarta.validation.Valid
import org.example.auction.application.dto.AuctionRequest

class ProductAndAuctionRequest(
    @field:Valid
    val productRequest: ProductRequest,
    @field:Valid
    val auctionRequest: AuctionRequest,
) {

}
