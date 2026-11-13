package com.simats.codeingo.data.model

/**
 * ⚛️ Post-Quantum Cryptography & Modern Cryptographic Horizons
 *
 * Implements Phase 4.6:
 * - Lattice-Based Cryptography (NIST PQC Standards: ML-KEM / Crystals-Kyber, ML-DSA / Crystals-Dilithium)
 * - Quantum Threat Analysis: Shor's Algorithm (breaks RSA, Diffie-Hellman, ECC) vs Grover's Algorithm (halves symmetric keys)
 * - Zero-Knowledge Proofs (ZKP): Ali Baba Cave interactive intuition
 * - Passkeys & WebAuthn: FIDO2 public-key credential authentication
 */
data class ModernCryptoTopic(
    val id: String,
    val title: String,
    val subtitle: String,
    val tag: String,
    val securityLevel: String,
    val quantumResistance: String,
    val coreFormula: String,
    val conceptualExplanation: String,
    val keyTakeaways: List<String>
)

object ModernCryptoRegistry {
    val topics: List<ModernCryptoTopic> = listOf(
        ModernCryptoTopic(
            id = "pqc_crystals_kyber",
            title = "ML-KEM: Crystals-Kyber (Lattice KEM)",
            subtitle = "NIST Post-Quantum Key Encapsulation Standard (FIPS 203)",
            tag = "Post-Quantum",
            securityLevel = "NIST Level 1 / 3 / 5 (AES-128/192/256 equivalent)",
            quantumResistance = "SECURE against Quantum Computer attacks",
            coreFormula = "Module Learning With Errors (M-LWE): As + e mod q",
            conceptualExplanation = "Unlike RSA which depends on integer factorization, Kyber hides secrets in high-dimensional geometric lattices. Finding the closest vector in an n-dimensional lattice with added noise is intractable even for quantum computers running Shor's algorithm.",
            keyTakeaways = listOf(
                "Standardized by NIST in August 2024 as FIPS 203.",
                "Replaces classical Diffie-Hellman and RSA key agreement.",
                "Public key size: ~800 to 1,568 bytes (compact for lattice crypto)."
            )
        ),
        ModernCryptoTopic(
            id = "pqc_crystals_dilithium",
            title = "ML-DSA: Crystals-Dilithium (Digital Signatures)",
            subtitle = "NIST Post-Quantum Digital Signature Standard (FIPS 204)",
            tag = "Post-Quantum",
            securityLevel = "NIST Level 2 / 3 / 5",
            quantumResistance = "SECURE against Quantum Computer attacks",
            coreFormula = "Fiat-Shamir with Aborts over Module Lattices",
            conceptualExplanation = "Dilithium generates unforgeable digital signatures using rejection sampling over module lattices. If a signer produces a signature vector that leaks information about the secret key, it rejects the sample and regenerates, ensuring absolute zero-knowledge leakage.",
            keyTakeaways = listOf(
                "Standardized by NIST in August 2024 as FIPS 204.",
                "Replaces ECDSA and RSA signatures in TLS certificates and code signing.",
                "Strong resistance against side-channel and quantum polynomial-time attacks."
            )
        ),
        ModernCryptoTopic(
            id = "zero_knowledge_proofs",
            title = "Zero-Knowledge Proofs (ZKP) Intuition",
            subtitle = "Proving Truth Without Revealing the Secret",
            tag = "Privacy & Scaling",
            securityLevel = "Cryptographic Completeness, Soundness & Zero-Knowledge",
            quantumResistance = "Algorithm Dependent (STARKs are Quantum-Safe)",
            coreFormula = "Pr[Verifier Accepts | Honest Prover] = 1 (Completeness)",
            conceptualExplanation = "The Ali Baba Cave Analogy: Peggy wants to prove to Victor that she knows the secret magic word opening a locked door inside a circular cave. Victor stands outside and commands Peggy to emerge from path A or path B. After 40 rounds, Peggy succeeds every time. The probability of guessing is 1 in 2^40. Victor is mathematically convinced, yet never learns the secret word!",
            keyTakeaways = listOf(
                "Completeness: An honest prover convinces an honest verifier.",
                "Soundness: A dishonest prover cannot fool the verifier (except with negligible probability).",
                "Zero-Knowledge: The verifier learns nothing beyond the fact that the statement is true."
            )
        ),
        ModernCryptoTopic(
            id = "passkeys_webauthn",
            title = "Passkeys & FIDO2 / WebAuthn",
            subtitle = "Phishing-Resistant Public Key Authentication",
            tag = "Authentication",
            securityLevel = "Hardware Enclave Protected (FIDO Alliance)",
            quantumResistance = "Current ECDSA secp256r1; PQC hybrid migration in progress",
            coreFormula = "Sig = Sign_device(Challenge || Origin, DevicePrivateKey)",
            conceptualExplanation = "Passkeys eliminate shared secrets (passwords) entirely. The user's device creates an asymmetric key pair bound strictly to the website's domain origin. When logging in, the server sends a cryptographic challenge, the device signs it inside the Secure Enclave via biometrics, and the server verifies with the public key. Phishing is physically impossible because credentials never match spoofed domains.",
            keyTakeaways = listOf(
                "Eliminates phishing, credential stuffing, and server database password breaches.",
                "Cryptographically bound to the web origin (RP ID).",
                "Supported natively across Android, iOS, Windows, and modern browsers."
            )
        )
    )
}

