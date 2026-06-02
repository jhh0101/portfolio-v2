package org.example.product.controller.internal.command

import auction.auctionproductapi.auction.status.AuctionStatus
import auction.auctionproductapi.product.dto.ProductCommonResponse
import auction.auctionproductapi.product.dto.ProductDetailResponse
import auction.auctionproductapi.product.status.ProductStatus
import org.example.product.application.service.command.ProductUserCommandService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/user")
class InternalProductCommandController(
    private val productUserCommandService: ProductUserCommandService,
) {
    @DeleteMapping("/{id}/delete")
    fun deleteProductsBySuspendedUser(@PathVariable("id") userId: Long) {
        productUserCommandService.deleteProductsBySuspendedUser(userId)
    }
}