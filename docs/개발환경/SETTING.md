# 프로젝트 시작 과정 정리

이 문서는 프로젝트를 처음 만들고, Java 17까지 맞춘 과정을 순서대로 정리한 기록입니다.

## 1. start.spring.io에서 프로젝트 만들기

1. 브라우저에서 `https://start.spring.io`에 접속합니다.
2. 화면에서 프로젝트 옵션을 선택합니다.
    - Project: `Gradle - Groovy`
    - Language: `Java`
    - Spring Boot: 17선택
3. 프로젝트 이름 관련 정보를 입력합니다.
    - Group, Artifact, Name, Package Name
4. 필요한 의존성(Dependencies)을 추가합니다.
5. `GENERATE` 버튼을 눌러 프로젝트 압축 파일을 다운로드합니다.
6. 다운로드한 압축 파일을 원하는 위치에 풉니다.

## 2. IDE에서 프로젝트 열기

1. IntelliJ 같은 IDE를 실행합니다.
2. 방금 압축 해제한 프로젝트 폴더를 엽니다.
3. IDE가 Gradle 프로젝트를 자동으로 인식하고 동기화(Sync)하는지 확인합니다.

## 3. JDK 17 설치하고 프로젝트에 연결하기

1. JDK 17을 설치합니다.
2. IDE에서 프로젝트가 사용할 Java 버전을 `17`로 설정합니다.
    - IntelliJ 기준: `Project Structure` → `Project SDK` → `JDK 17`
3. Gradle이 사용하는 JVM도 `JDK 17`로 설정합니다.
4. 프로젝트를 실행하거나 빌드해서 Java 17이 적용되었는지 확인합니다.

## 현재 완료된 내용

- start.spring.io에서 만든 프로젝트를 다운로드하고 로컬에서 열었습니다.
- 프로젝트 SDK와 Gradle JVM을 모두 JDK 17로 맞췄습니다.



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

