# 구매 기준 Commerce Flow

## 목적

구매자가 상품을 결제하는 흐름을 기준으로 Seller, Product, Order, Payment가 어떻게 연결되는지 정리한다.

현재 실습 흐름은 다음 패턴을 함께 확인하기 위한 구조다.

- Product 생성 시 Seller 검증 ACL
- Order 생성 후 Payment 결제
- Payment 완료 후 AFTER_COMMIT 이벤트 처리
- Payment 이벤트를 통해 Order 상태 변경

## 전체 흐름

```text
Seller 생성
 -> Product 생성
 -> Product 생성 시 Seller ACL 검증
 -> Buyer가 Product 기준으로 Order 생성
 -> Toss Payment 결제 요청
 -> Payment 승인 성공
 -> Payment CONFIRMED 저장
 -> PaymentConfirmedEvent 발행
 -> AFTER_COMMIT 이후 Order markPaid 실행
 -> Order 상태 PAID 변경
```

## 1. Seller 준비

상품은 판매자 기준으로 등록된다.

Product는 Seller의 내부 엔티티를 직접 신뢰하지 않고, ACL을 통해 판매자 검증 결과만 전달받는다.

```text
ProductCommandService
 -> SellerValidationAcl
 -> SellerValidationAclAdapter
 -> SellerRepository
 -> SellerValidationTranslator
 -> SellerValidation
```

검증 기준은 현재 `Seller.status == ACTIVE`다.

## 2. Product 등록

상품 생성 요청에는 `sellerId`가 포함된다.

Product 생성 시점에 Seller ACL을 호출한다.

```text
Product 생성 요청
 -> sellerId 검증
 -> Seller 존재 여부 확인
 -> Seller ACTIVE 여부 확인
 -> Product 저장
 -> ProductCreatedEvent 발행
```

Seller가 없거나 ACTIVE가 아니면 Product 생성은 실패한다.

## 3. Order 생성

구매자는 상품을 결제하기 전에 주문을 먼저 생성한다.

현재 테스트 화면에서는 `toss-payment.html`의 `createOrder()`가 주문을 생성한다.

```text
결제 버튼 클릭
 -> createOrder()
 -> POST /api/v1/orders
 -> Order READY 생성
 -> 응답 orderNo 반환
```

Order 생성 시 기본 상태는 `READY`다.

결제가 아직 완료되지 않았으므로 `paidAt`은 비어 있을 수 있다.

## 4. Toss Payment 결제 요청

주문 생성이 성공하면 반환된 `orderNo`를 Toss의 `orderId`로 사용한다.

```text
Order.orderNo
 -> Toss requestPayment orderId
```

결제 금액은 Order의 `grossAmount`와 맞춘다.

현재 실습 기준:

```text
grossAmount = 40000
feeAmount = 1000
netAmount = 39000
```

결제는 구매자가 지불하는 총 금액인 `grossAmount` 기준이다.

`feeAmount`, `netAmount`는 정산 관점의 값이다.

## 5. Payment 승인

Toss 결제가 성공하면 성공 페이지로 이동한다.

```text
/payments/success.html?orderId={orderNo}&paymentKey={paymentKey}&amount={amount}
```

성공 페이지는 서버 승인 API를 호출한다.

```text
POST /api/v1/payments/confirm
```

서버는 Toss 승인 API를 호출하고, 성공하면 Payment를 저장한다.

```text
Payment.status = CONFIRMED
Payment.orderId = Order.orderNo
Payment.totalAmount = 결제 금액
```

## 6. Payment 완료 이벤트

Payment 저장 후 `PaymentConfirmedEvent`를 발행한다.

```text
PaymentService.confirm()
 -> Payment 저장
 -> PaymentConfirmedEvent 발행
```

이벤트는 `AFTER_COMMIT` 시점에 처리된다.

```text
PaymentIntegrationEventHandler
 -> @TransactionalEventListener(phase = AFTER_COMMIT)
 -> OrderUseCase.markPaid()
```

## 7. Order 결제 완료 처리

Payment 트랜잭션이 커밋된 이후 Order를 수정해야 하므로, Order 결제 완료 처리는 새 트랜잭션으로 실행한다.

```text
OrderApplicationService.markPaid()
 -> @Transactional(propagation = REQUIRES_NEW)
```

처리 결과:

```text
Order.status = PAID
Order.paidAt = Payment.approvedAt
```

## 최종 상태

정상 처리 후 데이터 상태는 다음과 같다.

```text
Payment
 - status: CONFIRMED
 - orderId: Order.orderNo
 - totalAmount: Order.grossAmount

Order
 - status: PAID
 - paidAt: 결제 승인 시각
```

## 핵심 포인트

- Product는 Seller를 직접 의존하지 않고 ACL로 검증한다.
- Order는 결제 전 `READY` 상태로 생성된다.
- Payment 완료 후 바로 Order를 직접 수정하지 않고 이벤트로 연결한다.
- `AFTER_COMMIT` 이후 DB 변경은 새 트랜잭션이 필요하다.
- 구매 기준 결제 금액은 `grossAmount`다.
