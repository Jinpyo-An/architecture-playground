# CQRS 및 기타 구현 방식

## 1. CQRS (Command Query Responsibility Segregation)
- **명령(Command)**: 상태 변경 작업 (등록/수정/삭제)
- **조회(Query)**: 데이터 읽기 작업
- 두 모델을 **분리하여 각각 최적화**

### 장점
- 읽기 성능 향상
- 단순한 Query 모델 유지 가능
### 단점
- 구조 복잡도 증가
- 데이터 동기화 이슈

---

## 2. Event Sourcing
- 상태를 저장하지 않고 **이벤트의 이력**을 저장
- 시스템은 이벤트를 재생(replay)하여 상태 복원

### 장점
- 감사 및 추적 용이
- CQRS와 궁합 좋음
### 단점
- 이벤트 버전 관리 필요
- 학습 난이도 높음

---

## 3. Hexagonal Architecture (Ports & Adapters)
- 도메인과 외부 시스템을 포트(Ports)와 어댑터(Adapters)로 분리
- 의존성 방향: 외부 → 내부

---

## 4. Clean / Onion Architecture
- 의존성 역전(DIP) 원칙 강화
- Domain Layer가 최상위
- 프레임워크·DB는 가장 외곽에 위치

---

## 5. 각 방식 비교
| 아키텍처 | 장점 | 단점 |
|-----------|------|------|
| CQRS | 성능, 확장성 | 복잡성 |
| Event Sourcing | 변경이력, 감사 | 구현 난이도 |
| Hexagonal | 테스트 용이 | 구조 설계 부담 |
| Onion | 의존성 명확 | 초기 설계 복잡 |

---

## 6. 요약
> DDD는 도메인 중심의 철학이고,  
> CQRS·Event Sourcing·Hexagonal 등은 이를 **구현하는 구체적 아키텍처 선택지**다.
