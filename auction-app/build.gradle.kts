dependencies {
    implementation(project(":auction-common"))
    implementation(project(":auction-user-service"))
    implementation(project(":auction-seller-service"))
    implementation(project(":auction-auction-service"))
    implementation(project(":auction-product-service"))
    implementation(project(":auction-category-service"))
    implementation(project(":auction-rating-service"))
    implementation(project(":auction-order-service"))
    implementation(project(":auction-bid-service"))

    // 스프링 부트의 핵심 기능을 직접 추가
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // 기존에 있던 모듈 의존성들
    implementation(project(":auction-common"))
    implementation(project(":auction-user-service"))
}

tasks.bootJar {
    enabled = true
}