plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("kapt")
}

dependencies {
    implementation(project(":auction-common"))

    // interface
    implementation(project(":auction-user-api"))
    implementation(project(":auction-product-api"))
    implementation(project(":auction-seller-api"))
    implementation(project(":auction-bid-api"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Redis
    implementation("org.redisson:redisson:3.42.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // --- Querydsl 필수 설정 ---
    // 1. 라이브러리 (jakarta 버전 사용)
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")

    // 2. Kapt 설정
    kapt(platform("org.springframework.boot:spring-boot-dependencies:4.0.0"))

    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // 개발 편의성
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}