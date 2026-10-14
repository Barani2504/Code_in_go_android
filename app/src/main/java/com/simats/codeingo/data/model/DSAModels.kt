package com.simats.codeingo.data.model

import androidx.compose.ui.graphics.Color
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DsaBlueDark
import com.simats.codeingo.ui.theme.DsaGreen
import com.simats.codeingo.ui.theme.DsaGreenDark
import com.simats.codeingo.ui.theme.DsaOrange
import com.simats.codeingo.ui.theme.DsaOrangeDark
import com.simats.codeingo.ui.theme.DsaPurple
import com.simats.codeingo.ui.theme.DsaPurpleDark
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DsaRedDark
import com.simats.codeingo.ui.theme.DsaTeal
import java.util.UUID

// MARK: - DSA Level
enum class DSALevel(val value: Int, val title: String, val emoji: String) {
    PROGRAMMING_BASICS(1, "Programming Basics", "🟢"),
    LINEAR_STRUCTURES(2, "Linear Structures", "🔵"),
    NON_LINEAR_STRUCTURES(3, "Non-Linear Structures", "🟣"),
    ALGORITHMS(4, "Algorithms", "🟠"),
    ADVANCED_DSA(6, "Advanced DSA", "🔴");

    val themeColor: Color
        get() = when (this) {
            PROGRAMMING_BASICS -> DsaGreen
            LINEAR_STRUCTURES -> DsaBlue
            NON_LINEAR_STRUCTURES -> DsaPurple
            ALGORITHMS -> DsaOrange
            ADVANCED_DSA -> DsaRed
        }

    val themeDarkColor: Color
        get() = when (this) {
            PROGRAMMING_BASICS -> DsaGreenDark
            LINEAR_STRUCTURES -> DsaBlueDark
            NON_LINEAR_STRUCTURES -> DsaPurpleDark
            ALGORITHMS -> DsaOrangeDark
            ADVANCED_DSA -> DsaRedDark
        }
}

// MARK: - DSA Topic
enum class DSATopic(val title: String) {
    // Level 1
    VARIABLES("Variables"),
    ARRAYS("Arrays"),
    STRINGS("Strings"),
    // Level 2
    LINKED_LIST("Linked List"),
    STACK("Stack"),
    QUEUE("Queue"),
    // Level 3
    TREES("Trees"),
    BST("Binary Search Tree"),
    HEAP("Heap"),
    GRAPH("Graph"),
    // Level 4
    SEARCHING("Searching"),
    SORTING("Sorting"),
    RECURSION("Recursion"),
    DYNAMIC_PROGRAMMING("Dynamic Programming"),
    // Level 5
    GREEDY("Greedy"),
    GRAPH_ALGORITHMS("Graph Algorithms"),
    BACKTRACKING("Backtracking"),
    ADVANCED_DP("Advanced DP");

    val level: DSALevel
        get() = when (this) {
            VARIABLES, ARRAYS, STRINGS -> DSALevel.PROGRAMMING_BASICS
            LINKED_LIST, STACK, QUEUE -> DSALevel.LINEAR_STRUCTURES
            TREES, BST, HEAP, GRAPH -> DSALevel.NON_LINEAR_STRUCTURES
            SEARCHING, SORTING, RECURSION, DYNAMIC_PROGRAMMING -> DSALevel.ALGORITHMS
            GREEDY, GRAPH_ALGORITHMS, BACKTRACKING, ADVANCED_DP -> DSALevel.ADVANCED_DSA
        }

    val description: String
        get() = when (this) {
            VARIABLES -> "Building blocks of programming"
            ARRAYS -> "Sequential data in memory"
            STRINGS -> "Text manipulation & patterns"
            LINKED_LIST -> "Nodes connected by pointers"
            STACK -> "LIFO - Last In, First Out"
            QUEUE -> "FIFO - First In, First Out"
            TREES -> "Hierarchical data structure"
            BST -> "Sorted binary tree"
            HEAP -> "Priority-based tree"
            GRAPH -> "Vertices and edges"
            SEARCHING -> "Find elements efficiently"
            SORTING -> "Arrange data in order"
            RECURSION -> "Functions calling themselves"
            DYNAMIC_PROGRAMMING -> "Optimal substructure problems"
            GREEDY -> "Locally optimal choices"
            GRAPH_ALGORITHMS -> "Traversal & shortest paths"
            BACKTRACKING -> "Explore all possibilities"
            ADVANCED_DP -> "Complex DP patterns"
        }

    val xpReward: Int
        get() = when (this) {
            VARIABLES, ARRAYS, STRINGS -> 10
            LINKED_LIST, STACK, QUEUE -> 15
            TREES, BST, HEAP, GRAPH -> 20
            SEARCHING, SORTING, RECURSION, DYNAMIC_PROGRAMMING -> 25
            GREEDY, GRAPH_ALGORITHMS, BACKTRACKING, ADVANCED_DP -> 30
        }
}

// MARK: - DSA Question Type
enum class DSAQuestionType(val title: String) {
    MULTIPLE_CHOICE("Multiple Choice"),
    PREDICT_OUTPUT("Predict the Output"),
    CONCEPT_CHECK("Concept Check"),
    VISUAL_MATCH("Visual Match"),
    CODE_COMPLETION("Complete the Code")
}

// MARK: - DSA Quiz Question
data class DSAQuizQuestion(
    val id: String = UUID.randomUUID().toString(),
    val type: DSAQuestionType,
    val topic: DSATopic,
    val prompt: String,
    val visualContext: String? = null,
    val codeSnippet: String? = null,
    val options: List<String> = emptyList(),
    val correctIndex: Int,
    val explanation: String,
    val xpReward: Int = 15,
    val hint: String? = null
)

// MARK: - DSA Achievement
data class DSAAchievement(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val badge: String,
    val requirement: String,
    var isUnlocked: Boolean = false
)

// MARK: - XP Activity
enum class XPActivity(val title: String, val xpReward: Int, val iconName: String) {
    COMPLETE_LESSON("Complete Lesson", 10, "book.fill"),
    QUIZ("Quiz", 15, "questionmark.circle.fill"),
    DAILY_CHALLENGE("Daily Challenge", 30, "calendar.badge.clock"),
    CODING_CHALLENGE("Coding Challenge", 50, "code"),
    PERFECT_LESSON("Perfect Lesson", 10, "star.fill"),
    SEVEN_DAY_STREAK("7-Day Streak", 100, "flame.fill")
}

// MARK: - CNS Visualizer Type (Dual-Skin Parity)
enum class DSAVisualizerType(val title: String, val iconName: String) {
    ARRAY("Caesar & Substitution Cipher", "lock"),
    STACK("DES Feistel Network", "layers"),
    QUEUE("AES 128-Bit State Matrix", "grid"),
    LINKED_LIST("Vigenère Polyalphabetic Cipher", "link"),
    BINARY_TREE("Diffie-Hellman Key Exchange", "vpn_key"),
    AVL_TREE("RSA Public Key Encryption", "key"),
    TRIE("Elliptic Curve Points (ECC)", "timeline"),
    GRAPH("SHA-256 Hash Construction", "fingerprint"),
    SORTING("HMAC & Collision Bound", "verified_user"),
    STEP_RECORDER("TLS 1.3 Handshake Tracer", "security");

    val color: Color
        get() = when (this) {
            ARRAY -> DsaGreen
            STACK -> DsaBlue
            QUEUE -> DsaPurple
            LINKED_LIST -> DsaOrange
            BINARY_TREE -> Color(0xFF00CD9C)
            AVL_TREE -> DsaTeal
            TRIE -> Color.Cyan
            GRAPH -> Color(0xFFA05AFF)
            SORTING -> DsaRed
            STEP_RECORDER -> Color(0xFFFF9600)
        }

    val category: String
        get() = when (this) {
            ARRAY, LINKED_LIST -> "Classical Ciphers"
            STACK, QUEUE -> "Symmetric Encryption"
            BINARY_TREE, AVL_TREE, TRIE -> "Asymmetric & ECC"
            GRAPH, SORTING -> "Hashes & Signatures"
            STEP_RECORDER -> "Network Security"
        }

    val filterCategory: String
        get() = when (this) {
            ARRAY, LINKED_LIST -> "Ciphers"
            STACK, QUEUE -> "Symmetric"
            BINARY_TREE, AVL_TREE, TRIE -> "Asymmetric"
            GRAPH, SORTING, STEP_RECORDER -> "Protocols"
        }

    val tagline: String
        get() = when (this) {
            ARRAY -> "Modular Shift Ring: C = (P + K) mod 26"
            STACK -> "16-Round Feistel Swap & Key Schedule"
            QUEUE -> "SubBytes · ShiftRows · MixColumns · AddRoundKey"
            LINKED_LIST -> "Tabula Recta & Repeated Keystream Alignment"
            BINARY_TREE -> "Discrete Logarithm Problem: g^(ab) mod p"
            AVL_TREE -> "Prime Factorization & Euler's Totient phi(n)"
            TRIE -> "Chord & Tangent Rule on secp256k1"
            GRAPH -> "Merkle-Damgard 512-Bit Chunk Compression"
            SORTING -> "Nested Hash Integrity & Birthday Attack 2^(n/2)"
            STEP_RECORDER -> "1-RTT Speculative KeyShare & AEAD Session"
        }

    val shortDescription: String
        get() = when (this) {
            ARRAY -> "Foundational monoalphabetic substitution shifting letters along the alphabet ring modulo 26. Breakable using frequency analysis."
            STACK -> "Symmetric block cipher splitting 64-bit plaintext into 32-bit halves, applying non-linear S-Box substitution and round swaps."
            QUEUE -> "Standard symmetric block cipher transforming a 4x4 byte state matrix across 10 rounds using Galois Field GF(2^8) arithmetic."
            LINKED_LIST -> "Polyalphabetic substitution using a secret repeating keyword to defeat single-letter frequency analysis."
            BINARY_TREE -> "Revolutionary key agreement protocol allowing two parties to establish a shared secret over an insecure channel."
            AVL_TREE -> "Asymmetric cipher relying on the hardness of factoring large semiprimes. Encrypt with public (e, n), decrypt with private (d, n)."
            TRIE -> "Ultra-compact public-key cryptography operating on algebraic points y^2 = x^3 + ax + b with point addition and doubling."
            GRAPH -> "One-way cryptographic hash processing padded 512-bit blocks through 64 rounds to produce a 256-bit avalanche digest."
            SORTING -> "Keyed-hash message authentication code preventing length extension attacks, alongside Birthday Bound collision demonstration."
            STEP_RECORDER -> "Transport Layer Security 1.3 packet inspector tracing ClientHello, KeyShare, Certificate validation, and encrypted payload records."
        }

    val difficulty: String
        get() = when (this) {
            ARRAY, LINKED_LIST -> "Beginner"
            STACK, QUEUE, BINARY_TREE, GRAPH -> "Intermediate"
            AVL_TREE, TRIE, SORTING, STEP_RECORDER -> "Advanced"
        }

    val difficultyColor: Color
        get() = when (difficulty) {
            "Beginner" -> DsaGreen
            "Intermediate" -> DsaOrange
            else -> DsaRed
        }

    val timeComplexity: String
        get() = when (this) {
            ARRAY -> "O(n) Shift"
            STACK -> "16 Rounds"
            QUEUE -> "10 Rounds"
            LINKED_LIST -> "O(n) Stream"
            BINARY_TREE -> "O(log p) ModExp"
            AVL_TREE -> "O(k^3) ModExp"
            TRIE -> "O(256) Mult"
            GRAPH -> "64 Rounds"
            SORTING -> "2x Hash Ops"
            STEP_RECORDER -> "1-RTT Handshake"
        }

    val spaceComplexity: String
        get() = when (this) {
            ARRAY -> "Key Space 26"
            STACK -> "64-bit Block"
            QUEUE -> "128-bit Block"
            LINKED_LIST -> "O(k) Keystream"
            BINARY_TREE -> "2048-bit Prime"
            AVL_TREE -> "2048-bit Key"
            TRIE -> "256-bit Curve"
            GRAPH -> "256-bit Digest"
            SORTING -> "2^(n/2) Birthday"
            STEP_RECORDER -> "Ephemeral Keys"
        }

    val keyOperations: List<String>
        get() = when (this) {
            ARRAY -> listOf("shift(k)", "frequencyCount()", "caesarDecrypt()", "mod26()")
            STACK -> listOf("feistelRound()", "expandPermute()", "sboxLookup()", "keySchedule()")
            QUEUE -> listOf("subBytes()", "shiftRows()", "mixColumns()", "addRoundKey()")
            LINKED_LIST -> listOf("tabulaRecta()", "alignKey()", "kasiskiTest()", "decrypt()")
            BINARY_TREE -> listOf("generatePrivate()", "calcPublic()", "exchange()", "computeSecret()")
            AVL_TREE -> listOf("choosePrimes()", "calcTotient()", "genKeyPair()", "modExp()")
            TRIE -> listOf("pointAdd()", "pointDouble()", "scalarMultiply()", "secp256k1()")
            GRAPH -> listOf("padMessage()", "messageSchedule()", "compressionRound()", "produceDigest()")
            SORTING -> listOf("innerHash()", "outerHash()", "verifyTag()", "birthdaySim()")
            STEP_RECORDER -> listOf("clientHello()", "keyShare()", "serverHello()", "encryptData()")
        }

    val realWorldApplications: List<String>
        get() = when (this) {
            ARRAY -> listOf(
                "Historical military command encoding (Julius Caesar)",
                "Rot13 obfuscation in online forums and source code",
                "Educational foundation for modular arithmetic in ciphers",
                "Cryptanalysis puzzle solving and letter distribution tests"
            )
            STACK -> listOf(
                "Legacy financial ATM networks and Triple-DES (3DES)",
                "S-Box non-linear confusion hardware designs",
                "Feistel network foundation in Blowfish and Twofish",
                "Understanding differential and linear cryptanalysis attacks"
            )
            QUEUE -> listOf(
                "HTTPS / TLS bulk symmetric packet encryption (AES-GCM)",
                "Full disk encryption in Android (dm-crypt) and macOS (FileVault)",
                "Hardware-accelerated AES-NI CPU instruction sets",
                "WPA3 Wi-Fi network packet confidentiality"
            )
            LINKED_LIST -> listOf(
                "Historical diplomatic telegraphy and secret correspondence",
                "Understanding polyalphabetic key streaming and index of coincidence",
                "Breaking repeating-key XOR ciphers in CTF security challenges",
                "Foundation of the unbreakable One-Time Pad (OTP)"
            )
            BINARY_TREE -> listOf(
                "TLS 1.3 Ephemeral Diffie-Hellman (ECDHE) key exchange",
                "SSH (Secure Shell) remote terminal session establishment",
                "Signal Protocol end-to-end encrypted messaging",
                "Perfect Forward Secrecy (PFS) session establishment"
            )
            AVL_TREE -> listOf(
                "X.509 SSL/TLS digital certificate signatures",
                "SSH key pairs (id_rsa, id_rsa.pub) for server authentication",
                "PGP/GPG encrypted email and code signing (git commit -S)",
                "Secure software update verification in mobile operating systems"
            )
            TRIE -> listOf(
                "Bitcoin and Ethereum blockchain transaction signatures (ECDSA)",
                "Apple Secure Enclave and Android Hardware Keystore (secp256r1)",
                "Modern TLS 1.3 curve negotiations (X25519 & Ed25519)",
                "Ultra-fast IoT hardware authentication with 256-bit keys"
            )
            GRAPH -> listOf(
                "Git commit object hashing and repository tree integrity",
                "Blockchain Proof-of-Work (Bitcoin mining hash target)",
                "Password storage via PBKDF2, bcrypt, and Argon2",
                "Operating system file integrity monitoring and digital forensics"
            )
            SORTING -> listOf(
                "API authentication tokens (AWS Signature Version 4, Stripe webhooks)",
                "JWT (JSON Web Token) HS256 tampering prevention",
                "Constant-time verification defending against timing side-channels",
                "Preventing length extension attacks in cryptographic protocols"
            )
            STEP_RECORDER -> listOf(
                "Securing all modern web traffic (HTTPS over TCP port 443)",
                "Zero-RTT and 1-RTT connection latency optimization",
                "Wireshark packet analysis and protocol debugging",
                "Enforcing Forward Secrecy and modern AEAD-only cipher suites"
            )
        }

    val nextType: DSAVisualizerType
        get() {
            val all = entries
            val idx = all.indexOf(this)
            return if (idx >= 0) all[(idx + 1) % all.size] else STACK
        }
}
