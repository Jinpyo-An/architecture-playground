# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

학습용(실습용) 커머스/정산 백엔드. Spring Boot 4.0.6 + Java 21 + Spring Batch 6 + PostgreSQL.
DDD 전술/전략 설계와 클린 아키텍처를 연습하는 것이 코드의 1차 목적이며, 실제 송금/외부 결제 정산 같은 운영 기능은 의도적으로 스텁 처리되어 있다 (`docs/practice/settlement-flow.md` 참고).

## Commands

```bash
./gradlew build                 # 컴파일 + 테스트 + 패키징
./gradlew bootRun               # 앱 실행 (DB, .env 필요)
./gradlew test                  # 전체 테스트
./gradlew test --tests "com.example.exercise.ExerciseApplicationTests"   # 단일 테스트 클래스
```

- 린트/포매터 설정 없음. JUnit 5(`useJUnitPlatform`) + `spring-batch-test` 사용.
- 실행에는 PostgreSQL과 프로젝트 루트의 `.env`가 필요하다. `application.yaml`이 `optional:file:.env`를 import하며, `DB_*` / `TOSS_PAYMENT_SECRET` 키를 읽는다. PostgreSQL 설치는 `docs/개발환경/postgres18_brew.md` 참고.
- `spring.jpa.hibernate.ddl-auto=update` — 스키마는 엔티티에서 자동 생성된다(마이그레이션 도구 없음).
- API 기본 경로는 `/api/v1` (`api.init` 프로퍼티). Swagger UI: `/swagger-ui.html`.

## Architecture

### Bounded contexts

각 컨텍스트는 `com.example.exercise.<context>` 아래에 독립적으로 패키징된다: `product`, `seller`, `order`, `payment`, `settlement`. **다른 컨텍스트의 엔티티를 직접 import 하지 않는다** — 교차 접근은 항상 ACL 또는 이벤트를 통한다.

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

4. **이벤트 기반 통합(EDI)**: 컨텍스트 간 후속 처리는 `ApplicationEventPublisher`로 도메인 이벤트(record)를 발행하고, 다른 컨텍스트의 `infrastructure/event/XxxIntegrationEventHandler`가 `@TransactionalEventListener(phase = AFTER_COMMIT)`로 수신한다. 커밋 이후 처리이므로 수신 측 쓰기는 `@Transactional(propagation = REQUIRES_NEW)`로 새 트랜잭션에서 수행한다. 예: 결제 확정 → `PaymentConfirmedEvent`, 정산 완료 → `SettlementCompletedEvent` → `Order.settled = true`.

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
