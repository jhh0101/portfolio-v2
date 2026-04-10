plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":auction-common"))

    // interface
    implementation(project(":auction-user-api"))
    implementation(project(":auction-seller-api"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // 개발 편의성
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}