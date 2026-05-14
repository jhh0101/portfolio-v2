package org.example.category.controller

import jakarta.validation.Valid
import org.example.category.application.dto.CategoryRequest
import org.example.category.application.dto.CategoryResponse
import org.example.category.application.service.CategoryService
import org.example.common.global.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/category")
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    fun addCategory(@RequestBody request: @Valid CategoryRequest): ResponseEntity<ApiResponse<CategoryResponse>> {
        val response: CategoryResponse = categoryService.addCategory(request)
        return ResponseEntity.ok(ApiResponse.success("카테고리 생성", response))
    }

    @GetMapping
    fun searchCategory(@RequestParam(required = false) parentId: Long): ResponseEntity<ApiResponse<List<CategoryResponse>>> {
        val response: List<CategoryResponse> = categoryService.searchCategory(parentId)
        return ResponseEntity.ok(ApiResponse.success("카테고리 조회", response))
    }

    @PatchMapping("/{id}")
    fun updateCategory(
        @PathVariable id: Long,
        @RequestBody request: CategoryRequest
    ): ResponseEntity<ApiResponse<CategoryResponse>> {
        val response: CategoryResponse = categoryService.updateCategory(id, request)
        return ResponseEntity.ok(ApiResponse.success("카테고리 수정", response))
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): ResponseEntity<ApiResponse<Void>> {
        categoryService.deleteCategory(id)
        return ResponseEntity.ok(ApiResponse.success("카테고리 삭제", null))
    }
}
