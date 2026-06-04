# ACL (Anti-Corruption Layer)

## 1. ACL이 뭔가요?
ACL은 외부 시스템의 데이터 형식과 용어가
우리 도메인 코드로 직접 들어오지 못하게 막는 "번역 벽"입니다.

쉽게 말해:
- 외부 말 -> 내부 말로 바꿔주는 통역 계층
- 도메인을 보호하는 안전지대

---

## 2. 왜 필요할까요?
외부 시스템은 보통 우리와 규칙이 다릅니다.

예:
- 상태값: `ACTIVE` 대신 `A`, `00`, `Y`
- 필드명: `seller_no`, `sellerStatusCode`
- 타입: 문자열 UUID, 커스텀 코드값

이걸 서비스/도메인에서 직접 다루면
외부 변경이 내부 코드 전반으로 퍼집니다.

ACL을 두면 외부 변경은 ACL 내부에서만 처리하면 됩니다.

---

## 3. 현재 프로젝트에서는 이렇게 쓰고 있어요

### 핵심 구조
1. Application은 `SellerAcl` 인터페이스만 의존
2. Infrastructure의 `SellerAclAdapter`가 외부 응답을 번역
3. 번역된 내부 모델(`SellerIdentity`)만 서비스에서 사용

### 실제 클래스 위치
- ACL 포트(내부 인터페이스)
  - `src/main/java/com/grepp/backend5/product/application/acl/SellerAcl.java`
- 내부 모델
  - `src/main/java/com/grepp/backend5/product/application/acl/SellerIdentity.java`
- ACL 어댑터(번역기)
  - `src/main/java/com/grepp/backend5/product/infrastructure/acl/SellerAclAdapter.java`
- 외부 클라이언트(현재는 스텁)
  - `src/main/java/com/grepp/backend5/product/infrastructure/acl/client/ExternalSellerClient.java`
  - `src/main/java/com/grepp/backend5/product/infrastructure/acl/client/StubExternalSellerClient.java`

---

## 4. 호출 흐름 (아주 간단히)

`ProductApplicationService.create()`
-> `sellerAcl.loadActiveSeller(sellerId)` 호출
-> `SellerAclAdapter`가 외부 데이터 조회/검증
-> 내부용 `SellerIdentity` 반환
-> 반환값으로 Product 생성 계속 진행

즉, Product 서비스는 "외부 데이터 형식"을 모릅니다.

---

## 5. 실패 케이스도 ACL에서 분리
외부 Seller 확인 실패는 ACL/애플리케이션 예외로 분리합니다.

- `SellerNotFoundException`
- `InactiveSellerException`

그리고 `GlobalExceptionHandler`에서 HTTP 응답으로 변환합니다.

---

## 6. 기억할 3가지
1. 도메인/서비스에서 외부 DTO를 직접 쓰지 않는다.
2. 외부 값 해석(코드값, 상태값)은 ACL 어댑터에서만 한다.
3. 외부 연동 에러는 도메인 에러와 섞지 않고 분리한다.

---

## 7. 한 줄 정리
ACL은
"외부 시스템 변화로부터 우리 도메인을 지키는 번역 방화벽"입니다.
