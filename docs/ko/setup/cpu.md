# CPU 설정

## 이 설정이 맞는 경우
- GPU 없이 일단 실행만 해보고 싶을 때
- Mac과 Windows 둘 다 사용할 수 있습니다
- 가장 느리지만 가장 단순합니다

## 추천 옵션
```text
--use-cpu all --precision full --no-half --skip-torch-cuda-test
```

## Mac에서 설정할 때
수정 파일:

```text
webui-user.sh
```

넣을 내용:

```bash
export COMMANDLINE_ARGS="--use-cpu all --precision full --no-half --skip-torch-cuda-test"
```

## Windows에서 설정할 때
수정 파일:

```text
webui-user.bat
```

넣을 내용:

```bat
set COMMANDLINE_ARGS=--use-cpu all --precision full --no-half --skip-torch-cuda-test
```

## 뜻만 간단히 보면
- `--use-cpu all`: 가능한 모듈을 CPU로 돌립니다
- `--precision full`: 안정성 위주입니다
- `--no-half`: CPU에서 반정밀도 문제를 피합니다
- `--skip-torch-cuda-test`: CUDA 검사 단계를 건너뜁니다

## 주의
- CPU는 많이 느립니다
- 처음에는 `512x512`, `batch size 1`로 시작하는 편이 좋습니다

## 다음 단계
- Mac이면 [Mac 실행](../run/mac/run.md)
- Windows면 [Windows 실행](../run/windows/run.md)
