# CQRS (Command Query Responsibility Segregation)

## 1. 한 줄 정의
**CQRS**는 상태를 변경하는 작업(Command)과  
데이터를 조회하는 작업(Query)을 분리하는 설계 방식이다.

---

## 2. 왜 필요한가?
하나의 모델로 "쓰기 + 읽기"를 모두 처리하면 다음 문제가 생긴다.

- 조회 화면이 복잡해질수록 조인이 많아져 성능이 떨어짐
- 읽기 최적화를 하려다 쓰기 도메인 규칙이 흐려짐
- 변경 영향 범위가 커져 유지보수가 어려워짐

CQRS는 "규칙 중심 모델"과 "조회 중심 모델"을 나눠 이 문제를 줄인다.

---

## 3. 핵심 개념
1. **Command 모델**: 생성/수정/삭제, 도메인 규칙, 일관성 책임
2. **Query 모델**: 목록/검색/통계, 읽기 성능 책임
3. **동기화 방식**: 이벤트 기반 비동기 동기화(Eventual Consistency)

---

## 4. 구조 그림
```text
[Client]
   |                    (비동기)
   | Command API        Event Bus
   v                       |
[Command Model] ---------->|
   |                       v
   | write             [Query Model]
   v                       |
[Command DB]           [Query DB / View]
                           |
                           v
                        Query API
```

핵심: 쓰기와 읽기가 같은 저장소일 필요가 없다.

---

## 5. 예시 (주문 도메인)
### Command 측
- `PlaceOrder`: 주문 생성
- `CancelOrder`: 주문 취소
- `Order` 애그리게이트가 상태 전이 규칙을 보장

### Query 측
- `OrderSummaryView`: 주문 목록 화면 전용 뷰
- 필요한 필드만 미리 조합해 빠르게 조회

```java
// Command 쪽 결과 이벤트
public record OrderPlaced(String orderId, long totalAmount, String customerName) {}

// Query 쪽 뷰 갱신 핸들러
public class OrderSummaryProjector {
    public void on(OrderPlaced event) {
        // order_summary_view upsert
    }
}
```

---

## 6. 장점과 단점
### 장점
- 조회 성능 최적화가 쉬움
- 쓰기 모델의 도메인 규칙을 선명하게 유지 가능
- 읽기/쓰기 트래픽을 독립적으로 확장 가능

### 단점
- 구조 복잡도 증가
- 읽기 모델 지연으로 즉시 일관성이 깨질 수 있음
- 운영 포인트(이벤트/재처리/모니터링) 증가

---

## 7. 자주 하는 실수
### 실수 1: 단순 CRUD에도 바로 CQRS 적용
복잡도가 낮은 서비스에는 과설계가 될 수 있다.

### 실수 2: Query 모델을 규칙 검증에 사용
규칙 검증은 Command 모델 책임이다.

### 실수 3: 지연 일관성 UX를 고려하지 않음
"방금 생성한 데이터가 목록에 바로 안 보임"을 사용자 경험으로 설계해야 한다.

---

## 8. 적용 판단 기준
- 조회 트래픽이 쓰기보다 훨씬 높은가?
- 읽기 화면 요구사항이 복잡한가? (검색/집계/정렬/통계)
- 쓰기 규칙을 강하게 보호해야 하는가?
- 이벤트 기반 운영 역량(모니터링/재처리)이 준비되어 있는가?

3개 이상 "예"라면 CQRS를 검토할 가치가 크다.

---

## 9. 최소 체크리스트
- Command/Query 책임이 코드와 API에서 분리되어 있는가?
- Query 모델 재생성(리빌드) 전략이 있는가?
- 이벤트 중복 처리(idempotency) 방어가 있는가?
- 지연 일관성 상황에 대한 화면/알림 설계가 있는가?

---

## 10. 요약
CQRS는  
**쓰기 모델은 정확성, 읽기 모델은 성능**에 집중하게 만드는 패턴이다.

핵심은 "무조건 분리"가 아니라,  
복잡한 조회와 강한 도메인 규칙이 동시에 필요한 구간에 선택적으로 적용하는 것이다.

