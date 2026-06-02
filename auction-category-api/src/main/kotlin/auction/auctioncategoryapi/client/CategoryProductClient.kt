package auction.auctioncategoryapi.client

import auction.auctioncategoryapi.dto.CategoryCommonResponse

interface CategoryProductClient {
    fun categoryDtoByPath(path: String?) : List<CategoryCommonResponse>
}