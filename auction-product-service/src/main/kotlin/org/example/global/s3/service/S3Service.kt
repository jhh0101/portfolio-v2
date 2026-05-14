package org.example.global.s3.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

@Service
class S3Service(
    private val s3Client: S3Client, @param:Value(
        "\${cloud.aws.s3.bucket}"
    ) private val bucket: String
) {
    fun uploadFile(file: MultipartFile, folderName: String): String {
        // 1. 파일명 중복 방지를 위한 UUID 생성
        val fileName = folderName + "/" + UUID.randomUUID() + "_" + file.originalFilename

        try {
            // 2. 업로드 요청 생성
            val putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(file.contentType)
                .build()

            // 3. S3로 파일 전송
            s3Client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(file.inputStream, file.size)
            )

            // 4. 저장된 파일의 공개 URL 반환
            return s3Client.utilities()
                .getUrl(Consumer { builder: GetUrlRequest.Builder -> builder.bucket(bucket).key(fileName) }).toString()
        } catch (e: IOException) {
            throw RuntimeException("파일 업로드 중 오류가 발생했습니다.", e)
        }
    }

    fun deleteFile(fileUrl: String) {
        val key = extractKeyFromUrl(fileUrl) // URL에서 S3 Key(폴더명+파일명)만 추출하는 메서드
        val decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8)
        try {
            val deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(decodedKey)
                .build()

            s3Client.deleteObject(deleteObjectRequest)
            println("S3 파일 삭제 완료: $decodedKey")
        } catch (e: Exception) {
            throw RuntimeException("S3 파일 삭제 중 오류가 발생했습니다.", e)
        }
    }

    fun deleteFiles(imageUrls: MutableList<String>) {
        if (imageUrls.isEmpty()) {
            return
        }

        val keys = imageUrls.stream()
            .map { url: String ->
                val key = extractKeyFromUrl(url)
                val decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8)
                ObjectIdentifier.builder().key(decodedKey).build()
            }
            .collect(Collectors.toList())

        val delete = Delete.builder()
            .objects(keys)
            .quiet(false)
            .build()

        val deleteObjectsRequest = DeleteObjectsRequest.builder()
            .bucket(bucket)
            .delete(delete)
            .build()

        try {
            val response = s3Client.deleteObjects(deleteObjectsRequest)

            println("S3 일괄 삭제 완료: " + response.deleted().size + "개 파일")

            if (!response.errors().isEmpty()) {
                response.errors()
                    .forEach(Consumer { error: S3Error -> System.err.println("삭제 실패: " + error.key() + " - " + error.message()) }
                    )
            }
        } catch (e: Exception) {
            throw RuntimeException("S3 파일 일괄 삭제 중 오류가 발생했습니다.", e)
        }
    }

    private fun extractKeyFromUrl(fileUrl: String): String {
        return fileUrl.substring(fileUrl.lastIndexOf(bucket) + bucket.length + 1)
    }
}
