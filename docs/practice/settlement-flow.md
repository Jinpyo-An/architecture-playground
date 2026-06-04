# 정산 Flow

## 목적

결제가 완료된 주문을 기준으로 판매자에게 정산할 금액을 계산하고, 정산 완료 후 주문을 정산 완료 상태로 변경하는 흐름을 정리한다.

현재 정산은 실습용 구조다.

- 실제 송금은 하지 않는다.
- 판매자 계좌 정보는 다루지 않는다.
- 정산 실패/재시도는 아직 없다.
- PAID 상태의 미정산 주문을 대상으로 정산 로직만 수행한다.

## 전체 흐름

```text
Order PAID 완료
 -> Settlement 실행
 -> SettlementOrderAcl로 정산 대상 Order 조회
 -> Order 데이터를 SettlementOrder로 번역
 -> SettlementBatch 생성
 -> SettlementItem 생성
 -> SettlementCompletedEvent 발행
 -> AFTER_COMMIT 이후 Order settled 처리
```

## 1. 정산 대상 기준

정산 대상은 다음 조건을 만족하는 주문이다.

```text
Order.status = PAID
Order.settled = false
Order.paidAt이 정산 기준일 범위 안에 있음
```

정산 기준일이 `2026-06-03`이면 조회 범위는 다음과 같다.

```text
2026-06-03 00:00:00 <= paidAt < 2026-06-04 00:00:00
```

## 2. ACL 구조

Settlement는 Order 엔티티를 직접 사용하지 않는다.

정산에 필요한 주문 정보는 ACL을 통해 가져온다.

```text
SettlementApplicationService
 -> SettlementOrderAcl
 -> SettlementOrderAclAdapter
 -> OrderRepository
 -> SettlementOrderTranslator
 -> SettlementOrder
```

`SettlementOrder`는 정산 도메인이 사용할 주문 모델이다.

```text
orderId
orderNo
sellerId
grossAmount
feeAmount
refundAmount
netAmount
paidAt
```

즉 ACL의 역할은 다음과 같다.

```text
Order 모델
 -> Settlement 정산 모델
```

## 3. 정산 실행

정산 API를 호출하면 정산 유스케이스가 실행된다.

```text
POST /api/v1/settlements
```

요청 예시:

```json
{
  "settlementDate": "2026-06-03",
  "actorId": "11111111-1111-1111-1111-111111111111"
}
```

실행 흐름:

```text
SettlementApplicationService.execute()
 -> 정산 기준일 결정
 -> SettlementOrderAcl.findSettlementCandidates()
 -> SettlementBatch 생성
 -> SettlementItem 생성
 -> SettlementBatch 저장
```

정산 대상 주문이 없으면 실패한다.

## 4. 정산 금액 계산

현재 정산 금액은 Order의 금액 값을 그대로 사용한다.

```text
grossAmount = 구매자가 결제한 총 금액
feeAmount = 수수료
refundAmount = 환불 금액
netAmount = grossAmount - feeAmount - refundAmount
```

정산 항목의 금액:

```text
SettlementItem.settlementAmount = Order.netAmount
```

정산 배치의 합계:

```text
totalGrossAmount = SettlementItem.grossAmount 합계
totalFeeAmount = SettlementItem.feeAmount 합계
totalRefundAmount = SettlementItem.refundAmount 합계
totalSettlementAmount = SettlementItem.settlementAmount 합계
```

## 5. EDI 구조

정산 저장이 끝나면 정산 완료 이벤트를 발행한다.

```text
SettlementCompletedEvent
```

발행 위치:

```text
SettlementApplicationService.execute()
 -> eventPublisher.publishEvent(...)
```

이벤트 데이터:

```text
settlementBatchId
orderIds
actorId
```

## 6. AFTER_COMMIT 처리

정산 배치 저장 트랜잭션이 커밋된 뒤 이벤트 핸들러가 실행된다.

```text
SettlementIntegrationEventHandler
 -> @TransactionalEventListener(phase = AFTER_COMMIT)
 -> OrderUseCase.markSettled()
```

Order 변경은 커밋 이후에 다시 수행되므로 새 트랜잭션으로 처리한다.

```text
OrderApplicationService.markSettled()
 -> @Transactional(propagation = REQUIRES_NEW)
```

처리 결과:

```text
Order.settled = true
Order.settlementBatchId = SettlementBatch.id
```

## 7. 최종 상태

정산 완료 후 데이터 상태는 다음과 같다.

```text
SettlementBatch
 - status: COMPLETED
 - settlementDate: 정산 기준일
 - totalGrossAmount: 총 주문 금액
 - totalFeeAmount: 총 수수료
 - totalRefundAmount: 총 환불 금액
 - totalSettlementAmount: 총 정산 금액

SettlementItem
 - orderId: 정산 대상 주문 ID
 - sellerId: 판매자 ID
 - settlementAmount: 주문별 정산 금액

Order
 - settled: true
 - settlementBatchId: 정산 배치 ID
```

## 핵심 포인트

- Settlement는 Order를 직접 의존하지 않고 ACL로 정산 후보를 가져온다.
- ACL은 Order 데이터를 SettlementOrder로 번역한다.
- 정산 완료 후 Order 변경은 EDI 이벤트로 연결한다.
- AFTER_COMMIT 이후 Order 수정은 새 트랜잭션으로 처리한다.
- 현재 정산은 실제 송금이 아닌 정산 로직 실습 단계다.
