# Mac 실행

## 먼저 확인
- Apple Silicon이면 [MPS 설정](../../setup/mps.md)
- GPU 없이 돌리면 [CPU 설정](../../setup/cpu.md)

## 실행 명령
터미널에서 프로젝트 폴더로 들어간 뒤 실행합니다.

```bash
cd stable-diffusion-webui
chmod +x webui.sh webui-user.sh
./webui.sh
```

## 실행 후
- 첫 실행은 가상환경 생성과 패키지 설치 때문에 시간이 걸립니다
- 브라우저에서 아래 주소를 열면 됩니다

```text
http://127.0.0.1:7860
```

## 종료
터미널에서 `Ctrl + C`를 누르면 됩니다.

## 자주 쓰는 위치
- 설정 파일: `webui-user.sh`
- 모델 폴더: `models/Stable-diffusion/`
