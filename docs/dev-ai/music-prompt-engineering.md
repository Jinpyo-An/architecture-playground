# Suno 음악 프롬프트 엔지니어링

Suno 음악 프롬프트 엔지니어링은 원하는 장르, 분위기, 보컬, 악기, 곡 구조를 입력으로 정리해서 AI가 그 방향으로 곡을 만들게 하는 작업이다.

## 무엇을 정하는가

- 장르: 팝, 발라드, 힙합, EDM 같은 스타일
- 분위기: 밝음, 몽환적, 서정적, 강렬함
- 템포: 느림, 보통, 빠름 또는 BPM
- 악기: 피아노, 기타, 신스, 드럼, 스트링
- 보컬: 남성, 여성, 랩, 코러스 여부
- 언어: 영어, 한국어 등
- 곡 구조: Intro, Verse, Chorus, Bridge 같은 전개

## 왜 필요한가

- 원하는 분위기를 더 정확하게 맞출 수 있다
- 곡의 구조를 통제하기 쉽다
- 결과가 너무 넓게 퍼지는 것을 줄일 수 있다

## Suno에서 중요한 점

Suno는 장르와 분위기만 적는 것보다, 곡 구조를 같이 적을 때 더 의도에 가까운 결과를 내기 쉽다.

특히 `Lyrics` 안에 `[Verse]`, `[Chorus]`, `[Bridge]` 같은 구조 태그를 넣으면 전개를 더 명확하게 전달할 수 있다.

## 원하는 구조

보통 아래처럼 적으면 이해하기 쉽다.

```text
Intro -> Verse 1 -> Pre-Chorus -> Chorus -> Verse 2 -> Pre-Chorus -> Chorus -> Bridge -> Final Chorus -> Outro
```

## 예시

아래는 실제로 Suno에 넣을 수 있는 형태의 예시다.

```text
Style: Emotional indie pop ballad, female vocal, intimate piano, warm strings, soft drums, 78 BPM.

Desired structure:
Intro -> Verse 1 -> Pre-Chorus -> Chorus -> Verse 2 -> Pre-Chorus -> Chorus -> Bridge -> Final Chorus -> Outro

Lyrics:
[Verse 1]
I keep the lights on when the night gets cold
Holding on to words we never told

[Pre-Chorus]
Every step feels like a song we knew
Every silence brings me back to you

[Chorus]
I still hear your voice in the rain
Like a melody that calls your name
Even when the world moves on
Your shadow stays in every song
```

## 해석 기준

- `Style`은 음악의 전반적인 색깔을 정한다
- `Desired structure`는 곡이 어떤 순서로 진행되는지 정한다
- `Lyrics`는 실제 가사나 섹션 태그를 넣는 자리다

## 기억할 것

- 설명은 한국어로 적는다
- 실제 프롬프트는 영어로 적는다
- 구조를 같이 적어야 Suno가 의도를 더 잘 따라간다
