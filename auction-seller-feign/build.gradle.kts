plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("java-library")
}

dependencies {
    implementation(project(":auction-common"))
    api(project(":auction-seller-api"))

    // Feign
    api("org.springframework.cloud:spring-cloud-starter-openfeign")
}

