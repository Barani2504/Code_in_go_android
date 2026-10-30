# BACKEND CONTRACT SPECIFICATION
## Codeingo Android (Cryptography & Network Security) ↔ Spring Boot Backend
**Version:** 1.0.0  
**Status:** PROPOSED & FROZEN FOR CLIENT INTEGRATION  
**Target Backend:** Spring Boot REST API (`https://qkc9q70m-8080.inc1.devtunnels.ms` / `https://api.codeingo.com`)  
**Target Client:** Android Codeingo (`cns_defense` curriculum track)

---

## 1. Architectural Model: Remote Progression, Local Content
1. **Local Content Sovereignty:** The Android app delivers an 11-Chapter Cryptography & Network Security (CNS) curriculum (Basecamp Chapter 0, Core Chapters 1–5, Expansion Chapters 6–10, and 35 interactive cipher arenas). All lesson node titles, explanations, interactive cryptographic widgets, and simulations are rendered strictly from local Kotlin definitions (`ChapterRegistry.kt`).
2. **Backend Responsibilities:** The backend synchronizes **user state** only:
   - Authentication (JWT tokens, password, guest, OAuth)
   - Gamification currencies (`totalXp`, `gems`, `currentStreak`, `activePhoenixStage`)
   - Completion status flags (set of completed lesson IDs)

---

## 2. Existing Endpoints Utilized

### 2.1 Authentication & Profile
* `POST /api/auth/authenticate`  
  * Request: `{"email": "...", "password": "..."}`  
  * Response: `{"token": "...", "refreshToken": "...", "user": {...}}`
* `POST /api/auth/register`  
  * Request: `{"email": "...", "password": "...", "username": "...", "name": "...", "age": 25, "codingGoal": "cns", "activeCourseId": "cns_defense"}`  
  * Response: `{"token": "...", "refreshToken": "...", "user": {...}}`
* `POST /api/auth/guest-start`  
  * Request: `{"activeCourseId": "cns_defense"}`  
  * Response: `{"token": "...", "refreshToken": "...", "user": {...}}`
* `POST /api/auth/forgot-password`  
  * Request: `{"email": "..."}`  
  * Response: `{"message": "...", "success": true}`
* `POST /api/auth/verify-otp`  
  * Request: `{"email": "...", "otp": "..."}`  
  * Response: `{"message": "...", "success": true}`
* `POST /api/auth/reset-password`  
  * Request: `{"email": "...", "otp": "...", "newPassword": "..."}`  
  * Response: `{"message": "...", "success": true}`
* `GET /api/users/me` (Auth Required)  
  * Response: `UserDTO` (`id`, `username`, `email`, `totalXp`, `hearts`, `gems`, `currentStreak`, `highestStreak`, `activePhoenixStage`, `completedLessonIds`)
* `PATCH /api/users/me/update` (Auth Required)  
  * Request: `UpdateProfileRequest` (`name`, `activeCourseId`, `selectedLanguage`, `themeMode`, etc.)  
  * Response: `UserDTO`

### 2.2 Progress Tracking
* `GET /api/progress/status` (Auth Required)  
  * Response: `{"completedLessons": ["cns.c0.l1", ...], "completedUnits": ["1"], "currentUnitIndex": 1, "totalXp": 120, "hearts": 5, "gems": 450, "streakDays": 3}`
* `POST /api/progress/lesson/complete` (Auth Required)  
  * Request: `{"lessonId": "cns.c1.l1", "accuracyPercentage": 100}`  
  * Response: `{"xpEarned": 15, "totalXp": 135, "currentStreak": 4, "gemsEarned": 5, "totalGems": 455}`
* `POST /api/progress/sync-batch` (Auth Required)  
  * Request: `[{"lessonId": "cns.c1.l1", "accuracyPercentage": 100}, ...]`  
  * Response: Aggregated `BackendLessonCompleteResponse`

---

## 3. Required Backend Modifications & Enhancements (Change Requests)

### CR-1: JWT Token Refresh Endpoint
* **Current Gap:** Backend issues `refreshToken` in `AuthResponse`, but provides no refresh endpoint. When JWT access tokens expire, clients receive HTTP 401 and must force full re-login.
* **Requested Endpoint:**
  ```http
  POST /api/auth/refresh
  Content-Type: application/json

  {
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5..."
  }
  ```
* **Expected Response (`200 OK`):**
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5...",
    "expiresIn": 86400
  }
  ```
* **Error Response (`401 Unauthorized`):**
  ```json
  {
    "error": "Refresh token expired or revoked",
    "status": 401
  }
  ```

---

### CR-2: Course-Scoped Progress & Leaderboard Filtering
* **Current Gap:** iOS uses `activeCourseId: "dsa"` (Data Structures & Algorithms); Android uses `activeCourseId: "cns_defense"` (Cryptography & Network Security). Without scoping, lesson IDs and leaderboard rankings collide.
* **Requested Updates:**
  1. `GET /api/progress/status?courseId=cns_defense`  
     * Must return `completedLessons` filtered to the requested course.
  2. `GET /api/users/leaderboard?courseId=cns_defense&league=BRONZE`  
     * Returns rankings filtered to users studying the CNS defense curriculum.

---

### CR-3: Progress Completion Idempotency & Metadata
* **Current Gap:** If a network drops during `/api/progress/lesson/complete`, a retried request can inadvertently double-award XP, gems, or streak days.
* **Requested Request Schema Update for `/api/progress/lesson/complete`:**
  ```json
  {
    "lessonId": "cns.c1.l1",
    "accuracyPercentage": 100,
    "completedAtEpochMs": 1793181000000,
    "idempotencyKey": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d"
  }
  ```
* **Backend Behavior:**
  * If `idempotencyKey` has already been processed for this `userId`, return `200 OK` with the existing recorded XP/gems without double-incrementing.
  * If `lessonId` was already completed, update `bestAccuracy` if higher, but do not re-award full base XP.

---

### CR-4: Per-Item Status for `/api/progress/sync-batch`
* **Current Gap:** Current batch sync endpoint returns a single aggregated total. If 1 of 5 items fails, client cannot determine which item failed.
* **Requested Response Schema for `/api/progress/sync-batch`:**
  ```json
  {
    "results": [
      {
        "lessonId": "cns.c0.l1",
        "status": "SUCCESS",
        "xpEarned": 12,
        "gemsEarned": 5
      },
      {
        "lessonId": "cns.invalid.id",
        "status": "FAILED",
        "error": "Invalid lesson ID"
      }
    ],
    "totalXpEarned": 12,
    "totalGemsEarned": 5,
    "newTotalXp": 132,
    "currentStreak": 4
  }
  ```

---

### CR-5: Hearts Decoupling / Configurable Max Hearts
* **Current Gap:** Server hardcodes max hearts to 5 and `/api/users/me/refill-hearts` costs 350 gems. Android features a 10-heart max with a local regeneration timer and a 20-gem refill.
* **Requested Update:**
  * Accept `maxHearts: 10` in `UserDTO` or allow `activeCourseId: "cns_defense"` to default to 10 hearts.
  * In the interim, Android will treat hearts as client-authoritative to avoid truncating user capacity to 5.

---

## 4. Lesson ID Parity & Translation Standard

The backend must store and recognize the following standardized, namespaced IDs generated directly from Android's `ChapterRegistry`:

| Unit / Chapter | Local Level Indices | Standardized Backend Lesson IDs |
| :--- | :---: | :--- |
| **Chapter 0: Foundations Basecamp** | `101..105` | `cns.c0.l1`, `cns.c0.l2`, `cns.c0.l3`, `cns.c0.l4`, `cns.c0.l5` (Boss) |
| **Chapter 1: Classical Ciphers** | `1..5` | `cns.c1.l1`, `cns.c1.l2`, `cns.c1.l3`, `cns.c1.l4`, `cns.c1.l5` (Boss) |
| **Chapter 2: Symmetric Encryption** | `6..10` | `cns.c2.l1`, `cns.c2.l2`, `cns.c2.l3`, `cns.c2.l4`, `cns.c2.l5` (Boss) |
| **Chapter 3: Asymmetric Cryptography** | `11..15` | `cns.c3.l1`, `cns.c3.l2`, `cns.c3.l3`, `cns.c3.l4`, `cns.c3.l5` (Boss) |
| **Chapter 4: Hashes & Signatures** | `16..20` | `cns.c4.l1`, `cns.c4.l2`, `cns.c4.l3`, `cns.c4.l4`, `cns.c4.l5` (Boss) |
| **Chapter 5: Network Security** | `21..25` | `cns.c5.l1`, `cns.c5.l2`, `cns.c5.l3`, `cns.c5.l4`, `cns.c5.l5` (Boss) |
| **Chapter 6: Auth & Zero Trust** | `26..30` | `cns.c6.l1`, `cns.c6.l2`, `cns.c6.l3`, `cns.c6.l4`, `cns.c6.l5` (Boss) |
| **Chapter 7: MalSec & Defense** | `31..35` | `cns.c7.l1`, `cns.c7.l2`, `cns.c7.l3`, `cns.c7.l4`, `cns.c7.l5` (Boss) |
| **Chapter 8: PKI & Trust Chains** | `36..40` | `cns.c8.l1`, `cns.c8.l2`, `cns.c8.l3`, `cns.c8.l4`, `cns.c8.l5` (Boss) |
| **Chapter 9: Advanced Cryptanalysis** | `41..45` | `cns.c9.l1`, `cns.c9.l2`, `cns.c9.l3`, `cns.c9.l4`, `cns.c9.l5` (Boss) |
| **Chapter 10: Consensus & Blockchain**| `46..50` | `cns.c10.l1`, `cns.c10.l2`, `cns.c10.l3`, `cns.c10.l4`, `cns.c10.l5` (Boss) |
| **Interactive Arenas (35 Challenges)**| *Arena Hub* | `cns.arena.cipher.{1..7}`, `cns.arena.block.{1..7}`, `cns.arena.asym.{1..7}`, `cns.arena.hash.{1..7}`, `cns.arena.net.{1..7}` |
