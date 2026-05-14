dependencies {
    implementation(project(":auction-common"))
    implementation(project(":auction-user-service"))
    implementation(project(":auction-seller-service"))
    implementation(project(":auction-product-service"))
    implementation(project(":auction-category-service"))
    implementation(project(":auction-rating-service"))
    implementation(project(":auction-order-service"))
    implementation(project(":auction-bid-service"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
}

tasks.bootJar {
    enabled = true
}