plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
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

    // redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // 개발 편의성
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}