# Windows 실행

## 먼저 확인
- NVIDIA 그래픽카드면 [CUDA 설정](../../setup/cuda.md)
- GPU 없이 돌리면 [CPU 설정](../../setup/cpu.md)

## 실행 방법 1
가장 쉬운 방법은 `webui-user.bat`를 더블클릭하는 것입니다.

## 실행 방법 2
PowerShell이나 명령 프롬프트에서 직접 실행해도 됩니다.

```powershell
cd stable-diffusion-webui
.\webui-user.bat
```

## 실행 후
- 첫 실행은 가상환경 생성과 패키지 설치 때문에 시간이 걸립니다
- 브라우저에서 아래 주소를 열면 됩니다

```text
http://127.0.0.1:7860
```

## 종료
- 터미널 실행이면 `Ctrl + C`
- 더블클릭 실행이면 창을 닫으면 됩니다

## 자주 쓰는 위치
- 설정 파일: `webui-user.bat`
- 모델 폴더: `models/Stable-diffusion/`
