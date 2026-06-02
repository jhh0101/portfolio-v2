package org.example.category.application.service.module

import auction.auctioncategoryapi.client.CategoryProductClient
import auction.auctioncategoryapi.dto.CategoryCommonResponse
import org.example.category.domain.categories.repository.CategoryQueryRepository
import org.example.category.domain.categories.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryProductService(
    private val categoryQueryRepository: CategoryQueryRepository,
) : CategoryProductClient {
    override fun categoryDtoByPath(path: String?) : List<CategoryCommonResponse> {
        return categoryQueryRepository.categoryDto(path)
    }
}