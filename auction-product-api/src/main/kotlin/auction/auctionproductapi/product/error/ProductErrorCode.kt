package auction.auctionproductapi.product.error

import org.example.common.global.error.ErrorCode

enum class ProductErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    // Product
    PRODUCT_NOT_FOUND("PRODUCT001", "상품을 찾을 수 없습니다"),
    CANNOT_MODIFY_AFTER_BID("PRODUCT002", "입찰한 상품은 수정할 수 없습니다"),
    CANNOT_DELETE_AFTER_BID("PRODUCT003", "입찰한 상품은 삭제할 수 없습니다"),

    // Image
    IMAGE_NOT_FOUND("IMAGE001", "이미지를 찾을 수 없습니다"),
    IMAGE_IS_MAIN("IMAGE002", "이미 메인 이미지입니다"),
    IMAGE_UPLOAD_FAILED("IMAGE003", "이미지 업로드에 실패했습니다"),
    INVALID_IMAGE_FORMAT("IMAGE004", "지원하지 않는 이미지 형식입니다"),
}