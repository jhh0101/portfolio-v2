package auction.auctioncategoryapi.client

import auction.auctioncategoryapi.dto.CategoryCommonResponse

interface CategoryClient {
    fun categoryModuleDto(categoryId: Long) : CategoryCommonResponse
}