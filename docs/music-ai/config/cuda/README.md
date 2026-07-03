# CUDA 설정

이 문서는 NVIDIA GPU를 쓰는 설정입니다.

## 핵심만 먼저

- 따로 `--cuda` 옵션은 없습니다.
- CUDA가 가능하면 ACE-Step이 자동으로 GPU를 잡습니다.
- GPU 번호를 고를 때는 `--device_id`를 씁니다.

## 기본 실행 예시

```powershell
acestep --checkpoint_path .\checkpoints --port 7865 --device_id 0 --bf16 true
```

mac 또는 Linux라면 경로만 바꾸면 됩니다.

```bash
acestep --checkpoint_path ./checkpoints --port 7865 --device_id 0 --bf16 true
```

## VRAM이 부족할 때

공식 README 기준으로 아래 옵션 조합이 도움이 될 수 있습니다.

```powershell
acestep --checkpoint_path .\checkpoints --port 7865 --device_id 0 --bf16 true --torch_compile true --cpu_offload true --overlapped_decode true
```

## Windows에서 `torch_compile`를 쓸 때

필요하면 아래를 먼저 설치합니다.

```powershell
pip install triton-windows
```

## 확인 방법

- Windows에서는 작업 관리자 또는 `nvidia-smi`로 GPU 사용량을 확인하면 됩니다.
- GPU 메모리가 부족하면 `--cpu_offload true`를 먼저 붙여보면 됩니다.
