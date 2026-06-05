plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("kapt")
    `java-library`
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    kapt(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))

    // 1. Web & Security (서비스의 뼈대)
    api("org.springframework.boot:spring-boot-starter-web")           // Spring MVC 기반 API 개발
    api("org.springframework.boot:spring-boot-starter-security")     // 인증/인가 보안 기본 설정
    testImplementation("org.springframework.security:spring-security-test") // 보안 테스트

    // 2. Data & Persistence (데이터 처리 및 조회)
    api("org.springframework.boot:spring-boot-starter-data-jpa")     // JPA 데이터 접근 계층
    runtimeOnly("org.postgresql:postgresql")                          // PostgreSQL 드라이버
    api("com.querydsl:querydsl-jpa:5.0.0:jakarta")                    // QueryDSL (타입 세이프 쿼리)
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")                   // QueryDSL 코드 생성 도구

    // 3. Auth & JWT (인증 로직)
    api("io.jsonwebtoken:jjwt-api:0.12.3")                            // JWT 인터페이스
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")                   // JWT 구현체
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")                // JWT Jackson 연결

    // 4. JSON & Serialization (데이터 변환)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // Kotlin 객체 직렬화/역직렬화

    // 5. Build & Configuration (설정 및 어노테이션 처리)
    // Configuration Properties를 IDE가 인식하고 자동완성 도와주는 도구
    kapt("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.bootJar {
    enabled = false
}
tasks.jar {
    enabled = true
}

sourceSets {
    main {
        java {
            // kapt가 생성한 자바 파일(Q클래스)들이 있는 경로를 소스셋에 추가합니다.
            srcDirs("build/generated/source/kapt/main")
        }
    }
}