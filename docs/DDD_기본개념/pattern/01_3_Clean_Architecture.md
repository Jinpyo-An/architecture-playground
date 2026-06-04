# Clean Architecture

## 1. 한 줄 정의
**Clean Architecture**는  
비즈니스 규칙을 중심에 두고,  
프레임워크/DB/UI 같은 기술 요소를 바깥 계층으로 분리하는 설계 방식이다.

---

## 2. 왜 필요한가?
프로젝트가 커지면 기술 코드가 비즈니스 규칙을 덮어버리기 쉽다.

- 서비스 로직이 프레임워크 어노테이션/ORM 코드에 종속됨
- 테스트할 때 웹 서버/DB 구동이 필요해짐
- 기술 변경(프레임워크 교체)이 비즈니스 코드까지 흔듦

Clean Architecture는 의존성 방향을 통제해 이 문제를 줄인다.

---

## 3. 핵심 원칙
1. **의존성은 바깥 -> 안쪽으로만 향한다**
2. **안쪽 계층은 바깥 계층을 모른다**
3. **비즈니스 규칙은 가장 안쪽에 둔다**

즉, 도메인/유스케이스는 DB나 웹 프레임워크를 직접 알지 않는다.

---

## 4. 계층 구조
```text
[Entities]
  - 핵심 도메인 규칙

[Use Cases]
  - 애플리케이션 유스케이스(시나리오)

[Interface Adapters]
  - Controller, Presenter, Gateway, Mapper

[Frameworks & Drivers]
  - Web Framework, DB, Message Broker, External API
```

안쪽으로 갈수록 변경이 적고, 바깥으로 갈수록 변경이 잦다.

---

## 5. 예시 (주문 생성)
```java
// Use Case Input Port
public interface PlaceOrderInputPort {
    void handle(PlaceOrderCommand command);
}

// Use Case Output Port (Gateway)
public interface OrderGateway {
    void save(Order order);
}

// Use Case
public class PlaceOrderInteractor implements PlaceOrderInputPort {
    private final OrderGateway orderGateway;

    public PlaceOrderInteractor(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    @Override
    public void handle(PlaceOrderCommand command) {
        Order order = Order.create(command.orderId());
        orderGateway.save(order);
    }
}

// Outer Adapter (Framework/DB)
public class JpaOrderGateway implements OrderGateway {
    @Override
    public void save(Order order) {
        // JPA save
    }
}
```

핵심은 `PlaceOrderInteractor`가 JPA를 모른다는 점이다.

---

## 6. Hexagonal과의 관계
Clean Architecture와 Hexagonal은 방향이 매우 비슷하다.

- 공통점: 도메인 중심, 의존성 역전, 외부 기술 분리
- 차이점: Clean은 계층 구조를 더 명시적으로 설명하고,  
  Hexagonal은 Port/Adapter 관점으로 경계 연결을 강조한다.

실무에서는 두 방식을 혼합해서 사용하는 경우가 많다.

---

## 7. 장점과 단점
### 장점
- 비즈니스 규칙 보호
- 테스트 용이성 향상
- 기술 교체 비용 감소

### 단점
- 초기 구조 설계 비용 증가
- 인터페이스/매퍼 코드 증가
- 팀이 원칙을 지키지 않으면 형식만 남을 수 있음

---

## 8. 자주 하는 실수
### 실수 1: Use Case에서 프레임워크 타입 사용
예: `ResponseEntity`, `EntityManager`를 Use Case에 직접 사용.

### 실수 2: Entity를 DB 엔티티와 동일시
도메인 Entity와 ORM Entity를 분리하지 않으면 경계가 무너진다.

### 실수 3: 계층은 나눴지만 의존성은 역방향
Interface는 안쪽에 두고 구현은 바깥에 둬야 한다.

---

## 9. 최소 체크리스트
- Use Case 계층이 웹/DB 프레임워크 import 없이 동작하는가?
- 저장소/외부 연동 인터페이스가 안쪽 계층에 정의되어 있는가?
- 바깥 계층 구현 교체 시 Use Case 변경이 최소인가?
- 테스트에서 Use Case를 독립 실행할 수 있는가?

---

## 10. 요약
Clean Architecture는  
**핵심 비즈니스 로직을 기술 변화로부터 분리해 보호하는 설계 방식**이다.

핵심은 계층 개수보다  
"의존성 방향을 안쪽으로 고정"하는 원칙을 지키는 것이다.

