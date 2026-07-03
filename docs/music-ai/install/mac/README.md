# ACE-Step mac 설치

이 문서는 macOS 기준 설치 방법입니다.

## 준비물

- `Python 3.10` 이상
- `git`

## 1. 저장소 받기

```bash
git clone https://github.com/ace-step/ACE-Step.git
cd ACE-Step
```

이미 이 폴더에 들어와 있다면 `cd ACE-Step`은 생략해도 됩니다.

## 2. 가상환경 만들기

```bash
python3 -m venv .venv
source .venv/bin/activate
python -m pip install --upgrade pip
```

## 3. 패키지 설치

```bash
pip install -e .
```

## 4. 설치 확인

아래 명령어가 보이면 설치가 된 것입니다.

```bash
.venv/bin/acestep --help
```

## 5. 다음 단계

- Apple Silicon이면: [MPS 설정](../../config/mps/README.md)
- GPU 없이 쓸 거면: [CPU 설정](../../config/cpu/README.md)
- 실행 방법은: [mac 실행](../../run/mac/README.md)

## 자주 생기는 문제

### `command not found: python3`

Python 3를 먼저 설치해야 합니다.

### `acestep`가 안 보일 때

가상환경이 켜진 상태에서 다시 설치하면 됩니다.

```bash
source .venv/bin/activate
pip install -e .
```
