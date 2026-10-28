package com.simats.codeingo.ui.visualizer.model

import com.simats.codeingo.data.model.DSAVisualizerType

data class CNSChallengeQuestion(
    val id: String,
    val labType: DSAVisualizerType,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val hint: String
)

object CNSChallengeRepository {

    val CHALLENGES: List<CNSChallengeQuestion> = listOf(
        // --- 1. Caesar Cipher (ARRAY) ---
        CNSChallengeQuestion(
            id = "caesar_1",
            labType = DSAVisualizerType.ARRAY,
            question = "Why is single-letter frequency analysis capable of breaking any Caesar cipher effortlessly?",
            options = listOf(
                "Because it decrypts 128-bit blocks simultaneously",
                "Because each letter is mapped 1:1, shifting the frequency distribution without flattening it",
                "Because the Caesar key changes with every single letter",
                "Because it uses an asymmetric trapdoor function"
            ),
            correctIndex = 1,
            explanation = "In monoalphabetic substitution ciphers like Caesar, every instance of 'E' shifts to the exact same ciphertext character. The entire letter frequency profile simply shifts along the alphabet, leaving the peaks (E, T, A, O, I, N) identifiable.",
            hint = "Look at the 26-letter frequency histogram tab: notice how the distribution curve shifts rigidly without changing shape."
        ),
        CNSChallengeQuestion(
            id = "caesar_2",
            labType = DSAVisualizerType.ARRAY,
            question = "What is the exact keyspace size of the standard English Caesar cipher?",
            options = listOf(
                "2^128 possible keys",
                "256 possible keys",
                "26 shifts (or 25 non-trivial shifts)",
                "Infinite combinations"
            ),
            correctIndex = 2,
            explanation = "Since the English alphabet has 26 letters, shifting by 0 is identical to plaintext, leaving only 25 non-trivial key shifts. This takes less than 1 millisecond for any computer to test exhaustively.",
            hint = "How many distinct letters can you shift by before wrapping back to the beginning?"
        ),
        CNSChallengeQuestion(
            id = "caesar_3",
            labType = DSAVisualizerType.ARRAY,
            question = "If 'E' (the most frequent letter in English) appears most frequently as 'H' in an intercepted Caesar ciphertext, what is the most likely key shift K?",
            options = listOf(
                "K = 1",
                "K = 3",
                "K = 7",
                "K = 13"
            ),
            correctIndex = 1,
            explanation = "'E' is index 4 (0-based) and 'H' is index 7. (7 - 4) = 3. Therefore, the probable shift key used is K = 3.",
            hint = "Calculate the distance from E to H: E (4) -> F (5) -> G (6) -> H (7)."
        ),

        // --- 2. DES Feistel Network (STACK) ---
        CNSChallengeQuestion(
            id = "des_1",
            labType = DSAVisualizerType.STACK,
            question = "What is the key length of DES in bytes/bits, and why is it considered deprecated today?",
            options = listOf(
                "128 bits; deprecated due to side-channel timing leaks",
                "56 effective bits (64 bits with 8 parity bits); cracked in 56 hours by EFF Deep Crack in 1998",
                "32 bits; deprecated because it cannot encrypt images",
                "256 bits; still actively considered secure by NIST"
            ),
            correctIndex = 1,
            explanation = "Standard DES accepts a 64-bit key, but 8 bits are parity bits, leaving only 56 effective key bits (2^56 ≈ 7.2 × 10^16 keys). In 1998, EFF built the custom Deep Crack machine for $250,000 which cracked DES keys in under 56 hours.",
            hint = "Notice the deprecation banner in the lab: 56-bit keys are far too short to resist modern parallel hardware."
        ),
        CNSChallengeQuestion(
            id = "des_2",
            labType = DSAVisualizerType.STACK,
            question = "What fundamental property of Feistel networks allows decryption using the exact same round function F without inverting it?",
            options = listOf(
                "The round function F must be an invertible mathematical bijection",
                "XOR is self-inverting: (A ⊕ B) ⊕ B = A, so swapping halves and reversing subkeys decrypts automatically",
                "The S-boxes are mathematically linear",
                "The initial permutation IP cancels out the final permutation FP"
            ),
            correctIndex = 1,
            explanation = "Because L_{i+1} = R_i and R_{i+1} = L_i ⊕ F(R_i, K_i), running the Feistel network in reverse with subkeys K_16 down to K_1 recovers L_i using (L_i ⊕ F) ⊕ F = L_i. The function F itself never needs to be invertible!",
            hint = "Think about what happens when you XOR a value with the same mask twice."
        ),
        CNSChallengeQuestion(
            id = "des_3",
            labType = DSAVisualizerType.STACK,
            question = "What is the key complementation property of DES: if C = DES(P, K), what is DES(~P, ~K)?",
            options = listOf(
                "Zero (0x0000000000000000)",
                "Identical to C",
                "The bitwise complement of C (~C)",
                "An invalid ciphertext that crashes the cipher"
            ),
            correctIndex = 2,
            explanation = "Due to the XOR operations in the key schedule and Feistel rounds, bitwise complementing both plaintext and key causes every internal register and output to be complemented: DES(~P, ~K) = ~DES(P, K). This reduces brute-force search space by a factor of 2.",
            hint = "Notice how (~A) ⊕ (~B) = A ⊕ B."
        ),

        // --- 3. AES-128 State Matrix (QUEUE) ---
        CNSChallengeQuestion(
            id = "aes_1",
            labType = DSAVisualizerType.QUEUE,
            question = "Which specific transformation step in the AES round provides non-linear confusion?",
            options = listOf(
                "ShiftRows (cyclically permutes row bytes)",
                "SubBytes (multiplicative inverse in GF(2^8) S-box + affine mapping)",
                "MixColumns (matrix multiplication in GF(2^8))",
                "AddRoundKey (bitwise XOR with round subkey)"
            ),
            correctIndex = 1,
            explanation = "SubBytes is the only non-linear component in AES! It computes the multiplicative inverse in the Galois Field GF(2^8) followed by an affine transformation to resist differential and linear cryptanalysis.",
            hint = "Look at the 4 steps: ShiftRows and MixColumns are linear operations; which step substitutes bytes via the S-Box?"
        ),
        CNSChallengeQuestion(
            id = "aes_2",
            labType = DSAVisualizerType.QUEUE,
            question = "Why is ECB (Electronic Codebook) mode strictly forbidden for encrypting structured messages or images?",
            options = listOf(
                "Because it is too slow to run on mobile processors",
                "Because identical 16-byte plaintext blocks always produce identical ciphertext blocks, leaking data patterns",
                "Because it requires an authenticated Diffie-Hellman handshake",
                "Because it cannot encrypt files larger than 128 bytes"
            ),
            correctIndex = 1,
            explanation = "In ECB mode, each 16-byte block is encrypted independently with key K: C_i = E_K(P_i). Identical input blocks yield identical output blocks, causing image silhouettes (such as the famous ECB Tux penguin) to remain completely visible.",
            hint = "Check the 'Modes & Attack Demos' tab: what happened to identical blocks when encrypted under ECB?"
        ),
        CNSChallengeQuestion(
            id = "aes_3",
            labType = DSAVisualizerType.QUEUE,
            question = "In AES Counter (CTR) mode, what critical catastrophe occurs if the same Nonce/IV is ever reused with the same key?",
            options = listOf(
                "The cipher generates an AEADBadTagException",
                "The key is corrupted on disk",
                "Keystream cancels out: C1 ⊕ C2 = P1 ⊕ P2, allowing attackers to recover plaintexts via crib dragging",
                "The ciphertext becomes 0"
            ),
            correctIndex = 2,
            explanation = "In CTR mode, C = P ⊕ Keystream. If the keystream KS is identical for two messages, C1 ⊕ C2 = (P1 ⊕ KS) ⊕ (P2 ⊕ KS) = P1 ⊕ P2. The encryption is completely stripped, leaving a two-time pad that is trivially broken.",
            hint = "Review the CTR Nonce Reuse experiment in the Attack tab: notice how C1 ⊕ C2 matched P1 ⊕ P2 byte-for-byte."
        ),

        // --- 4. Vigenère Cipher (LINKED_LIST) ---
        CNSChallengeQuestion(
            id = "vigenere_1",
            labType = DSAVisualizerType.LINKED_LIST,
            question = "How does the Vigenère polyalphabetic cipher successfully defeat single-letter frequency analysis?",
            options = listOf(
                "By using SHA-256 to hash each character",
                "By using a repeating secret keyword so that each occurrence of a plaintext letter is shifted by different key letters",
                "By permuting 64-bit blocks across 16 Feistel rounds",
                "By encrypting backwards from the end of the text"
            ),
            correctIndex = 1,
            explanation = "Because the keyword repeats cyclically, a common letter like 'E' will be encrypted with different Caesar shifts depending on its alignment with the keyword, flattening monoalphabetic frequency spikes.",
            hint = "Look at the Tabula Recta mapping strip: does the same plaintext letter always encrypt to the same ciphertext letter?"
        ),
        CNSChallengeQuestion(
            id = "vigenere_2",
            labType = DSAVisualizerType.LINKED_LIST,
            question = "What does Kasiski Examination look for in a Vigenère ciphertext to deduce the keyword length L?",
            options = listOf(
                "Repeated 3-grams/words and the common factors of the distances between them",
                "The presence of uppercase letters",
                "The length of the entire file divided by 2",
                "Prime numbers in the ASCII values"
            ),
            correctIndex = 0,
            explanation = "If a common word or 3-gram in the plaintext happens to align with the same position in the repeating keyword, it encrypts to the exact same ciphertext 3-gram. The distance between these repeated segments will almost always be a multiple of the key length L.",
            hint = "Check the Kasiski tab: what do the distance GCD factors tell us about the keyword?"
        ),
        CNSChallengeQuestion(
            id = "vigenere_3",
            labType = DSAVisualizerType.LINKED_LIST,
            question = "What is the expected Index of Coincidence (IoC) for standard English plaintext versus random text / flat polyalphabetic text?",
            options = listOf(
                "English ≈ 0.0667, Random ≈ 0.0385",
                "English ≈ 1.0000, Random ≈ 0.0000",
                "English ≈ 0.0385, Random ≈ 0.0667",
                "Both are always exactly 0.5000"
            ),
            correctIndex = 0,
            explanation = "The Index of Coincidence measures the probability that two randomly selected letters from the text are identical. English text has non-uniform frequency peaks giving IoC ≈ 0.0667. Completely random text or polyalphabetic ciphertexts with long keys have IoC ≈ 1/26 ≈ 0.0385.",
            hint = "English is heavily biased toward E, T, A, O, so pairs of matching letters are much more likely than in random noise."
        ),

        // --- 5. Diffie-Hellman Key Exchange (BINARY_TREE) ---
        CNSChallengeQuestion(
            id = "dh_1",
            labType = DSAVisualizerType.BINARY_TREE,
            question = "What underlying mathematical problem makes the Diffie-Hellman key exchange computationally secure?",
            options = listOf(
                "Integer Factorization of large composites",
                "The Discrete Logarithm Problem (DLP) over finite fields or elliptic curves",
                "Linear programming and Gaussian elimination",
                "Traveling Salesperson Problem"
            ),
            correctIndex = 1,
            explanation = "Given generator g, prime p, and public value A = g^a mod p, finding the secret exponent a is the Discrete Logarithm Problem (DLP). In properly sized groups (2048+ bit primes or 256-bit elliptic curves), DLP is computationally intractable.",
            hint = "Computing modular exponentiation g^a mod p is easy; what is the inverse operation?"
        ),
        CNSChallengeQuestion(
            id = "dh_2",
            labType = DSAVisualizerType.BINARY_TREE,
            question = "Why is unauthenticated Diffie-Hellman vulnerable to an active Man-in-the-Middle (Mallory) attack?",
            options = listOf(
                "Because Mallory can guess the secret key in 1 step",
                "Because neither party has verified cryptographic identity; Mallory intercepts public keys and establishes separate secrets with Alice and Bob",
                "Because the prime p is shared publicly",
                "Because modular arithmetic does not work over insecure networks"
            ),
            correctIndex = 1,
            explanation = "Without digital signatures or PKI certificates, Alice cannot be sure she is exchanging keys with Bob. Mallory intercepts Alice's public key A, sends Mallory's own public key M to Bob, and vice-versa. Mallory then reads and tampers with all messages in transit.",
            hint = "Toggle the 'MITM ACTIVE' switch in the Diffie-Hellman lab: notice how Mallory derives keys with both parties."
        ),
        CNSChallengeQuestion(
            id = "dh_3",
            labType = DSAVisualizerType.BINARY_TREE,
            question = "What crucial security guarantee is provided by Ephemeral Diffie-Hellman (ECDHE / DHE)?",
            options = listOf(
                "Shorter key sizes than symmetric ciphers",
                "Perfect Forward Secrecy (PFS): compromising the server's long-term private key in the future does not decrypt past recorded sessions",
                "Zero latency handshake",
                "Immunity to quantum computer attacks"
            ),
            correctIndex = 1,
            explanation = "Because new ephemeral keys (a, b) are generated dynamically for each session and destroyed immediately after deriving the shared secret, an adversary who steals the server's long-term private key years later cannot retroactively decrypt recorded traffic.",
            hint = "The 'E' in DHE stands for 'Ephemeral'—temporary keys thrown away after every single session."
        ),

        // --- 6. RSA Public Key Cryptosystem (AVL_TREE) ---
        CNSChallengeQuestion(
            id = "rsa_1",
            labType = DSAVisualizerType.AVL_TREE,
            question = "In RSA key generation, how is the private exponent d mathematically related to public exponent e?",
            options = listOf(
                "d = e + phi(n)",
                "d ≡ e^(-1) (mod phi(n)), meaning (e * d) mod phi(n) = 1",
                "d = sqrt(n)",
                "d = e * n"
            ),
            correctIndex = 1,
            explanation = "d is the modular multiplicative inverse of e modulo Euler's totient phi(n) = (p - 1)(q - 1). It is efficiently computed using the Extended Euclidean Algorithm.",
            hint = "Check the RSA Key Pair Math card: (e * d) mod phi(n) must equal 1."
        ),
        CNSChallengeQuestion(
            id = "rsa_2",
            labType = DSAVisualizerType.AVL_TREE,
            question = "Why is textbook RSA (without OAEP padding) insecure against homomorphic malleability attacks?",
            options = listOf(
                "Because c1 * c2 mod n decrypts exactly to (m1 * m2) mod n",
                "Because the modulus n is always an even number",
                "Because e is public",
                "Because it runs in O(1) time"
            ),
            correctIndex = 0,
            explanation = "Because (m1^e * m2^e) mod n = (m1 * m2)^e mod n, an attacker can multiply ciphertexts together without decrypting them to create a valid ciphertext representing the product of their plaintexts. RSA-OAEP randomized padding eliminates this vulnerability.",
            hint = "Check the 'Homomorphic Attack' tab in the RSA lab: notice how the product of two ciphertexts decrypted directly to m1 * m2."
        ),
        CNSChallengeQuestion(
            id = "rsa_3",
            labType = DSAVisualizerType.AVL_TREE,
            question = "When generating a digital signature with RSA, why is it standard practice to 'Hash-then-Sign' rather than signing the raw message directly?",
            options = listOf(
                "Because hashing produces a fixed-size digest and prevents existential forgery and malleability attacks",
                "Because RSA cannot encrypt text characters",
                "Because hashing makes the signature smaller than 1 bit",
                "Because private keys only accept hex strings"
            ),
            correctIndex = 0,
            explanation = "Signing raw messages directly is vulnerable to length limits (m must be < n) and existential forgery attacks. Hashing compresses arbitrary-length messages into a collision-resistant fixed-size digest (e.g. SHA-256) and destroys algebraic structure.",
            hint = "Think about message size limits: what happens if a file is 10 MB and the RSA modulus is only 2048 bits?"
        ),

        // --- 7. Elliptic Curve Cryptography (TRIE) ---
        CNSChallengeQuestion(
            id = "ecc_1",
            labType = DSAVisualizerType.TRIE,
            question = "What key size in Elliptic Curve Cryptography (secp256r1 / NIST P-256) provides equivalent security to a 3072-bit RSA key?",
            options = listOf(
                "56 bits",
                "128 bits",
                "256 bits",
                "4096 bits"
            ),
            correctIndex = 2,
            explanation = "A 256-bit elliptic curve key offers roughly 128 bits of symmetric security, matching a massive 3072-bit RSA key. This 12x reduction in key size drastically speeds up handshakes and reduces bandwidth.",
            hint = "ECC keys are drastically smaller than RSA keys for the same cryptographic security level."
        ),
        CNSChallengeQuestion(
            id = "ecc_2",
            labType = DSAVisualizerType.TRIE,
            question = "In the group law of elliptic curves over a finite field F_p, what does 2P (point doubling) represent geometrically?",
            options = listOf(
                "Drawing the tangent line to the curve at point P and reflecting its intersection with the curve across the x-axis",
                "Multiplying the x and y coordinates by 2",
                "Adding the prime p to the coordinates",
                "Rotating the point 90 degrees around the origin"
            ),
            correctIndex = 0,
            explanation = "To add point P to itself, we calculate the slope of the tangent line m = (3x^2 + a)/(2y) mod p, find where this tangent intersects the curve again, and reflect that point across the x-axis to find 2P.",
            hint = "Think about the chord-and-tangent rule: when adding distinct points you draw a chord; what do you draw when adding a point to itself?"
        ),
        CNSChallengeQuestion(
            id = "ecc_3",
            labType = DSAVisualizerType.TRIE,
            question = "What is the identity element (O) in an elliptic curve group?",
            options = listOf(
                "The origin point (0, 0)",
                "The Point at Infinity (where vertical lines intersect the curve)",
                "The generator point G",
                "The scalar 1"
            ),
            correctIndex = 1,
            explanation = "In elliptic curve arithmetic, the group identity element is the 'Point at Infinity' O. Any point added to its vertical reflection (P + (-P)) equals O: P + (-P) = O.",
            hint = "Look at the subgroup sequence: what point is reached when k reaches the order of the group (19G)?"
        ),

        // --- 8. SHA-256 Hashing (GRAPH) ---
        CNSChallengeQuestion(
            id = "sha_1",
            labType = DSAVisualizerType.GRAPH,
            question = "What is the Strict Avalanche Criterion (SAC) satisfied by cryptographically secure hash functions like SHA-256?",
            options = listOf(
                "Flipping a single input bit must flip each output bit with a probability of approximately 50%",
                "All output bits must be set to 1",
                "The output must remain unchanged if only 1 bit is flipped",
                "The hash must be reversible given the first 4 bits"
            ),
            correctIndex = 0,
            explanation = "The Strict Avalanche Criterion ensures that any small change in input (even flipping 1 bit) causes an unpredictable cascade, flipping roughly 50% (128 out of 256) of the output bits.",
            hint = "Check the Avalanche analysis tab in the SHA-256 lab: what percentage of bits flipped when changing a single character?"
        ),
        CNSChallengeQuestion(
            id = "sha_2",
            labType = DSAVisualizerType.GRAPH,
            question = "In the Merkle-Damgård hash construction, what vulnerability allows an attacker to append data to an authenticated message without knowing the secret key prefix?",
            options = listOf(
                "Birthday Attack",
                "Length Extension Attack",
                "Padding Oracle Attack",
                "Side-Channel Power Attack"
            ),
            correctIndex = 1,
            explanation = "Because Merkle-Damgård outputs the internal register state H as the hash, an attacker knowing H(Secret || Message) can use that output as the initial state of the compression function to compute H(Secret || Message || Padding || ExtraData) without ever knowing Secret!",
            hint = "Why does HMAC exist? What specific attack on raw hash functions H(K || M) did HMAC fix?"
        ),
        CNSChallengeQuestion(
            id = "sha_3",
            labType = DSAVisualizerType.GRAPH,
            question = "According to the Birthday Paradox, how many operations are required on average to find a collision in a hash function with an n-bit output?",
            options = listOf(
                "2^n operations",
                "n^2 operations",
                "2^(n/2) operations (approx 2^128 for SHA-256)",
                "Exactly n operations"
            ),
            correctIndex = 2,
            explanation = "Due to the birthday problem, finding two arbitrary messages that produce the same digest requires testing only O(2^(n/2)) inputs, which is 2^128 operations for SHA-256.",
            hint = "Recall the square root bound: for an n-bit hash, the birthday collision search space is halved in the exponent."
        ),

        // --- 9. HMAC & Passwords (SORTING) ---
        CNSChallengeQuestion(
            id = "hmac_1",
            labType = DSAVisualizerType.SORTING,
            question = "How does the nested RFC 2104 HMAC construction H((K ⊕ opad) || H((K ⊕ ipad) || M)) prevent Length Extension Attacks?",
            options = listOf(
                "By using an asymmetric private key",
                "The outer hash H(K ⊕ opad || innerHash) wraps the inner digest, hiding the internal state of the inner hash from the attacker",
                "By encrypting the message with AES-128",
                "By sorting the bytes in ascending order"
            ),
            correctIndex = 1,
            explanation = "Because the inner hash is hashed again with the secret outer key (K ⊕ opad), the attacker cannot use the final output to continue the compression chain of the inner hash.",
            hint = "Look at the nested formula: the attacker only gets the output of the outer hash, which does not match the inner state."
        ),
        CNSChallengeQuestion(
            id = "hmac_2",
            labType = DSAVisualizerType.SORTING,
            question = "Why are fast cryptographic hashes like raw SHA-256 considered completely unsafe for password storage?",
            options = listOf(
                "Because modern GPUs and ASICs can calculate billions of SHA-256 hashes per second, making brute-force cracking trivial",
                "Because SHA-256 has been mathematically broken",
                "Because SHA-256 digests cannot be stored in SQL databases",
                "Because SHA-256 only works on ASCII text"
            ),
            correctIndex = 0,
            explanation = "Fast hashes are optimized for throughput. Modern GPU clusters test over 100 billion SHA-256 hashes per second. Secure password hashing requires memory-hard, tunable KDFs like Argon2id, bcrypt, or PBKDF2.",
            hint = "Check the PBKDF2 Benchmark tab: what does increasing the iteration count do to cracking time?"
        ),
        CNSChallengeQuestion(
            id = "hmac_3",
            labType = DSAVisualizerType.SORTING,
            question = "What is the primary cryptographic purpose of a Salt when hashing passwords?",
            options = listOf(
                "To encrypt the password symmetrically",
                "To ensure that identical passwords yield distinct hashes, defeating precomputed Rainbow Tables",
                "To make the password easier to remember",
                "To compress the password length"
            ),
            correctIndex = 1,
            explanation = "A unique random salt per user guarantees that even if two users choose 'password123', their stored hashes will be completely different. This forces attackers to crack each user's hash individually, rendering precomputed rainbow tables useless.",
            hint = "If 1,000 users have the same password, what prevents an attacker from cracking all 1,000 in a single hash lookup?"
        ),

        // --- 10. TLS 1.3 Handshake & Protocol Lab (STEP_RECORDER) ---
        CNSChallengeQuestion(
            id = "tls_1",
            labType = DSAVisualizerType.STEP_RECORDER,
            question = "How many round trips (RTT) are required in a full TLS 1.3 handshake before encrypted application data can be sent?",
            options = listOf(
                "0 RTT (always)",
                "1 RTT (Client sends ClientHello with KeyShare, Server responds with ServerHello and Finished)",
                "2 RTT (like TLS 1.2)",
                "4 RTT"
            ),
            correctIndex = 1,
            explanation = "TLS 1.3 optimizes latency down to 1-RTT by having the client speculatively send its ECDHE key share (g^a) directly in the ClientHello! The server responds with its key share (g^b) and certificate, completing key agreement in a single round trip.",
            hint = "Look at the swim-lane diagram: count how many round trips happen before Application Data begins."
        ),
        CNSChallengeQuestion(
            id = "tls_2",
            labType = DSAVisualizerType.STEP_RECORDER,
            question = "Why did TLS 1.3 completely ban static RSA key transport (encrypting premaster secret with server public key)?",
            options = listOf(
                "Because RSA is too slow",
                "To strictly mandate Perfect Forward Secrecy (PFS); with static RSA, compromising the server's private key retroactively decrypts all past recorded traffic",
                "Because RSA cannot be used with HTTPS",
                "Because RSA keys cannot be stored in X.509 certificates"
            ),
            correctIndex = 1,
            explanation = "With static RSA, anyone recording encrypted traffic from a server can decrypt everything years later if they ever obtain the server's private key. TLS 1.3 mandates ephemeral Diffie-Hellman (ECDHE) for all handshakes to enforce Forward Secrecy.",
            hint = "What happens to past recorded sessions if a server's private key leaks in 5 years?"
        ),
        CNSChallengeQuestion(
            id = "tls_3",
            labType = DSAVisualizerType.STEP_RECORDER,
            question = "In TLS 1.3, how does the client verify that an encrypted application record was not tampered with by an active network adversary?",
            options = listOf(
                "By checking the TCP checksum",
                "Through the 16-byte AEAD authentication tag (e.g. Poly1305 or AES-GCM GHASH tag); modifying even 1 bit causes decryption to fail with AEADBadTagException",
                "By re-sending the ClientHello",
                "By checking the HTTP status code"
            ),
            correctIndex = 1,
            explanation = "All TLS 1.3 application traffic uses AEAD (Authenticated Encryption with Associated Data). The 16-byte tag proves both confidentiality and message integrity. Flipping a single bit in transit causes tag verification to fail immediately.",
            hint = "Try the interactive 'Record Tamper' tab: what happened when 1 bit in the record was flipped?"
        )
    )

    fun getChallengesForLab(labType: DSAVisualizerType): List<CNSChallengeQuestion> {
        return CHALLENGES.filter { it.labType == labType }
    }
}
