# DDD의 기본 개념 (Domain-Driven Design)

## 1. 정의
DDD는 복잡한 비즈니스 도메인을 **도메인 모델 중심**으로 설계하여  
비즈니스 규칙을 코드 구조와 일치시키는 설계 철학이다.

## 2. 주요 목표
- 도메인 전문가와 개발자 간 **공통 언어(Ubiquitous Language)** 확립
- **비즈니스 로직을 기술 세부사항으로부터 분리**
- 코드가 도메인 개념을 **직접적으로 표현**

## 3. 핵심 구성요소
- **엔티티(Entity)**: 고유 식별자를 가지는 객체 (예: 주문, 회원)
- **값 객체(Value Object)**: 불변이며 값으로 동일성 판단 (예: 주소, 금액)
- **애그리게이트(Aggregate)**: 트랜잭션 단위가 되는 객체 묶음
- **도메인 서비스(Domain Service)**: 특정 엔티티에 속하지 않는 핵심 규칙
- **리포지토리(Repository)**: 루트 단위의 영속성 관리
- **이벤트(Domain Event)**: 상태 변경의 의미적 표현

## 4. 장점
- 유지보수성 향상
- 비즈니스 중심 코드
- 테스트 용이성 증가

## 5. MSA에서 자주 함께 채택되는 설계 구조
DDD는 MSA 도입 시 서비스 경계와 책임을 나누는 기준으로 자주 사용된다.

- **Bounded Context 기반 서비스 분리**: 컨텍스트 경계를 기준으로 마이크로서비스를 나누어 모델 충돌을 줄임
- **Hexagonal(Ports & Adapters) / Clean Architecture**: 도메인 계층을 외부 기술(DB, 메시지 브로커, API 프레임워크)에서 분리
- **CQRS 적용**: 쓰기 모델(도메인 규칙 중심)과 읽기 모델(조회 최적화)을 분리해 확장성과 성능 확보
- **이벤트 기반 통합(Event-Driven)**: `OrderPlaced` 같은 도메인 이벤트로 서비스 간 결합도를 낮춤
- **Saga/Process Manager**: 분산 트랜잭션을 보상 트랜잭션 기반으로 처리해 서비스 간 일관성 유지
- **ACL(Anti-Corruption Layer)**: 레거시/외부 시스템 모델이 도메인 모델을 오염시키지 않도록 번역 계층 사용

즉, DDD는 "무엇을 분리할지(도메인 경계)"를 정의하고,  
MSA 아키텍처 패턴은 "분리된 도메인을 어떻게 운영할지(통신/일관성/배포)"를 보완한다.

## 6. 단점
- 초기 설계 복잡도 높음
- 작은 규모 프로젝트에는 과한 구조일 수 있음

## 7. 예제 코드
```java
// 값 객체(Value Object)
public final class Money {
    private final BigDecimal amount;
    private final String currency;

    // 생성 시점에 유효성을 강제해 "항상 올바른 값"만 존재하도록 보장한다.
    private Money(BigDecimal amount, String currency) {
        if (amount == null || currency == null) {
            throw new IllegalArgumentException("금액과 통화는 null일 수 없습니다.");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("금액은 음수일 수 없습니다.");
        }
        this.amount = amount;
        this.currency = currency;
    }

    // 정적 팩토리: 생성 의도를 이름으로 드러낸다.
    public static Money of(long amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    // 합계 계산의 시작값(항등원)으로 사용하기 위한 0원 객체.
    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    // 값 객체는 불변이므로 기존 값을 바꾸지 않고 새 객체를 반환한다.
    public Money add(Money other) {
        // 다른 통화끼리 연산되지 않도록 도메인 규칙을 보호.
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("같은 통화 단위만 더할 수 있습니다.");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money multiply(int factor) {
        // 수량은 1 이상이라는 비즈니스 규칙.
        if (factor <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)), this.currency);
    }

    public BigDecimal amount() {
        return amount;
    }

    public String currency() {
        return currency;
    }
}

// 애그리게이트 루트(Entity)
public class Order {
    private final String id;
    // OrderLine은 루트(Order)를 통해서만 변경되게 하여 경계를 지킨다.
    private final List<OrderLine> orderLines;
    private OrderStatus status;

    private Order(String id) {
        this.id = id;
        this.orderLines = new ArrayList<>();
        this.status = OrderStatus.NEW;
    }

    public static Order create(String id) {
        return new Order(id);
    }

    public void addLine(String productId, Money price, int quantity) {
        // 상태 기반 규칙: NEW 상태에서만 상품 라인 추가 가능.
        if (this.status != OrderStatus.NEW) {
            throw new IllegalStateException("확정된 주문에는 상품을 추가할 수 없습니다.");
        }
        this.orderLines.add(OrderLine.of(productId, price, quantity));
    }

    public void markPaid() {
        // 상태 전이를 한 곳에 모아 애그리게이트 일관성을 유지.
        if (this.status != OrderStatus.NEW) {
            throw new IllegalStateException("이미 결제된 주문입니다.");
        }
        this.status = OrderStatus.PAID;
    }

    public Money totalAmount() {
        // 주문 라인 소계를 합산해 주문 총액을 계산한다.
        return this.orderLines.stream()
            .map(OrderLine::subtotal)
            .reduce(Money.zero("KRW"), Money::add);
    }

    public String getId() {
        return id;
    }
}

enum OrderStatus {
    NEW, PAID, SHIPPED
}

// 애그리게이트 내부 값 객체(Value Object)
final class OrderLine {
    private final String productId;
    private final Money price;
    private final int quantity;

    // OrderLine 자체 규칙(수량 검증)은 내부에서 스스로 보장한다.
    private OrderLine(String productId, Money price, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
    }

    public static OrderLine of(String productId, Money price, int quantity) {
        return new OrderLine(productId, price, quantity);
    }

    public Money subtotal() {
        return price.multiply(quantity);
    }
}

// 리포지토리 인터페이스(Repository)
public interface OrderRepository {
    // 애그리게이트 루트 단위로 조회/저장한다.
    Optional<Order> findById(String id);
    void save(Order order);
}

// 도메인 이벤트(Domain Event)
public final class OrderPlaced {
    private final String orderId;
    private final Money total;
    // 이벤트 발생 시각을 함께 기록해 후속 처리(감사/추적)에 사용한다.
    private final Instant occurredAt = Instant.now();

    public OrderPlaced(String orderId, Money total) {
        this.orderId = orderId;
        this.total = total;
    }
}

// 도메인 서비스(Domain Service)
public class PlaceOrderService {
    private final OrderRepository orderRepository;
    private final Consumer<OrderPlaced> publishEvent;

    public PlaceOrderService(OrderRepository orderRepository, Consumer<OrderPlaced> publishEvent) {
        this.orderRepository = orderRepository;
        this.publishEvent = publishEvent;
    }

    public void place(String orderId, List<OrderLineRequest> lines) {
        // 생성/검증 규칙은 애그리게이트에 위임하고, 서비스는 흐름만 조율한다.
        Order order = Order.create(orderId);
        lines.forEach(line -> order.addLine(line.productId(), line.price(), line.quantity()));

        // 상태 변경을 저장한 뒤 이벤트를 발행해 일관된 처리 순서를 유지한다.
        orderRepository.save(order);
        publishEvent.accept(new OrderPlaced(order.getId(), order.totalAmount()));
    }
}

record OrderLineRequest(String productId, Money price, int quantity) {}
```
