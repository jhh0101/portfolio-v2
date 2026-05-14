plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("kapt")
    `java-library`
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.springframework.security:spring-security-test")
    runtimeOnly("org.postgresql:postgresql")

    api("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-web")
    api("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")

    kapt(platform("org.springframework.boot:spring-boot-dependencies:4.0.0"))
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:4.0.0"))
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Redis & Redisson
    implementation("org.redisson:redisson:3.42.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation(platform("software.amazon.awssdk:bom:2.20.0"))
    implementation("software.amazon.awssdk:s3")
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