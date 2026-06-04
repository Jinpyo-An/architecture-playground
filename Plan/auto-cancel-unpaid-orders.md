# 미결제 주문 자동 취소 배치 — 설계 계획서

> 작성일: 2026-06-04
> 최종 갱신: 2026-06-04 (사용자 결정 1차 반영)
> 범위: `order` 바운디드 컨텍스트
> 상태: **결정 1차 반영 / 일부 항목 미결정** — §0 의 5개 항목과 §9 의 일부는 확정. 나머지 Q들은 추천안 잠정 적용 중이며 §9 표의 "확인 필요" 표시 참조.

---

## 사용자 1차 결정 요약 (§0 주석 기반)

| 항목 | 결정 | 반영된 본문 |
|------|------|------------|
| 1. 상태 명칭 | **`"PAYMENT_WAIT"` 신규 도입** (Q1=b) | §1-A, §1-B, §4, §6-3, §8 |
| 2. 주문 취소 메서드 | **필요 → `markCanceled` 신설** (Q2=a) | §1-B |
| 3. 24시간 기준 | **추천안 채택 → `reg_dt` 기준 즉석 계산** (Q3=a) | §1-C, §4 |
| 4. 배치 기술 | **`@Scheduled` 도입** (Q6=a) | §3, §8 |
| 5. 이벤트 발행 | **미발행** (Q8=b) | §2, §8 |

> §9 의 나머지 항목(Q4 timezone, Q5 timeout 외부설정, Q7 주기, Q9 멀티 인스턴스, Q10 markPaid 가드, Q11 리컨실레이션)은 사용자의 명시적 답이 없어 **추천안을 잠정 적용** 했습니다. 본 문서 최하단 "확인 필요" 섹션 참조.

---

## 0. 요구사항 재진술과 전제 점검

사용자 요청(원문):
```
주문 생성 → PAYMENT_WAIT 상태 → 24시간 경과 → 배치 실행 → 미결제 주문 조회 → 주문 취소
```

| # | 사용자 표현 | 현 코드의 실제 상태 | 결정 |
|---|------------|--------------------|------|
| 1 | `PAYMENT_WAIT` | `Order.status` 초기값 = `"READY"` (Order.java:167) | ✅ `"PAYMENT_WAIT"` 신규 도입 |
| 2 | "주문 취소" | `Order.java:61` @Comment 에 `CANCELED` 언급. 메서드 부재 | ✅ `markCanceled` 메서드 신설, 상태값 `"CANCELED"` |
| 3 | "24시간 경과" | `reg_dt` 외 별도 컬럼 없음 | ✅ `reg_dt + timeout` 즉석 계산 |
| 4 | "배치 실행" | spring-batch/quartz 없음, `@EnableScheduling` 미적용 | ✅ `@Scheduled` 도입 |
| 5 | (암묵) 후속 처리 | Payment 도메인이 이벤트 발행하듯 일관성 검토 필요 | ✅ 이벤트 미발행 (단순 상태 변경에 한정) |

<!--
1. PAYMENT_WAIT 신규 명치 도입
2. 주문 취소 메서드 필요
3. 추천하는 방법을 따르겠음.
4. @Scheduled 도입
5. 이벤트 발행x

--> 

> **AI 가 강한 영역(패턴/시뮬레이션)** 으로 본 계획서는 옵션 비교와 케이스 누락 점검을 제공합니다.
> **사람이 결정해야 하는 영역(의도/가치 판단)** 은 §9 의 결정 항목으로 분리되어 있습니다.

---

## 1. 도메인 모델 영향

### 1-A. 상태 어휘 — **결정: 옵션 B**

기존 `"READY"` 를 `"PAYMENT_WAIT"` 으로 일괄 교체. CLAUDE.md 의 "enum 마이그레이션 금지" 제약 하에 `String` 그대로 사용.

**영향 범위 (교차 검증 완료)**:

| 파일 | 라인 | 현재 | 변경 |
|------|------|------|------|
| `order/domain/model/Order.java` | 167 | `status = "READY"` | `status = "PAYMENT_WAIT"` |
| `src/main/resources/static/toss-payment.html` | 87 | `status: 'READY'` | `status: 'PAYMENT_WAIT'` |
| `docs/practice/buyer-commerce-flow.md` | 73, 77, 178 | "READY" 표기 | "PAYMENT_WAIT" 표기 |

**영향 받지 않는 항목 (혼동 주의)**:
- `payment/domain/PaymentStatus.java` 의 `READY` enum → Payment 도메인 자체 라이프사이클. **절대 건드리면 안 됨.**
- `OrderJpaRepository.findByStatusAndSettledFalseAnd...` → `"PAID"` 만 조회하므로 영향 없음.

### 1-B. 상태 전이 메서드 추가

```java
public void markCanceled(String reason, UUID actorId) {
    if (!"PAYMENT_WAIT".equals(this.status)) {
        throw new IllegalStateException("Only PAYMENT_WAIT orders can be canceled: " + this.id);
    }
    this.status = "CANCELED";
    this.canceledAt = LocalDateTime.now();
    this.cancelReason = reason;
    this.modifyId = actorId;
}
```

가드 절은 동시성(§6-1) 대응의 1차 방어선.

### 1-C. 신규 컬럼

| 컬럼 | 도입 | 비고 |
|------|------|------|
| `canceled_at` | ✅ | 감사·통계용. `modify_dt` 만으로는 취소 시각 추적 불가 |
| `cancel_reason` (length=30) | ✅ | `"AUTO_TIMEOUT"`, 향후 `"BUYER_CANCEL"` 등 |
| `payment_due_at` | ❌ | Q3=a 채택. `reg_dt + timeout-hours` 즉석 계산 |

> `jpa.hibernate.ddl-auto: update` 이므로 컬럼 추가는 자동 반영. 운영 DB 라면 별도 마이그레이션 도구 검토 (본 작업 범위 밖).

---

## 2. 신규 유스케이스: `cancelTimedOutOrders`

`OrderUseCase` 인터페이스에 메서드 추가. 스케줄러는 이 유스케이스만 호출.

```java
// OrderUseCase
int cancelTimedOutOrders(CancelTimedOutOrdersCommand command);

// CancelTimedOutOrdersCommand (record)
LocalDateTime cutoffAt;   // 이 시각 이전에 생성된 PAYMENT_WAIT 주문이 대상
UUID actorId;             // 시스템 액터
int batchSize;            // 한 번에 처리할 최대 건수
```

서비스 구현 (**Q8=b 반영 — 이벤트 발행 제거**):

```java
@Override
@Transactional(propagation = Propagation.REQUIRES_NEW)
public int cancelTimedOutOrders(CancelTimedOutOrdersCommand cmd) {
    List<Order> targets = orderRepository.findTimedOutPaymentWaitOrders(cmd.cutoffAt(), cmd.batchSize());
    int canceled = 0;
    for (Order o : targets) {
        try {
            o.markCanceled("AUTO_TIMEOUT", cmd.actorId());
            canceled++;
        } catch (IllegalStateException e) {
            // 동시성: 누군가 PAID 로 바꿔치기. skip + WARN 로깅.
            log.warn("skip cancel (status changed): orderId={} status={}", o.getId(), o.getStatus());
        }
    }
    return canceled;
}
```

이유:
- `REQUIRES_NEW`: Payment 이벤트 핸들러 패턴과 일관. 향후 다른 트랜잭션 내부 호출 대비.
- **이벤트 발행 없음**: Q8=b. 후속 작업(재고 복원/알림)이 필요해지면 그 시점에 `OrderCanceledEvent` 추가 + 핸들러 도메인 결정.
- **개별 try/catch**: 한 건 실패가 전체 배치를 중단시키지 않게.

---

## 3. 배치 실행 — **결정: `@Scheduled`**

| 방식 | 비교 (참고용) |
|------|--------------|
| **A. `@Scheduled`** ✅ | 의존성 없음, 코드 적음, 멀티 인스턴스 시 중복 위험은 §9-Q9 |
| B. Spring Batch | JobRepository 메타 테이블 필요. 학습용 단계에 과함 |
| C. Quartz | 멀티 인스턴스 운영 시 재검토 가치 있음 |

구현 골자:

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCancelScheduler {

    private static final UUID SYSTEM_ACTOR = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final OrderUseCase orderUseCase;

    @Value("${order.auto-cancel.timeout-hours:24}")
    private long timeoutHours;

    @Value("${order.auto-cancel.batch-size:500}")
    private int batchSize;

    @Scheduled(cron = "${order.auto-cancel.cron:0 */10 * * * *}", zone = "Asia/Seoul")
    public void run() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(timeoutHours);
        int canceled = orderUseCase.cancelTimedOutOrders(
            new CancelTimedOutOrdersCommand(cutoff, SYSTEM_ACTOR, batchSize)
        );
        log.info("auto-cancel batch done: cutoffAt={} canceledCount={}", cutoff, canceled);
    }
}
```

- `@EnableScheduling` 은 `ExerciseApplication` 에 추가하거나 별도 `SchedulingConfig` 신설.
- `cron` / 타임아웃 / 배치 크기 모두 외부 설정 — 하드코딩 회피 (§9-Q5 추천안).
- `Asia/Seoul`: §9-Q4 추천안.

---

## 4. Repository 확장

```java
// 도메인 포트 (OrderRepository)
List<Order> findTimedOutPaymentWaitOrders(LocalDateTime cutoffAt, int limit);

// JPA (OrderJpaRepository)
@Query("""
    select o from Order o
    where o.status = 'PAYMENT_WAIT'
      and o.regDt < :cutoffAt
    order by o.regDt asc
""")
List<Order> findTimedOutPaymentWaitOrders(
    @Param("cutoffAt") LocalDateTime cutoffAt,
    Pageable pageable
);
```

어댑터는 `PageRequest.of(0, limit)` 로 위임. **`LIMIT` 가드 필수** — 운영 데이터 누적 시 OOM 위험.

---

## 5. Payment 도메인과의 상호작용

미결제 = Payment row 부재만이 아님. 4가지 케이스:

| 케이스 | Payment row | PaymentFailure row | 처리 |
|--------|-------------|--------------------|------|
| 1. 결제창 미진입 | 없음 | 없음 | 단순 취소 OK |
| 2. 결제창 진입, confirm 미호출 | 없음 | 없음 | 단순 취소 OK |
| 3. confirm → Toss 실패 | 없음 | 있음 | 단순 취소 OK (24h 지남) |
| 4. confirm → Toss 성공 → commit 실패 (드묾) | CONFIRMED | 없음 | **리컨실레이션 필요** — §9-Q11, 본 작업 범위 밖 |

본 배치는 `Order.status = 'PAYMENT_WAIT'` 만 대상이므로 케이스 4는 자연스럽게 제외. 다만 발생 시 결제완료/주문대기 불일치 상태가 누적될 수 있음을 인지.

---

## 6. 동시성 / 정합성 리스크

1. **TOCTOU 윈도우**: 배치가 `PAYMENT_WAIT` 으로 조회 → 그 사이 사용자가 결제 완료 → markPaid 호출 → 배치의 markCanceled 가 `IllegalStateException`. 가드 덕분에 데이터 손상 없음. WARN 로깅으로 디버깅 가능.

2. **이중 배치 실행** (멀티 인스턴스): 현 단계 단일 인스턴스 가정 (§9-Q9 추천안). 멀티 배포 결정 시 ShedLock 또는 `select ... for update skip locked` 재검토.

3. **취소 후 결제 도착**: 배치 취소 직후 Toss webhook 도착 → `OrderUseCase.markPaid` 가 호출됨. 현 `markPaid` (Order.java:218-222) 는 상태 무관하게 'PAID' 로 덮어씀 — **결함**. §9-Q10 추천안에 따라 본 작업과 함께 가드 추가:
   ```java
   public void markPaid(LocalDateTime paidAt, UUID actorId) {
       if ("CANCELED".equals(this.status)) {
           throw new IllegalStateException("Cannot mark canceled order as paid: " + this.id);
       }
       this.status = "PAID";
       ...
   }
   ```
   호출자(`PaymentIntegrationEventHandler`) 에서 이 예외를 캐치할지/전파할지도 함께 결정 필요.

4. **타임존 함정**: 현 코드 `LocalDateTime.now()` 사용 → JVM 기본 timezone 의존. `@Scheduled(zone="Asia/Seoul")` 만으로는 부족함. cutoff 계산의 `LocalDateTime.now()` 도 같은 timezone 가정이 성립해야 함. 운영 환경 timezone 고정 (예: JVM `-Duser.timezone=Asia/Seoul`) 권장.

---

## 7. 테스트 전략

| 레이어 | 검증 대상 | 도구 |
|--------|----------|------|
| 도메인 단위 | `Order.markCanceled` 가드, `markPaid` 의 CANCELED 가드 | JUnit |
| Repository | `findTimedOutPaymentWaitOrders` cutoff 경계, status 필터 | `@DataJpaTest` |
| 서비스 통합 | `cancelTimedOutOrders` — PAYMENT_WAIT/PAID/CANCELED 혼합 fixture 에서 PAYMENT_WAIT 만 변경되는지, 동시성 시 skip 동작 | `@SpringBootTest` |
| 스케줄러 | `run()` 단위만. cron 트리거는 직접 테스트 안 함 | Mockito |

cutoff 경계값은 `o.regDt < :cutoffAt` 의 strict less-than 이 의도된 것임을 테스트로 명시 (정확히 `cutoffAt` 시각의 주문은 제외).

---

## 8. 변경 파일 체크리스트 (확정 사항 반영)

- `order/domain/model/Order.java`
  - `status` 초기값 `"READY"` → `"PAYMENT_WAIT"` (line 167)
  - `canceledAt` (LocalDateTime), `cancelReason` (String, length=30) 컬럼 추가
  - `markCanceled(String reason, UUID actorId)` 메서드 추가
  - `markPaid` 에 CANCELED 가드 추가 (§9-Q10 추천안)
- `order/domain/repository/OrderRepository.java` — `findTimedOutPaymentWaitOrders` 시그니처 추가
- `order/infrastructure/persistence/OrderJpaRepository.java` — `@Query` + `Pageable`
- `order/infrastructure/persistence/OrderRepositoryAdapter.java` — `PageRequest.of(0, limit)` 위임
- `order/application/usecase/OrderUseCase.java` — `cancelTimedOutOrders` 시그니처
- `order/application/dto/CancelTimedOutOrdersCommand.java` (신규 record)
- `order/application/service/OrderApplicationService.java` — 구현
- `order/infrastructure/scheduler/OrderCancelScheduler.java` (신규)
- `ExerciseApplication.java` 또는 별도 `SchedulingConfig` — `@EnableScheduling`
- `application.yaml`
  - `order.auto-cancel.timeout-hours: 24`
  - `order.auto-cancel.cron: "0 */10 * * * *"`
  - `order.auto-cancel.batch-size: 500`
- `src/main/resources/static/toss-payment.html` — `status: 'READY'` → `status: 'PAYMENT_WAIT'` (line 87)
- `docs/practice/buyer-commerce-flow.md` — "READY" 3곳 → "PAYMENT_WAIT"
- 테스트 파일 (§7 항목별)

**제거 (Q8=b 반영)**: `OrderCanceledEvent` 신설 없음.

---

## 9. 의사결정 항목 — 1차 답변 반영 후 갱신본

| # | 질문 | 결정 | 출처 |
|---|------|------|------|
| Q1 | 결제 대기 상태 명칭 | **(b) `"PAYMENT_WAIT"` 일괄 변경** | 사용자 명시 |
| Q2 | 취소 상태 명칭 | **(a) `"CANCELED"`** | 사용자 명시 (취소 메서드 필요) |
| Q3 | 24시간 기준 컬럼 | **(a) `reg_dt` 즉석 계산** | 사용자 "추천 따름" |
| Q4 | 타임존 | (b) `Asia/Seoul` 명시 | ⚠️ **추천안 잠정 적용 — 확인 필요** |
| Q5 | timeout 시간 | (b) `application.yaml` 외부 설정 | ⚠️ **추천안 잠정 적용 — 확인 필요** |
| Q6 | 배치 기술 | **(a) `@Scheduled`** | 사용자 명시 |
| Q7 | 배치 주기 | (b) 10분 (cron `0 */10 * * * *`) | ⚠️ **추천안 잠정 적용 — 확인 필요** |
| Q8 | `OrderCanceledEvent` 발행 | **(b) 미발행** | 사용자 명시 |
| Q9 | 멀티 인스턴스 중복 실행 방지 | (a) 현재 미대응, 단일 인스턴스 가정 | ⚠️ **추천안 잠정 적용 — 확인 필요** |
| Q10 | `markPaid` 에 CANCELED 가드 추가 | (a) 추가 | ⚠️ **추천안 잠정 적용 — 확인 필요 (데이터 정합성 직결)** |
| Q11 | 결제완료/주문대기 불일치 리컨실레이션 | (b) 별도 작업 | ⚠️ **추천안 잠정 적용 — 확인 필요** |

---

## 10. 단계별 구현 순서

1. **도메인 계층**
   - `Order` 의 `status` 디폴트 `"PAYMENT_WAIT"` 로 변경
   - `markCanceled` + `canceled_at` / `cancel_reason` 컬럼
   - `markPaid` 에 CANCELED 가드 (Q10 추천안)
   - 도메인 단위 테스트
2. **Repository 계층**
   - `findTimedOutPaymentWaitOrders` (`@Query` + Pageable)
   - `@DataJpaTest`
3. **Application 계층**
   - `CancelTimedOutOrdersCommand` record
   - `OrderUseCase.cancelTimedOutOrders` + 구현
   - 서비스 통합 테스트
4. **Infrastructure 계층**
   - `OrderCancelScheduler` + `@EnableScheduling` + `application.yaml`
5. **외부 파일 동기화**
   - `toss-payment.html` 의 `status: 'READY'` → `'PAYMENT_WAIT'`
   - `docs/practice/buyer-commerce-flow.md` 의 "READY" 표기 갱신
6. **수동 검증**
   - 임시로 `timeout-hours: 0`, `cron: "*/30 * * * * *"` (30초마다) 로 설정
   - toss-payment.html 로 주문 생성 후 결제하지 않음
   - 1분 내에 `status='CANCELED'`, `canceled_at`, `cancel_reason='AUTO_TIMEOUT'` 확인
   - 설정 원복

각 단계는 독립 커밋. 한 PR 에 몰지 않고 1→2→3→4 순으로 분리 권장.

---

## 11. 본 계획서가 다루지 않는 것

- 취소 주문에 대한 **재고 복원** (product 도메인)
- 취소 주문 **사용자 알림** (이메일/푸시)
- 이미 결제된 주문의 **사용자 요청 취소 / 부분 취소 / 환불**
- Toss 측 결제 취소 API 호출 (본 케이스는 "결제 자체가 없는" 주문 한정)
- 어드민 화면 (취소 이력 조회 UI)
- 정산 배치와의 상호작용 — 취소 주문은 `status != 'PAID'` 라 자동 제외, 별도 작업 불필요
- 결제완료/주문대기 불일치 리컨실레이션 (Q11, 별도 작업)

---

## 확인 필요 (구현 착수 전 사용자 confirm 권장)

§9 의 ⚠️ 표시 항목 6개에 대해 추천안을 적용했습니다. 그대로 진행해도 되는지, 또는 일부 항목에 다른 결정을 원하는지 확인 부탁드립니다.

**특히 짚고 싶은 항목**:
- **Q10 (markPaid CANCELED 가드)**: 데이터 정합성에 직결됩니다. 본 작업 범위 안에 포함시킬지 별도 작업으로 뺄지에 따라 PR 의 크기와 영향 범위가 달라집니다.
- **Q5 (외부 설정)**: 단순히 코드에 24h 박지 않고 `application.yaml` 로 빼는 이유는 **수동 검증(§10 단계 6) 시 timeout 을 1분으로 바꿔야 하기 때문**입니다. 하드코딩이면 검증 자체가 어려워집니다.

이 두 항목만 별도로 confirm 해주시면 나머지(Q4·Q7·Q9·Q11)는 추천안대로 진행하겠습니다.

**다음 액션**: 위 확인 답변 → 구현 1단계(도메인 계층)부터 착수.
