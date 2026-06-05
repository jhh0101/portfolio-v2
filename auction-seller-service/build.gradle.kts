dependencies {
    implementation(project(":auction-common"))

    // interface
    implementation(project(":auction-seller-api"))

    // feign
    implementation(project(":auction-user-feign"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // 개발 편의성
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("kapt")
}

dependencies {
    implementation(project(":auction-common"))

    // interface
    implementation(project(":auction-rating-api"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // feign
    implementation(project(":auction-user-feign"))

    // 2. Kapt 설정
    kapt(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}
tasks.getByName<Jar>("jar") {
    enabled = true
}