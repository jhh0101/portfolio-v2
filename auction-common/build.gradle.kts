dependencies {

    // 하위 모듈이니까 최상위에 없는 '특정 기술'만 추가합니다!
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j") // DB 드라이버 등
}

// 🚨 핵심: 부품 모듈이므로 실행 가능한 bootJar 생성을 막고, 일반 jar로 만듭니다.
tasks.bootJar {
    enabled = false
}
tasks.jar {
    enabled = true
}