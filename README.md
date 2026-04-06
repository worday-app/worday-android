# Worday 📚

> AI-powered English vocabulary learning app for Turkish speakers.

[![CI](https://github.com/busraknya/worday-android/actions/workflows/ci.yml/badge.svg)](https://github.com/busraknya/worday-android/actions/workflows/ci.yml)
![Platform](https://img.shields.io/badge/platform-Android-green)
![Language](https://img.shields.io/badge/language-Kotlin-purple)
![Architecture](https://img.shields.io/badge/architecture-Clean%20Architecture%20%2B%20MVVM-blue)

---

## About

Worday helps you learn English vocabulary through spaced repetition (SM-2 algorithm), daily quizzes, and optional AI-generated example sentences. It works fully offline — internet is only needed when you request additional AI examples.

**5604 words** · Oxford 5000 based · A1–C2 levels · Turkish interface

---

## Features

- Daily learning sessions with 4 quiz formats
- Spaced repetition (SM-2) — weak words appear more often
- Streak tracking and statistics
- Word notebook with level/type filters
- AI-generated example sentences via Gemini API (optional, offline-first)
- Android TTS pronunciation — no internet needed

---

## Tech Stack

| | |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture + MVVM + Multi-Module |
| DI | Hilt |
| Database | Room |
| Preferences | DataStore |
| Network (optional) | Retrofit + Gemini API |
| Background jobs | WorkManager |
| Analytics | Firebase Analytics + Crashlytics |
| Ads | AdMob |
| CI/CD | GitHub Actions |
| Testing | JUnit5 + MockK + Turbine |

---

## Module Structure

```
:app
:build-logic
:core:common
:core:domain
:core:data
:core:network
:core:ui
:core:analytics
:core:ads
:core:notification
:core:testing
:feature:onboarding
:feature:learn
:feature:quiz
:feature:wordbook
:feature:stats
```

---

## Team

| | GitHub |
|---|---|
| Büşra | [@busraknya](https://github.com/busraknya) |
| Esra | [@esrakonya](https://github.com/esrakonya) |

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for branch strategy and PR guidelines.

---

## License

MIT License — see [LICENSE](LICENSE)
