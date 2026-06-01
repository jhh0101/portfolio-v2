plugins {
    kotlin("jvm")
    id("java-library")
}

dependencies {
    // Redis & Redisson
    implementation("org.redisson:redisson:3.42.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation(project(":auction-common"))
}

tasks.bootJar {
    enabled = false
}
tasks.jar {
    enabled = true
}