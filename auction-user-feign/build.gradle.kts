plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":auction-common"))
    implementation(project(":auction-user-api"))

    // Feign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.1")
}

