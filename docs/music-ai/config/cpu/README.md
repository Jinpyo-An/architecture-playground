# CPU 설정

이 문서는 GPU 없이 CPU로 실행하는 설정입니다.

## 핵심만 먼저

- GPU가 없으면 CPU로 자동 실행됩니다.
- CPU에서는 `--bf16 false`로 두는 편이 안전합니다.
- `--cpu_offload`는 GPU 메모리를 아끼는 옵션이라 CPU 전용 실행에는 큰 의미가 없습니다.

## 실행 예시

### mac / Linux

```bash
acestep --checkpoint_path ./checkpoints --port 7865 --bf16 false
```

### windows

```powershell
acestep --checkpoint_path .\checkpoints --port 7865 --bf16 false
```

## 꼭 알아둘 점

- CPU 실행은 많이 느릴 수 있습니다.
- 처음에는 짧은 길이로 테스트하는 편이 좋습니다.
- 먼저 CPU로 실행 확인을 하고, 그 다음 GPU 설정으로 옮겨가면 문제 찾기가 쉽습니다.
