package org.example.category.application.service.module

import auction.auctioncategoryapi.client.CategoryClient
import auction.auctioncategoryapi.dto.CategoryCommonResponse
import auction.auctioncategoryapi.error.CategoryErrorCode
import org.example.category.domain.categories.repository.CategoryRepository
import org.example.common.global.error.CustomException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CategoryCommonService(
    private val categoryRepository: CategoryRepository,
) : CategoryClient {
    override fun categoryModuleDto(categoryId: Long): CategoryCommonResponse {
        val category = categoryRepository.findByIdOrNull(categoryId)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        return CategoryCommonResponse(
            categoryId = category.categoryId ?: 0L,
            categoryName = category.category
        )
    }

    override fun categoryListModuleDto(categoryIds: List<Long>): List<CategoryCommonResponse> {
        val categorise = categoryRepository.findByCategoryIdIn(categoryIds)

        return categorise.map {category ->
            CategoryCommonResponse(
                categoryId = category.categoryId ?: 0L,
                categoryName = category.category
            )
        }
    }
}