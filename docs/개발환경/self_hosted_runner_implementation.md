# GitHub Actions Self-hosted Runner 구현 방법

## 1. 개념

Self-hosted Runner는 GitHub Actions 작업을 GitHub 서버가 아니라 **내 PC, 사내 서버, EC2, Mac mini 같은 개인 서버에서 직접 실행**하게 해주는 방식이다.

일반 GitHub Actions 흐름:

```text
GitHub Actions 서버
  ↓
build / test / deploy 실행
```

Self-hosted Runner 흐름:

```text
GitHub Repository
  ↓
내 서버의 Runner
  ↓
Gradle Build
  ↓
Docker Build
  ↓
Minikube / Kubernetes 배포
```

---

## 2. 왜 Self-hosted Runner를 쓰는가?

Kubernetes, Docker, Minikube 같은 로컬 환경에 직접 접근해야 할 때 사용한다.

예를 들어 현재 구조에서는:

```text
config-service
discovery-service
apigateway
demo-service
```

를 빌드하고, Docker 이미지를 만들고, Minikube에 로드한 뒤 Kubernetes에 배포해야 한다.

GitHub 기본 Runner는 내 로컬 Minikube에 접근할 수 없기 때문에 Self-hosted Runner가 필요하다.

---

## 3. 전체 구조

```text
GitHub Repository
  ↓ push
GitHub Actions Workflow 실행
  ↓
Self-hosted Runner 서버
  ↓
./gradlew clean bootJar
  ↓
docker build
  ↓
minikube image load
  ↓
kubectl apply -k k8s/core
```

---

## 4. Runner 설치

GitHub Repository에서:

```text
Settings
  → Actions
  → Runners
  → New self-hosted runner
```

운영체제 선택 후 GitHub가 안내하는 명령어를 그대로 실행한다.

예시:

```bash
mkdir actions-runner && cd actions-runner

curl -o actions-runner-osx-arm64.tar.gz -L https://github.com/actions/runner/releases/download/v2.xxx.x/actions-runner-osx-arm64-x.y.z.tar.gz

tar xzf ./actions-runner-osx-arm64.tar.gz
```

Runner 등록:

```bash
./config.sh --url https://github.com/계정명/저장소명 --token 발급받은토큰
```

Runner 실행:

```bash
./run.sh
```

백그라운드 서비스로 등록하려면:

```bash
sudo ./svc.sh install
sudo ./svc.sh start
```

---

## 5. Runner 서버에 필요한 도구

Self-hosted Runner가 설치된 서버에는 아래 도구들이 있어야 한다.

```bash
java -version
docker version
kubectl version --client
minikube version
```

현재 프로젝트 기준으로는 Java, Docker, Kubectl, Minikube가 필요하다.

---

## 6. GitHub Actions Workflow 예시

파일 위치:

```text
.github/workflows/deploy-k8s.yml
```

예시:

```yaml
name: Deploy MSA to Minikube

on:
  push:
    branches:
      - backend6-main

jobs:
  deploy:
    runs-on: self-hosted

    steps:
      - name: Checkout source
        uses: actions/checkout@v4

      - name: Build config-service
        run: |
          cd config
          ./gradlew clean bootJar -x test
          cd ..

      - name: Build discovery-service
        run: |
          cd discovery
          ./gradlew clean bootJar -x test
          cd ..

      - name: Build apigateway
        run: |
          cd apigateway
          ./gradlew clean bootJar -x test
          cd ..

      - name: Build demo-service
        run: |
          cd demo-service
          ./gradlew clean bootJar -x test
          cd ..

      - name: Build Docker images
        run: |
          docker build -f config/Dockerfile -t msa-config:k8s config
          docker build -f discovery/Dockerfile -t msa-discovery:k8s discovery
          docker build -f apigateway/Dockerfile -t msa-apigateway:k8s apigateway
          docker build -f demo-service/Dockerfile -t demo-service:k8s demo-service

      - name: Load images to Minikube
        run: |
          minikube image load msa-config:k8s
          minikube image load msa-discovery:k8s
          minikube image load msa-apigateway:k8s
          minikube image load demo-service:k8s

      - name: Apply Kubernetes manifests
        run: |
          kubectl apply -k k8s/core

      - name: Check rollout
        run: |
          kubectl rollout status deployment/config-service -n msa
          kubectl rollout status deployment/discovery -n msa
          kubectl rollout status deployment/apigateway -n msa
          kubectl rollout status deployment/demo-service -n msa
```

---

## 7. Deployment 이미지 설정

Kubernetes Deployment에는 반드시 빌드한 이미지명과 동일하게 적어야 한다.

예:

```yaml
containers:
  - name: apigateway
    image: msa-apigateway:k8s
    imagePullPolicy: Never
```

Minikube에 직접 이미지를 로드해서 쓰는 구조라면:

```yaml
imagePullPolicy: Never
```

또는:

```yaml
imagePullPolicy: IfNotPresent
```

를 사용한다.

---

## 8. 배포 확인 명령어

전체 확인:

```bash
kubectl get all -n msa
```

Pod 확인:

```bash
kubectl get pods -n msa
```

Service 확인:

```bash
kubectl get svc -n msa
```

로그 확인:

```bash
kubectl logs deployment/apigateway -n msa
kubectl logs deployment/demo-service -n msa
```

---

## 9. 외부 접속 확인

NodePort 사용 시:

```bash
minikube ip
```

예:

```text
192.168.49.2
```

접속:

```text
http://192.168.49.2:30080
```

단, Mac + Docker Driver 환경에서는 NodePort 직접 접속이 안 될 수 있다.

이 경우 port-forward를 사용한다.

```bash
kubectl port-forward svc/apigateway 8000:8000 -n msa
```

접속:

```text
http://localhost:8000/actuator/health
http://localhost:8000/swagger-ui/index.html
```

---

## 10. 주의할 점

### 10.1 Config Server 주소

Kubernetes 내부에서는 localhost를 쓰면 안 된다.

잘못된 예:

```yaml
spring:
  cloud:
    config:
      uri: http://localhost:8888
```

올바른 예:

```yaml
spring:
  cloud:
    config:
      uri: http://config-service:8888
```

또는 환경변수:

```yaml
env:
  - name: SPRING_CLOUD_CONFIG_URI
    value: http://config-service:8888
```

---

### 10.2 Gateway 자기 자신 호출 주의

API Gateway 안에서 다시 API Gateway를 호출하면 무한 루프가 발생할 수 있다.

위험한 구조:

```text
Gateway
  ↓
http://apigateway:8000/demo-service/...
  ↓
Gateway
  ↓
반복
```

인가 체크 같은 내부 호출은 직접 서비스로 보내는 것이 안전하다.

예:

```text
http://demo-service:8080/api/v1/authorizations/check
```

또는 Eureka LoadBalancer를 사용한다.

---

### 10.3 Dockerfile 이미지 태그

오래된 이미지:

```dockerfile
FROM openjdk:8-jre-alpine
```

는 더 이상 못 받을 수 있다.

대체:

```dockerfile
FROM eclipse-temurin:8-jre
```

단, Alpine이 아니므로 `apk` 대신 `apt-get`을 써야 한다.

```dockerfile
RUN apt-get update && apt-get install -y curl
```

---

## 11. 최종 실행 흐름 요약

```text
GitHub push
  ↓
Self-hosted Runner 실행
  ↓
Gradle bootJar
  ↓
Docker image build
  ↓
Minikube image load
  ↓
kubectl apply -k
  ↓
Kubernetes 배포 완료
```

---

## 12. 핵심 결론

Self-hosted Runner는 로컬 Kubernetes/Minikube 환경에 직접 배포하고 싶을 때 사용한다.

현재 MSA 실습 구조에서는 GitHub Actions 기본 Runner보다 Self-hosted Runner가 더 적합하다.

특히 다음 작업을 자동화할 수 있다.

```text
빌드
이미지 생성
Minikube 이미지 로드
Kubernetes 배포
Rollout 확인
```
