# ACE-Step windows 설치

이 문서는 Windows 기준 설치 방법입니다.

## 준비물

- `Python 3.10` 이상
- `git`
- PowerShell

## 1. 저장소 받기

```powershell
git clone https://github.com/ace-step/ACE-Step.git
cd ACE-Step
```

## 2. 가상환경 만들기

```powershell
py -3.10 -m venv .venv
.venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
```

PowerShell 실행 제한 오류가 나오면 한 번만 아래를 실행하면 됩니다.

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

## 3. PyTorch 설치

### NVIDIA GPU를 쓸 때

```powershell
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu126
```

### CPU만 쓸 때

```powershell
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cpu
```

## 4. ACE-Step 설치

```powershell
pip install -e .
```

## 5. 설치 확인

```powershell
acestep --help
```

## 6. 다음 단계

- NVIDIA GPU면: [CUDA 설정](../../config/cuda/README.md)
- GPU 없이 돌리면: [CPU 설정](../../config/cpu/README.md)
- 실행 방법은: [windows 실행](../../run/windows/README.md)

## 자주 생기는 문제

### `py` 명령이 안 될 때

Python 설치가 제대로 안 되었거나 PATH 설정이 안 된 상태입니다.

### CUDA 설치가 헷갈릴 때

일단 CPU 버전으로 먼저 설치하고 실행 확인 후 CUDA로 바꾸는 편이 안전합니다.
