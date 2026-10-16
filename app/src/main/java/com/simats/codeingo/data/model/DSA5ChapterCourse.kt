package com.simats.codeingo.data.model

import androidx.compose.ui.graphics.Color
import com.simats.codeingo.ui.theme.*

// ══════════════════════════════════════════════════════════════════
// DSA 5-Chapter Course Model
// Direct port of iOS DSA5ChapterCourse.swift
// ══════════════════════════════════════════════════════════════════

// ── Environment Themes ────────────────────────────────────────────

enum class DSAEnvironmentTheme(
    val displayName: String,
    val icon: String,    // Material icon name
    val emoji: String,
) {
    HIGHWAY       ("The Cipher Sanctum",        "security",        "🏛️"),
    CHAIN_WORLD   ("The Block Cipher Forge",    "vpn_key",         "⚙️"),
    TOWER_PLATES  ("The Key Exchange Citadel",  "castle",          "🏰"),
    TICKET_STATION("The Integrity Foundry",     "verified_user",   "🛡️"),
    DATA_KINGDOM  ("The Cyber Bastion",         "shield",          "⚡"),
}

// ── Interaction Types ─────────────────────────────────────────────

enum class DSALevelInteractionType(val displayName: String) {
    MULTIPLE_CHOICE          ("Multiple Choice"),
    TAP_CORRECT_NODE         ("Tap the Correct Node"),
    DRAG_AND_DROP_ORDER      ("Drag & Drop Order"),
    CONNECT_NODES            ("Connect Nodes"),
    INSERT_NODE              ("Insert a Node"),
    PUSH_STACK               ("Push to Stack"),
    POP_STACK                ("Pop from Stack"),
    STACK_SEQUENCE_CHALLENGE ("Stack Sequence Challenge"),
    FIFO_QUEUE_ORDER         ("Understand FIFO"),
    ENQUEUE_LINE             ("Enqueue to Back"),
    DEQUEUE_LINE             ("Dequeue from Front"),
    QUEUE_SEQUENCE_CHALLENGE ("Queue Sequence Puzzle"),
    FIND_TREE_ROOT           ("Find the Root"),
    TREE_PARENT_CHILD        ("Parent & Child"),
    FIND_TREE_LEAVES         ("Find the Leaves"),
    TREE_TRAVERSAL           ("Tree Traversal"),
    TIMED_BOSS_RACE          ("Timed Boss Race"),
    BROKEN_CHAIN_BOSS        ("Broken Chain Repair"),
    ESCAPE_TOWER_BOSS        ("Escape the Tower Boss"),
    TICKET_RUSH_BOSS         ("Ticket Rush Boss"),
    SAVE_KINGDOM_BOSS        ("Save Data Kingdom Boss"),
}

// ── Visual Primitives ─────────────────────────────────────────────

data class DSAVisualBoxItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val value: String,
    val index: Int,
    val isHighlighted: Boolean = false,
    val isTarget: Boolean = false,
    val colorName: String = "blue",
)

data class DSALinkedNode(
    val id: String = java.util.UUID.randomUUID().toString(),
    val value: String,
    val nextId: String? = null,
    val isHead: Boolean = false,
    val isNull: Boolean = false,
    val isHighlighted: Boolean = false,
)

data class DSAStackPlate(
    val id: String = java.util.UUID.randomUUID().toString(),
    val value: String,
    val color: Color,
    val isRemoved: Boolean = false,
)

data class DSAQueuePerson(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val emoji: String,
    val color: Color,
    val isServed: Boolean = false,
)

data class DSATreeNodeItem(
    val id: String,
    val label: String,
    val level: Int,
    val parentId: String?,
    val isRoot: Boolean = false,
    val isLeaf: Boolean = false,
    val isSelected: Boolean = false,
    val isHighlighted: Boolean = false,
    val xPos: Float = 0f,
    val yPos: Float = 0f,
)

// ── Rapid Boss Question ───────────────────────────────────────────

data class DSARapidQuestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
)

// ── Level Specification ───────────────────────────────────────────

data class DSALevelSpec(
    val id: String,
    val chapterId: Int,
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val interactionType: DSALevelInteractionType,
    val storyExplanation: String,
    val promptQuestion: String,
    val hint: String,
    val explanation: String,
    val xpReward: Int = 15,
    // Multiple choice
    val mcOptions: List<String> = emptyList(),
    val mcCorrectIndex: Int = 0,
    // Tap-box
    val boxes: List<DSAVisualBoxItem> = emptyList(),
    val correctBoxIndex: Int? = null,
    // Drag & drop
    val draggableItems: List<String> = emptyList(),
    val correctOrder: List<String> = emptyList(),
    // Linked list
    val linkedNodes: List<DSALinkedNode> = emptyList(),
    val targetNodeValue: String = "",
    val insertNodeValue: String = "",
    val brokenNodeIndex: Int = 1,
    // Stack
    val stackInitial: List<String> = emptyList(),
    val targetPushItems: List<String> = emptyList(),
    val stackSequence: List<String> = emptyList(),
    val stackSequenceAnswer: String = "40",
    // Queue
    val queueInitial: List<String> = emptyList(),
    val queueEnqueueItems: List<String> = emptyList(),
    val queueSequence: List<String> = emptyList(),
    val queueSequenceAnswer: List<String> = emptyList(),
    // Tree
    val treeNodes: List<DSATreeNodeItem> = emptyList(),
    val correctLeafIds: Set<String> = emptySet(),
    val traversalSolution: List<String> = emptyList(),
    // Boss
    val timeLimitSeconds: Int = 45,
    val bossTotalQuestions: Int = 5,
    val bossRapidQuestions: List<DSARapidQuestion> = emptyList(),
) {
    /** True when this level is a boss challenge. */
    val isBoss: Boolean get() = interactionType in listOf(
        DSALevelInteractionType.TIMED_BOSS_RACE,
        DSALevelInteractionType.BROKEN_CHAIN_BOSS,
        DSALevelInteractionType.ESCAPE_TOWER_BOSS,
        DSALevelInteractionType.TICKET_RUSH_BOSS,
        DSALevelInteractionType.SAVE_KINGDOM_BOSS,
    )
}

// ── Chapter Definition ────────────────────────────────────────────

data class DSAChapterModel(
    val id: Int,
    val number: Int,
    val title: String,
    val subtitle: String,
    val theme: DSAEnvironmentTheme,
    val story: String,
    val badgeName: String,
    val primaryColor: Color,
    val darkColor: Color,
    val accentColor: Color,
    val characterEmoji: String,
    val completionBonusXP: Int = 100,
    val levels: List<DSALevelSpec>,
) {
    val levelCount: Int get() = levels.size
    val bossLevel: DSALevelSpec? get() = levels.firstOrNull { it.isBoss }
}

// ══════════════════════════════════════════════════════════════════
// 5-CHAPTER MASTER CATALOG
// ══════════════════════════════════════════════════════════════════
typealias DSA5ChapterCourse = DSA5ChapterData

object DSA5ChapterData {

    val chapters: List<DSAChapterModel> = listOf(

        // ──────────────────────────────────────────────────────────
        // CHAPTER 1 — CLASSICAL CIPHERS: "The Cipher Sanctum"
        // ──────────────────────────────────────────────────────────
        DSAChapterModel(
            id = 1, number = 1,
            title = "Classical Ciphers",
            subtitle = "The Cipher Sanctum",
            theme = DSAEnvironmentTheme.HIGHWAY,
            story = "Welcome to the Cipher Sanctum! Classical cryptography begins with substitution and transposition ciphers — shifting letters across modular rings and permuting text blocks using secret keys to defend communications.",
            badgeName = "Cipher Master",
            primaryColor = Color(red = 28f/255, green = 176f/255, blue = 246f/255),
            darkColor    = Color(red = 18f/255, green = 120f/255, blue = 180f/255),
            accentColor  = Color(red = 88f/255, green = 204f/255, blue = 2f/255),
            characterEmoji = "🔐",
            completionBonusXP = 100,
            levels = listOf(
                DSALevelSpec(
                    id = "c1_l1", chapterId = 1, levelNumber = 1,
                    title = "Meet the Shift Cipher",
                    subtitle = "Caesar & modular arithmetic",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "The Caesar Cipher shifts each plaintext letter by key k positions along the alphabet: E(x) = (x + k) mod 26. To decrypt, we reverse the shift: D(c) = (c - k) mod 26.",
                    promptQuestion = "With shift key k = 3, what does letter 'A' encrypt to?",
                    hint = "A is 0 in zero-based indexing. Calculate: (0 + 3) mod 26 = 3, which corresponds to...",
                    explanation = "A (0) shifted by 3 becomes D (3)! In Caesar's cipher, A -> D, B -> E, C -> F. 🔐",
                    xpReward = 10,
                    mcOptions = listOf("B", "C", "D", "E"),
                    mcCorrectIndex = 2,
                    boxes = listOf(
                        DSAVisualBoxItem(value = "A → D", index = 0, colorName = "red"),
                        DSAVisualBoxItem(value = "B → E", index = 1, colorName = "yellow"),
                        DSAVisualBoxItem(value = "C → F", index = 2, colorName = "orange"),
                        DSAVisualBoxItem(value = "D → G", index = 3, colorName = "green"),
                    ),
                ),
                DSALevelSpec(
                    id = "c1_l2", chapterId = 1, levelNumber = 2,
                    title = "Substitution Ciphers",
                    subtitle = "Monoalphabetic key space",
                    interactionType = DSALevelInteractionType.TAP_CORRECT_NODE,
                    storyExplanation = "A monoalphabetic substitution maps each letter to a unique cipher letter. While its key space is massive (26! ≈ 4 × 10²⁶ keys), it preserves natural letter frequencies!",
                    promptQuestion = "Which English letter has the highest natural frequency (~12.7%) making substitution vulnerable? Tap the target letter.",
                    hint = "Remember classic English frequency order: E, T, A, O, I, N...",
                    explanation = "Letter 'E' appears ~12.7% of the time in English text, followed by 'T' (~9.1%)! 🎯",
                    xpReward = 15,
                    boxes = listOf(
                        DSAVisualBoxItem(value = "E (12.7%)", index = 0),
                        DSAVisualBoxItem(value = "T (9.1%)",  index = 1),
                        DSAVisualBoxItem(value = "A (8.2%)",  index = 2),
                        DSAVisualBoxItem(value = "O (7.5%)",  index = 3),
                        DSAVisualBoxItem(value = "Z (0.07%)", index = 4),
                    ),
                    correctBoxIndex = 0,
                ),
                DSALevelSpec(
                    id = "c1_l3", chapterId = 1, levelNumber = 3,
                    title = "Transposition Ciphers",
                    subtitle = "Permuting symbol positions",
                    interactionType = DSALevelInteractionType.DRAG_AND_DROP_ORDER,
                    storyExplanation = "Transposition ciphers do NOT substitute letters. Instead, they permute letter positions (e.g. Rail Fence or Columnar Transposition).",
                    promptQuestion = "Arrange these rail-fence blocks in order to reconstruct the decrypted plaintext: 'S-E-C-U-R-E'.",
                    hint = "Assemble letters in order: S, E, C, U, R, E...",
                    explanation = "Transposition changes letter positions without replacing characters! 'SECURE' successfully decrypted. 🏎️💨",
                    xpReward = 15,
                    draggableItems = listOf("C", "S", "R", "E", "U", "E"),
                    correctOrder   = listOf("S", "E", "C", "U", "R", "E"),
                ),
                DSALevelSpec(
                    id = "c1_l4", chapterId = 1, levelNumber = 4,
                    title = "Frequency Cryptanalysis",
                    subtitle = "Breaking classical encryption",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "Cryptanalysis breaks encryption without knowing the key. For monoalphabetic substitution, counting letter and digraph frequencies immediately cracks the ciphertext.",
                    promptQuestion = "Why is a monoalphabetic substitution cipher completely vulnerable to ciphertext-only attacks?",
                    hint = "Does substitution mask the natural letter frequency distribution of the language?",
                    explanation = "Because every letter is substituted 1:1, frequency analysis shatters the 26! key space instantly! ⚡",
                    xpReward = 15,
                    mcOptions = listOf(
                        "It only works with numbers",
                        "Letter frequency distributions directly match natural language patterns (e.g. ETAOIN)",
                        "The key length is restricted to 3",
                        "It requires an internet connection to decrypt",
                    ),
                    mcCorrectIndex = 1,
                ),
                DSALevelSpec(
                    id = "c1_boss", chapterId = 1, levelNumber = 5,
                    title = "Chapter Boss: The Cipher Breaker",
                    subtitle = "Answer 5 rapid crypto challenges before timer runs out!",
                    interactionType = DSALevelInteractionType.TIMED_BOSS_RACE,
                    storyExplanation = "The Cipher Breaker has encrypted the Phoenix communication channel! Decipher 5 security challenges to restore transmission before time expires!",
                    promptQuestion = "Crack all 5 Cryptographic challenges!",
                    hint = "Trust your modular arithmetic and frequency principles!",
                    explanation = "You defeated The Cipher Breaker! You mastered Classical Ciphers and earned the Cipher Master trophy! 🏆",
                    xpReward = 100, timeLimitSeconds = 50, bossTotalQuestions = 5,
                    bossRapidQuestions = listOf(
                        DSARapidQuestion(
                            prompt = "In a Caesar cipher, what is the formula for encrypting letter x with key k?",
                            options = listOf("E(x) = (x + k) mod 26", "E(x) = (x * k) mod 26", "E(x) = (x - k) mod 26", "E(x) = x^k mod 26"),
                            correctIndex = 0,
                            explanation = "Caesar encryption shifts by k: E(x) = (x + k) mod 26."
                        ),
                        DSARapidQuestion(
                            prompt = "What is the size of the key space for an arbitrary monoalphabetic substitution cipher?",
                            options = listOf("26! (≈ 4 × 10²⁶)", "26", "2²⁶", "26²"),
                            correctIndex = 0,
                            explanation = "There are 26! possible permutations of the 26 alphabet letters."
                        ),
                        DSARapidQuestion(
                            prompt = "Which classical cipher scrambles letter positions without changing the letters themselves?",
                            options = listOf("Transposition Cipher", "Caesar Cipher", "Vigenère Cipher", "One-Time Pad"),
                            correctIndex = 0,
                            explanation = "Transposition permutes positions; substitution changes symbols."
                        ),
                        DSARapidQuestion(
                            prompt = "Which letter in English has the highest natural frequency in plain text?",
                            options = listOf("T", "E", "A", "O"),
                            correctIndex = 1,
                            explanation = "Letter 'E' appears ~12.7% of the time, followed by T (~9.1%) and A (~8.2%)."
                        ),
                        DSARapidQuestion(
                            prompt = "How does a polyalphabetic cipher (e.g. Vigenère) improve upon simple substitution?",
                            options = listOf("It uses multiple shift keys to flatten single-letter frequency spikes", "It encrypts letters as images", "It uses prime number factorization", "It automatically deletes the ciphertext"),
                            correctIndex = 0,
                            explanation = "Polyalphabetic substitution rotates through multiple shift alphabets to obscure letter frequencies."
                        ),
                    ),
                ),
            ),
        ),

        // ──────────────────────────────────────────────────────────
        // CHAPTER 2 — SYMMETRIC ENCRYPTION: "The Block Fortress"
        // ──────────────────────────────────────────────────────────
        DSAChapterModel(
            id = 2, number = 2,
            title = "Symmetric Encryption",
            subtitle = "The Block Fortress",
            theme = DSAEnvironmentTheme.CHAIN_WORLD,
            story = "Welcome to the Block Fortress! Symmetric cryptography secures modern data using shared secret keys. Explore Feistel networks, the 128-bit state matrix of AES, block cipher chaining modes, and Galois/Counter Mode authentication!",
            badgeName = "Block Fortress Guardian",
            primaryColor = Color(red = 175f/255, green = 82f/255, blue = 222f/255),
            darkColor    = Color(red = 120f/255, green = 40f/255, blue = 170f/255),
            accentColor  = Color(red = 255f/255, green = 215f/255, blue = 0f/255),
            characterEmoji = "🏰",
            completionBonusXP = 100,
            levels = listOf(
                DSALevelSpec(
                    id = "c2_l1", chapterId = 2, levelNumber = 1,
                    title = "DES & Feistel Networks",
                    subtitle = "16 rounds & Feistel structure",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "The Data Encryption Standard (DES) splits 64-bit plaintext into Left (L) and Right (R) 32-bit halves. Over 16 rounds, L_i = R_{i-1} and R_i = L_{i-1} ⊕ F(R_{i-1}, K_i).",
                    promptQuestion = "In a Feistel cipher round, how is the new right half R_i computed from L_{i-1} and round function F?",
                    hint = "Remember the core Feistel formula: L_{i-1} is XORed with the output of F...",
                    explanation = "R_i = L_{i-1} ⊕ F(R_{i-1}, K_i)! Decryption uses the exact same hardware in reverse! 🛡️",
                    xpReward = 10,
                    mcOptions = listOf(
                        "R_i = L_{i-1} ⊕ F(R_{i-1}, K_i)",
                        "R_i = L_{i-1} + R_{i-1}",
                        "R_i = F(L_{i-1}, K_i)",
                        "R_i = L_{i-1} * K_i"
                    ),
                    mcCorrectIndex = 0,
                    boxes = listOf(
                        DSAVisualBoxItem(value = "L_0 (32-bit)", index = 0, colorName = "blue"),
                        DSAVisualBoxItem(value = "R_0 (32-bit)", index = 1, colorName = "purple"),
                        DSAVisualBoxItem(value = "Round F(R, K)", index = 2, colorName = "orange"),
                        DSAVisualBoxItem(value = "16 Rounds", index = 3, colorName = "green"),
                    ),
                ),
                DSALevelSpec(
                    id = "c2_l2", chapterId = 2, levelNumber = 2,
                    title = "AES Architecture",
                    subtitle = "SubBytes, ShiftRows, MixColumns",
                    interactionType = DSALevelInteractionType.TAP_CORRECT_NODE,
                    storyExplanation = "The Advanced Encryption Standard (AES) processes 128-bit blocks as a 4×4 byte matrix. SubBytes provides non-linear confusion via Galois Field inversion S-Boxes, while ShiftRows and MixColumns diffuse bits across the matrix.",
                    promptQuestion = "Which AES transformation provides non-linear confusion by substituting each byte using S-Boxes? Tap the target operation.",
                    hint = "Look for the substitution operation that provides non-linear Shannon Confusion...",
                    explanation = "SubBytes substitutes each byte using an 8-bit Rijndael S-Box, providing critical non-linear confusion! 💎",
                    xpReward = 15,
                    boxes = listOf(
                        DSAVisualBoxItem(value = "SubBytes (Confusion)", index = 0),
                        DSAVisualBoxItem(value = "ShiftRows (Diffusion)", index = 1),
                        DSAVisualBoxItem(value = "MixColumns (Diffusion)", index = 2),
                        DSAVisualBoxItem(value = "AddRoundKey (Key XOR)", index = 3),
                    ),
                    correctBoxIndex = 0,
                ),
                DSALevelSpec(
                    id = "c2_l3", chapterId = 2, levelNumber = 3,
                    title = "Block Cipher Modes",
                    subtitle = "ECB, CBC & Counter mode",
                    interactionType = DSALevelInteractionType.DRAG_AND_DROP_ORDER,
                    storyExplanation = "Block cipher modes determine how long messages are encrypted. ECB encrypts identical blocks identically (leaking patterns). CBC chains blocks with an IV: C_i = E(P_i ⊕ C_{i-1}).",
                    promptQuestion = "Arrange the 4 steps of Cipher Block Chaining (CBC) encryption for block i into order:",
                    hint = "First XOR with previous ciphertext C_{i-1}, then encrypt with key, then emit and store C_i...",
                    explanation = "CBC chains each block with the previous ciphertext, preventing identical plaintext blocks from producing identical ciphertexts! 🔗",
                    xpReward = 20,
                    draggableItems = listOf(
                        "Run block cipher encryption E(key, ...)",
                        "XOR plaintext P_i with previous ciphertext C_{i-1}",
                        "Store C_i to chain with next block P_{i+1}",
                        "Emit ciphertext block C_i"
                    ),
                    correctOrder = listOf(
                        "XOR plaintext P_i with previous ciphertext C_{i-1}",
                        "Run block cipher encryption E(key, ...)",
                        "Emit ciphertext block C_i",
                        "Store C_i to chain with next block P_{i+1}"
                    ),
                ),
                DSALevelSpec(
                    id = "c2_l4", chapterId = 2, levelNumber = 4,
                    title = "Authenticated Encryption",
                    subtitle = "GCM & AEAD data integrity",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "Encryption alone does not prevent message tampering. Galois/Counter Mode (AES-GCM) provides Authenticated Encryption with Associated Data (AEAD), producing both ciphertext and an authentication tag via GHASH multiplication.",
                    promptQuestion = "Why is AES-GCM preferred in modern TLS 1.3 over legacy CBC-mode encryption?",
                    hint = "Does GCM combine confidentiality and tamper-proof authentication in parallel streams?",
                    explanation = "AES-GCM provides authenticated encryption (AEAD) with parallel CTR execution and GHASH integrity verification, preventing padding oracle attacks! 🚀",
                    xpReward = 25,
                    mcOptions = listOf(
                        "It provides both confidentiality and cryptographic integrity in parallel hardware-accelerated streams",
                        "It uses smaller 16-bit keys",
                        "It avoids using secret keys",
                        "It runs only on quantum processors"
                    ),
                    mcCorrectIndex = 0,
                ),
                DSALevelSpec(
                    id = "c2_boss", chapterId = 2, levelNumber = 5,
                    title = "Chapter Boss: The Block Overlord",
                    subtitle = "Master all 5 symmetric block cipher trials!",
                    interactionType = DSALevelInteractionType.TIMED_BOSS_RACE,
                    storyExplanation = "The Block Overlord has locked down the data vault with layered Feistel and AES barriers! Solve all 5 symmetric encryption challenges before the timer expires!",
                    promptQuestion = "Overcome all 5 Block Overlord trials!",
                    hint = "Recall key sizes, AES transformations, and CBC initialization vectors!",
                    explanation = "The Block Overlord is defeated! You mastered modern Symmetric Encryption and earned the Block Fortress Guardian trophy! 🏆",
                    xpReward = 100, timeLimitSeconds = 50, bossTotalQuestions = 5,
                    bossRapidQuestions = listOf(
                        DSARapidQuestion(
                            prompt = "What is the effective secret key length of the original DES algorithm?",
                            options = listOf("56 bits", "64 bits", "128 bits", "256 bits"),
                            correctIndex = 0,
                            explanation = "DES takes a 64-bit input key, but 8 bits are parity checks, leaving an effective 56-bit key."
                        ),
                        DSARapidQuestion(
                            prompt = "What standard block size does the Advanced Encryption Standard (AES) operate on?",
                            options = listOf("128 bits", "64 bits", "256 bits", "512 bits"),
                            correctIndex = 0,
                            explanation = "AES always operates on 128-bit (16-byte) blocks regardless of key size (128, 192, or 256 bits)."
                        ),
                        DSARapidQuestion(
                            prompt = "Which block cipher mode of operation leaks structural patterns (such as the famous Tux penguin)?",
                            options = listOf("ECB (Electronic Codebook)", "CBC (Cipher Block Chaining)", "CTR (Counter)", "GCM (Galois/Counter)"),
                            correctIndex = 0,
                            explanation = "ECB encrypts identical plaintext blocks into identical ciphertext blocks, preserving visual patterns."
                        ),
                        DSARapidQuestion(
                            prompt = "What is the critical requirement for the Initialization Vector (IV) in CBC mode?",
                            options = listOf("It must be unpredictable and unique for every message", "It must be kept secret like the key", "It must consist only of zero bytes", "It must be 1024 bits long"),
                            correctIndex = 0,
                            explanation = "CBC IVs must be unpredictable (random) and never reused under the same key."
                        ),
                        DSARapidQuestion(
                            prompt = "What cryptographic protection does an AEAD mode (like AES-GCM) provide beyond basic encryption?",
                            options = listOf("Cryptographic integrity verification to detect any ciphertext tampering", "Automatic data compression", "Public key distribution", "DNS resolution"),
                            correctIndex = 0,
                            explanation = "AEAD outputs an authentication tag ensuring any ciphertext modification is detected immediately."
                        ),
                    ),
                ),
            ),
        ),

        // ──────────────────────────────────────────────────────────
        // CHAPTER 3 — ASYMMETRIC CRYPTOGRAPHY: "The Asymmetric Spire"
        // ──────────────────────────────────────────────────────────
        DSAChapterModel(
            id = 3, number = 3,
            title = "Asymmetric Cryptography",
            subtitle = "The Asymmetric Spire",
            theme = DSAEnvironmentTheme.TOWER_PLATES,
            story = "Welcome to the Asymmetric Spire! Public-key cryptography revolutionized security through one-way mathematical trapdoor functions. Discover Diffie-Hellman key exchange, RSA prime factorization, and Elliptic Curve Cryptography!",
            badgeName = "Public Key Master",
            primaryColor = Color(red = 255f/255, green = 149f/255, blue = 0f/255),
            darkColor    = Color(red = 190f/255, green = 100f/255, blue = 0f/255),
            accentColor  = Color(red = 255f/255, green = 75f/255,  blue = 85f/255),
            characterEmoji = "🔑",
            completionBonusXP = 100,
            levels = listOf(
                DSALevelSpec(
                    id = "c3_l1", chapterId = 3, levelNumber = 1,
                    title = "Modular Mathematics",
                    subtitle = "Euler's Totient & Fermat's Theorem",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "Public-key cryptography relies on number theory. Fermat's Little Theorem states a^{p-1} ≡ 1 (mod p). Euler's Totient function φ(n) counts integers coprime to n; for distinct primes p and q, φ(pq) = (p-1)(q-1).",
                    promptQuestion = "If p = 3 and q = 11 are two distinct primes, what is Euler's totient φ(n) where n = p * q = 33?",
                    hint = "Calculate φ(33) = (3 - 1) * (11 - 1)...",
                    explanation = "φ(33) = 2 * 10 = 20! Exactly 20 integers between 1 and 33 share no common factors with 33. 📐",
                    xpReward = 10,
                    mcOptions = listOf(
                        "φ(33) = (3-1)(11-1) = 20",
                        "φ(33) = 33",
                        "φ(33) = 14",
                        "φ(33) = 30"
                    ),
                    mcCorrectIndex = 0,
                ),
                DSALevelSpec(
                    id = "c3_l2", chapterId = 3, levelNumber = 2,
                    title = "Diffie-Hellman Key Exchange",
                    subtitle = "Discrete log & shared secrets",
                    interactionType = DSALevelInteractionType.TAP_CORRECT_NODE,
                    storyExplanation = "Diffie-Hellman allows Alice and Bob to establish a shared secret over an untrusted channel. Alice sends A = g^a mod p; Bob sends B = g^b mod p. Both compute K = g^{ab} mod p without ever sending a or b!",
                    promptQuestion = "What shared secret key K do Alice and Bob compute, which an eavesdropper cannot calculate without solving discrete logs? Tap the target equation.",
                    hint = "Alice computes B^a mod p and Bob computes A^b mod p...",
                    explanation = "Both arrive at K = g^{ab} mod p! The Discrete Logarithm Problem prevents eavesdroppers from finding a or b. 🤝",
                    xpReward = 15,
                    boxes = listOf(
                        DSAVisualBoxItem(value = "K = g^(ab) mod p", index = 0),
                        DSAVisualBoxItem(value = "K = g^(a+b) mod p", index = 1),
                        DSAVisualBoxItem(value = "K = (A + B) mod p", index = 2),
                        DSAVisualBoxItem(value = "K = g^p mod ab", index = 3),
                    ),
                    correctBoxIndex = 0,
                ),
                DSALevelSpec(
                    id = "c3_l3", chapterId = 3, levelNumber = 3,
                    title = "RSA Public-Key Encryption",
                    subtitle = "Prime factorization & trapdoors",
                    interactionType = DSALevelInteractionType.DRAG_AND_DROP_ORDER,
                    storyExplanation = "RSA relies on the difficulty of prime factorization. We pick primes p and q, compute n = pq and φ(n) = (p-1)(q-1). Public key is (e, n); private key d satisfies e · d ≡ 1 (mod φ(n)).",
                    promptQuestion = "Arrange the 4 steps of RSA key generation into their correct algebraic sequence:",
                    hint = "Start with primes p and q, calculate n and totient, choose e, then invert e mod φ(n)...",
                    explanation = "RSA key generation establishes the mathematical trapdoor: multiplying primes is fast, factoring modulus n is intractable! 🔐",
                    xpReward = 20,
                    draggableItems = listOf(
                        "Choose public exponent e coprime to φ(n)",
                        "Select large random primes p and q",
                        "Compute private key d = e^(-1) mod φ(n)",
                        "Compute modulus n = p*q and totient φ(n) = (p-1)(q-1)"
                    ),
                    correctOrder = listOf(
                        "Select large random primes p and q",
                        "Compute modulus n = p*q and totient φ(n) = (p-1)(q-1)",
                        "Choose public exponent e coprime to φ(n)",
                        "Compute private key d = e^(-1) mod φ(n)"
                    ),
                ),
                DSALevelSpec(
                    id = "c3_l4", chapterId = 3, levelNumber = 4,
                    title = "Elliptic Curve Cryptography",
                    subtitle = "ECC point multiplication & secp256k1",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "Elliptic Curve Cryptography (ECC) uses points on curves y² = x³ + ax + b over finite fields. Computing Q = d · G is fast, but finding scalar d from Q is the Elliptic Curve Discrete Log Problem (ECDLP).",
                    promptQuestion = "Why does modern mobile security (e.g. Apple Secure Enclave, Bitcoin, TLS 1.3) favor 256-bit ECC over 3072-bit RSA?",
                    hint = "Does ECC provide equivalent cryptographic strength with vastly smaller keys and lower battery consumption?",
                    explanation = "A 256-bit ECC key matches 3072-bit RSA security with significantly lower compute, memory, and energy costs! ⚡📱",
                    xpReward = 25,
                    mcOptions = listOf(
                        "Equivalent cryptographic strength with vastly smaller keys, lower memory footprint, and faster operations",
                        "ECC does not require mathematics",
                        "RSA has been legally deprecated worldwide",
                        "ECC keys never expire"
                    ),
                    mcCorrectIndex = 0,
                ),
                DSALevelSpec(
                    id = "c3_boss", chapterId = 3, levelNumber = 5,
                    title = "Chapter Boss: The Quantum Cryptanalyst",
                    subtitle = "Survive 5 rapid asymmetric cryptographic trials!",
                    interactionType = DSALevelInteractionType.TIMED_BOSS_RACE,
                    storyExplanation = "The Quantum Cryptanalyst threatens to break classical key exchanges using Shor's algorithm! Prove your mastery of asymmetric trapdoors and post-quantum defenses!",
                    promptQuestion = "Defeat the Quantum Cryptanalyst!",
                    hint = "Review RSA trapdoors, Diffie-Hellman MITM attacks, and quantum threats!",
                    explanation = "You overcame the Quantum Cryptanalyst! You mastered Asymmetric Cryptography and earned the Public Key Master trophy! 🏆",
                    xpReward = 100, timeLimitSeconds = 50, bossTotalQuestions = 5,
                    bossRapidQuestions = listOf(
                        DSARapidQuestion(
                            prompt = "Which quantum algorithm can factor large integers and solve discrete logs in polynomial time?",
                            options = listOf("Shor's Algorithm", "Grover's Algorithm", "Dijkstra's Algorithm", "Euclid's Algorithm"),
                            correctIndex = 0,
                            explanation = "Peter Shor discovered in 1994 that quantum computers can factor integers in polynomial O(log³ n) time."
                        ),
                        DSARapidQuestion(
                            prompt = "What vulnerability does unauthenticated Diffie-Hellman suffer from?",
                            options = listOf("Man-in-the-Middle (MITM) attack", "Brute-force key exhaustion", "Buffer overflow", "SQL injection"),
                            correctIndex = 0,
                            explanation = "Without digital signatures or certificates, an active attacker can negotiate separate keys with Alice and Bob."
                        ),
                        DSARapidQuestion(
                            prompt = "What mathematical hard problem protects RSA encryption?",
                            options = listOf("Integer Factorization Problem", "Knapsack Problem", "Shortest Path Problem", "Graph Isomorphism"),
                            correctIndex = 0,
                            explanation = "Factoring the product n = pq of two large prime numbers remains computationally intractable for classical computers."
                        ),
                        DSARapidQuestion(
                            prompt = "What mathematical structure forms the foundation of modern Post-Quantum Cryptography (PQC)?",
                            options = listOf("Lattice-based cryptography (e.g. ML-KEM / Kyber)", "Caesar shift rings", "DES S-Boxes", "MD5 digests"),
                            correctIndex = 0,
                            explanation = "NIST standardized lattice-based algorithms (ML-KEM/Kyber) because high-dimensional lattices resist Shor's algorithm."
                        ),
                        DSARapidQuestion(
                            prompt = "Why must RSA encryption use randomized padding such as OAEP (Optimal Asymmetric Encryption Padding)?",
                            options = listOf("To prevent deterministic textbook RSA attacks and chosen-ciphertext exploits", "To make ciphertexts smaller", "To speed up CPU clock cycles", "To allow encryption without a key"),
                            correctIndex = 0,
                            explanation = "Textbook RSA C = M^e mod n is deterministic and homomorphic; OAEP adds randomized diffusion to guarantee IND-CCA2 security."
                        ),
                    ),
                ),
            ),
        ),

        // ──────────────────────────────────────────────────────────
        // CHAPTER 4 — INTEGRITY & HASHES: "The Signature Bastion"
        // ──────────────────────────────────────────────────────────
        DSAChapterModel(
            id = 4, number = 4,
            title = "Hashes & Signatures",
            subtitle = "The Signature Bastion",
            theme = DSAEnvironmentTheme.TICKET_STATION,
            story = "Welcome to the Signature Bastion! Security requires integrity and authenticity. Learn how cryptographic hash functions, Merkle-Damgård vs Sponge constructions, HMACs, and X.509 PKI digital certificate chains defend truth!",
            badgeName = "Integrity Champion",
            primaryColor = Color(red = 88f/255,  green = 204f/255, blue = 2f/255),
            darkColor    = Color(red = 70f/255,  green = 163f/255, blue = 2f/255),
            accentColor  = Color(red = 255f/255, green = 195f/255, blue = 0f/255),
            characterEmoji = "🛡️",
            completionBonusXP = 100,
            levels = listOf(
                DSALevelSpec(
                    id = "c4_l1", chapterId = 4, levelNumber = 1,
                    title = "Cryptographic Hash Functions",
                    subtitle = "Pre-image & collision resistance",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "Cryptographic hash functions map arbitrary data to a fixed-size digest. They require Pre-image resistance (one-way), Second pre-image resistance, and Collision resistance (no two distinct inputs produce matching outputs).",
                    promptQuestion = "Which property ensures an attacker cannot take an existing hash H(M) and reverse-engineer the original plaintext message M?",
                    hint = "Look for the property that makes a hash function strictly a 'one-way' street...",
                    explanation = "Pre-image resistance ensures that given hash digest h, finding any message m such that H(m) = h is computationally infeasible! 🔒",
                    xpReward = 10,
                    mcOptions = listOf(
                        "Pre-image Resistance (One-Way Property)",
                        "Collision Resistance",
                        "Reversibility",
                        "High Compression Ratio"
                    ),
                    mcCorrectIndex = 0,
                ),
                DSALevelSpec(
                    id = "c4_l2", chapterId = 4, levelNumber = 2,
                    title = "SHA-2 & SHA-3 Families",
                    subtitle = "Merkle-Damgård & sponge functions",
                    interactionType = DSALevelInteractionType.TAP_CORRECT_NODE,
                    storyExplanation = "While SHA-256 uses the Merkle-Damgård construction, it is vulnerable to Length Extension Attacks. SHA-3 (Keccak) solves this using an innovative Sponge Construction featuring Absorbing and Squeezing phases over a 1600-bit state permutation.",
                    promptQuestion = "Which architectural paradigm powers SHA-3 (Keccak) to eliminate Merkle-Damgård length-extension vulnerabilities? Tap the target architecture.",
                    hint = "Look for the structure with Absorbing and Squeezing phases...",
                    explanation = "The Sponge Construction absorbs input blocks into internal state and squeezes output blocks, completely eliminating length extension attacks! 🧽",
                    xpReward = 15,
                    boxes = listOf(
                        DSAVisualBoxItem(value = "Sponge Construction (Absorb / Squeeze)", index = 0),
                        DSAVisualBoxItem(value = "Merkle-Damgård Compression", index = 1),
                        DSAVisualBoxItem(value = "Feistel Network", index = 2),
                        DSAVisualBoxItem(value = "Galois Field Matrix", index = 3),
                    ),
                    correctBoxIndex = 0,
                ),
                DSALevelSpec(
                    id = "c4_l3", chapterId = 4, levelNumber = 3,
                    title = "Message Authentication Codes",
                    subtitle = "HMAC & authentication tags",
                    interactionType = DSALevelInteractionType.DRAG_AND_DROP_ORDER,
                    storyExplanation = "A hash alone does not prove who sent a message. An HMAC (Hash-based Message Authentication Code) mixes a shared secret key K with the message: HMAC(K, M) = H((K ⊕ opad) || H((K ⊕ ipad) || M)).",
                    promptQuestion = "Order the 3 stages of computing an HMAC:",
                    hint = "First XOR key with inner pad and hash with message, then XOR key with outer pad, then compute outer hash...",
                    explanation = "HMAC's nested hashing structure mathematically guarantees authenticity and resists length-extension vulnerabilities! 🔑🛡️",
                    xpReward = 20,
                    draggableItems = listOf(
                        "XOR secret key K with outer pad (opad)",
                        "XOR secret key K with inner pad (ipad) and hash with message M",
                        "Concatenate opad result with inner hash, then hash final block"
                    ),
                    correctOrder = listOf(
                        "XOR secret key K with inner pad (ipad) and hash with message M",
                        "XOR secret key K with outer pad (opad)",
                        "Concatenate opad result with inner hash, then hash final block"
                    ),
                ),
                DSALevelSpec(
                    id = "c4_l4", chapterId = 4, levelNumber = 4,
                    title = "Digital Signatures & PKI",
                    subtitle = "X.509 certificates & trust chains",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "Digital signatures provide non-repudiation. The sender encrypts the message digest with their private key: S = Sign(K_priv, H(M)). Anyone can verify with public key: Verify(K_pub, H(M), S). Public Key Infrastructure (PKI) binds public keys to identities using X.509 certificates.",
                    promptQuestion = "In a digital signature system, which key must the signer use to create the signature?",
                    hint = "Only the signer must possess this key to ensure non-repudiation...",
                    explanation = "The signer's Private Key! Because only the signer holds the private key, no one else can forge the signature (non-repudiation). ✍️",
                    xpReward = 25,
                    mcOptions = listOf(
                        "Signer's Private Key",
                        "Signer's Public Key",
                        "Recipient's Public Key",
                        "Certificate Authority Root Key"
                    ),
                    mcCorrectIndex = 0,
                ),
                DSALevelSpec(
                    id = "c4_boss", chapterId = 4, levelNumber = 5,
                    title = "Chapter Boss: The Collision Beast",
                    subtitle = "Survive 5 rapid hash and signature trials!",
                    interactionType = DSALevelInteractionType.TIMED_BOSS_RACE,
                    storyExplanation = "The Collision Beast attempts to forge digital signatures and generate hash collisions using the Birthday Paradox! Stand your ground with cryptographic integrity!",
                    promptQuestion = "Defeat the Collision Beast!",
                    hint = "Recall the Birthday Paradox complexity, HMAC keys, and certificate revocation!",
                    explanation = "The Collision Beast is defeated! You mastered Cryptographic Hashes & Digital Signatures and earned the Integrity Champion trophy! 🏆",
                    xpReward = 100, timeLimitSeconds = 50, bossTotalQuestions = 5,
                    bossRapidQuestions = listOf(
                        DSARapidQuestion(
                            prompt = "According to the Birthday Paradox, how many random hash trials are needed to find a collision in an n-bit hash?",
                            options = listOf("2^(n/2)", "2^n", "n²", "n / 2"),
                            correctIndex = 0,
                            explanation = "The Birthday Paradox dramatically reduces collision resistance from 2^n to approximately 2^(n/2) operations."
                        ),
                        DSARapidQuestion(
                            prompt = "Why was MD5 officially abandoned for secure digital certificates?",
                            options = listOf("Practical collision attacks can generate colliding certificates in seconds", "MD5 was too slow", "MD5 keys were too long", "MD5 only works on Linux"),
                            correctIndex = 0,
                            explanation = "Researchers demonstrated practical MD5 collision attacks allowing rogue CA certificate generation."
                        ),
                        DSARapidQuestion(
                            prompt = "What crucial property do Digital Signatures provide that symmetric HMACs cannot?",
                            options = listOf("Non-Repudiation (third parties can verify the signer)", "Lower memory usage", "Faster execution speed", "Symmetric key sharing"),
                            correctIndex = 0,
                            explanation = "Because HMAC uses a shared secret, either party could produce it. Digital signatures use asymmetric keys, proving non-repudiation."
                        ),
                        DSARapidQuestion(
                            prompt = "How can a client check in real time if an X.509 certificate has been revoked by its CA?",
                            options = listOf("OCSP (Online Certificate Status Protocol) query", "DNS lookup", "Ping command", "HTTP GET /favicon.ico"),
                            correctIndex = 0,
                            explanation = "OCSP allows clients to query Certificate Authorities in real time to verify that a certificate has not been revoked."
                        ),
                        DSARapidQuestion(
                            prompt = "What attack takes advantage of naive string comparison (e.g. '==' in Python) when verifying HMAC tags?",
                            options = listOf("Timing Attack (side-channel leak)", "Buffer overflow", "SQL injection", "Man-in-the-Middle"),
                            correctIndex = 0,
                            explanation = "Early-exit string comparisons leak verification timing byte-by-byte; cryptographic systems must use constant-time comparison."
                        ),
                    ),
                ),
            ),
        ),

        // ──────────────────────────────────────────────────────────
        // CHAPTER 5 — NETWORK SECURITY: "The Firewall Citadel"
        // ──────────────────────────────────────────────────────────
        DSAChapterModel(
            id = 5, number = 5,
            title = "Network Security",
            subtitle = "The Firewall Citadel",
            theme = DSAEnvironmentTheme.DATA_KINGDOM,
            story = "Welcome to the Firewall Citadel! Defend the global network across all OSI layers. Master the 1-RTT TLS 1.3 handshake, IPsec encrypted VPN tunnels, stateful packet filtering firewalls, and mitigation of SYN Floods and DNS spoofing!",
            badgeName = "Cyber Citadel Legend",
            primaryColor = Color(red = 50f/255,  green = 215f/255, blue = 75f/255),
            darkColor    = Color(red = 20f/255,  green = 140f/255, blue = 50f/255),
            accentColor  = Color(red = 255f/255, green = 214f/255, blue = 10f/255),
            characterEmoji = "🌐",
            completionBonusXP = 150,
            levels = listOf(
                DSALevelSpec(
                    id = "c5_l1", chapterId = 5, levelNumber = 1,
                    title = "TLS 1.3 Handshake",
                    subtitle = "Encrypted transport & 1-RTT exchange",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "Transport Layer Security (TLS 1.3) protects HTTPS traffic. By allowing clients to send Diffie-Hellman key shares in the initial ClientHello message, it completes the handshake in just 1 Round Trip Time (1-RTT) while eliminating legacy insecure ciphers.",
                    promptQuestion = "How does TLS 1.3 achieve 1-RTT connection setup compared to TLS 1.2's 2-RTT?",
                    hint = "Does the client send its ephemeral key share speculatively in the very first message?",
                    explanation = "Client sends key shares directly inside ClientHello, allowing ServerHello to immediately finalize session keys in a single round trip! ⚡",
                    xpReward = 10,
                    mcOptions = listOf(
                        "Client sends Diffie-Hellman key shares speculatively inside ClientHello",
                        "It eliminates encryption altogether",
                        "It uses unverified UDP packets without handshakes",
                        "It hardcodes a shared master key into the browser binary"
                    ),
                    mcCorrectIndex = 0,
                ),
                DSALevelSpec(
                    id = "c5_l2", chapterId = 5, levelNumber = 2,
                    title = "IPsec & Secure VPNs",
                    subtitle = "AH, ESP & secure tunnels",
                    interactionType = DSALevelInteractionType.TAP_CORRECT_NODE,
                    storyExplanation = "IPsec operates at Network Layer 3. It provides two core protocols: Authentication Header (AH, integrity only) and Encapsulating Security Payload (ESP, confidentiality and integrity). In Tunnel Mode, the entire original IP packet is encrypted inside a new IP header.",
                    promptQuestion = "Which IPsec protocol encrypts the original packet payload to provide confidentiality in VPN tunnels? Tap the target protocol.",
                    hint = "Look for the protocol that provides encryption / confidentiality...",
                    explanation = "ESP (Encapsulating Security Payload) encrypts packet contents, providing confidentiality and integrity in site-to-site VPN tunnels! 🚇🛡️",
                    xpReward = 15,
                    boxes = listOf(
                        DSAVisualBoxItem(value = "ESP (Encapsulating Security Payload)", index = 0),
                        DSAVisualBoxItem(value = "AH (Authentication Header)", index = 1),
                        DSAVisualBoxItem(value = "ICMP (Echo Request)", index = 2),
                        DSAVisualBoxItem(value = "ARP (Address Resolution)", index = 3),
                    ),
                    correctBoxIndex = 0,
                ),
                DSALevelSpec(
                    id = "c5_l3", chapterId = 5, levelNumber = 3,
                    title = "Firewalls & Zero Trust",
                    subtitle = "Stateful inspection & perimeter defense",
                    interactionType = DSALevelInteractionType.DRAG_AND_DROP_ORDER,
                    storyExplanation = "Stateful firewalls maintain connection tables tracking TCP handshake states (SYN_SENT, ESTABLISHED). Zero Trust Architecture (ZTA) extends this with 'Never Trust, Always Verify', enforcing micro-segmentation and continuous identity authentication.",
                    promptQuestion = "Order the 4 steps of a Stateful Packet Inspection firewall processing an incoming packet:",
                    hint = "Extract headers, check state table for matching connection, if established allow, if new SYN check rulebase...",
                    explanation = "Stateful firewalls accelerate traffic by bypassing rules for established sessions while strictly inspecting new SYN handshakes! 🧱🔥",
                    xpReward = 20,
                    draggableItems = listOf(
                        "If matching ESTABLISHED connection: allow packet through immediately",
                        "Extract source/destination IP, port, and TCP flags from packet header",
                        "If new SYN packet: evaluate firewall security rulebase to permit or drop",
                        "Lookup packet 5-tuple in active connection state table"
                    ),
                    correctOrder = listOf(
                        "Extract source/destination IP, port, and TCP flags from packet header",
                        "Lookup packet 5-tuple in active connection state table",
                        "If matching ESTABLISHED connection: allow packet through immediately",
                        "If new SYN packet: evaluate firewall security rulebase to permit or drop"
                    ),
                ),
                DSALevelSpec(
                    id = "c5_l4", chapterId = 5, levelNumber = 4,
                    title = "Network Attacks & Defenses",
                    subtitle = "Mitigating SYN flood, ARP & DNS attacks",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "Defenders mitigate classic network attacks: TCP SYN Floods are mitigated by SYN Cookies; ARP Spoofing is blocked by Dynamic ARP Inspection (DAI); DNS Cache Poisoning is stopped by DNSSEC digital signature chains.",
                    promptQuestion = "How do SYN Cookies protect a server against TCP SYN Flood denial-of-service attacks?",
                    hint = "Does the server encode connection parameters into the initial sequence number instead of allocating RAM?",
                    explanation = "SYN Cookies encode connection state into the TCP sequence number, eliminating RAM buffer allocation until the client returns ACK! 🍪🛡️",
                    xpReward = 25,
                    mcOptions = listOf(
                        "Server encodes state into the TCP sequence number, avoiding state table RAM allocation until client returns ACK",
                        "Server shuts down all network sockets",
                        "Server randomly drops 99% of incoming connections",
                        "Server blocks all traffic originating from port 80"
                    ),
                    mcCorrectIndex = 0,
                ),
                DSALevelSpec(
                    id = "c5_boss", chapterId = 5, levelNumber = 5,
                    title = "Chapter Boss: The Cyber Titan",
                    subtitle = "Ultimate Network Defense Challenge!",
                    interactionType = DSALevelInteractionType.TIMED_BOSS_RACE,
                    storyExplanation = "The Cyber Titan has launched an all-out assault against the global network backbone! Deploy your knowledge of TLS 1.3, IPsec, Firewalls, and DNS defenses to save the network before time expires!",
                    promptQuestion = "Defend the Global Network against the Cyber Titan!",
                    hint = "Trust your full journey across Cryptography and Network Security!",
                    explanation = "THE CYBER TITAN IS DEFEATED! The global network is defended and secure! You are awarded Cyber Citadel Legend and completed the Master Course! 👑🎆🏆",
                    xpReward = 150, timeLimitSeconds = 60, bossTotalQuestions = 5,
                    bossRapidQuestions = listOf(
                        DSARapidQuestion(
                            prompt = "What property guarantees that compromising a server's long-term private key cannot decrypt past recorded TLS traffic?",
                            options = listOf("Forward Secrecy (using ephemeral Diffie-Hellman)", "Symmetric padding", "Certificate Revocation", "DNSSEC"),
                            correctIndex = 0,
                            explanation = "Ephemeral Diffie-Hellman keys are discarded after session completion, ensuring forward secrecy."
                        ),
                        DSARapidQuestion(
                            prompt = "What is the primary difference between IPsec Transport Mode and Tunnel Mode?",
                            options = listOf("Tunnel mode encrypts the entire original IP packet inside a new IP header; Transport mode encrypts payload only", "Transport mode is only for email", "Tunnel mode does not use keys", "Transport mode uses HTTP"),
                            correctIndex = 0,
                            explanation = "Tunnel Mode encapsulates and encrypts the entire original IP packet, creating site-to-site VPNs."
                        ),
                        DSARapidQuestion(
                            prompt = "What core architectural tenet defines Zero Trust Security (ZTA)?",
                            options = listOf("Never Trust, Always Verify — continuous authentication regardless of network location", "Trust all internal LAN connections", "Remove firewalls completely", "Rely exclusively on passwords"),
                            correctIndex = 0,
                            explanation = "Zero Trust assumes threats exist inside and outside the perimeter, enforcing continuous verification and least privilege."
                        ),
                        DSARapidQuestion(
                            prompt = "What does DNSSEC add to the DNS protocol to prevent DNS cache poisoning?",
                            options = listOf("Cryptographic digital signature chains (RRSIG, DNSKEY, DS)", "Faster UDP timeouts", "Encryption of web page HTML", "Automatic domain name expiration"),
                            correctIndex = 0,
                            explanation = "DNSSEC uses public-key digital signatures to authenticate DNS query responses from root servers down to domains."
                        ),
                        DSARapidQuestion(
                            prompt = "What attack manipulates local Layer 2 MAC-to-IP mappings to intercept local subnet traffic?",
                            options = listOf("ARP Spoofing / ARP Poisoning", "SYN Flood", "Cross-Site Scripting (XSS)", "SQL Injection"),
                            correctIndex = 0,
                            explanation = "ARP spoofing sends forged ARP responses to map the attacker's MAC address to the default gateway IP."
                        ),
                    ),
                ),
            ),
        ),
    )
}
