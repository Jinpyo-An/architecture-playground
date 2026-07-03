# MPS 설정

## 이 설정이 맞는 경우
- Apple Silicon Mac(M1, M2, M3, M4)
- 이 저장소의 현재 로컬 설정도 이 방식입니다

## 수정할 파일
```text
webui-user.sh
```

## 가장 쉬운 설정
파일 아래쪽의 `export` 줄을 아래처럼 맞추면 됩니다.

```bash
export PYTORCH_ENABLE_MPS_FALLBACK=1
export PYTORCH_MPS_HIGH_WATERMARK_RATIO=0.0
export COMMANDLINE_ARGS="--no-half-vae --no-half --precision full --skip-torch-cuda-test --opt-sub-quad-attention"
```

## 뜻만 간단히 보면
- `PYTORCH_ENABLE_MPS_FALLBACK=1`: MPS에서 안 되는 작업을 CPU로 넘깁니다
- `PYTORCH_MPS_HIGH_WATERMARK_RATIO=0.0`: 메모리 관련 오류를 줄이는 데 도움이 됩니다
- `--no-half`, `--no-half-vae`: 반정밀도 문제를 피합니다
- `--precision full`: 안정성 위주로 실행합니다
- `--skip-torch-cuda-test`: Mac에서 CUDA 검사 메시지를 피합니다
- `--opt-sub-quad-attention`: 메모리를 조금 아끼는 쪽입니다

## 참고
- Mac 기본 스크립트인 `webui-macos-env.sh`도 자동으로 읽습니다
- 그래도 실제로 자주 만지는 파일은 `webui-user.sh`입니다

## 다음 단계
[Mac 실행](../run/mac/run.md)로 가서 실행하면 됩니다.
