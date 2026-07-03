# Mac 설치

## 이 문서는 이런 경우에 맞습니다
- MacBook, Mac mini, iMac에서 설치할 때
- Apple Silicon(M1, M2, M3, M4)은 나중에 `MPS 설정`을 쓰면 됩니다
- Intel Mac은 나중에 `CPU 설정`을 쓰는 편이 안전합니다

## 1. 준비물 설치
터미널을 열고 아래 순서대로 진행하면 됩니다.

### Xcode Command Line Tools
```bash
xcode-select --install
```

### Homebrew
Homebrew가 없으면 설치합니다.

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### Python 3.10과 Git
```bash
brew install python@3.10 git
```

## 2. 프로젝트 받기
이미 이 폴더가 있으면 이 단계는 건너뛰면 됩니다.

```bash
git clone https://github.com/AUTOMATIC1111/stable-diffusion-webui.git
cd stable-diffusion-webui
```

## 3. 모델 넣기
사용할 체크포인트 파일(`.safetensors`, `.ckpt`)을 아래 폴더에 넣습니다.

```text
models/Stable-diffusion/
```

## 4. 다음 단계
- Apple Silicon이면 [MPS 설정](../../setup/mps.md)
- Intel Mac이거나 GPU를 안 쓰면 [CPU 설정](../../setup/cpu.md)
- 설정이 끝나면 [Mac 실행](../../run/mac/run.md)
