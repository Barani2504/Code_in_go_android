# AshCrypt Backend Requests & Proposed Contracts

> **Protocol Rule (Hard Constraint 1):** The live backend repository (`Code-in-go-Backend`) is completely untouched. Every endpoint listed below is implemented on Android via Retrofit, placed behind a `FeatureFlag` or safe fallback, with graceful offline handling.

---

## 1. Verifiable Mastery Certificate Generation & Verification

* **Purpose**: Issue a cryptographically signed completion certificate when a learner masters all 5 CNS chapters, with a public verification endpoint.
* **Method**: `POST`
* **Path**: `/api/v1/certificates/issue`
* **Auth**: `Bearer <JWT_TOKEN>`

### Request Body
```json
{
  "userId": "user_9872",
  "courseId": "cns_mastery_foundations",
  "completionTimestamp": 1794781200,
  "domainsCompleted": [
    "Classical Cryptography",
    "Symmetric Ciphers (DES/AES)",
    "Asymmetric Cryptography (RSA/ECC)",
    "Integrity & Hashes",
    "Protocols & TLS 1.3"
  ]
}
```

### Response Body (`200 OK`)
```json
{
  "certificateId": "CERT-ASH-2026-9872",
  "learnerName": "Kaushal S.",
  "courseTitle": "Cryptography & Network Security Professional Mastery",
  "issueDate": "2026-11-14T00:00:00Z",
  "signatureHash": "0x7F9A2B4C1D8E3F0A1B2C3D4E5F6A7B8C",
  "verificationUrl": "https://ashcrypt.app/verify/CERT-ASH-2026-9872",
  "qrPayload": "ashcrypt://verify/CERT-ASH-2026-9872"
}
```

---

## 2. Global Daily Cipher Challenge

* **Purpose**: Provide a single daily puzzle synchronized globally across iOS and Android learners.
* **Method**: `GET`
* **Path**: `/api/v1/challenges/daily?date=2026-11-14`
* **Auth**: Optional / `Bearer <JWT_TOKEN>`

### Response Body (`200 OK`)
```json
{
  "dayIndex": 42,
  "date": "2026-11-14",
  "ciphertext": "SKRHQLA",
  "cipherType": "CAESAR",
  "hint": "Classic Roman shift",
  "solutionLength": 7,
  "xpReward": 30,
  "gemReward": 5
}
```

---

## 3. Server-Authoritative Arena Wave Completion

* **Purpose**: Submit completed arena wave with anti-cheat idempotency token to validate score and update weekly league XP on the server.
* **Method**: `POST`
* **Path**: `/api/v1/arenas/wave-complete`
* **Auth**: `Bearer <JWT_TOKEN>`

### Request Body
```json
{
  "idempotencyKey": "wave_bcf_lvl3_1794781200",
  "arenaId": "block_cipher_forge",
  "levelIndex": 3,
  "waveNumber": 1,
  "score": 120,
  "clientTimestamp": 1794781200
}
```

### Response Body (`200 OK`)
```json
{
  "status": "ACCEPTED",
  "xpAwarded": 25,
  "gemsAwarded": 10,
  "newTotalXp": 450,
  "leagueRank": 4,
  "unlockedNextLevel": true
}
```

