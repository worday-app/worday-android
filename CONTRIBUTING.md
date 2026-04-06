# Contributing — Worday

## Branch Stratejisi

```
main
 └── develop
      ├── feature/busra/onboarding
      ├── feature/esra/quiz-screen
      ├── fix/streak-reset-bug
      └── chore/update-dependencies
```

### Branch'ler

| Branch | Amaç |
|---|---|
| `main` | Play Store'a giden sürümler. Direkt push kapalı. |
| `develop` | Aktif geliştirme. Her ikimiz PR açarak buraya merge eder. |
| `feature/{isim}/{konu}` | Yeni özellik geliştirme |
| `fix/{konu}` | Bug fix |
| `chore/{konu}` | Bağımlılık güncellemesi, refactor, config değişikliği |

---

## Günlük İş Akışı

```bash
# 1. Develop'tan güncel al
git checkout develop
git pull origin develop

# 2. Kendi branch'ini aç
git checkout -b feature/busra/flashcard-screen

# 3. Çalış, commit'le
git add .
git commit -m "feat(learn): add flashcard swipe animation"

# 4. Push
git push origin feature/busra/flashcard-screen

# 5. GitHub'da PR aç → develop'a
```

---

## Commit Mesajı Formatı

```
<tip>(<modül>): <kısa açıklama>
```

| Tip | Ne zaman |
|---|---|
| `feat` | Yeni özellik |
| `fix` | Bug düzeltme |
| `refactor` | Davranış değişmeden kod düzenleme |
| `test` | Test ekleme/güncelleme |
| `chore` | Bağımlılık, config, build |
| `docs` | Dökümantasyon |

**Örnekler:**
```
feat(quiz): add fill-in-the-blank exercise format
fix(sm2): correct interval calculation on first failure
refactor(core:data): extract word mapper to separate class
test(domain): add SM-2 edge case unit tests
chore(deps): update compose bom to 2024.05.00
```

---

## PR Kuralları

- Her PR **develop** branch'ine açılır, direkt `main`'e asla
- PR açmadan önce CI geçmeli (lint + test + build)
- Diğer kişinin review'u zorunlu (1 approval gerekli)
- Kendi PR'ını kendin merge edemezsin
- PR başlığı commit formatına uymalı

---

## Release Süreci

```
develop → main (PR ile)
```

`main`'e merge sadece release için yapılır. Merge sonrası GitHub'da tag oluşturulur:

```bash
git tag -a v1.0.0 -m "Initial release"
git push origin v1.0.0
```


