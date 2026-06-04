# Hexagonal Architecture (Ports & Adapters)

## 1. 한 줄 정의
**Hexagonal Architecture(Ports & Adapters)** 는  
도메인(핵심 비즈니스 로직)을 중심에 두고,  
DB/외부 API/메시지 브로커 같은 기술 요소를 바깥으로 분리하는 구조다.

---

## 2. 왜 필요한가?
프로젝트에서 자주 생기는 문제는 다음과 같다.

- 서비스 코드가 JPA, HTTP 클라이언트, Kafka 코드와 뒤섞임
- 비즈니스 규칙 테스트를 하려면 DB/외부 시스템이 필요함
- 기술이 바뀔 때 도메인 코드까지 같이 바뀜

Hexagonal은 이 문제를 "의존성 방향"으로 해결한다.

---

## 3. 핵심 개념 3가지
1. **도메인이 중심**이다.
2. **포트(Port)는 인터페이스**다.
3. **어댑터(Adapter)는 포트를 구현**한다.

즉, 도메인은 "무엇이 필요하다"만 말하고,  
어댑터가 "어떻게 할지"를 기술로 구현한다.

---

## 4. 포트와 어댑터를 쉽게 이해하기
### 포트(Port)
도메인이 바깥과 대화하기 위한 "약속(인터페이스)"이다.

- Inbound Port: 외부 요청이 도메인 유스케이스로 들어오는 진입점
- Outbound Port: 도메인이 외부 자원(DB, 메시지, API)을 사용할 때의 출구

### 어댑터(Adapter)
포트를 실제 기술로 연결하는 구현체다.

- Inbound Adapter 예: REST Controller, GraphQL Resolver
- Outbound Adapter 예: JPA Repository Adapter, Redis Adapter, Kafka Publisher Adapter

---

## 5. 구조 그림
```text
[Client]
   |
   v
[Inbound Adapter: REST Controller]
   |
   v
[Inbound Port: PlaceOrderUseCase]
   |
   v
[Domain: Order Aggregate]
   |
   v
[Outbound Port: OrderRepositoryPort, EventPublisherPort]
   |
   v
[Outbound Adapter: JPA, Kafka]
```

핵심: 의존성은 바깥 -> 안쪽으로만 향한다.

---

## 6. 예시 코드 (주문 생성)
```java
// Inbound Port (Use Case)
public interface PlaceOrderUseCase {
    void execute(PlaceOrderCommand command);
}

// Outbound Port
public interface OrderRepositoryPort {
    void save(Order order);
}

// Outbound Port
public interface EventPublisherPort {
    void publish(Object event);
}

// Domain/Application Service
public class PlaceOrderService implements PlaceOrderUseCase {
    private final OrderRepositoryPort orderRepository;
    private final EventPublisherPort eventPublisher;

    public PlaceOrderService(OrderRepositoryPort orderRepository, EventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(PlaceOrderCommand command) {
        Order order = Order.create(command.orderId());
        command.lines().forEach(line -> order.addLine(line.productId(), line.price(), line.quantity()));

        orderRepository.save(order);
        eventPublisher.publish(new OrderPlaced(order.getId(), order.totalAmount()));
    }
}

// Outbound Adapter (JPA)
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort {
    @Override
    public void save(Order order) {
        // JPA entity mapping + save
    }
}

// Outbound Adapter (Kafka)
public class KafkaEventPublisherAdapter implements EventPublisherPort {
    @Override
    public void publish(Object event) {
        // Kafka publish
    }
}
```

이 구조에서는 도메인 서비스가 JPA/Kafka 클래스를 직접 모른다.

---

## 7. 장점과 단점
### 장점
- 도메인 코드가 기술 변경에 덜 흔들림
- 단위 테스트가 쉬워짐 (포트를 목/페이크로 대체 가능)
- 팀 내 책임 분리가 명확해짐

### 단점
- 인터페이스/어댑터 코드가 늘어나 초기 생산성이 낮아 보일 수 있음
- 작은 CRUD 프로젝트에서는 과한 구조가 될 수 있음

---

## 8. 자주 하는 실수
### 실수 1: 포트에 기술 타입을 넣음
예: 포트 메서드에 `EntityManager`, `JpaEntity`, `ResponseEntity` 등을 노출.

좋지 않은 이유:
- 도메인이 기술에 오염됨

### 실수 2: 어댑터 없이 서비스에서 바로 외부 API 호출
좋지 않은 이유:
- 테스트가 어려워지고 교체 비용 증가

### 실수 3: 모든 기능에 과하게 적용
좋지 않은 이유:
- 팀 숙련도/복잡도 대비 효율이 떨어질 수 있음

---

## 9. 최소 체크리스트
- 유스케이스 인터페이스(Inbound Port)가 있는가?
- 저장/발행/조회 외부 의존이 Outbound Port로 분리되었는가?
- 도메인 계층이 프레임워크 클래스를 import하지 않는가?
- 어댑터 교체 시 도메인 코드 수정이 최소화되는가?

---

## 10. 요약
Hexagonal은  
**도메인은 안쪽, 기술은 바깥쪽**이라는 원칙으로 설계하는 방식이다.
1. 도메인은 인터페이스(포트)만 의존한다.
2. DB/API/Kafka는 어댑터로 바깥에서 연결한다.

