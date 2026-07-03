# CUDA 설정

## 이 설정이 맞는 경우
- Windows + NVIDIA 그래픽카드
- 속도를 가장 중요하게 볼 때

## 먼저 알아둘 점
- 이 문서 세트에서는 CUDA를 `Windows 기준`으로 설명합니다
- 요즘 Mac에서는 보통 CUDA 방식으로 쓰지 않습니다

## 수정할 파일
```text
webui-user.bat
```

## 가장 쉬운 설정
`webui-user.bat`를 아래처럼 두면 됩니다.

```bat
@echo off

set PYTHON=
set GIT=
set VENV_DIR=
set COMMANDLINE_ARGS=--xformers

call webui.bat
```

## VRAM이 부족하면
아래처럼 바꾸면 조금 더 버티기 쉽습니다.

```bat
set COMMANDLINE_ARGS=--medvram --xformers
```

## 아주 기본으로만 쓰고 싶으면
옵션 없이 시작해도 됩니다.

```bat
set COMMANDLINE_ARGS=
```

## 옵션 뜻
- `--xformers`: 속도와 메모리 사용에 도움될 수 있습니다
- `--medvram`: VRAM이 적을 때 조금 더 안전합니다

## 다음 단계
[Windows 실행](../run/windows/run.md)로 가서 실행하면 됩니다.
