# OpenCrew 설치 방법(OS별)

OpenCrew는 독립 실행형 제품이 아니라, [OpenClaw](https://docs.openclaw.ai/) 위에 올려서 쓰는 멀티 에이전트 협업 프레임워크다.

즉, 설치 순서는 항상 이렇다.

1. OS에 맞게 OpenClaw를 설치한다.
2. `openclaw status`로 정상 동작을 확인한다.
3. Slack 워크스페이스와 채널을 준비한다.
4. OpenCrew 파일을 OpenClaw 상태 디렉터리에 배포한다.

## 먼저 알아둘 것

- OpenCrew의 최소 구성은 `CoS`, `CTO`, `Builder` 3개 에이전트다.
- Slack에서는 최소 `#hq`, `#cto`, `#build` 채널을 먼저 만든다.
- OpenCrew는 큰 변경보다, 기존 OpenClaw에 안전하게 추가하는 방식으로 배포하는 것을 권장한다.

## macOS

### 1. OpenClaw 설치

가장 쉬운 방법은 OpenClaw 앱을 설치하고 실행하는 것이다.

또는 CLI 기반으로 설치할 수도 있다.

- OpenClaw 설치 문서: [Getting started](https://docs.openclaw.ai/start/getting-started)
- macOS 전용 안내: [macOS dev setup](https://docs.openclaw.ai/platforms/mac/dev-setup)

### 2. 확인

```bash
openclaw status
openclaw onboard --install-daemon
openclaw gateway status
```

### 3. OpenCrew 배포

OpenCrew 저장소의 `shared/`와 `workspaces/` 내용을 `~/.openclaw` 아래로 복사한다.

```bash
cp ~/.openclaw/openclaw.json ~/.openclaw/openclaw.json.bak-$(date +%Y%m%d%H%M)
mkdir -p ~/.openclaw/shared
cp shared/*.md ~/.openclaw/shared/
for a in cos cto builder; do
  mkdir -p ~/.openclaw/workspace-$a/memory
  rsync -a --ignore-existing "workspaces/$a/" "$HOME/.openclaw/workspace-$a/"
  [ -e "$HOME/.openclaw/workspace-$a/shared" ] || ln -s "$HOME/.openclaw/shared" "$HOME/.openclaw/workspace-$a/shared"
done
```

## Windows

Windows는 두 가지가 있다.

- 데스크톱 중심이면 OpenClaw의 Windows 안내를 따른다.
- 서버/개발자 방식이면 WSL2를 쓰는 편이 OpenCrew 배포와 더 잘 맞는다.

### 1. OpenClaw 설치

- Windows 안내: [Windows](https://docs.openclaw.ai/platforms/windows)

WSL2를 쓰는 경우:

```powershell
wsl --install
```

그다음 WSL 안에서 Linux 방식으로 OpenClaw를 설치한다.

### 2. 확인

```bash
openclaw status
openclaw gateway status
```

### 3. OpenCrew 배포

Windows에서도 핵심은 같다.

- Slack에서 `#hq`, `#cto`, `#build` 채널을 만든다.
- `~/.openclaw` 아래에 OpenCrew의 shared/workspace 파일을 복사한다.
- 배포 후 `openclaw gateway restart`로 반영한다.

## Linux

### 1. OpenClaw 설치

Linux는 보통 OpenClaw의 설치 스크립트나 CLI 설치를 쓴다.

- 설치 개요: [Install](https://docs.openclaw.ai/install)
- Linux 서버용 안내: [Linux server](https://docs.openclaw.ai/vps)

### 2. 확인

```bash
openclaw status
openclaw onboard --install-daemon
openclaw gateway status
```

### 3. OpenCrew 배포

Linux에서는 문서에 나온 표준 배포 방식이 가장 잘 맞는다.

```bash
cp ~/.openclaw/openclaw.json ~/.openclaw/openclaw.json.bak-$(date +%Y%m%d%H%M)
mkdir -p ~/.openclaw/shared
cp shared/*.md ~/.openclaw/shared/
for a in cos cto builder; do
  mkdir -p ~/.openclaw/workspace-$a/memory
  rsync -a --ignore-existing "workspaces/$a/" "$HOME/.openclaw/workspace-$a/"
  [ -e "$HOME/.openclaw/workspace-$a/shared" ] || ln -s "$HOME/.openclaw/shared" "$HOME/.openclaw/workspace-$a/shared"
done
openclaw gateway restart
```

## 배포 후 확인

1. `openclaw gateway restart`를 실행한다.
2. Slack에서 `#hq`에 메시지를 보내 CoS가 응답하는지 확인한다.
3. `#cto`에서 작업을 넘겨 `#build`로 실제 태스크가 생성되는지 확인한다.

## 공식 참고

- [OpenClaw docs](https://docs.openclaw.ai/)
- [OpenCrew getting started](https://github.com/AlexAnys/opencrew/blob/main/docs/en/GETTING_STARTED.md)
- [OpenCrew FAQ](https://github.com/AlexAnys/opencrew/blob/main/docs/en/FAQ.md)
- [OpenCrew deploy guide](https://github.com/AlexAnys/opencrew/blob/main/DEPLOY.en.md)
