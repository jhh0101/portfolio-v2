# 🚀 Architecture (MSA Transition Project)

---

## 💡 One Line

"MSA 구조를 **학습**하기 위해 모놀리스 기반에서 서비스 분리를 적용해보며 설계와 한계를 실험한 프로젝트입니다."

---

## 🧭 Overview

본 프로젝트는 초기 단일(Monolithic) 구조의 강한 결합도를 개선하고, 향후 서비스 독립 배포 및 완전한 MSA 전환을 유연하게 처리할 수 있도록 설계된 **멀티 모듈(Modular Monolith) 기반의 분산 아키텍처**입니다. 

> ※ 본 프로젝트는 완전한 독립 배포 MSA가 아닌, 모듈러 모놀리스에서 서비스 분리 구조로 점진적으로 전환 중인 하이브리드 아키텍처입니다.

---

## 🏗️ Domain Structure (7대 핵심 도메인 분리)

도메인별로 데이터베이스 스키마와 비즈니스 로직을 철저히 격리하여 모듈 간 독립성을 확보했습니다.

**API Gateway**
 └── 모든 클라이언트 요청의 단일 진입점 (인증 / 라우팅 처리)

**User Domain**
 └── 사용자 정보 및 포인트 관리 모듈

**Seller Domain**
 └── 판매자 자격 신청 및 상태 관리 모듈

**Product & Auction Domain**
 └── 상품 정보 및 다중 이미지(S3) 관리 모듈
 └── 실시간 경매 상태 및 마감 스케줄링 관리 모듈

**Bid Domain**
 └── 실시간 입찰 등록/취소 및 최고가 관리 모듈

**Order Domain**
 └── 경매 결과 기반 최종 주문 내역 생성 및 조회 모듈

**Category & Rating Domain**
 └── 카테고리 계층 관리 및 판매자 평점 부여 모듈

---

## 🔗 Service Communication

**Feign Client 기반 동기 HTTP 통신 구조**

- 서비스 간 직접 DB 접근 없음 (독립 스키마 유지)
- API(인터페이스) 기반 도메인 분리로 모듈 간 순환 참조 방지
- 초기 개발 속도와 구조 단순성 확보

---

## 🚀 Improvement Direction (설계적 한계와 개선 방향)

- **데이터 정합성 보장**
  - **현재 구조:** Feign 기반 동기 호출 + 로컬 트랜잭션 중심 처리
  - **문제 인식:** 타 서비스 호출 후 실패 시 부분 성공/실패로 인한 데이터 불일치(예: 포인트 증발) 발생 가능
  - **개선 방향:**
    - 보상 트랜잭션 기반 명시적 Rollback API(취소 호출) 설계 및 적용
    - SAGA 패턴 적용 시 Choreography 방식으로 확장 가능하도록 설계 여지 확보
    - 향후 Kafka 기반 이벤트 흐름(Event-Driven)으로 전환할 수 있는 구조 고려

- **Service-to-Service 인증 구조**
  - **현재 구조:** 내부 API(`/internal`) 통신 시 서비스 간 인증 경계 부족 문제 인식
  - **개선 방향:**
    - Feign `RequestInterceptor` 기반 내부 전용 헤더(Secret) 주입 적용 가능
    - Gateway 기반 외부 인증과 내부 서비스 간 인증을 분리하는 구조 고려

- **환경 결합도 해제 및 배포 확장성**
  - **현재 구조:** Feign Client URL(`localhost`) 기반 정적 통신 구조
  - **문제 인식:** 환경 변경(로컬 vs Docker/Cloud) 시 설정 수정의 번거로움
  - **개선 방향:**
    - Spring Cloud Config 또는 환경 변수 기반 설정 분리
    - 향후 Service Discovery(Eureka 등) 기반 동적 라우팅 구조로 확장 가능

---

## 🎯 Architecture Goal

`단일 구조 (Monolith)` → `도메인 모듈 분리 (Modular Separation)` → `MSA 전환 (MSA Transition)`

- 도메인 중심 서비스 분리
- 확장 가능한 구조 설계
- MSA 전환 기반 아키텍처 확보

---

## 🔥 Summary

MSA로의 전환 과정에서 발생하는 구조적 문제를 해결하기 위해 모듈러 모놀리스 기반에서 서비스 분리 구조로 점진적으로 확장 가능한 아키텍처를 설계한 프로젝트입니다.
