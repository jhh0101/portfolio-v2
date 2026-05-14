package org.example.category.application.dto

data class CategoryRequest(
    val category: String,
    val parentId: Long?,
)

