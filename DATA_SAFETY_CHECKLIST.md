# GOOGLE PLAY DATA SAFETY FORM CHECKLIST & COMPLIANCE
## Codeingo Android (Cryptography & Network Security)
**Target Package:** `com.simats.codeingo`  
**Compliance Version:** 1.0.0  
**Applicable Policies:** Google Play User Data Policy, Account Deletion Policy, Families Policy (COPPA / GDPR-K)

---

## 1. Data Collection & Sharing Overview

| Data Category | Data Type | Collected? | Shared with 3rd Parties? | Purpose | Ephemeral? | Required / Optional |
| :--- | :--- | :---: | :---: | :--- | :---: | :--- |
| **Personal Info** | Name / Username | Yes | No | Account management, in-app display | No | Optional |
| **Personal Info** | Email Address | Yes | No | Account authentication, password reset | No | Required for registered users; not collected for guests |
| **Personal Info** | Age | Yes | No | Compliance & minor protection (COPPA) | No | Optional |
| **App Activity** | Progress & Gamification (XP, Gems, Streak, Completed Lessons) | Yes | No | App functionality, cross-device sync | No | Required for progress tracking |
| **App Activity** | Interactive Mascot Emotion Telemetry | Yes | No | Analytics & gameplay feedback | No | **Strictly Optional (Opt-in only via `sec_telemetry`)** |
| **Financial Info** | Payment / Purchase Info | **No** | No | In-app economy uses local virtual gems only; no real-money transactions | N/A | N/A |
| **Location** | Precise / Approximate Location | **No** | No | None | N/A | N/A |
| **Messages / Contacts**| SMS, Contacts, Photos | **No** | No | None | N/A | N/A |

---

## 2. Security Practices

* **Encryption in Transit:**
  * All communications between the app and the backend use industry-standard HTTPS (TLS 1.2 / TLS 1.3).
  * Cleartext traffic is disabled globally (`usesCleartextTraffic="false"`); emulator loopback is permitted exclusively in debug builds via debug network security configuration.
* **Encryption at Rest:**
  * Sensitive authentication tokens (JWT) are encrypted with Google Tink AEAD (`AesGcmKeyManager`) backed by the Android Keystore.
  * Local passwords use salted PBKDF2-HMAC-SHA256 (legacy plaintext passwords purged via automatic migration).
* **Data in Crash Reports & Logs:**
  * `HttpLoggingInterceptor` redaction: `Authorization: Bearer [REDACTED]`.
  * Authentication endpoints (`/api/auth/*`) log headers only, with complete omission of request/response bodies.
  * Tokens and passwords excluded from Auto Backup (`allowBackup="true"`, backup rules exclude encrypted token storage).

---

## 3. Account & Data Deletion Mechanisms (Google Play Policy Compliance)

Google Play requires apps that support account creation to provide both **in-app** and **web-based** paths for users to delete their account and associated data.

### 3.1 In-App Deletion
* **Location:** `Settings` → `Account` Tab → `⚠️ DELETE ACCOUNT & DATA`
* **Action:**
  1. Purges local session tokens from encrypted storage.
  2. Clears the Room database outbox queue (`offline_sync_queue`).
  3. Purges local DataStore preferences, progression snapshots, and cached mistakes.
  4. Resets the application state and redirects to the unauthenticated onboarding/login screen.

### 3.2 Web-Based Deletion Request Link
* **Public URL:** `https://codeingo.com/delete-account`
* **In-App Entry Points:**
  - Direct button in `Settings` → `Account` tab: `🌐 REQUEST DELETION VIA WEB`.
  - Linked inside the in-app confirmation dialog.
  - Linked within the public Privacy Policy.

---

## 4. Minor Protection & Age-Gating (Families & COPPA Compliance)

* **Age Collection:**
  - Users under the age of 13 (or under 16 in applicable EU jurisdictions) are flagged as minor accounts.
* **Child Account Restrictions:**
  1. **Telemetry Disabled:** `sec_telemetry` defaults to `false` and cannot be activated for minor accounts.
  2. **Leaderboard Privacy:** Minors' real names or emails are never shown on public course leaderboards; fallback avatar and generic rank identifiers ("Cadet") are utilized.
  3. **Zero Behavioral Tracking:** No personalized ads or third-party behavioral profiling.

