plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("java-library")
}

dependencies {
    implementation(project(":auction-common"))
    api(project(":auction-order-api"))

    // Feign
    api("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.1")
}

