# Worday Backend — Architecture Proposal

> This directory documents the backend boundary and initial contract. It is not yet a runnable server implementation.

## Why a backend

The current Android application calls Gemini directly from `:core:network` and places the API key in Android BuildConfig. Any key packaged in an APK/AAB can be extracted. A server-side proxy is required before public release. A backend also enables account-scoped cloud backup/synchronization and enforceable AI quotas.

## Recommended stack

- Kotlin + Java 21
- Spring Boot REST API
- PostgreSQL + Flyway
- Firebase Authentication ID-token verification (Firebase Admin SDK)
- Gemini API called only from server
- Docker for local development and container deployment

This is a proposed stack, not a claim that the repository already contains these services.

## Target topology

```text
Android UI / ViewModels
        |
        v
Domain use cases + repository contracts
        |
        +---- Room (local source of truth; always available)
        |
        +---- Retrofit :core:network
                    |
                    v
              Worday API (HTTPS)
                    |
          +---------+----------+
          |         |          |
       Auth       Sync       AI Proxy
          |         |          |
     Firebase   PostgreSQL   Gemini API
```

## API v1 proposal

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/v1/health` | Health check |
| GET | `/api/v1/me` | Authenticated account metadata |
| GET | `/api/v1/sync/bootstrap` | Initial remote state |
| POST | `/api/v1/sync/push` | Upload local events idempotently |
| GET | `/api/v1/sync/pull?cursor=...` | Pull changes after cursor |
| POST | `/api/v1/ai/example-sentences` | Server-side Gemini proxy |

Protected endpoints require `Authorization: Bearer <Firebase ID token>`. The server verifies the token and derives the Firebase UID from the verified claims. A client-supplied UID must never determine data ownership.

## Initial persistence model

- `user_profile`: Firebase UID, preferences, created/updated timestamps.
- `word_progress`: user UID + stable vocabulary ID, repetition count, ease factor, interval, next review time, learned/saved flags, revision.
- `review_event`: immutable event UUID, word ID, quality, client event time, server receive time.
- `daily_session`: client-generated UUID, normalized study date, counts and completion.
- `sync_change`: monotonic sequence/cursor for incremental pull.

The 5,604-word vocabulary catalog should remain bundled in the first backend milestone. Only user-specific progress is synchronized. Catalog updates require explicit catalog versioning.

## Offline-first synchronization contract

1. Room remains the UI read source; network calls never block learning.
2. Every local mutation is committed to Room first.
3. A UUID event is appended to an outbox in the same local transaction.
4. WorkManager uploads pending events with bounded exponential backoff.
5. Server enforces a unique constraint on (user_uid, event_id).
6. Pull uses an opaque cursor; client applies remote changes transactionally.
7. Conflicts are resolved per word using explicit server revisions and immutable review events.
8. Authentication expiry pauses protected sync; local learning remains usable.

## Gemini security and quota

- Remove direct Gemini endpoint/key use from Android.
- Store `GEMINI_API_KEY` in a server secret manager/environment, never source control or mobile BuildConfig.
- Validate word input, response size and requested sentence count.
- Enforce per-account and per-IP rate limits and daily quota on server.
- Do not log prompts, ID tokens or provider credentials.
- Return typed errors for quota exceeded, provider unavailable and invalid input.
- Treat AI output as untrusted content; render as text, not HTML.

## Security baseline

- HTTPS outside local development.
- Firebase ID-token verification on every protected request.
- Ownership checks at service/repository boundaries.
- Request validation, payload limits, rate limiting and audit events.
- Secrets manager for DB credentials, Firebase Admin credentials and Gemini key.
- Separate dev/staging/prod environments.
- Database backups, retention policy, deletion workflow and migration tests.

## Delivery milestones

### M0 — Backend foundation
Create runnable Spring Boot project, health endpoint, Docker Compose PostgreSQL, Flyway and CI tests.

### M1 — Authentication
Verify Firebase ID tokens, implement `/me`, test missing/expired/invalid token and cross-user access.

### M2 — AI proxy
Implement Gemini server adapter, quota/rate limit, timeout, response validation and log redaction. Switch Android AI repository to backend.

### M3 — Sync
Implement schema, bootstrap, idempotent push/pull, cursor and conflict contract.

### M4 — Android integration
Add auth token provider, DTOs, repository contract and Room outbox. Add WorkManager sync with retry. Preserve offline learning.

### M5 — Production readiness
Load/security tests, monitoring, backups, privacy/deletion workflows, deployment and rollback runbook.

## Explicit non-goals for first release

- Replacing Room with remote reads.
- Requiring connectivity for learning or quizzes.
- Moving SM-2 to a network-dependent request.
- Uploading the static vocabulary catalog on each installation.

## Current status

Only this architecture proposal is present. No Spring Boot source, database migrations, Docker Compose, backend tests, deployment workflow or Android sync integration has been implemented yet.
