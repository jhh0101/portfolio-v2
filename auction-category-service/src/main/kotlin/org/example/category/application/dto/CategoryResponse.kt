package org.example.category.application.dto

import org.example.category.domain.categories.entity.Category

data class CategoryResponse(
    val categoryId: Long,
    val path: String?,
    val category: String,
    val parentId: Long?,
    val children: List<CategoryResponse>,
)

fun Category.toDto() : CategoryResponse {
    return CategoryResponse(
        categoryId = this.categoryId ?: 0L,
        path = this.path,
        category = this.category,
        parentId = this.parent?.categoryId,
        children = this.children.mapNotNull { it?.toDto() }
    )
}