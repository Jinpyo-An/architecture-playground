# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

학습용(실습용) 커머스/정산 백엔드. Spring Boot 4.0.6 + Java 21 + Spring Batch 6.0.3 + PostgreSQL + Kafka.
DDD 전술/전략 설계와 클린 아키텍처를 연습하는 것이 코드의 1차 목적이며, 실제 송금/외부 결제 정산 같은 운영 기능은 의도적으로 스텁 처리되어 있다 (`docs/practice/settlement-flow.md` 참고).

**단일 Gradle 모듈이지만 MSA 서비스로 동작하도록 구성되어 있다**: Spring Cloud Config Client(Config Server `:8888`), Eureka Client(`:8761`), Kafka 메시징, JWT 인가. `server.port=0`(랜덤 포트)으로 뜨고 Eureka에 등록된다. 멀티모듈/멀티서비스 프로젝트가 아니라, 단일 서비스가 MSA 인프라에 붙는 형태다.

## Commands

```bash
./gradlew build                 # 컴파일 + 테스트 + 패키징
./gradlew bootRun               # 앱 실행 (DB, .env 필요)
./gradlew test                  # 전체 테스트
./gradlew test --tests "com.example.exercise.ExerciseApplicationTests"   # 단일 테스트 클래스
```

```bash
docker-compose -f docker-compose.kafka.yml up   # 로컬 Kafka(KRaft 단일 노드, :9092) 기동
```

- 린트/포매터 설정 없음. JUnit 5(`useJUnitPlatform`) + `spring-batch-test` 사용. 테스트 클래스는 현재 2개뿐(`ExerciseApplicationTests`, `member/util/JwtProviderTest`).
- 실행 전제: ① PostgreSQL, ② 프로젝트 루트의 `.env`, ③ **Spring Cloud Config Server(`:8888`)**, ④ 필요 시 Eureka 서버(`:8761`)와 Kafka(`:9092`). `application.yaml`이 `optional:file:.env`를 import하며 `DB_*` / `TOSS_PAYMENT_SECRET` / `PRIVATE_KEY` / `PUBLIC_KEY` 키를 읽는다. PostgreSQL 설치는 `docs/개발환경/postgres18_brew.md` 참고.
- **datasource/JPA/batch 프로퍼티는 `application.yaml`에서 주석 처리되어 있고 Config Server(`bootstrap.yaml` → name `exercise-service`)에서 외부 주입받는다.** 로컬에서 Config Server 없이 DB를 직접 띄우려면 `application.yaml`의 해당 블록(6–17행)을 다시 활성화해야 한다. 스키마는 `ddl-auto=update`로 엔티티에서 자동 생성(마이그레이션 도구 없음).
- API 기본 경로는 `/api/v1` (`api.init` 프로퍼티). Swagger UI: `/swagger-ui.html`. 인가는 JWT(`jwt.private-key`/`jwt.public-key`) 기반.

## Architecture

### Bounded contexts

각 컨텍스트는 `com.example.exercise.<context>` 아래에 독립적으로 패키징된다.

- **핵심 도메인 컨텍스트**: `product`, `seller`, `order`, `payment`, `settlement`, `member`. 위 4-레이어 구조를 따른다(단, `member`는 `infrastructure` 대신 `infra` 디렉터리명을 쓴다).
- **보조 패키지**: `auth`(presentation 레이어만 있는 인가용), `kafka`(`async-orders` 토픽 비동기 주문 처리 샘플 — application/config/dto/presentation), `config`(컨텍스트 아님 — `SecurityConfig`/`SwaggerConfig`/`RestClientConfig` 등 전역 설정).

**다른 컨텍스트의 엔티티를 직접 import 하지 않는다** — 교차 접근은 항상 ACL 또는 이벤트(in-process 또는 Kafka)를 통한다.

### 컨텍스트 내부 레이어 (모든 컨텍스트 공통)

```
presentation/   Controller + dto (요청/응답 record). HTTP만 담당.
application/     usecase(인터페이스) + service(구현) + dto(Command/Result) + acl(인터페이스) + event(record)
domain/          model(엔티티/VO) + repository(인터페이스)
infrastructure/  persistence(repository 어댑터 + *JpaRepository) + acl(어댑터 + Translator) + event(핸들러) + batch
```

핵심 규칙들 — 새 코드는 반드시 이 패턴을 따른다:

1. **UseCase 패턴**: Controller는 `XxxUseCase` 인터페이스에만 의존하고, `XxxApplicationService`(또는 `XxxService`)가 이를 구현한다. 서비스는 클래스 레벨 `@Transactional(readOnly = true)`, 쓰기 메서드에만 `@Transactional`을 붙인다.

2. **Repository 어댑터 패턴**: 도메인은 `domain/repository/XxxRepository` 인터페이스만 안다. `infrastructure/persistence/XxxRepositoryAdapter`(`@Repository`)가 이를 구현하며 Spring Data의 `XxxJpaRepository`에 위임한다. 도메인/애플리케이션 레이어는 `JpaRepository`를 직접 보지 않는다.

3. **ACL (Anti-Corruption Layer)**: 외부 컨텍스트/외부 시스템 접근용. `application/acl/XxxAcl` 인터페이스 + `domain/model`의 번역 모델(예: `SellerValidation`, `SettlementOrder`)을 정의하고, `infrastructure/acl/`에 `XxxAclAdapter`(소스 호출) + `XxxTranslator`(원본 → 도메인 모델 번역)를 둔다. 예: `product`가 `seller`를 검증할 때 `Seller` 엔티티가 아니라 `SellerValidation`으로 번역해 사용. `seller`의 사업자 검증은 외부 HTTP 클라이언트(`ExternalBusinessVerificationClient`, 현재 스텁) 호출을 같은 구조로 감싼다.

4. **이벤트 기반 통합(EDI)** — in-process와 Kafka가 **공존**한다:
   - **In-process (같은 VM)**: `ApplicationEventPublisher`로 도메인 이벤트(record)를 발행하고, 다른 컨텍스트의 `infrastructure/event/XxxIntegrationEventHandler`가 `@TransactionalEventListener(phase = AFTER_COMMIT)`로 수신한다. 커밋 이후 처리이므로 수신 측 쓰기는 `@Transactional(propagation = REQUIRES_NEW)`로 새 트랜잭션에서 수행한다. 예: 정산 완료 → `SettlementCompletedEvent` → `Order.settled = true`(`SettlementIntegrationEventHandler`).
   - **Kafka (컨텍스트 간 비동기)**: 결제→주문 같은 컨텍스트 간 전파는 Kafka로 한다. 패턴은 **in-process 이벤트를 받아 Kafka로 중계**하는 형태다 — 예: `PaymentService.confirm()`이 `PaymentConfirmedEvent`(in-process) 발행 → `payment/infrastructure/event/PaymentIntegrationEventHandler`가 `AFTER_COMMIT`에 수신 → `KafkaTemplate`으로 `MarkOrderPaidCommand`를 `order-service` 토픽에 전송 → `order/infrastructure/event/OrderKafkaEvent`(`@KafkaListener`, group `order-payment`)가 수신 → `OrderUseCase.markPaid()` 호출.
   - Kafka 설정은 `KafkaConfig`(전역, `async-orders` 샘플) + `payment/config/PaymentKafkaConfig`(프로듀서) + `order/config/OrderKafkaConfig`(컨슈머). 직렬화는 Jackson JSON. `kafka.enabled=true` 프로퍼티로 토글되며, false면 Kafka 빈이 생성되지 않는다(`@ConditionalOnProperty`).

### 도메인 모델 컨벤션

- 엔티티 ID는 `UUID`. 생성은 정적 팩토리(`Order.create(...)`, `SettlementBatch.create(...)`, `SettlementItem.from(...)`)로, public 생성자를 노출하지 않는 경향.
- 금액은 `BigDecimal`(precision 15, scale 2). 정산식: `netAmount = grossAmount - feeAmount - refundAmount`.
- 컬럼/테이블에 `@Comment` 한글 주석을 단다. 테이블명 충돌 회피용으로 `@Table(name = "\"order\"")`처럼 따옴표 escape 사용.

### Settlement & Spring Batch

정산은 두 가지 배치 잡으로 구현되어 있다 (둘 다 `settlement/infrastructure/batch/`):

- `SettlementChunkJobConfig` (`settlementChunkJob`): **Chunk** 방식(`JpaPagingItemReader` → `ItemProcessor` → `ItemWriter`, 청크 1000). **정산의 정식 경로**이며 대용량 처리를 전제로 한다. reader가 PAID·미정산·기준일 범위의 `Order`를 페이징 조회하고, processor가 `SettlementOrderTranslator`로 `SettlementItem`을 만든다.
- `SettlementJobConfig` (`settlementJob`): **Tasklet** 방식. `SettlementUseCase.execute()`에 위임 — ACL로 정산 후보 조회 → `SettlementBatch`/`SettlementItem` 생성 → 저장 → `SettlementCompletedEvent` 발행. 단일 트랜잭션으로 전체를 처리하는 비교/학습용 경로.

배치 실행 제어:
- `spring.batch.job.enabled=false` — 부팅 시 자동 실행 안 함.
- API(`SettlementBatchController`)로 수동 실행하거나, `SettlementBatchScheduler`가 cron(`settlement.batch.cron`, 기본 매일 03:00)으로 트리거.
- `SettlementSchedulingConfig`가 `@EnableBatchProcessing` + `@EnableJdbcJobRepository(databaseType = "postgres")` + `@EnableScheduling`을 켠다. 배치 메타 스키마는 `spring.batch.jdbc.initialize-schema=always`로 생성.

## Docs

- `docs/practice/settlement-flow.md` — 정산 전체 흐름(ACL/이벤트/AFTER_COMMIT)의 1차 자료. 정산 작업 전 반드시 읽을 것.
- `docs/practice/buyer-commerce-flow.md` — 구매 흐름. `docs/practice/settlement-by-seller-seed.sql` — 정산 실습용 시드 데이터.
- `docs/DDD_기본개념/` — 이 코드베이스가 따르는 DDD/CQRS/배치 개념 정리.
