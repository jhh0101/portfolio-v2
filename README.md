# 🚀 AuctionMarket V2 (Modular Monolith)
> 단일 구조(Monolithic) 프로젝트의 결합도 한계를 개선하기 위해, Kotlin 기반 API-Service 구조의 멀티 모듈(Modular Monolith) 아키텍처로 리팩토링한 실시간 경매 서비스입니다.

---

## 🏗️ 아키텍처 및 모듈 구조 (Architecture & Modules)

도메인 간의 직접적인 결합을 제어하고, 향후 서비스 분리 가능성까지 유연하게 고려할 수 있도록 인터페이스와 비즈니스 로직 레이어를 격리한 멀티 모듈 형태로 설계되었습니다.

* **`auction-app`**
  * 프로젝트의 메인 실행 모듈 및 애플리케이션 런타임 환경 구성
* **`auction-common`**
  * 공통 인프라 환경 설정(Redis, Security, S3 등) 및 전역 예외 처리 레이어
* **`auction-[domain]-service`**
  * 도메인별 핵심 비즈니스 로직(Controller, Service, Processor), JPA 엔티티 및 Querydsl 쿼리(Repository) 레이어
* **`auction-[domain]-api`**
  * 모듈 간 직접 참조 및 순환 의존성을 방지하기 위한 클라이언트(Client) 인터페이스 및 DTO 레이어

---

## 🛠️ 기술 스택 (Tech Stacks)

* **Backend** : Kotlin 2.2.21, Spring Boot 4.0.0, Spring Security
* **Database** : PostgreSQL (Main DB), Redis / Redisson
* **Data Access** : Spring Data JPA, Querydsl 5.0.0
* **Infrastructure** : Docker, Docker Compose, Nginx (로컬 검증 환경)

---

## 🔥 핵심 트러블슈팅 및 아키텍처 고민 (Troubleshooting)

### 1. [아키텍처] Common 모듈 의존성 비대화 문제

* **문제 상황**
  * 모듈러 모놀리스 구조 특성상 공통 인프라 설정과 예외 처리 로직이 `auction-common` 모듈에 집중되었습니다.
  * 이 과정에서 특정 도메인 서비스가 굳이 사용하지 않는 의존성까지 함께 상속받게 되는 **Common 모듈 비대화(Fat Common Module)** 문제가 발생했습니다.
* **해결 및 개선 방향**
  * 도메인 간 직접 참조를 방어하기 위해 `[domain]-api` 인터페이스 모듈을 경유하도록 통신 구조를 분리했습니다.
  * 향후 도메인별 DB 스키마 격리 및 인프라 설정 이관을 통해 서비스 독립성을 높이는 방향을 고려하고 있습니다.

---

### 2. [아키텍처 및 성능] 멀티 모듈 환경에서의 Querydsl 빌드 안정화 및 N+1 방어 전략

* **문제 상황**
  * 기존 Java V1 프로젝트 시절부터 복잡한 검색 로직을 위해 **Querydsl**을 적극 활용해왔습니다.
  * V2 리팩토링 과정에서 Kotlin 기반 빌드 환경(Kapt)과의 호환성 문제로 QClass 자동 생성 에러를 경험했습니다. 또한, 도메인 모듈 격리에 따라 DB 조인(`Join`)이 제한되면서 분리된 모듈 간 데이터를 조합할 때 **N+1 조회 성능 저하 리스크**가 발생할 수 있음을 확인했습니다.
* **해결 방식**
  * **[Kapt 빌드 안정화]** : Gradle 스크립트에 `kapt` 플러그인과 `querydsl-apt` 설정을 정교하게 바인딩하여 빌드 파이프라인의 호환성을 확보했습니다.
  * **[모듈 내부 최적화]** : 단일 모듈 내 조인이 가능한 엔티티 검색(`Product-Auction`) 시 Querydsl에 `fetchJoin()`을 결합하여 단일 쿼리로 데이터를 즉시 적재했습니다.
  * **[모듈 간 데이터 조립 (App Layer Join)]** : 도메인이 물리적으로 분리된 모듈 간 데이터 조합 시에는 반복적인 단건 조회를 배제했습니다. 대상 ID 리스트를 추출해 `IN 절` 기반으로 벌크 조회 API를 통합 호출한 뒤, 어플리케이션 레이어에서 `Map(associateBy)` 구조로 가공하는 매칭 전략을 적용하여 성능을 안정적으로 방어했습니다.

---

### 3. [동시성 및 성능] Virtual Thread 환경에서의 데이터 정합성 처리

* **문제 상황**
  * 실시간 입찰 시스템의 처리량(Throughput) 향상을 위해 가상 스레드(`spring.threads.virtual.enabled=true`)를 적용했습니다.
  * 이 환경에서 Java 내장 락(`synchronized`) 사용 시 발생할 수 있는 Pinning 현상이 시스템 전체의 컨텍스트 스위칭 효율을 저하시킬 위험이 있음을 확인했습니다.
* **해결 방식**
  * 동시성이 필수적인 주요 비즈니스 로직에서 내장 락 사용을 배제하고, **Redis(Redisson) 기반 분산 락**을 적용하여 가상 스레드의 런타임 성능 이점을 유지하면서 정합성을 확보했습니다.
