# PARITY CONTRACT & RESKIN SPECIFICATION
## Codeingo Dual-Skin Architecture: iOS (DSA) ↔ Android (CNS)

---

### 1. Architectural Overview & Parity Contract

Codeingo is designed under a strict **Dual-Skin Architecture**:
- **iOS Build:** Teaches Data Structures & Algorithms (DSA). (FROZEN — untouched).
- **Android Build:** Teaches Cryptography & Network Security (CNS). (Active reskin).

Both builds represent two synchronized skins of **ONE unified educational platform**. To guarantee seamless cloud synchronisation, cross-platform progress migration, shared backend analytics, and zero schema drift, the **PARITY CONTRACT** is the highest architectural priority.

```
       ┌────────────────────────────────────────────────────────┐
       │                CODEINGO CLOUD BACKEND                  │
       │   Progress Tracking | XP Engine | Leaderboards | Sync  │
       └───────────────────────────┬────────────────────────────┘
                                   │
              ┌────────────────────┴────────────────────┐
              ▼                                         ▼
   ┌──────────────────────┐                  ┌──────────────────────┐
   │    iOS CLIENT        │                  │    ANDROID CLIENT    │
   │  DSA Curriculum      │                  │  CNS Curriculum      │
   │  (Untouched / Frozen)│                  │  (Reskinned / Active)│
   └──────────────────────┘                  └──────────────────────┘
              │                                         │
   ┌──────────────────────┐                  ┌──────────────────────┐
   │  Unit 1: Intro / BigO│                  │  Chapter 1: Ciphers  │
   │  Unit 2: Arrays      │                  │  Chapter 2: Symm Enc │
   │  Unit 3: Linked Lists│                  │  Chapter 3: Asymm Enc│
   │  Unit 4: Stacks/Queue│                  │  Chapter 4: Hashes   │
   │  Unit 5: Trees/Graphs│                  │  Chapter 5: NetSec   │
   └──────────────────────┘                  └──────────────────────┘
              ▲                                         ▲
              └────────────── IDENTICAL ────────────────┘
                         • unitId (1..5)
                         • Level IDs (c1_l1..c5_boss)
                         • Boss IDs (boss_unit_01..05)
                         • Progress Keys (dsa_total_xp)
                         • Heart & Streak Mechanics
```

---

### 2. Frozen Schema Contract & Invariants

The following schemas, database structures, interfaces, and identifiers are **FROZEN INVARIANTS**. No modification, renaming, or structural reformatting is permitted on Android:

#### A. Unit & Chapter Identifiers
| Unit ID | iOS Entity Title (DSA) | Android Entity Title (CNS) | Database / Route Key |
|:---:|:---|:---|:---|
| **1** | Introduction to Algorithms | Classical Ciphers & Cryptanalysis | `unit_01_intro` / `unitId = 1` |
| **2** | Arrays & Memory Layout | Symmetric Encryption & Block Ciphers | `unit_02_arrays` / `unitId = 2` |
| **3** | Linked Lists & Pointers | Asymmetric Cryptography & Public Keys | `unit_03_linkedlists` / `unitId = 3` |
| **4** | Stacks, Queues & Recursion | Cryptographic Hashes & Signatures | `unit_04_stack_queues` / `unitId = 4` |
| **5** | Trees, Graphs & Traversals | Network Security & Defense Protocols | `unit_05_trees` / `unitId = 5` |

#### B. Level Identifiers (25 Levels Total)
Every level identifier across both platforms maps byte-identically:
- **Chapter 1:** `c1_l1`, `c1_l2`, `c1_l3`, `c1_l4`, `c1_boss`
- **Chapter 2:** `c2_l1`, `c2_l2`, `c2_l3`, `c2_l4`, `c2_boss`
- **Chapter 3:** `c3_l1`, `c3_l2`, `c3_l3`, `c3_l4`, `c3_boss`
- **Chapter 4:** `c4_l1`, `c4_l2`, `c4_l3`, `c4_l4`, `c4_boss`
- **Chapter 5:** `c5_l1`, `c5_l2`, `c5_l3`, `c5_l4`, `c5_boss`

#### C. Boss Identifiers & HP Metrics
| Boss ID | Unit | Boss Name (CNS Skin) | Boss Title (CNS Skin) | Base HP | Time Limit |
|:---|:---:|:---|:---|:---:|:---:|
| `boss_unit_01` | 1 | The Sphinx of Shadows | Guardian of the Cipher Sanctum | 100 HP | 45s |
| `boss_unit_02` | 2 | The Block Overlord | Master of the S-Box Citadel | 120 HP | 50s |
| `boss_unit_03` | 3 | The Quantum Cryptanalyst | Breaker of Asymmetric Keys | 140 HP | 55s |
| `boss_unit_04` | 4 | The Collision Beast | Devourer of Hash Digest Integrity | 160 HP | 60s |
| `boss_unit_05` | 5 | The Cyber Titan | Sovereign of the Distributed Perimeter | 200 HP | 75s |

#### D. Persistent Progress Keys (SharedPreferences & Room)
To prevent progress corruption between versions, persistent storage keys remain frozen:
- `dsa_total_xp`: Accumulates all learner XP.
- `dsa_streak_days`: Tracks consecutive daily learning streak.
- `dsa_current_hearts`: Tracks active heart count (Max 3 hearts).
- `dsa_unlocked_level`: Stores latest unlocked level progression index.
- `dsa_completed_lessons`: Bitset / JSON set of completed level IDs.
- `dsa_phoenix_stage`: Phoenix evolution tier (1..5).

#### E. Game Rules & Heart Engine
- **Learner Hearts:** 3 hearts per session.
- **Heart Loss:** 1 heart deducted per incorrect answer or boss penalty.
- **XP Multipliers:**
  - Base question correct: +10 XP.
  - Combo streak (x2, x3, x5): Multipliers up to +50 XP.
  - Boss completion: `unitId * 50` XP.
  - Star awards: 5 stars (100% accuracy), 4 stars (80-99%), 3 stars (60-79%), 2 stars (40-59%), 1 star (1-39%).

---

### 3. Curriculum Content Mapping (CNS)

The 25 levels contain 125 meticulously authored, pedagogically rigorous Cryptography and Network Security items across 6 gamified exercise types:
- `MULTIPLE_CHOICE` (Concept Check / Trapdoor analysis)
- `FILL_CODE` (Syntax completion in Python / Rust / OpenSSL)
- `MATCH_PAIRS` (Algorithm properties / attack vectors / cipher primitives)
- `TRUE_FALSE_SWIPE` (Fact swipe verification)
- `ORDER_STEPS` (Parsons puzzle: Handshakes, key generation, protocol execution)
- `COMPLEXITY_DIAL` (Cryptographic attack work-factor / birthday bound dials)

#### Chapter 1: Classical Ciphers & Cryptanalysis (The Cipher Sanctum)
- **Level 1 (c1_l1):** Caesar Cipher & Modular Shifts ($C = (P + K) \pmod{26}$).
- **Level 2 (c1_l2):** Monoalphabetic Substitution & Frequency Analysis ($E, T, A, O, I, N$).
- **Level 3 (c1_l3):** Polyalphabetic Vigenère Cipher & Kasiski Examination ($C_i = (P_i + K_i) \pmod{26}$).
- **Level 4 (c1_l4):** Transposition Ciphers & Rail Fence Permutations.
- **Level 5 (c1_boss):** Boss Battle — The Sphinx of Shadows (Brute force resistance, Caesar wrap-around, Vigenère key recovery).

#### Chapter 2: Symmetric Encryption & Block Ciphers (The S-Box Citadel)
- **Level 6 (c2_l1):** DES & The Feistel Network Architecture (16 rounds, S-box non-linearity, key scheduling).
- **Level 7 (c2_l2):** AES Architecture & Galois Field $GF(2^8)$ (SubBytes, ShiftRows, MixColumns, AddRoundKey).
- **Level 8 (c2_l3):** Block Cipher Modes of Operation (ECB penguin leakage, CBC IV chaining, CTR stream conversion).
- **Level 9 (c2_l4):** Authenticated Encryption & Galois/Counter Mode (GCM GHASH, GMAC, nonce uniqueness).
- **Level 10 (c2_boss):** Boss Battle — The Block Overlord (AES round sequences, GCM tag verification, IV non-reuse).

#### Chapter 3: Asymmetric Cryptography & Public Keys (The Key Keepers)
- **Level 11 (c3_l1):** Modular Arithmetic, Coprimality & Euler's Totient $\phi(n)$.
- **Level 12 (c3_l2):** Diffie-Hellman Key Exchange & Discrete Logarithm Problem ($g^{ab} \pmod p$).
- **Level 13 (c3_l3):** RSA Public Key Cryptosystem ($n = pq, ed \equiv 1 \pmod{\phi(n)}$).
- **Level 14 (c3_l4):** Elliptic Curve Cryptography ($y^2 = x^3 + ax + b$, secp256k1 point doubling/addition).
- **Level 15 (c3_boss):** Boss Battle — The Quantum Cryptanalyst (Shor's algorithm threat, trapdoor one-way functions, RSA private key generation).

#### Chapter 4: Cryptographic Hashes & Signatures (The Integrity Realm)
- **Level 16 (c4_l1):** Hash Function Properties (Pre-image, Second Pre-image, Collision Resistance).
- **Level 17 (c4_l2):** SHA-2 (Merkle-Damgård) vs SHA-3 (Keccak Sponge Construction).
- **Level 18 (c4_l3):** HMAC & Length Extension Attack Mitigation ($HMAC(K, m) = H((K \oplus opad) \parallel H((K \oplus ipad) \parallel m))$).
- **Level 19 (c4_l4):** Digital Signatures & X.509 Public Key Infrastructure (Non-repudiation, CA chain validation, OCSP).
- **Level 20 (c4_boss):** Boss Battle — The Collision Beast (Birthday paradox $2^{n/2}$, MD5/SHA-1 deprecation, constant-time verification).

#### Chapter 5: Network Security & Protocols (The Cyber Bastion)
- **Level 21 (c5_l1):** TLS 1.3 Transport Security (1-RTT handshake, speculative Key Share in ClientHello, PFS mandate, AEAD only).
- **Level 22 (c5_l2):** IPsec & Secure VPN Tunnels (AH integrity vs ESP confidentiality, Tunnel Mode encapsulation vs Transport Mode).
- **Level 23 (c5_l3):** Stateful Firewalls & Zero Trust Architecture (5-tuple connection tables, microsegmentation, "Never Trust, Always Verify").
- **Level 24 (c5_l4):** Network Attacks & Defenses (TCP SYN Cookies stateless defense, ARP Spoofing Dynamic ARP Inspection, DNSSEC trust anchors).
- **Level 25 (c5_boss):** Grand Finale Boss — The Cyber Titan (Complete defense protocol, TLS 1.3, IPsec ESP, SYN flood mitigation, Zero Trust perimeter).

---

### 4. The Phoenix Lock Contract

The Phoenix mascot is Codeingo's iconic visual anchor and emotional core. The following rules are strictly enforced:

1. **Asset Immutability:** All 54 Phoenix assets in `res/drawable*` and `res/raw*` are permanently fingerprinted and locked. No PNG, WebP, SVG, or GIF file may be replaced, edited, renamed, or deleted.
2. **Coil GIF Engine:** Animated GIFs are decoded at native framerate with infinite repetition across all effect tiers (`FULL`, `REDUCED`, `OFF`).
3. **No Pixel Filtering:** Jetpack Compose modifiers applied to Phoenix composables MUST NOT include `colorFilter`, `blur`, `blendMode`, or clipping masks that alter mascot pixels.
4. **Surrounding Geometry Separation:**
   - `BehindPhoenixGlow`: Rendered on a canvas positioned strictly *behind* the mascot in Z-order.
   - `BossTimerRing`: Rendered with an inner padding margin of at least 8.dp to prevent stroke overlap.
   - `DynamicPillHUD`: Anchored to the top status bar / camera cutout, leaving a minimum of 40.dp clearance from the mascot's head feathers.

---

### 5. Migration & Refactoring Advisory

For future package clean-up (e.g., standardizing `com.simats.codeingo.data.model.DSAExerciseType` to a domain-agnostic `ExerciseType`):
- Perform refactoring using Git symbolic rename (`git mv`) to preserve file history.
- Maintain deprecated typealiases during transition:
  ```kotlin
  @Deprecated("Use ExerciseType for dual-skin parity", ReplaceWith("ExerciseType"))
  typealias DSAExerciseType = ExerciseType
  ```
- Retain Room migration version numbers without dropping old column names.

---

### 6. Interactive Visualizers & 3D Battle Arenas Specification (CNS)

The 10 interactive visualizer engines and 5 standalone 3D battle arena modules have been fully migrated to Cryptography & Network Security, maintaining exact enum keys (`DSAVisualizerType`) and route identifiers:

#### A. 10 Interactive CNS Visualizers (`CNSVisualizers.kt`)
1. **Caesar & Substitution Cipher (`ARRAY`):** Modular alphabet shift wheel $C = (P + K) \pmod{26}$, direction toggle (Encrypt $P+K$ vs Decrypt $C-K$), brute-force drawer exploring all 26 shifts, live frequency distribution bar chart analysis, and educational toy security warnings.
2. **DES Feistel Network (`STACK`):** 16-round interactive Feistel engine, 32-bit $L_i, R_i$ register swaps, round subkeys $K_i$, $F(R_i, K_i)$ expansion preview, and EFF Deep Crack deprecation advisory.
3. **AES 128-Bit State Matrix & Modes (`QUEUE`):** Multi-mode studio containing (1) $4\times 4$ byte state matrix in hex with interactive tabs for `SubBytes` (GF($2^8$) S-Box), `ShiftRows`, `MixColumns`, and `AddRoundKey`; and (2) Block Cipher Operating Modes tab comparing ECB (penguin duplicate block pattern leakage), CBC (with IV chaining), CTR (stream mode), and GCM (AEAD with GMAC authentication).
4. **Vigenère Polyalphabetic Cipher (`LINKED_LIST`):** Secret keyword repeating keystream alignment ribbon, bidirectional Encrypt/Decrypt modes, Tabula Recta coordinate intersection lookup, and Kasiski / Index of Coincidence cryptanalysis notes.
5. **Diffie-Hellman Key Agreement (`BINARY_TREE`):** Public prime $p=23$ and generator $g=5$, interactive private secret sliders for Alice ($a$) and Bob ($b$), insecure eavesdropper channel, and shared secret $g^{ab} \pmod p$ matching verification.
6. **RSA Public Key Cryptosystem & Signatures (`AVL_TREE`):** Primes $(p=61, q=53)$, modulus $n=3233$, Euler's totient $\phi(n)=3120$, public key $(e=17, n)$, private key $(d=2753, n)$, dual modes for Confidentiality ($c = m^e \bmod n, m = c^d \bmod n$) and Digital Signatures ($s = m^d \bmod n, m = s^e \bmod n$), with Bleichenbacher padding oracle / RSA-PSS advisories.
7. **Elliptic Curve Points (`TRIE`):** Explicit real-number $\mathbb{R}$ curve canvas illustration with secant chord line through $P$ and $Q$ and scalar slider $kP$, coupled with a toy finite field $\mathbb{F}_{17}$ arithmetic engine ($y^2 = x^3 + x + 6 \bmod 17$) computing discrete point coordinates and explaining ECDLP hardness.
8. **SHA-256 Hash Construction & Avalanche (`GRAPH`):** Live plaintext input, 512-bit block padding breakdown, 8 working registers $(A..H)$, real-time 256-bit hexadecimal digest calculation, and an interactive Avalanche comparison engine comparing two inputs with 1-character difference to demonstrate $\sim 50\%$ bit diffusion.
9. **Real HMAC-SHA256 & Password Salting (`SORTING`):** Genuine RFC 2104 nested hashing using Java `Mac.getInstance("HmacSHA256")` with editable key and message, coupled with an interactive Birthday Paradox $2^{n/2}$ collision bound simulator and a dedicated Password Hashing & Salting card (Argon2id, bcrypt, cryptographic salts vs GPU rainbow tables).
10. **TLS 1.3 Handshake Swim-Lane Tracer (`STEP_RECORDER`):** Client-to-server and server-to-client swim-lane packet inspector (ClientHello, ServerHello, EncryptedExtensions, CertificateVerify, Finished, AEAD application traffic) with key derivation indicators and a TLS 1.2 vs TLS 1.3 architectural comparison matrix.

#### B. 5 Flagship 3D Battle Arenas
1. **The Cipher Sanctum Arena (`ArrayKingdomArenaScreen`):** Caesar shift ring, modular offset cryptanalysis, frequency analysis hunt, Vigenère keystream alignment, unscrambled columnar transposition grid, 20s cryptanalysis sprint, and 3-phase Sphinx of Shadows Boss battle with Caesar decryption challenges and heart deductions on incorrect answers.
2. **The S-Box Citadel Arena (`LinkedListRoadArenaScreen`):** AES SubBytes S-Box lookup table with heart penalty, animated 4×4 ShiftRows cyclic row rotation grid, MixColumns GF($2^8$) column matrix diffusion, AddRoundKey bitwise XOR calculation quiz, 16-round Feistel swap, avalanche diffusion sprint, and 3-phase Block Overlord Boss battle testing AES internal transformations.
3. **The Key Keepers Spire Arena (`StackTowerArenaScreen`):** Modular exponentiation $b^e \bmod m$, Diffie-Hellman secret synthesis quiz with heart deductions, RSA Euler's totient $\phi(n)$, confidential private key distribution challenge, digital signature verification, prime factorization sprint, and 3-phase Quantum Cryptanalyst Boss battle testing Shor's algorithm vulnerabilities and NIST post-quantum cryptography (ML-KEM/Kyber).
4. **The Integrity Realm Station Arena (`QueueStationArenaScreen`):** SHA-256 512-bit block chunking quiz, 1-bit avalanche effect demonstration, Keccak 5×5 lane sponge absorb/squeeze phases, Merkle tree root validation, Birthday Paradox collision bound ($2^{n/2}$), HMAC nested authentication purpose quiz, and 3-phase Collision Beast Boss battle with collision & length-extension defense challenges.
5. **The Cyber Bastion Citadel Arena (`BinaryTreeForestArenaScreen`):** TLS 1.3 1-RTT handshake packet flow, modern AEAD cipher suite negotiation, IPsec ESP tunnel packet segmentation (SPI & sequence anti-replay), stateful firewall port 443 inspection, X.509 PKI certificate chain of trust verification, DDoS SYN Cookie stateless defense challenge, and 3-phase Cyber Titan Boss battle defending against SYN floods, MITM rogue certificates, and data exfiltration.

---

## 7. Visualizer Engine Theme Adaptation (Light & Dark Mode Parity)

All 10 Cryptography and DSA visualizer topics and surrounding host containers adapt to the user's active theme (`AppTheme.LIGHT`, `AppTheme.DARK`, or `AppTheme.SYSTEM`):

- **Dynamic Theme Tokens (`LocalDynamicThemeColors.current`):**
  - Card, input, and container backgrounds seamlessly adapt between `PhoenixLightCard` (`#FFFFFF`), `PhoenixLightInput` (`#ECEFF5`), and dark modes (`PhoenixCard` `#131D2E`, `PhoenixInput` `#0F1726`).
  - Text and subtext dynamically bind to `textPrimary` and `textSecondary` (`PhoenixLightText` `#121826` in light mode, `#FFFFFF` in dark mode).
- **High-Contrast Light Accent Pairs:**
  - Amber / Gold: `#B45309` (Light) vs `AmberGold` `#FFC800` (Dark)
  - Cyan: `#0284C7` (Light) vs `Color.Cyan` (Dark)
  - Teal / Mint: `#047857` / `#0D9488` (Light) vs `Color(0xFF00CD9C)` / `DsaTeal` (Dark)
  - Emerald: `#15803D` (Light) vs `DsaGreen` (Dark)
- **Canvas & Interactive Elements:**
  - ECC Point Addition canvas dynamically adapts coordinate axes, grid ticks, and tangent chord lines between subtle dark (`Color.Black.copy(0.12f)`) and light strokes (`Color.White.copy(0.18f)`).
  - Language implementation code cards feature language chips and complexity tags that adapt between `dynamicColors.inputBackground` and themed amber borders.
  - Quick topic switcher carousels and deep-dive header metrics adapt background tints and borders dynamically.

---

## 8. Application-Wide Theme Adaptation & Enhanced Typography System

1. **Full Light Mode & Dark Mode System:**
   - **5 Battle Arenas (`ArrayKingdom`, `LinkedListRoad`, `StackTower`, `QueueStation`, `BinaryTreeForest`):**
     - Dark Mode: Immersive cosmic gradients (Crimson Obsidian, Blood Quartz, Molten Amber, Void Violet, Deep Emerald).
     - Light Mode: Elegant ambient realm gradients (Soft Rose Amber, Soft Blush Crimson, Warm Cream Amber, Soft Lavender, Soft Mint).
     - Adaptive question dialogs, shift rings, puzzle matrices, code slots, and interactive cells bind dynamically to `cardBackground` (`#FFFFFF` in light mode), `inputBackground` (`#ECEFF5` in light mode), and `textPrimary` (`#121826` in light mode).
   - **Dashboard & Navigation:**
     - Level node detail popup cards feature theme-adaptive gradient envelopes, close buttons, action tags, and high-contrast text.
     - `BottomNavBar` floating glass capsule and `TopStatsHeader` status bar adapt specular highlights and drop shadows.
   - **Onboarding & Auth:**
     - `ProfileCreationScreen` and `OnboardingScreen` containers, guide bubbles, age selector chips, and bottom sheets adapt dynamically.

2. **Enhanced Typography System (`Type.kt`):**
   - **Bundled Font Resources (`res/font/`):**
     - `outfit.ttf`: Modern, geometric, hyper-legible tech typeface for headlines, metrics, code commentary, and body copy.
     - `nunito.ttf`: Rounded geometric sans-serif typeface matching Apple's `.system(..., design: .rounded)` standard for gamified learning.
   - **Micro-Tuned Typography Scales:**
     - Proportional letter spacing (`-0.5.sp` to `+0.3.sp`) and optimized line heights (`15.sp` to `38.sp`) across all 15 Material 3 typography tokens (`displayLarge` through `labelSmall`).
   - **Universal Font Inheritance (`Theme.kt`):**
     - Global `LocalTextStyle` provides `AppFontFamily` (`Outfit`) and `dynamicColors.textPrimary` across all unstyled composables with zero text drift or invisible white-on-white artifacts.

