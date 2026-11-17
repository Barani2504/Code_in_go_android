# AshCrypt Syllabus Coverage Matrix
## Complete Mapping: Anna University CS8792 & Stallings CNS (8th Ed.) to 25 AshCrypt Chapters

AshCrypt organizes the entire Cryptography and Network Security curriculum into **5 Realms × 5 Chapters = 25 Chapters**, exactly mapping the 5 Units of standard computer science university syllabi (Anna University CS8792, JNTU, VTU, Mumbai University) and William Stallings' *Cryptography and Network Security: Principles and Practice* (8th Edition).

---

## 🗺️ Master Curriculum Overview

| Realm | Title | Syllabus Unit | Stallings Chapters | Capstone Boss & Threat Arena |
|---|---|---|---|---|
| **Realm 1** | **Secrets 101** | Unit I: Security Concepts, Attacks & Classical Ciphers | Ch. 1, 2, 3 | **The Cipher Breaker** (`boss_unit_01`)<br>*Cipher Sanctum* |
| **Realm 2** | **Symmetric Forge** | Unit II: Symmetric Ciphers & Number Theory I | Ch. 4, 6, 7, 8 | **The Block Overlord** (`boss_unit_02`)<br>*Block Cipher Forge* |
| **Realm 3** | **Public-Key Citadel** | Unit III: Asymmetric Cryptography & Number Theory II | Ch. 9, 10, 11 | **The Quantum Cryptanalyst** (`boss_unit_03`)<br>*Key Exchange Citadel* |
| **Realm 4** | **Trust & Proof** | Unit IV: Cryptographic Data Integrity & Signatures | Ch. 12, 13, 14, 15 | **The Collision Beast** (`boss_unit_04`)<br>*Integrity Foundry* |
| **Realm 5** | **Real-World Defense** | Unit V: Network Protocols, Defenses & Modern Trends | Ch. 16, 17, 18, 19, 20+ | **The Cyber Titan** (`boss_unit_05`)<br>*Cyber Bastion* |

---

## 📋 Comprehensive Chapter-by-Chapter Mapping

### Realm 1: Secrets 101 (Unit I — Security Concepts & Classical Ciphers)
*Focus: Security fundamentals, threat categorization, historical ciphers, and cryptanalysis.*

| Chapter | Plain-Language Title | Detailed Syllabus Topics Covered | Stallings References | Visualizer / Interactive Lab |
|:---:|---|---|---|---|
| **1** | Why do we need security? | CIA Triad (Confidentiality, Integrity, Availability), security trends, security policies, policy trade-offs, legal and ethical frameworks, cybercrime laws. | Ch. 1 (1.1–1.4) | **CIA Triad Tap-Game** (`cia_triad`) |
| **2** | Attacks, services and defenses | Passive attacks (eavesdropping, traffic analysis) vs Active attacks (masquerade, replay, modification, DoS), OSI security architecture (X.800), security mechanisms, network security model. | Ch. 1 (1.5–1.7) | **Attack or Defense? Sorter** (`attack_defense_sorter`) |
| **3** | Swapping letters | Substitution ciphers, Caesar shift, Monoalphabetic cipher, Playfair cipher 5x5 grid, Hill cipher matrix arithmetic, Polyalphabetic Vigenère cipher. | Ch. 2 (2.1–2.2) | **Caesar Wheel & Playfair Grid** (`substitution_lab`) |
| **4** | Shuffling & hiding | Transposition ciphers, Rail Fence cipher, Columnar transposition, Product ciphers, Rotor machines, Steganography (spatial domain LSB, transform domain). | Ch. 2 (2.3–2.5) | **Rail Fence & Hide-in-Image Stego** (`transposition_stego`) |
| **5** | Perfect secrecy & breaking codes | Claude Shannon's perfect secrecy, One-Time Pad (OTP), entropy, frequency analysis, index of coincidence, Kasiski examination, cryptanalysis attacks (COA, KPA, CPA, CCA). | Ch. 2 (2.6), Ch. 3 | **Frequency Lab & OTP Simulator** (`cryptanalysis_lab`)<br>*(Boss: The Cipher Breaker)* |

---

### Realm 2: Symmetric Forge (Unit II — Symmetric Encryption & Math Toolkit I)
*Focus: Modular arithmetic, Feistel architecture, modern block ciphers (DES, 3DES, AES), and block modes.*

| Chapter | Plain-Language Title | Detailed Syllabus Topics Covered | Stallings References | Visualizer / Interactive Lab |
|:---:|---|---|---|---|
| **6** | Clock math (math toolkit I) | Modular arithmetic, clock congruence, Euclidean algorithm for GCD, Extended Euclidean algorithm, algebraic structures: groups, rings, and Galois fields. | Ch. 4 (4.1–4.5) | **Clock Arithmetic & Euclid Steps** (`modular_clock`) |
| **7** | How block ciphers think | Stream ciphers vs block ciphers, Claude Shannon's confusion and diffusion, Feistel cipher structure, round functions, Simplified DES (S-DES) walkthrough. | Ch. 6 (6.1–6.3) | **Feistel Builder & S-DES** (`feistel_builder`) |
| **8** | DES and why it aged | Data Encryption Standard (DES) 16-round anatomy, Initial Permutation (IP), round keys, 8 S-Boxes, 56-bit key weakness, Differential and Linear cryptanalysis intuition, Triple-DES (3DES). | Ch. 6 (6.4–6.6), Ch. 7 | **DES 16-Round Explorer** (`des_explorer`) |
| **9** | AES, the modern standard | Advanced Encryption Standard (AES) / Rijndael, 4 round transformations: SubBytes, ShiftRows, MixColumns, AddRoundKey, key expansion schedule, finite field GF(2⁸) multiplication. | Ch. 7 (7.1–7.5), Ch. 8 | **AES State Matrix & GF(2⁸) Multiplier** (`aes_state_matrix`) |
| **10** | Modes & stream ciphers | Block cipher modes of operation: ECB (Penguin leakage), CBC (IV chaining), CFB, OFB, CTR (parallelism), Authenticated GCM, RC4 stream cipher keystream, CSPRNGs. | Ch. 8 (8.1–8.5) | **Block Modes Penguin & RC4 Keystream** (`block_modes_rc4`)<br>*(Boss: The Block Overlord)* |

---

### Realm 3: Public-Key Citadel (Unit III — Asymmetric Cryptography & Math Toolkit II)
*Focus: Number theory, RSA, Diffie-Hellman, ElGamal, and Elliptic Curve Cryptography.*

| Chapter | Plain-Language Title | Detailed Syllabus Topics Covered | Stallings References | Visualizer / Interactive Lab |
|:---:|---|---|---|---|
| **11** | Prime numbers (math toolkit II) | Prime numbers, prime factor trapdoor, primality testing (Fermat test, Miller-Rabin probabilistic test), Euler's Totient function $\phi(n)$, Fermat's Little Theorem, Euler's Theorem. | Ch. 9 (9.1–9.3) | **Prime Sieve & Miller-Rabin Inspector** (`prime_sieve`) |
| **12** | CRT & discrete logs | Chinese Remainder Theorem (CRT), fast modular exponentiation (square-and-multiply), discrete logarithm problem, primitive roots modulo $p$. | Ch. 9 (9.4–9.6) | **CRT Clock & Discrete Log Trapper** (`crt_discrete_log`) |
| **13** | RSA step by step | Public-key principles, RSA algorithm: key generation $(p, q, n, e, d)$, encryption $C = M^e \pmod n$, decryption $M = C^d \pmod n$, integer factorization hardness, OAEP padding. | Ch. 9 (9.7–9.9) | **RSA Step-by-Step with Tiny Primes** (`rsa_stepper`) |
| **14** | Sharing keys safely | Diffie-Hellman key exchange $(g^a \bmod p, g^b \bmod p)$, paint-mixing analogy, Man-in-the-Middle (MITM) vulnerability, ElGamal cryptosystem, public-key distribution authority. | Ch. 10 (10.1–10.3) | **Diffie-Hellman Paint & ElGamal** (`dh_elgamal_lab`) |
| **15** | Elliptic curves | Elliptic curve mathematics over real numbers and finite fields $\mathbb{F}_p$, point addition, point doubling, Elliptic Curve Diffie-Hellman (ECDH), ECDSA, key size efficiency vs RSA. | Ch. 10 (10.4–10.6) | **Elliptic Curve Graph & Point Addition** (`ecc_point_addition`)<br>*(Boss: The Quantum Cryptanalyst)* |

---

### Realm 4: Trust & Proof (Unit IV — Integrity, Hashes & Digital Signatures)
*Focus: One-way hash algorithms, MAC/HMAC, digital signature standards, authentication protocols, and PKI.*

| Chapter | Plain-Language Title | Detailed Syllabus Topics Covered | Stallings References | Visualizer / Interactive Lab |
|:---:|---|---|---|---|
| **16** | Hash functions | Cryptographic hash properties: Preimage resistance, Second preimage resistance, Collision resistance, Avalanche effect, Birthday paradox & square-root bound, MD5 vulnerabilities, SHA-1, SHA-2, SHA-3 (Keccak). | Ch. 12 (12.1–12.5) | **SHA-256 Digest & Avalanche Meter** (`sha256_avalanche`) |
| **17** | MAC, HMAC and AEAD | Message Authentication Code (MAC) requirements, Hash-based MAC (HMAC) inner/outer padding ($ipad \oplus K, opad \oplus K$), Cipher-based MAC (CMAC), Galois/Counter Mode (GCM) for AEAD. | Ch. 13 (13.1–13.4) | **HMAC Pad Builder & Collision Sandbox** (`hmac_lab`) |
| **18** | Digital signatures | Digital signature properties, signing with private key, verifying with public key, non-repudiation, RSA digital signatures, Digital Signature Standard (DSS) and DSA algorithm. | Ch. 14 (14.1–14.3) | **Sign & Verify Interactive Workbench** (`sign_verify_lab`) |
| **19** | Proving who you are | Authentication factors (something you know, have, are), password storage, cryptographic salt, adaptive key derivation (Argon2, bcrypt, PBKDF2), challenge-response authentication, biometrics & liveness. | Ch. 15 (15.1–15.4) | **Salted Hash Verifier & Challenge-Response** (`auth_challenge`) |
| **20** | Kerberos and certificates | Kerberos v4 and v5 architecture, Authentication Server (AS), Ticket Granting Server (TGS), Ticket Granting Ticket (TGT), X.509 certificate formats, PKI trust chains, CA root certs, CRLs, OCSP. | Ch. 15 (15.5), Ch. 14 | **Kerberos Ticket Flow & X.509 Chain** (`kerberos_pki`)<br>*(Boss: The Collision Beast)* |

---

### Realm 5: Real-World Defense (Unit V — Network Protocols, Defenses & Modern Frontiers)
*Focus: Transport & network layer security, wireless security, perimeters, web attacks, and post-quantum frontiers.*

| Chapter | Plain-Language Title | Detailed Syllabus Topics Covered | Stallings References | Visualizer / Interactive Lab |
|:---:|---|---|---|---|
| **21** | Email & IP security | Pretty Good Privacy (PGP) web of trust, S/MIME corporate certificates, IPsec architecture, Authentication Header (AH) vs Encapsulating Security Payload (ESP), Tunnel vs Transport modes, IKE protocol. | Ch. 16, 17 | **PGP Ring & IPsec Tunnel Simulator** (`ipsec_pgp`) |
| **22** | Web & wireless security | SSL / TLS 1.3 1-RTT handshake, ECDHE forward secrecy, HTTP Strict Transport Security (HSTS), Wi-Fi security evolution (WEP weaknesses, WPA2 4-way handshake & KRACK, WPA3 Simultaneous Authentication of Equals / Dragonfly). | Ch. 18, 19 | **TLS 1.3 Handshake & Wi-Fi 4-Way** (`tls_wifi_handshake`) |
| **23** | Firewalls, IDS and intruders | Packet filtering firewalls, stateful inspection firewalls, application proxy gateways, DMZ architecture, Intrusion Detection Systems (IDS) vs Intrusion Prevention Systems (IPS), signature vs anomaly detection, honeypots. | Ch. 20, 21 | **Interactive Firewall Rule Builder & IDS** (`firewall_builder`) |
| **24** | Malware, web attacks, cloud & IoT | Malware taxonomy (viruses, worms, trojans, ransomware, rootkits), DoS and DDoS amplification / botnets, OWASP Top 10 web injection attacks (SQLi parameterized queries, XSS contextual encoding, CSRF tokens), Cloud and IoT threat models. | Ch. 21, 22 | **SQLi "Break the Login" & DoS Flood Meter** (`sqli_dos_sandbox`) |
| **25** | The future of security | Post-Quantum Cryptography (NIST standards: ML-KEM/Kyber, ML-DSA/Dilithium), Zero-Knowledge Proofs intuition (Ali Baba Cave), Merkle trees and cryptographic blockchain ledgers, Passkeys / WebAuthn and FIDO2 standards. | Ch. 23+ (Modern) | **ZKP Cave & Merkle Tree Explorer** (`zkp_merkle_pqc`)<br>*(Boss: The Cyber Titan)* |

---

## 🎯 Verification Matrix & Curriculum Integrity Checks

1. **Syllabus Completeness**: 100% of Anna University CS8792 Units I–V topics and Stallings 8th edition chapters are fully represented without gaps.
2. **Pedagogical Constraints**:
   - Every chapter features 3–4 mini-lessons ($\le 40$ words each) starting with a relatable real-world analogy.
   - Every chapter features 6–8 questions with rich explanations, option counter-explanations, and CVE/real-world historical breach citations.
   - Every chapter provides 3 actionable learning goals starting with *"You will be able to..."*.
   - Every chapter links directly to a dedicated visualizer lab and concludes with an Ash mascot recap.
3. **No Legacy DSA Content**: All legacy data-structures (stacks, queues, binary trees, sorting algorithms) have been replaced by cryptography and network security topics.
