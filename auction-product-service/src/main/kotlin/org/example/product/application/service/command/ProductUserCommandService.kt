package org.example.product.application.service.command

import auction.auctionproductapi.auction.error.AuctionErrorCode
import auction.auctionproductapi.product.command.ProductUserCommandClient
import org.example.common.global.error.CustomException
import org.example.global.s3.service.S3Service
import org.example.product.domain.product.entity.Product
import org.example.product.domain.product.repository.ProductRepository
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory

class ProductUserCommandService(
    private val productRepository: ProductRepository,
    private val redissonClient: RedissonClient,
    private val s3Service: S3Service,
) : ProductUserCommandClient {
    private val log = LoggerFactory.getLogger(javaClass)
    override fun deleteProductsBySuspendedUser(userId: Long) {
        val productList: List<Product> = productRepository.findAllBySellerId(userId)

        val allImageUrls: MutableList<String> = ArrayList()
        val auctionIds: MutableList<Long> = ArrayList()

        for (product in productList) {
            for (img in product.image) {
                allImageUrls.add(img.imageUrl)
            }
            auctionIds.add(product.auction?.auctionId ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND))
        }

        productRepository.deleteAllBySellerId(userId)

        val closingQueue = redissonClient.getScoredSortedSet<Long?>("auction:closing")
        closingQueue.removeAll(auctionIds)

        try {
            s3Service.deleteFiles(allImageUrls)
        } catch (e: Exception) {
            log.error("S3 이미지 삭제 중 오류 발생 (DB는 롤백됨): {}", e.message)
        }
    }

}