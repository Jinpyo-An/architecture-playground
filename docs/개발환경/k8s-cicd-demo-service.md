# demo-service Kubernetes CI/CD

## 구조

- 이미지: `demo-service:k8s`
- 네임스페이스: `msa`
- 서비스: `demo-service`
- 외부 포트: `30080`
- DB/Kafka: 로컬 PC의 실행 중인 서비스를 `host.minikube.internal`로 연결
- Kafka는 로컬 Docker Kafka의 advertised listener 문제를 피하려고 minikube 배포에서는 꺼둔다.

## 로컬 배포

```bash
minikube start
./gradlew clean bootJar
docker build -t demo-service:k8s .
minikube image load demo-service:k8s
kubectl apply -k k8s/demo-service
kubectl rollout status deployment/demo-service -n msa
```

확인:

```bash
minikube service demo-service -n msa --url
curl http://$(minikube ip):30080/swagger-ui/index.html
```

## CI/CD 흐름

1. GitHub Actions가 jar를 빌드한다.
2. Docker 이미지를 만든다.
3. self-hosted runner에서 `kubectl apply -k k8s/demo-service`로 배포한다.

워크플로 파일:

```text
.github/workflows/demo-service-k8s.yml
```

## 운영/쿠버네티스 주의

운영에서는 `host.minikube.internal`을 쓰지 않는다.

```yaml
DB_HOST: postgres.default.svc.cluster.local
KAFKA_BOOTSTRAP_SERVERS: kafka.default.svc.cluster.local:9092
```

Secret 값도 반드시 실제 값으로 바꾼다.
