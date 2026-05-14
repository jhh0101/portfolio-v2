package org.example.category.application.service.module

import auction.auctioncategoryapi.client.CategoryProductClient
import auction.auctioncategoryapi.dto.CategoryCommonResponse
import org.example.category.domain.categories.repository.CategoryQueryRepository
import org.example.category.domain.categories.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryProductService(
    private val categoryQueryRepository: CategoryQueryRepository,
    private val categoryRepository: CategoryRepository,
) : CategoryProductClient {
    override fun categoryDtoByPath(path: String?) : List<CategoryCommonResponse> {
        return categoryQueryRepository.categoryDto(path)
    }

    override fun categoryDtoByIds(categoryIds: List<Long>): List<CategoryCommonResponse> {
        if (categoryIds.isEmpty()) {
            return emptyList()
        }
        val categories = categoryRepository.findByCategoryIdIn(categoryIds)
        return categories.map { categories ->
            CategoryCommonResponse(
                categoryId = categories.categoryId ?: 0L,
                categoryName = categories.category
            )
        }
    }
}