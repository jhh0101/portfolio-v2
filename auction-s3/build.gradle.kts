plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    // S3
    implementation(platform("software.amazon.awssdk:bom:2.20.0"))
    implementation("software.amazon.awssdk:s3")

    implementation(project(":auction-common"))
}

tasks.bootJar {
    enabled = false
}
tasks.jar {
    enabled = true
}