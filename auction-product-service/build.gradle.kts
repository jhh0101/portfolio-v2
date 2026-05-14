// auction-domain/build.gradle.kts
plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("kapt")
}

dependencies {
    implementation(project(":auction-common"))

    // interface
    implementation(project(":auction-product-api"))
    implementation(project(":auction-bid-api"))
    implementation(project(":auction-user-api"))
    implementation(project(":auction-order-api"))
    implementation(project(":auction-category-api"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Redis & Redisson
    implementation("org.redisson:redisson:3.42.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Querydsl
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")

    // 2. Kapt 설정
    kapt(platform("org.springframework.boot:spring-boot-dependencies:4.0.0"))

    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // S3
    implementation(platform("software.amazon.awssdk:bom:2.20.0"))
    implementation("software.amazon.awssdk:s3")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}
tasks.getByName<Jar>("jar") {
    enabled = true
}