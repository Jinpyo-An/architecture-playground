# Elasticsearch Docker 생성 방법

## 1. 기준 정보

현재 프로젝트 기준:

```text
컨테이너 이름: shop-es
이미지: docker.elastic.co/elasticsearch/elasticsearch:9.2.5
주소: http://localhost:9200
인덱스: shop-products
```

---

## 2. 컨테이너 생성 및 실행

```bash
docker run -d --name shop-es \
  -p 9200:9200 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  docker.elastic.co/elasticsearch/elasticsearch:9.2.5
```

옵션 의미:

```text
--name shop-es
  컨테이너 이름

-p 9200:9200
  로컬 9200 포트를 Elasticsearch 9200 포트에 연결

discovery.type=single-node
  로컬 단일 노드 실행

xpack.security.enabled=false
  로컬 실습용 보안 비활성화
```

---

## 3. 실행 확인

```bash
docker ps
```

Elasticsearch 응답 확인:

```bash
curl http://localhost:9200
```

클러스터 상태 확인:

```bash
curl "http://localhost:9200/_cluster/health?pretty"
```

---

## 4. 기존 컨테이너가 있을 때

이미 `shop-es`가 있으면 새로 생성하지 말고 시작한다.

```bash
docker start shop-es
```

상태 확인:

```bash
docker ps -a | grep shop-es
```

로그 확인:

```bash
docker logs -f shop-es
```

---

## 5. 중지 및 삭제

중지:

```bash
docker stop shop-es
```

삭제:

```bash
docker rm shop-es
```

다시 만들려면:

```bash
docker run -d --name shop-es \
  -p 9200:9200 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  docker.elastic.co/elasticsearch/elasticsearch:9.2.5
```

---

## 6. 인덱스 확인

인덱스 목록:

```bash
curl "http://localhost:9200/_cat/indices?v"
```

`shop-products` 인덱스 확인:

```bash
curl "http://localhost:9200/shop-products?pretty"
```

없으면 애플리케이션에서 인덱스 생성 API를 호출하거나 직접 생성한다.

```bash
curl -X PUT "http://localhost:9200/shop-products" \
  -H "Content-Type: application/json" \
  -d '{
    "mappings": {
      "properties": {
        "id":        { "type": "keyword" },
        "name":      { "type": "text" },
        "brand":     { "type": "keyword" },
        "category":  { "type": "keyword" },
        "price":     { "type": "integer" },
        "updatedAt": { "type": "date" }
      }
    }
  }'
```

---

## 7. 자주 생기는 문제

### 7.1 컨테이너 이름 중복

```text
Conflict. The container name "/shop-es" is already in use
```

해결:

```bash
docker start shop-es
```

또는 삭제 후 재생성:

```bash
docker rm shop-es
```

### 7.2 9200 포트 충돌

```text
port is already allocated
```

확인:

```bash
lsof -i :9200
```

기존 프로세스를 종료하거나 다른 포트를 사용한다.

```bash
docker run -d --name shop-es \
  -p 9201:9200 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  docker.elastic.co/elasticsearch/elasticsearch:9.2.5
```

이 경우 애플리케이션 설정도 `http://localhost:9201`로 맞춰야 한다.

---

## 8. 현재 상태 기준

현재 Docker에는 `shop-es` 컨테이너가 이미 존재한다.

따라서 새로 생성하지 말고 아래 명령으로 실행하면 된다.

```bash
docker start shop-es
```
