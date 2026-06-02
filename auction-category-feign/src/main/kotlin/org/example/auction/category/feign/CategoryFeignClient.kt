package org.example.auction.category.feign

import auction.auctioncategoryapi.client.CategoryClient
import auction.auctioncategoryapi.client.CategoryProductClient
import auction.auctioncategoryapi.dto.CategoryCommonResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "auction-category-service", url = "http://localhost:8085")
@RequestMapping("/internal/category")
interface CategoryFeignClient : CategoryClient, CategoryProductClient {

    @GetMapping("/{id}")
    override fun categoryModuleDto(@PathVariable("id") categoryId: Long) : CategoryCommonResponse

    @GetMapping("/list")
    override fun categoryListModuleDto(@RequestParam("categoryIds") categoryIds: List<Long>) : List<CategoryCommonResponse>

    @GetMapping("/path")
    override fun categoryDtoByPath(path: String?) : List<CategoryCommonResponse>
}