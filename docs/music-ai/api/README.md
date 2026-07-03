# ACE-Step API 호출

이 문서는 브라우저 말고 HTTP API로 ACE-Step을 호출하는 방법입니다.

## 1. 먼저 서버 실행

예시:

```bash
acestep --checkpoint_path ./checkpoints --port 7865 --bf16 false
```

서버가 떠 있으면 아래 주소로 API를 볼 수 있습니다.

- API 정보: `http://127.0.0.1:7865/gradio_api/info`
- OpenAPI: `http://127.0.0.1:7865/gradio_api/openapi.json`

## 2. 현재 메인 생성 엔드포인트

현재 앱 기준 text2music 생성 엔드포인트는 아래입니다.

```text
/gradio_api/run/__call__
```

긴 작업을 큐 방식으로 호출할 때는 아래를 씁니다.

```text
/gradio_api/call/__call__
```

## 3. 가장 쉬운 REST 호출

바로 결과를 받고 싶으면 `run`을 쓰면 됩니다.

```bash
curl -X POST "http://127.0.0.1:7865/gradio_api/run/__call__" \
  -H "Content-Type: application/json" \
  -d '{
    "data": [
      "wav",
      10,
      "Korean worship ballad, piano, warm male vocal",
      "[verse]\n주님 한 분만 바라봅니다",
      30,
      15,
      "euler",
      "apg",
      10,
      "1234",
      0.5,
      0.0,
      3.0,
      true,
      false,
      true,
      "",
      0.0,
      0.0,
      false,
      0.5,
      null,
      "none",
      1.0
    ]
  }'
```

## 4. 응답 형태

응답에는 보통 `data` 배열이 들어옵니다.

- 첫 번째 값: 생성된 오디오 파일 정보
- 두 번째 값: 실행에 사용한 파라미터 JSON

즉 보통 이런 느낌입니다.

```json
{
  "data": [
    {
      "path": "/absolute/path/to/output.wav",
      "url": "http://127.0.0.1:7865/gradio_api/file=/absolute/path/to/output.wav"
    },
    {
      "prompt": "Korean worship ballad, piano, warm male vocal",
      "lyrics": "[verse]\n주님 한 분만 바라봅니다"
    }
  ]
}
```

## 5. 긴 작업은 `call` 방식 추천

음악 생성은 시간이 걸릴 수 있어서 `call` 방식이 더 안정적입니다.

### 1단계: 작업 시작

```bash
curl -X POST "http://127.0.0.1:7865/gradio_api/call/__call__" \
  -H "Content-Type: application/json" \
  -d '{
    "data": [
      "wav",
      10,
      "Korean worship ballad, piano, warm male vocal",
      "[verse]\n주님 한 분만 바라봅니다",
      30,
      15,
      "euler",
      "apg",
      10,
      "1234",
      0.5,
      0.0,
      3.0,
      true,
      false,
      true,
      "",
      0.0,
      0.0,
      false,
      0.5,
      null,
      "none",
      1.0
    ]
  }'
```

응답 예시:

```json
{"event_id":"abcd1234"}
```

### 2단계: 결과 받기

```bash
curl -N "http://127.0.0.1:7865/gradio_api/call/__call__/abcd1234"
```

완료되면 SSE로 `event: complete`가 오고, 그 안에 결과 데이터가 들어옵니다.

## 6. Python requests로 호출

```python
import requests

url = "http://127.0.0.1:7865/gradio_api/run/__call__"
payload = {
    "data": [
        "wav",
        10,
        "Korean worship ballad, piano, warm male vocal",
        "[verse]\n주님 한 분만 바라봅니다",
        30,
        15,
        "euler",
        "apg",
        10,
        "1234",
        0.5,
        0.0,
        3.0,
        True,
        False,
        True,
        "",
        0.0,
        0.0,
        False,
        0.5,
        None,
        "none",
        1.0,
    ]
}

response = requests.post(url, json=payload, timeout=600)
print(response.json())
```

## 7. 파라미터 순서가 헷갈릴 때

이 앱의 `__call__` API는 이름 기반이 아니라 순서 기반으로 넣는 편이 안전합니다.

현재 순서는 아래입니다.

1. `format`
2. `audio_duration`
3. `prompt`
4. `lyrics`
5. `infer_step`
6. `guidance_scale`
7. `scheduler_type`
8. `cfg_type`
9. `omega_scale`
10. `manual_seeds`
11. `guidance_interval`
12. `guidance_interval_decay`
13. `min_guidance_scale`
14. `use_erg_tag`
15. `use_erg_lyric`
16. `use_erg_diffusion`
17. `oss_steps`
18. `guidance_scale_text`
19. `guidance_scale_lyric`
20. `audio2audio_enable`
21. `ref_audio_strength`
22. `ref_audio_input`
23. `lora_name_or_path`
24. `lora_weight`

정확한 최신 정보는 항상 아래에서 다시 확인하면 됩니다.

```text
http://127.0.0.1:7865/gradio_api/info
```

## 8. 오디오 파일 받기

응답의 `url` 값을 그대로 열거나 다운로드하면 됩니다.

예시:

```text
http://127.0.0.1:7865/gradio_api/file=/absolute/path/to/output.wav
```

## 같이 보면 좋은 문서

- [호출 방법](../call/README.md)
- [mac 실행](../run/mac/README.md)
- [windows 실행](../run/windows/README.md)
