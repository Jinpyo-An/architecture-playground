# ACE-Step mac 실행

이 문서는 macOS 기준 실행 방법입니다.

## 가장 쉬운 실행

저장소 루트에서 아래처럼 실행하면 됩니다.

```bash
cd ACE-Step
source .venv/bin/activate
chmod +x run_acestep.sh
./run_acestep.sh
```

브라우저에서 아래 주소를 열면 됩니다.

```text
http://127.0.0.1:7865
```

## 포트 바꾸기

```bash
./run_acestep.sh 7866
```

## 가상환경 이름이 `.venv`가 아닐 때

예를 들어 `.venv-mps`를 쓰고 있으면 아래처럼 실행하면 됩니다.

```bash
ACESTEP_VENV="$(pwd)/.venv-mps" ./run_acestep.sh
```

## 직접 실행

```bash
source .venv/bin/activate
acestep --checkpoint_path ./checkpoints --port 7865 --bf16 false
```

## 실행 후 확인

- 처음 실행이면 모델이 자동 다운로드될 수 있습니다.
- 로그에 `selected_device=mps`가 보이면 Apple GPU를 쓰는 상태입니다.
- 로그에 `selected_device=cpu`가 보이면 CPU로 도는 상태입니다.

## 같이 보면 좋은 문서

- [MPS 설정](../../config/mps/README.md)
- [CPU 설정](../../config/cpu/README.md)
