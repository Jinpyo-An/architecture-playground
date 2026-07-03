# MPS 설정

이 문서는 Apple Silicon Mac에서 GPU를 쓰는 설정입니다.

## 핵심만 먼저

- 따로 `--mps` 옵션을 넣는 방식이 아닙니다.
- MPS가 가능하면 ACE-Step이 자동으로 `mps`를 선택합니다.
- macOS에서는 `--bf16 false`로 실행하는 것이 안전합니다.

## 추천 실행 예시

`run_acestep.sh`를 쓰면 이미 `--bf16 false`가 들어갑니다.

```bash
./run_acestep.sh
```

직접 실행할 때는 아래처럼 하면 됩니다.

```bash
acestep --checkpoint_path ./checkpoints --port 7865 --bf16 false
```

## 확인 방법

실행 로그에 아래처럼 보이면 정상입니다.

```text
selected_device=mps
gpu_in_use=yes
```

## 문제 생기면

- `mps` 대신 `cpu`가 나오면 GPU 접근이 안 되는 상태입니다.
- 에러가 나면 먼저 `--torch_compile false` 상태로 다시 실행해보는 편이 안전합니다.
- 그래도 안 되면 [CPU 설정](../cpu/README.md)으로 먼저 실행 확인을 해보면 됩니다.
