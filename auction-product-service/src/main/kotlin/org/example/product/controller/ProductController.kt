package org.example.product.controller

import auction.auctionuserapi.auth.annotation.LoginUser
import auction.auctionuserapi.auth.dto.DetailsUser
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.example.common.global.response.ApiResponse
import org.example.product.application.dto.ProductAndAuctionRequest
import org.example.product.application.dto.ProductDetailAndAuctionResponse
import org.example.product.application.dto.ProductDetailResult
import org.example.product.application.dto.ProductImageResponse
import org.example.product.application.service.ProductService
import org.example.product.domain.product.dto.ProductAndAuctionResponse
import org.example.product.domain.product.dto.ProductListCondition
import org.example.product.domain.product.dto.ProductResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/product")
class ProductController(
    private val productService: ProductService
) {

    // 상품 추가
    @PostMapping
    fun addProduct(
        @RequestBody request: @Valid ProductAndAuctionRequest,
        @LoginUser user: DetailsUser
    ): ResponseEntity<ApiResponse<ProductResponse>> {
        val response: ProductResponse =
            productService.addProduct(user.id, request.productRequest, request.auctionRequest)
        return ResponseEntity.ok(ApiResponse.success("상품 등록", response))
    }

    // 상품 전체 리스트 조회
    @GetMapping
    fun productList(
        condition: ProductListCondition,
        @PageableDefault(size = 9) pageable: Pageable
    ): ResponseEntity<ApiResponse<org.springframework.data.domain.Page<ProductAndAuctionResponse>>> {
        val responses: org.springframework.data.domain.Page<ProductAndAuctionResponse> =
            productService.productList(condition, pageable)
        return ResponseEntity.ok(ApiResponse.success("상품 리스트 조회", responses))
    }

    // 상품 상세 조회
    @GetMapping("/{productId}")
    fun findProductDetail(
        @PathVariable productId: Long,
        @CookieValue(name = "viewedProducts", defaultValue = "") viewedCookieValue: String,
        response: HttpServletResponse,
        @LoginUser userId: Long
    ): ResponseEntity<ApiResponse<ProductDetailResult>> {
        val result = productService.findProductDetail(productId, userId, viewedCookieValue)

        val cookie = Cookie("viewedProducts", result.newCookieValue).apply {
            maxAge = 60 * 60 * 24 // 쿠키 유지 시간 설정 (예: 24시간)
            path = "/"            // 쿠키가 적용될 범위
            // isHttpOnly = true  // 필요시 보안 설정 추가
        }
        response.addCookie(cookie)
        return ResponseEntity.ok(ApiResponse.success("상품 단건 조회", result))
    }

    // 판매자(자신) 상품 리스트 조회
    @GetMapping("/my-product")
    fun myProductList(
        @LoginUser user: DetailsUser,
        condition: ProductListCondition,
        @PageableDefault(size = 5) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<ProductAndAuctionResponse>>> {
        val responses: Page<ProductAndAuctionResponse> =
            productService.myProductList(user.id, condition, pageable)
        return ResponseEntity.ok(ApiResponse.success("나의 상품 리스트 조회", responses))
    }

    // 상품 상세 수정
    @PatchMapping("/{productId}")
    fun updateProduct(
        @LoginUser user: DetailsUser,
        @PathVariable productId: Long,
        @RequestBody request: @Valid ProductAndAuctionRequest
    ): ResponseEntity<ApiResponse<ProductDetailAndAuctionResponse>> {
        val response: ProductDetailAndAuctionResponse = productService.updateProductDetail(
            user.id,
            productId,
            request.productRequest,
            request.auctionRequest
        )
        return ResponseEntity.ok(ApiResponse.success("상품 상세 수정", response))
    }

    // 상품 삭제
    @DeleteMapping("/{productId}")
    fun deleteProduct(
        @LoginUser user: DetailsUser,
        @PathVariable productId: Long
    ): ResponseEntity<ApiResponse<Void>> {
        productService.deleteProduct(user.id, productId)
        return ResponseEntity.ok(ApiResponse.success("상품 & 이미지 삭제", null))
    }

    // 이미지 메서드
    @PostMapping(value = ["/{id}/images"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImages(
        @PathVariable("id") productId: Long,  // 경로에서 상품 ID 추출
        @RequestPart("files") files: MutableList<MultipartFile>
    ): ResponseEntity<ApiResponse<MutableList<ProductImageResponse>>> {
        val response: MutableList<ProductImageResponse> =
            productService.uploadImages(productId, files)
        return ResponseEntity.ok(ApiResponse.success("이미지 업로드", response))
    }

    @GetMapping("/{id}/images")
    fun loadImages(@PathVariable("id") productId: Long): ResponseEntity<ApiResponse<List<ProductImageResponse>>> {
        val responses: List<ProductImageResponse> = productService.loadImage(productId)
        return ResponseEntity.ok(ApiResponse.success("이미지 로드", responses))
    }

    @PatchMapping("/{id}/images")
    fun moveToMain(@PathVariable("id") id: Long): ResponseEntity<ApiResponse<Void>> {
        productService.moveToMain(id)
        return ResponseEntity.ok(ApiResponse.success("이미지 메인 변경", null))
    }

    @DeleteMapping("/{id}/image")
    fun deleteImage(@PathVariable("id") imageId: Long): ResponseEntity<ApiResponse<Void>> {
        productService.deleteImage(imageId)
        return ResponseEntity.ok(ApiResponse.success("이미지 삭제", null))
    }
}
