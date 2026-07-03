# AI 하네스 명세서

이 문서는 AI 기능을 구현할 때 공통으로 적용할 하네스 요구사항을 정의한다.

하네스는 모델 호출 전후를 통제하는 실행 계층이며, 입력 검증, 요청 조립, 응답 검증, 실패 처리, 로그 기록을 책임진다.

## 1. 목적

AI 기능은 같은 입력에도 결과가 달라질 수 있다.

하네스는 다음을 보장해야 한다.

- 입력이 유효해야 한다
- 외부 호출은 필요한 경우에만 실행해야 한다
- 응답은 최소한의 검증을 거쳐야 한다
- 실패 시 사용자에게 일관된 fallback을 반환해야 한다
- 처리 경로와 실패 지점을 추적할 수 있어야 한다

## 2. 적용 범위

이 명세는 아래 기능에 적용한다.

- 상품 의미 검색과 LLM 응답
- 이미지 분석
- 이미지 생성

제외 대상은 다음과 같다.

- 모델 학습과 파인튜닝
- 벤치마크 자동 평가 시스템
- 대화 히스토리 저장

## 3. 공통 규칙

### 3.1 입력 정리

- 문자열 입력은 `trim()` 처리한다
- `null` 또는 blank 값은 기본값 또는 에러로 처리한다
- 파일 입력은 존재 여부와 비어 있지 않음을 먼저 확인한다

### 3.2 응답 검증

- 모델 응답이 비어 있으면 실패로 간주한다
- 응답 형식이 기대와 다르면 실패로 간주한다
- 파일 생성 응답은 포맷과 본문이 모두 존재해야 한다

### 3.3 실패 처리

- 요청 자체가 잘못되면 `400 Bad Request`를 반환한다
- 외부 모델 호출 실패는 `500 Internal Server Error`로 처리한다
- 검색 결과가 없으면 fallback 문구를 반환한다

### 3.4 로깅

- 요청 식별 정보는 남기되 원문 전체를 과도하게 남기지 않는다
- 처리 분기와 실패 사유를 로그에 남긴다
- 외부 호출 시간과 결과 상태를 추적할 수 있어야 한다

## 4. 상품 LLM 검색 명세

### 4.1 엔드포인트

`POST /api/v1/products/llm-search`

### 4.2 요청

```json
{
  "question": "영상 편집용 노트북 추천해줘",
  "size": 3
}
```

#### 필드

- `question`
  - type: `string`
  - required: `true`
  - description: 검색 질문
- `size`
  - type: `integer`
  - required: `false`
  - default: `3`
  - description: 반환할 상품 수

### 4.3 처리 순서

1. 요청 본문을 검증한다
2. 질문을 정리한다
3. 질문을 임베딩으로 변환한다
4. `product.embedding` 기준으로 가까운 상품을 찾는다
5. 검색 결과를 LLM 입력으로 전달한다
6. LLM 응답을 검증한다
7. 응답이 없으면 fallback 문구로 대체한다

### 4.4 응답

```json
{
  "question": "영상 편집용 노트북 추천해줘",
  "answer": "영상 편집 기준으로는 맥북 프로 14와 LG 그램 16을 먼저 확인해 보세요.",
  "products": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "맥북 프로 14",
      "price": 2590000,
      "status": "ACTIVE"
    },
    {
      "id": "660e8400-e29b-41d4-a716-446655440000",
      "name": "LG 그램 16",
      "price": 1790000,
      "status": "ACTIVE"
    }
  ]
}
```

#### 필드

- `question`
  - type: `string`
  - required: `true`
- `answer`
  - type: `string`
  - required: `true`
- `products`
  - type: `array<Product>`
  - required: `true`

### 4.5 실패 규칙

- 요청 본문이 없으면 `400 Bad Request`
- `question`이 없으면 `400 Bad Request`
- `size`가 없으면 `3`으로 처리
- `size`가 `0` 이하이면 `5`로 보정
- 검색 결과가 없으면 `관련 상품을 찾지 못했습니다.` 반환
- LLM 응답이 비어 있으면 fallback 문구로 대체

### 4.6 검증 기준

- 검색 결과가 있으면 답변은 검색 결과와 연결되어야 한다
- 검색 결과가 없으면 빈 결과와 fallback 문구를 반환해야 한다
- 응답 객체는 `question`, `answer`, `products`를 모두 포함해야 한다

### 4.7 테스트 케이스

- 정상 요청으로 검색 결과와 답변이 반환되는지 확인
- 질문이 누락되면 `400`이 반환되는지 확인
- 검색 결과가 없을 때 fallback 문구가 반환되는지 확인

### 4.8 관련 파일

- 컨트롤러: [ProductController.java](../../src/main/java/com/example/demo/product/presentation/ProductController.java)
- 서비스: [ProductApplicationService.java](../../src/main/java/com/example/demo/product/application/service/ProductApplicationService.java)
- 임베딩 서비스: [ProductEmbeddingService.java](../../src/main/java/com/example/demo/product/application/vector/ProductEmbeddingService.java)
- 응답 DTO: [ProductLlmSearchResponse.java](../../src/main/java/com/example/demo/product/presentation/dto/response/ProductLlmSearchResponse.java)

## 5. 이미지 분석 명세

### 5.1 엔드포인트

`POST /api/v1/ai/images/analyze`

### 5.2 요청

- `Content-Type`: `multipart/form-data`
- 필드:
  - `image`: 분석할 이미지 파일, required
  - `prompt`: optional

### 5.3 처리 순서

1. 이미지 파일 유무를 확인한다
2. 프롬프트를 정리한다
3. 프롬프트가 비어 있으면 기본값 `이 이미지를 설명해줘`를 사용한다
4. 이미지를 data URL로 변환한다
5. OpenAI responses API를 호출한다
6. 결과 텍스트를 추출한다
7. 결과가 비면 실패로 처리한다

### 5.4 응답

```json
{
  "prompt": "이 이미지를 설명해줘",
  "answer": "책상 위에 노트북이 놓여 있고 화면에는 작업 창이 보입니다."
}
```

#### 필드

- `prompt`
  - type: `string`
  - required: `true`
- `answer`
  - type: `string`
  - required: `true`

### 5.5 실패 규칙

- 이미지가 없거나 비어 있으면 `400 Bad Request`
- OpenAI API key가 없으면 `500 Internal Server Error`
- OpenAI 응답 텍스트가 없으면 `500 Internal Server Error`
- `prompt`가 없으면 기본값을 사용

### 5.6 검증 기준

- 이미지가 반드시 존재해야 한다
- 응답은 빈 문자열이면 안 된다
- 반환된 `prompt`는 실제 사용한 값이어야 한다

### 5.7 테스트 케이스

- 이미지와 프롬프트를 보내면 분석 결과가 반환되는지 확인
- 프롬프트를 비워도 기본값이 사용되는지 확인
- 이미지가 없으면 `400`이 반환되는지 확인

### 5.8 관련 파일

- 컨트롤러: [AiImageController.java](../../src/main/java/com/example/demo/ai/presentation/controller/AiImageController.java)
- 서비스: [AiImageService.java](../../src/main/java/com/example/demo/ai/application/service/AiImageService.java)
- 클라이언트: [OpenAiImageClient.java](../../src/main/java/com/example/demo/ai/infrastructure/client/OpenAiImageClient.java)
- 응답 DTO: [AiImageAnalyzeResponse.java](../../src/main/java/com/example/demo/ai/presentation/dto/AiImageAnalyzeResponse.java)

## 6. 이미지 생성 명세

### 6.1 엔드포인트

`POST /api/v1/ai/images/generate`

### 6.2 요청

```json
{
  "prompt": "노을지는 바다를 바라보는 흰 고양이",
  "size": "1024x1024"
}
```

#### 필드

- `prompt`
  - type: `string`
  - required: `true`
- `size`
  - type: `string`
  - required: `false`
  - default: `1024x1024`

### 6.3 처리 순서

1. 요청 본문을 검증한다
2. 프롬프트를 정리한다
3. 프롬프트가 비어 있으면 `400 Bad Request`를 반환한다
4. size가 비어 있으면 `1024x1024`를 사용한다
5. OpenAI images API를 호출한다
6. base64 이미지와 포맷을 추출한다
7. 결과를 응답으로 반환한다

### 6.4 응답

```json
{
  "prompt": "노을지는 바다를 바라보는 흰 고양이",
  "format": "png",
  "imageBase64": "iVBORw0KGgoAAA..."
}
```

#### 필드

- `prompt`
  - type: `string`
  - required: `true`
- `format`
  - type: `string`
  - required: `true`
- `imageBase64`
  - type: `string`
  - required: `true`

### 6.5 실패 규칙

- 요청 본문이 없으면 `400 Bad Request`
- `prompt`가 없으면 `400 Bad Request`
- OpenAI API key가 없으면 `500 Internal Server Error`
- OpenAI 응답 데이터가 없으면 `500 Internal Server Error`

### 6.6 검증 기준

- 생성 응답은 포맷과 본문을 모두 가져야 한다
- 반환된 프롬프트는 실제 사용한 값이어야 한다
- size는 비어 있으면 기본값을 사용해야 한다

### 6.7 테스트 케이스

- 정상 요청으로 이미지가 생성되는지 확인
- size를 비워도 기본값이 적용되는지 확인
- prompt가 없으면 `400`이 반환되는지 확인

### 6.8 관련 파일

- 컨트롤러: [AiImageController.java](../../src/main/java/com/example/demo/ai/presentation/controller/AiImageController.java)
- 서비스: [AiImageService.java](../../src/main/java/com/example/demo/ai/application/service/AiImageService.java)
- 클라이언트: [OpenAiImageClient.java](../../src/main/java/com/example/demo/ai/infrastructure/client/OpenAiImageClient.java)
- 요청 DTO: [AiImageGenerateRequest.java](../../src/main/java/com/example/demo/ai/presentation/dto/AiImageGenerateRequest.java)
- 응답 DTO: [AiImageGenerateResponse.java](../../src/main/java/com/example/demo/ai/presentation/dto/AiImageGenerateResponse.java)

## 7. 완료 기준

- `./gradlew compileJava`가 통과해야 한다
- 각 엔드포인트의 입력 검증이 동작해야 한다
- fallback 문구가 실패 시 일관되게 반환되어야 한다
- 응답 스키마가 문서와 일치해야 한다

## 8. 수정 대상

이 명세를 기준으로 구현할 때 우선 확인할 파일은 아래와 같다.

- [ProductController.java](../../src/main/java/com/example/demo/product/presentation/ProductController.java)
- [ProductApplicationService.java](../../src/main/java/com/example/demo/product/application/service/ProductApplicationService.java)
- [ProductEmbeddingService.java](../../src/main/java/com/example/demo/product/application/vector/ProductEmbeddingService.java)
- [AiImageController.java](../../src/main/java/com/example/demo/ai/presentation/controller/AiImageController.java)
- [AiImageService.java](../../src/main/java/com/example/demo/ai/application/service/AiImageService.java)
- [OpenAiImageClient.java](../../src/main/java/com/example/demo/ai/infrastructure/client/OpenAiImageClient.java)

## 9. 기억할 것

- 이 문서는 개발용 명세서다
- 구현은 이 문서의 규칙을 기준으로 맞춘다
- 설명보다 입력, 출력, 실패 조건이 우선이다

## 10. 관련 문서

- 하네스 개념: [harness-engineering.md](./harness-engineering.md)
- 이미지 API: [image-api-guide.md](../ai/image-api-guide.md)
- 상품 벡터 검색: [product-vector-guide.md](../ai/product-vector-guide.md)
- 상품 임베딩: [product-embedding-guide.md](../ai/product-embedding-guide.md)
- 상품 LLM 검색: [product-llm-search-guide.md](../ai/product-llm-search-guide.md)
