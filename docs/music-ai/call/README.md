# ACE-Step 호출 방법

이 문서는 ACE-Step을 실제로 어떻게 호출하는지 정리한 문서입니다.

## 1. 가장 쉬운 호출

보통은 터미널에서 `acestep` 명령으로 실행하면 됩니다.

### mac / Linux

```bash
source .venv/bin/activate
acestep --checkpoint_path ./checkpoints --port 7865 --bf16 false
```

### windows CUDA

```powershell
.venv\Scripts\Activate.ps1
acestep --checkpoint_path .\checkpoints --port 7865 --device_id 0 --bf16 true
```

### windows CPU

```powershell
.venv\Scripts\Activate.ps1
acestep --checkpoint_path .\checkpoints --port 7865 --bf16 false
```

## 2. 자주 쓰는 옵션

- `--checkpoint_path`: 모델 폴더 위치
- `--port`: 실행 포트
- `--device_id`: CUDA GPU 번호
- `--bf16`: 보통 CUDA는 `true`, mac/CPU는 `false`
- `--torch_compile`: 속도 최적화 옵션
- `--cpu_offload`: GPU 메모리 부족할 때 사용
- `--overlapped_decode`: 디코드 최적화 옵션

예시:

```bash
acestep --checkpoint_path ./checkpoints --port 7865 --bf16 false --torch_compile true --cpu_offload true --overlapped_decode true
```

## 3. Python 코드에서 직접 호출

라이브러리처럼 직접 부를 수도 있습니다.

```python
from acestep.pipeline_ace_step import ACEStepPipeline

pipe = ACEStepPipeline(
    checkpoint_dir="./checkpoints",
    dtype="float32",  # mac / CPU 추천
)

result = pipe(
    format="wav",
    audio_duration=10.0,
    prompt="Korean worship ballad, piano, warm male vocal",
    lyrics="[verse]\n주님 한 분만 바라봅니다",
    infer_step=30,
    guidance_scale=15.0,
    scheduler_type="euler",
    cfg_type="apg",
    omega_scale=10.0,
    manual_seeds=[1234],
    guidance_interval=0.5,
    guidance_interval_decay=0.0,
    min_guidance_scale=3.0,
    use_erg_tag=True,
    use_erg_lyric=True,
    use_erg_diffusion=True,
    oss_steps="",
    guidance_scale_text=0.0,
    guidance_scale_lyric=0.0,
    audio2audio_enable=False,
    ref_audio_strength=0.5,
    ref_audio_input=None,
    lora_name_or_path="none",
    lora_weight=1.0,
    task="text2music",
    save_path="./outputs",
    batch_size=1,
)

print(result)
```

## 4. Python 호출 시 꼭 알아둘 점

- `prompt`는 문자열로 넣는 편이 안전합니다.
- `lyrics`도 비워두더라도 `""` 같은 문자열로 넣는 편이 안전합니다.
- `save_path="./outputs"`처럼 폴더를 주면 그 안에 파일이 저장됩니다.
- 호출이 끝나면 오디오 파일과 함께 `_input_params.json` 파일도 같이 저장됩니다.

## 5. 반환값

`pipe(...)` 호출 결과는 보통 아래 형태입니다.

```python
[오디오파일경로1, 오디오파일경로2, ..., 입력파라미터딕셔너리]
```

즉 마지막 값은 실행에 사용한 설정 정보입니다.

## 6. CUDA에서 Python으로 직접 호출할 때

CUDA면 `dtype="bfloat16"`을 주는 편이 일반적입니다.

```python
pipe = ACEStepPipeline(
    checkpoint_dir="./checkpoints",
    dtype="bfloat16",
)
```

여기에 실행할 때 `CUDA_VISIBLE_DEVICES=0` 같은 환경변수를 같이 써도 됩니다.

## 7. 어떤 방법을 쓰면 좋은가

- 처음 실행 확인: 터미널에서 `acestep` 호출
- 반복 작업 자동화: Python 직접 호출
- GPU 문제 확인: CPU 설정으로 먼저 호출

## 같이 보면 좋은 문서

- [mac 실행](../run/mac/README.md)
- [windows 실행](../run/windows/README.md)
- [MPS 설정](../config/mps/README.md)
- [CUDA 설정](../config/cuda/README.md)
- [CPU 설정](../config/cpu/README.md)
