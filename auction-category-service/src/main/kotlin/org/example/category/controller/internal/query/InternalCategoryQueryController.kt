package org.example.category.controller.internal.query

import auction.auctioncategoryapi.dto.CategoryCommonResponse
import org.example.category.application.service.module.CategoryCommonService
import org.example.category.application.service.module.CategoryProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/category")
class InternalCategoryQueryController(
    private val categoryCommonService: CategoryCommonService,
    private val categoryProductService: CategoryProductService,
) {
    @GetMapping("/{id}")
    fun categoryModuleDto(@PathVariable("id") categoryId: Long) : CategoryCommonResponse {
        return categoryCommonService.categoryModuleDto(categoryId)
    }

    @GetMapping("/list")
    fun categoryListModuleDto(@RequestParam("categoryIds") categoryIds: List<Long>) : List<CategoryCommonResponse> {
        return categoryCommonService.categoryListModuleDto(categoryIds)
    }

    @GetMapping("/path")
    fun categoryDtoByPath(path: String?) : List<CategoryCommonResponse> {
        return categoryProductService.categoryDtoByPath(path)
    }

}