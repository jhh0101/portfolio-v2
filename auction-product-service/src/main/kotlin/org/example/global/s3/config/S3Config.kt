package org.example.global.s3.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI

@Configuration
class S3Config(
    @Value($$"${cloud.aws.s3.endpoint}")
    private val endpoint: String,

    @Value($$"${cloud.aws.credentials.access-key}")
    private val accessKey: String,

    @Value($$"${cloud.aws.credentials.secret-key}")
    private val secretKey: String,

    @Value($$"${cloud.aws.region.static}")
    private val region: String,
) {

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.of(region)) // OCI Object Storage의 S3 호환 엔드포인트를 설정합니다.
            .endpointOverride(URI.create(endpoint))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            ) // Oracle Cloud 환경에서는 Path Style 접근 방식을 활성화해야 안정적입니다.
            .serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build()
            )
            .build()
    }
}
