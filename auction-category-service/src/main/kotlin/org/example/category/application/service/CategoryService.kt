package org.example.category.application.service

import org.example.category.application.dto.CategoryRequest
import org.example.category.application.dto.CategoryResponse
import org.example.category.application.dto.toDto
import org.example.category.domain.categories.entity.Category
import org.example.category.domain.categories.repository.CategoryRepository
import org.example.common.global.error.CustomException
import org.example.common.global.error.GlobalErrorCode
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.String
import java.util.stream.Collectors
import kotlin.Any
import kotlin.Long
import kotlin.collections.MutableList
import kotlin.collections.isEmpty
import kotlin.collections.plus
import kotlin.plus
import kotlin.sequences.plus
import kotlin.text.isEmpty
import kotlin.text.plus

@Service
@Transactional
class CategoryService(
    val categoryRepository: CategoryRepository,
    val productRepository: ProductRepository,
) {
    fun addCategory(request: CategoryRequest): CategoryResponse {

        val parent = categoryRepository.findByIdOrNull(request.parentId)
            ?: throw CustomException(GlobalErrorCode.CATEGORY_NOT_FOUND)

        val categorySave = Category(
            category = request.category,
            parent = parent
        )

        val category: Category = categoryRepository.save(categorySave)

        val parentPath = parent.path ?: ""
        category.path = "$parentPath/${category.categoryId}"

        parent.addChildrenCategory(category)

        return category.toDto()
    }

    @Transactional(readOnly = true)
    fun searchCategory(parentId: Long?): MutableList<CategoryResponse?> {
        val categories: MutableList<Category?>
        if (parentId == null) {
            categories = categoryRepository.findByParentIsNull()
        } else {
            categories = categoryRepository.findByParent_CategoryId(parentId)
        }
        return categories.stream()
            .map<Any?>(CategoryResponse::from)
            .collect(Collectors.toList())
    }

    fun updateCategory(id: Long?, request: CategoryRequest): CategoryResponse {
        val category: Category = categoryRepository.findById(id)
            .orElseThrow({ CustomException(ErrorCode.CATEGORY_NOT_FOUND) })

        val oldPath: kotlin.String? = category.getPath()
        val newPath: kotlin.String?

        category.setCategory(request.getCategory())


        if (request.getParentId() != null) {
            val parent: Category = categoryRepository.findById(request.getParentId())
                .orElseThrow({ CustomException(ErrorCode.CATEGORY_NOT_FOUND) })
            category.setParent(parent)
            newPath = parent.getPath() + "/" + category.getCategoryId()
        } else {
            category.setParent(null)
            newPath = String.valueOf(category.getCategoryId())
        }
        category.setPath(newPath)

        categoryRepository.updatePathPrefix(oldPath + "/", newPath + "/")

        category.setCategory(request.getCategory())
        return CategoryResponse.from(category)
    }

    fun deleteCategory(id: Long?) {
        val category: Category = categoryRepository.findById(id)
            .orElseThrow({ CustomException(ErrorCode.CATEGORY_NOT_FOUND) })

        if (!category.getChildren().isEmpty()) {
            throw CustomException(ErrorCode.CATEGORY_HAS_CHILDREN)
        }

        // 연결 상품 존재 여부 확인 로직
        val product: Product? = productRepository.findByCategory_CategoryId(id)

        if (product != null) {
            throw CustomException(ErrorCode.CATEGORY_HAS_PRODUCT)
        }

        categoryRepository.delete(category)
    }
}
