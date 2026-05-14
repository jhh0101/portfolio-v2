package org.example.category.application.service

import auction.auctioncategoryapi.error.CategoryErrorCode
import auction.auctionproductapi.product.client.ProductCategoryClient
import org.example.category.application.dto.CategoryRequest
import org.example.category.application.dto.CategoryResponse
import org.example.category.application.dto.toDto
import org.example.category.domain.categories.entity.Category
import org.example.category.domain.categories.repository.CategoryRepository
import org.example.common.global.error.CustomException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.List
import kotlin.collections.map
import kotlin.plus

@Service
@Transactional
class CategoryService(
    val categoryRepository: CategoryRepository,
    val productCategoryClient: ProductCategoryClient,
) {
    fun addCategory(request: CategoryRequest): CategoryResponse {

        val parent = request.parentId?.let {
            categoryRepository.findByIdOrNull(request.parentId)
                ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)
        }

        val categorySave = Category(
            category = request.category,
            parent = parent
        )

        val category: Category = categoryRepository.save(categorySave)

        category.path = parent?.let { "${it.path}/${category.categoryId}" } ?: "${category.categoryId}"
        parent?.addChildrenCategory(category)

        return category.toDto()
    }

    @Transactional(readOnly = true)
    fun searchCategory(parentId: Long?): List<CategoryResponse> {
        val categories = if (parentId == null) {
            categoryRepository.findByParentIsNull()
        } else {
            categoryRepository.findByParent_CategoryId(parentId)
        }

        return categories.map { it.toDto() }
    }

    fun updateCategory(id: Long, request: CategoryRequest): CategoryResponse {
        val category: Category = categoryRepository.findByIdOrNull(id)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        val oldPath = category.path

        category.category = request.category

        val parent = request.parentId?.let {
            categoryRepository.findByIdOrNull(it)
                ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)
        }

        category.parent = parent

        val newPath = parent?.let { "${it.path}/${category.categoryId}" } ?: "${category.categoryId}"
        category.path = newPath

        categoryRepository.updatePathPrefix("$oldPath/", "$newPath/")

        return category.toDto()
    }

    fun deleteCategory(id: Long) {
        val category: Category = categoryRepository.findByIdOrNull(id)
            ?: throw CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        if (!category.children.isEmpty()) {
            throw CustomException(CategoryErrorCode.CATEGORY_HAS_CHILDREN)
        }

        val existsProduct = productCategoryClient.existsByCategoryId(id)

        if (existsProduct) {
            throw CustomException(CategoryErrorCode.CATEGORY_HAS_PRODUCT)
        }

        categoryRepository.delete(category)
    }
}
