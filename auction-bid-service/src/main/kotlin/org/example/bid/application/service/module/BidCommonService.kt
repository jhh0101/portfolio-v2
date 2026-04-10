package org.example.bid.application.service.module

import org.example.bid.domain.bid.repository.BidRepository
import org.springframework.stereotype.Service

@Service
class BidCommonService(
    private val bidRepository: BidRepository,
) {
    fun bidCount(userId: Long) : Long {
        return bidRepository.bidCount(userId)
    }
}