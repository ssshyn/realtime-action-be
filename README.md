# BidFlow

실시간 고트래픽 경매 시스템을 목표로 하는 백엔드 중심 프로젝트.

동시성 처리, 실시간 통신, Redis 기반 상태 관리, 분산 환경 대응 등을 학습하고 구현하기 위한 프로젝트입니다.

---

# 프로젝트 목표

BidFlow는 다음 문제를 해결하는 것을 목표로 합니다.

* 여러 사용자가 동시에 입찰하는 상황에서 데이터 정합성 보장
* 실시간 입찰 상태 브로드캐스트
* 대규모 트래픽 상황 대응
* 분산 환경에서 안정적인 경매 처리
* 경매 종료 시점의 정확한 낙찰 처리

---

# 핵심 기능

## 경매 생성

판매자(User) 또는 관리자(Admin)는 경매를 생성할 수 있습니다.

생성 가능한 정보:

* 상품명
* 상품 설명
* 시작 가격
* 최소 입찰 단위
* 비공개 희망 낙찰가(Reserve Price)
* 경매 시작 시간
* 경매 종료 시간

---

## 실시간 입찰

사용자는 진행 중인 경매에 실시간으로 참여할 수 있습니다.

지원 예정 기능:

* 실시간 최고 입찰가 갱신
* 실시간 입찰 로그
* 남은 시간 표시
* 참여 인원 수 표시
* 실시간 경매 상태 브로드캐스트

---

## 낙찰 처리

경매 종료 조건:

### 1. 희망 낙찰가 도달

판매자가 설정한 비공개 희망가 이상 입찰 시 즉시 종료.

### 2. 경매 시간 종료

설정된 종료 시간이 되면 최고 입찰가 기준으로 낙찰 처리.

---

# 주요 기술 포인트

## 동시성 처리

BidFlow의 핵심 주제.

다수의 사용자가 동시에 입찰하는 상황에서 다음 문제를 해결합니다.

* Race Condition
* Lost Update
* 중복 낙찰
* 낮은 금액 덮어쓰기
* 종료 직전 동시 입찰 처리

예정 기술:

* Optimistic Lock
* Pessimistic Lock
* Redis Distributed Lock

---

## 실시간 통신

실시간 경매 환경을 위해 WebSocket 기반 통신 사용.

예정 기술:

* Spring WebSocket
* STOMP
* Redis Pub/Sub

---

## Redis 활용

실시간 데이터 처리 및 성능 향상을 위해 Redis 사용.

예정 사용 영역:

* 현재 최고 입찰가 캐싱
* 실시간 랭킹
* Pub/Sub 메시징
* 분산락
* 세션/타이머 관리

---

## 부하 테스트

대량 입찰 상황을 가정한 부하 테스트 진행 예정.

예시:

* 동시 접속자 1,000명
* 초당 수천 건 입찰 요청
* 경매 종료 직전 트래픽 폭증 상황

예정 도구:

* k6
* JMeter

---

# 기술 스택

## Backend

* Java 21
* Spring Boot
* Spring WebSocket
* Spring Data JPA
* QueryDSL
* MySQL
* Redis

---

## Infra

* Docker
* Docker Compose
* Nginx

---

## Communication

* REST API
* WebSocket
* STOMP

---

# 시스템 아키텍처

```text
Client(App/Web)
        ↓
REST API + WebSocket
        ↓
Spring Boot
        ↓
Redis
        ↓
MySQL
```

---

# 프로젝트 구조 (예정)

```text
bidflow
 ├── backend
 ├── app
 └── infra
```

---

# API 예시

## 입찰 요청

```http
POST /api/auctions/{auctionId}/bids
```

Request:

```json
{
  "price": 150000
}
```

Response:

```json
{
  "success": true,
  "currentHighestPrice": 150000,
  "bidderId": 1
}
```

---

# 향후 추가 예정 기능

* Soft Close (종료 직전 입찰 시 자동 연장)
* 실시간 인기 경매 랭킹
* 입찰 제한 정책
* 입찰 알림 시스템
* 이벤트 기반 아키텍처 전환
* Kafka 기반 비동기 처리
* 다중 서버 환경 대응

---

# 프로젝트 목적

이 프로젝트는 단순 CRUD 서비스가 아닌,

"실시간 고트래픽 환경에서 안정적으로 동작하는 경매 시스템"

구현을 목표로 합니다.

특히 아래 기술 역량 향상에 집중합니다.

* 동시성 처리
* 분산 시스템 이해
* 실시간 데이터 처리
* Redis 활용
* WebSocket 기반 통신
* 대용량 트래픽 대응

---

# Author

김세현
