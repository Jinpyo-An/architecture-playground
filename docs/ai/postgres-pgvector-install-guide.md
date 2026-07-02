# PostgreSQL에 pgvector 설치

이 문서는 로컬에 설치된 PostgreSQL에 `pgvector`를 붙이는 방법을 OS별로 정리한 문서입니다.

## 먼저 확인

1. PostgreSQL 버전을 확인합니다.

```bash
psql --version
```

2. 현재 접속한 DB에서 확장 기능이 있는지 확인합니다.

```sql
SELECT name, default_version
FROM pg_available_extensions
WHERE name = 'vector';
```

결과가 나오면 `pgvector` 패키지가 설치된 상태입니다.

## macOS

Homebrew로 설치한 PostgreSQL이면 보통 아래처럼 진행합니다.

```bash
brew update
brew install pgvector
```

기존 PostgreSQL 서비스가 있으면 재시작합니다.

```bash
brew services restart postgresql
```

## Ubuntu / Debian

패키지 저장소에서 설치합니다.

```bash
sudo apt update
sudo apt install postgresql-16-pgvector
```

PostgreSQL 버전에 맞는 패키지 이름이 다를 수 있습니다.

설치 후 PostgreSQL을 재시작합니다.

```bash
sudo systemctl restart postgresql
```

## CentOS / RHEL / Rocky / AlmaLinux

PGDG 저장소를 쓰는 경우가 많습니다.

```bash
sudo dnf install pgvector_16
```

환경에 따라 패키지명이 `pgvector_15`, `pgvector_14`처럼 달라질 수 있습니다.

설치 후 PostgreSQL을 재시작합니다.

```bash
sudo systemctl restart postgresql-16
```

## Windows

Windows는 보통 직접 바이너리보다 배포 패키지나 Docker를 권장합니다.

가능한 경우:

1. PostgreSQL 배포판에 포함된 확장 패키지를 설치
2. WSL에서 Linux 방법으로 설치
3. Docker PostgreSQL 이미지 사용

일반적인 로컬 개발은 Docker가 가장 단순합니다.

## 확장 활성화

패키지를 설치한 뒤 각 DB에서 확장을 켭니다.

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

확인:

```sql
SELECT extname FROM pg_extension WHERE extname = 'vector';
```

## 자주 막히는 부분

- PostgreSQL 재시작을 안 해서 확장이 안 보이는 경우가 많습니다.
- 패키지 이름은 PostgreSQL 메이저 버전에 따라 달라집니다.
- 앱에서 `CREATE EXTENSION` 권한이 없으면 DB 관리자 계정으로 먼저 실행해야 합니다.

## 관련 문서

- [Product 벡터 문서](./product-vector-guide.md)
- [Product 임베딩 문서](./product-embedding-guide.md)
