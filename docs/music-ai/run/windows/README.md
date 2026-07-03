# ACE-Step windows 실행

이 문서는 Windows 기준 실행 방법입니다.

## 1. 가상환경 켜기

```powershell
cd ACE-Step
.venv\Scripts\Activate.ps1
```

## 2. 실행하기

### NVIDIA GPU 예시

```powershell
acestep --checkpoint_path .\checkpoints --port 7865 --device_id 0 --bf16 true
```

### CPU 예시

```powershell
acestep --checkpoint_path .\checkpoints --port 7865 --bf16 false
```

## 3. 브라우저에서 열기

```text
http://127.0.0.1:7865
```

## 4. 포트 바꾸기

```powershell
acestep --checkpoint_path .\checkpoints --port 7866 --device_id 0 --bf16 true
```

## 실행 후 확인

- 처음 실행이면 모델이 자동 다운로드될 수 있습니다.
- CUDA를 쓰는 경우 작업 관리자나 `nvidia-smi`에서 GPU 사용량을 확인할 수 있습니다.
- GPU 오류가 나면 먼저 [CPU 설정](../../config/cpu/README.md)으로 실행 확인을 해보는 편이 빠릅니다.

## 같이 보면 좋은 문서

- [CUDA 설정](../../config/cuda/README.md)
- [CPU 설정](../../config/cpu/README.md)
