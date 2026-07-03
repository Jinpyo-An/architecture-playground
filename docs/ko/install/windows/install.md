# Windows 설치

## 이 문서는 이런 경우에 맞습니다
- Windows 10 또는 Windows 11에서 설치할 때
- NVIDIA 그래픽카드가 있으면 나중에 `CUDA 설정`을 쓰면 됩니다
- 그래픽카드가 없으면 `CPU 설정`으로 가면 됩니다

## 1. 준비물 설치

### Python 3.10.6
- Python 3.10.6을 설치합니다
- 설치할 때 `Add Python to PATH`를 꼭 체크합니다

### Git
- Git을 설치합니다

### NVIDIA 사용자만
- CUDA로 돌릴 예정이면 NVIDIA 드라이버를 먼저 설치해두는 편이 좋습니다

## 2. 프로젝트 받기
명령 프롬프트나 PowerShell에서 진행하면 됩니다.

```powershell
git clone https://github.com/AUTOMATIC1111/stable-diffusion-webui.git
cd stable-diffusion-webui
```

zip으로 받았다면 압축만 풀고 폴더로 들어가면 됩니다.

## 3. 모델 넣기
사용할 체크포인트 파일(`.safetensors`, `.ckpt`)을 아래 폴더에 넣습니다.

```text
models/Stable-diffusion/
```

## 4. 다음 단계
- NVIDIA 그래픽카드가 있으면 [CUDA 설정](../../setup/cuda.md)
- GPU 없이 돌리면 [CPU 설정](../../setup/cpu.md)
- 설정이 끝나면 [Windows 실행](../../run/windows/run.md)
