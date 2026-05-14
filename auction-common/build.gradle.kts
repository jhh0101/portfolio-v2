plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("kapt")
    `java-library`
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
    api("org.springframework.boot:spring-boot-starter-web")

    api("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
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