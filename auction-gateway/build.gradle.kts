plugins {
    id("org.springframework.boot")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("io.spring.dependency-management")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.1.1.1")
    }
}

dependencies {
    implementation(project(":auction-common")) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
    }

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")
}