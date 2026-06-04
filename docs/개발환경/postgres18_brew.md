# PostgreSQL 18 설치 + 로그인 (macOS / Windows)

아래 순서로 하면 됩니다.

## macOS (Homebrew)

```bash
# 0) 설치
brew install postgresql@18

# 1) 서버 실행
brew services start postgresql@18

# 2) 로그인 (기본: 현재 macOS 사용자로 접속)
psql -d postgres
# 또는
psql -U $(whoami) -d postgres
```

자주 생기는 에러별 해결:

- `psql: command not found`
```bash
echo 'export PATH="$(brew --prefix)/opt/postgresql@18/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

- `FATAL: role "내사용자명" does not exist`
```bash
createuser -s $(whoami)
psql -d postgres
```

- `Data directory ... does not exist` (최초 1회 초기화 필요)
```bash
initdb -D "$(brew --prefix)/var/postgresql@18"
brew services start postgresql@18
```
종료는 `\q` 입니다.

---

## Windows

Windows에서는 `brew` 대신 공식 설치 프로그램으로 진행합니다.

### 0) 설치

1. [https://www.postgresql.org/download/windows/](https://www.postgresql.org/download/windows/) 접속
2. PostgreSQL 18 설치 파일(EDB Installer) 다운로드
3. 설치 중 아래 값은 기본값으로 진행해도 됩니다.
- Port: `5432`
- Superuser: `postgres`
- Password: 직접 지정 (꼭 기억)

### 1) 서버 실행 확인

설치하면 보통 PostgreSQL 서비스가 자동으로 실행됩니다.  
`Win + R` -> `services.msc` -> PostgreSQL 서비스가 `Running`인지 확인합니다.

### 2) 로그인

가장 쉬운 방법(초급 추천):
- 시작 메뉴에서 `SQL Shell (psql)` 실행
- 순서대로 입력:
    - Server: `localhost` (엔터)
    - Database: `postgres` (엔터)
    - Port: `5432` (엔터)
    - Username: `postgres` (엔터)
    - Password: 설치 때 입력한 비밀번호

CMD/PowerShell에서 바로 접속:

```powershell
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d postgres -h localhost -p 5432
```

### 자주 생기는 에러 해결

- `'psql'은(는) 내부 또는 외부 명령...`  
  `C:\Program Files\PostgreSQL\18\bin` 경로를 Windows PATH에 추가

- `password authentication failed for user "postgres"`  
  설치 때 입력한 비밀번호 재확인 (대소문자 포함)

종료는 `\q` 입니다.

---

### DBeaver 접속용 비밀번호 설정

DBeaver로 접속하려면 PostgreSQL 계정(Role)에 비밀번호가 있어야 합니다.

```bash
# 로컬 접속
psql -d postgres
```

`psql` 화면에서:

```sql
-- 계정 목록 확인
\du

-- 비밀번호 설정 (예: 현재 macOS 사용자 계정)
\password your_mac_username

-- postgres 계정 비밀번호를 설정하려면
\password postgres
```

비밀번호를 2번 입력하면 저장됩니다.

DBeaver 입력값:
- Host: `localhost`
- Port: `5432`
- Database: `postgres` (또는 본인 DB)
- Username: 비밀번호를 설정한 계정명
- Password: 방금 설정한 비밀번호

---

