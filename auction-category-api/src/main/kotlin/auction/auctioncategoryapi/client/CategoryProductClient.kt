package auction.auctioncategoryapi.client

import auction.auctioncategoryapi.dto.CategoryCommonResponse

interface CategoryProductClient {
    fun categoryDtoByPath(path: String?) : List<CategoryCommonResponse>
    fun categoryDtoByIds(categoryIds: List<Long>) : List<CategoryCommonResponse>
}