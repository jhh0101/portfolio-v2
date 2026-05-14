package auction.auctioncategoryapi.error

import org.example.common.global.error.ErrorCode

enum class CategoryErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    CATEGORY_NOT_FOUND("CATEGORY001", "카테고리를 찾을 수 없습니다"),
    CATEGORY_HAS_CHILDREN("CATEGORY002", "하위 카테고리가 존재합니다"),
    CATEGORY_HAS_PRODUCT("CATEGORY003", "등록된 상품이 존재합니다"),
}