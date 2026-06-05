package org.example.auction.category.feign

import auction.auctioncategoryapi.dto.CategoryCommonResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "auction-category-service", url = "http://localhost:8085", contextId = "categoryFeignClient")
interface CategoryFeignClient {

    @GetMapping("/internal/category/{id}")
    fun categoryModuleDto(@PathVariable("id") categoryId: Long) : CategoryCommonResponse

    @GetMapping("/internal/category/list")
    fun categoryListModuleDto(@RequestParam("categoryIds") categoryIds: List<Long>) : List<CategoryCommonResponse>

    @GetMapping("/internal/category/path")
    fun categoryDtoByPath(path: String?) : List<CategoryCommonResponse>
}