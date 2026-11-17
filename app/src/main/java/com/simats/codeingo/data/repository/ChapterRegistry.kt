package com.simats.codeingo.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simats.codeingo.data.model.DSABossSpec
import com.simats.codeingo.data.model.LessonNodeItem
import com.simats.codeingo.data.model.UnitCharacterType
import com.simats.codeingo.data.model.UnitModel

// ══════════════════════════════════════════════════════════════════
// 🗺️ ChapterRegistry — Syllabus-Complete 25-Chapter CNS Catalog
// Architecture:
//  - Track 1: FOUNDATION (Chapter 0: Basecamp, Levels 101..105)
//  - Track 2: REALM 1: Secrets 101 (Chapters 1..5, Levels 1..25, Boss: Cipher Breaker)
//  - Track 3: REALM 2: Symmetric Forge (Chapters 6..10, Levels 26..50, Boss: Block Overlord)
//  - Track 4: REALM 3: Public-Key Citadel (Chapters 11..15, Levels 51..75, Boss: Quantum Cryptanalyst)
//  - Track 5: REALM 4: Trust & Proof (Chapters 16..20, Levels 76..100, Boss: Collision Beast)
//  - Track 6: REALM 5: Real-World Defense (Chapters 21..25, Levels 106..130, Boss: Cyber Titan)
// ══════════════════════════════════════════════════════════════════

enum class CourseTrack(val displayName: String, val badgeEmoji: String) {
    FOUNDATION("Foundations Primer", "🏕️"),
    CORE("Core Curriculum (25 Chapters)", "🛡️"),
    EXPANSION("Advanced Defense Track", "⚡")
}

data class ChapterDefinition(
    val id: Int,
    val number: Int,
    val track: CourseTrack,
    val title: String,
    val subtitle: String,
    val worldTheme: String,
    val worldEmoji: String,
    val themeColor: Color,
    val themeDarkColor: Color,
    val accentColor: Color,
    val nodes: List<LessonNodeItem>,
    val bossSpec: DSABossSpec? = null,
    val isRecommendedFirst: Boolean = false,
    val description: String = ""
) {
    fun toUnitModel(): UnitModel = UnitModel(
        id = id,
        sectionNumber = number,
        unitNumber = number,
        titleKey = "chapter_${id}_title",
        titleDefault = "$worldEmoji Chapter $number: $title — $worldTheme",
        themeColor = themeColor,
        themeDarkColor = themeDarkColor,
        nodes = nodes,
        characterType = UnitCharacterType.PHOENIX,
        worldTheme = worldTheme,
        bossSpec = bossSpec
    )
}

object ChapterRegistry {

    private fun generateNodes(startLevel: Int, titles: List<String>, isLastBoss: Boolean = false): List<LessonNodeItem> {
        val offsets = listOf(0.dp, (-45).dp, 45.dp, (-20).dp, 0.dp)
        val icons = listOf("lock", "key", "arrow.triangle.swap", "chart.bar", if (isLastBoss) "flame" else "checkmark.shield")
        return (0..4).map { i ->
            LessonNodeItem(
                id = startLevel + i,
                levelNumber = startLevel + i,
                icon = icons[i],
                xOffset = offsets[i],
                title = titles.getOrElse(i) { "Stage ${i + 1}" },
                isBoss = (i == 4 && isLastBoss)
            )
        }
    }

    // ── CHAPTER 0: BASECAMP (Foundations Primer, Non-Blocking) ────
    val chapter0Basecamp: ChapterDefinition = ChapterDefinition(
        id = 0,
        number = 0,
        track = CourseTrack.FOUNDATION,
        title = "Basecamp",
        subtitle = "Cryptography Foundations & Binary Primer",
        worldTheme = "The Foundations Basecamp",
        worldEmoji = "🏕️",
        themeColor = Color(0xFF64748B),
        themeDarkColor = Color(0xFF334155),
        accentColor = Color(0xFF94A3B8),
        isRecommendedFirst = true,
        description = "Essential computer science primer: 8 bits to a byte, hexadecimal, clock arithmetic, and XOR logic.",
        bossSpec = DSABossSpec(
            id = "boss_unit_00",
            name = "The Basecamp Warden",
            title = "Guardian of Bitwise Foundations",
            worldTheme = "The Foundations Basecamp",
            avatarEmoji = "🏕️🦉",
            bossHp = 80,
            timeLimitSeconds = 45,
            quote = "Can you master binary switches, modular rings, and XOR truth before entering the Cipher Sanctum?",
            defeatQuote = "Solid foundations achieved! You are fully prepared to enter Chapter 1.",
            phaseCount = 3,
            targetPhoenixStageAwarded = 1
        ),
        nodes = listOf(
            LessonNodeItem(101, 101, "switch.2", 0.dp, "Stage 1: Bits, Bytes & Hex"),
            LessonNodeItem(102, 102, "clock", (-40).dp, "Stage 2: Clock Math (Modulo)"),
            LessonNodeItem(103, 103, "arrow.triangle.swap", 40.dp, "Stage 3: XOR Magic"),
            LessonNodeItem(104, 104, "shield.lefthalf.filled", (-20).dp, "Stage 4: Who Attacks What? (CIA Triad)"),
            LessonNodeItem(105, 105, "flame", 0.dp, "Stage 5: Boss: Basecamp Trial", isBoss = true)
        )
    )

    // ── 25 SYLLABUS-COMPLETE CNS CHAPTERS ────────────────────────

    val all25Chapters: List<ChapterDefinition> = listOf(
        // ── REALM 1: SECRETS 101 (Chapters 1..5) ──
        ChapterDefinition(
            id = 1, number = 1, track = CourseTrack.CORE,
            title = "Why do we need security?", subtitle = "Secrets 101: Foundation",
            worldTheme = "The Cipher Sanctum", worldEmoji = "🔥",
            themeColor = Color(0xFFFF6E0F), themeDarkColor = Color(0xFFC84105), accentColor = Color(0xFFFFC800),
            nodes = generateNodes(1, listOf("Stage 1: CIA Triad", "Stage 2: Security Policies", "Stage 3: Legal & Ethical Duties", "Stage 4: Threat Surfaces", "Stage 5: Foundation Review"))
        ),
        ChapterDefinition(
            id = 2, number = 2, track = CourseTrack.CORE,
            title = "Attacks, services and defenses", subtitle = "Secrets 101: Threat Models",
            worldTheme = "The Cipher Sanctum", worldEmoji = "🔥",
            themeColor = Color(0xFFFF6E0F), themeDarkColor = Color(0xFFC84105), accentColor = Color(0xFFFFC800),
            nodes = generateNodes(6, listOf("Stage 1: Active vs Passive", "Stage 2: Security Services", "Stage 3: OSI Architecture", "Stage 4: Defense in Depth", "Stage 5: Threat Sorter Review"))
        ),
        ChapterDefinition(
            id = 3, number = 3, track = CourseTrack.CORE,
            title = "Swapping letters", subtitle = "Secrets 101: Substitution",
            worldTheme = "The Cipher Sanctum", worldEmoji = "🔥",
            themeColor = Color(0xFFFF6E0F), themeDarkColor = Color(0xFFC84105), accentColor = Color(0xFFFFC800),
            nodes = generateNodes(11, listOf("Stage 1: Caesar Shift", "Stage 2: Monoalphabetic", "Stage 3: Playfair Matrix", "Stage 4: Polyalphabetic Vigenère", "Stage 5: Substitution Review"))
        ),
        ChapterDefinition(
            id = 4, number = 4, track = CourseTrack.CORE,
            title = "Shuffling & hiding", subtitle = "Secrets 101: Transposition",
            worldTheme = "The Cipher Sanctum", worldEmoji = "🔥",
            themeColor = Color(0xFFFF6E0F), themeDarkColor = Color(0xFFC84105), accentColor = Color(0xFFFFC800),
            nodes = generateNodes(16, listOf("Stage 1: Rail Fence Cipher", "Stage 2: Columnar Transposition", "Stage 3: LSB Steganography", "Stage 4: Product Ciphers", "Stage 5: Transposition Review"))
        ),
        ChapterDefinition(
            id = 5, number = 5, track = CourseTrack.CORE,
            title = "Perfect secrecy & breaking codes", subtitle = "Secrets 101: Cryptanalysis",
            worldTheme = "The Cipher Sanctum", worldEmoji = "🔥",
            themeColor = Color(0xFFFF6E0F), themeDarkColor = Color(0xFFC84105), accentColor = Color(0xFFFFC800),
            bossSpec = DSABossSpec(
                id = "boss_unit_01",
                name = "The Cipher Breaker",
                title = "Guardian of the Cipher Sanctum",
                worldTheme = "The Cipher Sanctum",
                avatarEmoji = "🔐👹",
                bossHp = 100,
                timeLimitSeconds = 45,
                quote = "All your classical ciphers will shatter under my frequency analysis!",
                defeatQuote = "Impossible! Your layered substitution and transposition defied all cryptanalysis!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 3
            ),
            nodes = generateNodes(21, listOf("Stage 1: One-Time Pad (OTP)", "Stage 2: Perfect Secrecy & Shannon", "Stage 3: Frequency Cryptanalysis", "Stage 4: Kasiski Examination", "Stage 5: Boss: The Cipher Breaker"), isLastBoss = true)
        ),

        // ── REALM 2: SYMMETRIC FORGE (Chapters 6..10) ──
        ChapterDefinition(
            id = 6, number = 6, track = CourseTrack.CORE,
            title = "Clock math (math toolkit I)", subtitle = "Symmetric Forge: Modulo",
            worldTheme = "The Block Cipher Forge", worldEmoji = "⚙️",
            themeColor = Color(0xFFF02D4B), themeDarkColor = Color(0xFFB41432), accentColor = Color(0xFFFF4D6D),
            nodes = generateNodes(26, listOf("Stage 1: Modular Congruence", "Stage 2: Clock Arithmetic", "Stage 3: Euclid's GCD", "Stage 4: Algebraic Structures", "Stage 5: Modulo Review"))
        ),
        ChapterDefinition(
            id = 7, number = 7, track = CourseTrack.CORE,
            title = "How block ciphers think", subtitle = "Symmetric Forge: Feistel",
            worldTheme = "The Block Cipher Forge", worldEmoji = "⚙️",
            themeColor = Color(0xFFF02D4B), themeDarkColor = Color(0xFFB41432), accentColor = Color(0xFFFF4D6D),
            nodes = generateNodes(31, listOf("Stage 1: Block vs Stream", "Stage 2: Confusion & Diffusion", "Stage 3: Feistel Cipher Structure", "Stage 4: Simplified DES (S-DES)", "Stage 5: Feistel Review"))
        ),
        ChapterDefinition(
            id = 8, number = 8, track = CourseTrack.CORE,
            title = "DES and why it aged", subtitle = "Symmetric Forge: DES Evolution",
            worldTheme = "The Block Cipher Forge", worldEmoji = "⚙️",
            themeColor = Color(0xFFF02D4B), themeDarkColor = Color(0xFFB41432), accentColor = Color(0xFFFF4D6D),
            nodes = generateNodes(36, listOf("Stage 1: DES 16-Round Anatomy", "Stage 2: Key Schedule & S-Boxes", "Stage 3: Brute Force & 56-bit Key", "Stage 4: Triple DES (3DES)", "Stage 5: DES Analysis Review"))
        ),
        ChapterDefinition(
            id = 9, number = 9, track = CourseTrack.CORE,
            title = "AES, the modern standard", subtitle = "Symmetric Forge: Rijndael",
            worldTheme = "The Block Cipher Forge", worldEmoji = "⚙️",
            themeColor = Color(0xFFF02D4B), themeDarkColor = Color(0xFFB41432), accentColor = Color(0xFFFF4D6D),
            nodes = generateNodes(41, listOf("Stage 1: AES 4x4 State Matrix", "Stage 2: SubBytes & ShiftRows", "Stage 3: MixColumns & AddRoundKey", "Stage 4: Galois Field GF(2^8)", "Stage 5: AES Architecture Review"))
        ),
        ChapterDefinition(
            id = 10, number = 10, track = CourseTrack.CORE,
            title = "Modes & stream ciphers", subtitle = "Symmetric Forge: Modes & RC4",
            worldTheme = "The Block Cipher Forge", worldEmoji = "⚙️",
            themeColor = Color(0xFFF02D4B), themeDarkColor = Color(0xFFB41432), accentColor = Color(0xFFFF4D6D),
            bossSpec = DSABossSpec(
                id = "boss_unit_02",
                name = "The Block Overlord",
                title = "Master of the S-Box Citadel",
                worldTheme = "The Block Cipher Forge",
                avatarEmoji = "⚙️👹",
                bossHp = 120,
                timeLimitSeconds = 50,
                quote = "Feel the crushing diffusion of my 16-round Feistel matrix!",
                defeatQuote = "Curse your Galois field arithmetic and inverse S-boxes!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 5
            ),
            nodes = generateNodes(46, listOf("Stage 1: ECB Penguin Trap", "Stage 2: CBC & IV Chaining", "Stage 3: CTR Mode & Parallelism", "Stage 4: Stream Ciphers & RC4", "Stage 5: Boss: The Block Overlord"), isLastBoss = true)
        ),

        // ── REALM 3: PUBLIC-KEY CITADEL (Chapters 11..15) ──
        ChapterDefinition(
            id = 11, number = 11, track = CourseTrack.CORE,
            title = "Prime numbers (math toolkit II)", subtitle = "Public-Key Citadel: Primes",
            worldTheme = "The Key Exchange Citadel", worldEmoji = "🔑",
            themeColor = Color(0xFFFFA500), themeDarkColor = Color(0xFFD26E00), accentColor = Color(0xFFFFD166),
            nodes = generateNodes(51, listOf("Stage 1: Primes & Factoring Trapdoors", "Stage 2: Primality Testing (Miller-Rabin)", "Stage 3: Euler's Totient Function", "Stage 4: Fermat's Little Theorem", "Stage 5: Primes Review"))
        ),
        ChapterDefinition(
            id = 12, number = 12, track = CourseTrack.CORE,
            title = "CRT & discrete logs", subtitle = "Public-Key Citadel: Modular Toolkit",
            worldTheme = "The Key Exchange Citadel", worldEmoji = "🔑",
            themeColor = Color(0xFFFFA500), themeDarkColor = Color(0xFFD26E00), accentColor = Color(0xFFFFD166),
            nodes = generateNodes(56, listOf("Stage 1: Chinese Remainder Theorem", "Stage 2: Modular Exponentiation", "Stage 3: Discrete Logarithm Problem", "Stage 4: Primitive Roots", "Stage 5: Mod Toolkit Review"))
        ),
        ChapterDefinition(
            id = 13, number = 13, track = CourseTrack.CORE,
            title = "RSA step by step", subtitle = "Public-Key Citadel: Asymmetric Keys",
            worldTheme = "The Key Exchange Citadel", worldEmoji = "🔑",
            themeColor = Color(0xFFFFA500), themeDarkColor = Color(0xFFD26E00), accentColor = Color(0xFFFFD166),
            nodes = generateNodes(61, listOf("Stage 1: Key Generation (p, q, n, e, d)", "Stage 2: RSA Encryption & Decryption", "Stage 3: Factoring Difficulty", "Stage 4: OAEP Padding Defense", "Stage 5: RSA Review"))
        ),
        ChapterDefinition(
            id = 14, number = 14, track = CourseTrack.CORE,
            title = "Sharing keys safely", subtitle = "Public-Key Citadel: Key Exchange",
            worldTheme = "The Key Exchange Citadel", worldEmoji = "🔑",
            themeColor = Color(0xFFFFA500), themeDarkColor = Color(0xFFD26E00), accentColor = Color(0xFFFFD166),
            nodes = generateNodes(66, listOf("Stage 1: Diffie-Hellman Key Exchange", "Stage 2: Paint-Mixing Analogy", "Stage 3: ElGamal Cryptosystem", "Stage 4: Man-in-the-Middle Attacks", "Stage 5: Key Exchange Review"))
        ),
        ChapterDefinition(
            id = 15, number = 15, track = CourseTrack.CORE,
            title = "Elliptic curves", subtitle = "Public-Key Citadel: Curves & PQC",
            worldTheme = "The Key Exchange Citadel", worldEmoji = "🔑",
            themeColor = Color(0xFFFFA500), themeDarkColor = Color(0xFFD26E00), accentColor = Color(0xFFFFD166),
            bossSpec = DSABossSpec(
                id = "boss_unit_03",
                name = "The Quantum Cryptanalyst",
                title = "Breaker of Asymmetric Keys",
                worldTheme = "The Key Exchange Citadel",
                avatarEmoji = "🔑👹",
                bossHp = 140,
                timeLimitSeconds = 55,
                quote = "Shor's algorithm will factor your primes into dust!",
                defeatQuote = "Your discrete log trapdoor held against my quantum simulation!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 8
            ),
            nodes = generateNodes(71, listOf("Stage 1: Elliptic Curve Geometry", "Stage 2: Point Addition & Doubling", "Stage 3: ECC vs RSA Key Sizes", "Stage 4: ECDH & ECDSA Standards", "Stage 5: Boss: The Quantum Cryptanalyst"), isLastBoss = true)
        ),

        // ── REALM 4: TRUST & PROOF (Chapters 16..20) ──
        ChapterDefinition(
            id = 16, number = 16, track = CourseTrack.CORE,
            title = "Hash functions", subtitle = "Trust & Proof: One-Way Digests",
            worldTheme = "The Integrity Foundry", worldEmoji = "🛡️",
            themeColor = Color(0xFFB946FA), themeDarkColor = Color(0xFF8228BE), accentColor = Color(0xFFD884FD),
            nodes = generateNodes(76, listOf("Stage 1: One-Way Property & Preimage", "Stage 2: Avalanche Effect", "Stage 3: Birthday Paradox", "Stage 4: SHA-2 & SHA-3 Families", "Stage 5: Hash Review"))
        ),
        ChapterDefinition(
            id = 17, number = 17, track = CourseTrack.CORE,
            title = "MAC, HMAC and AEAD", subtitle = "Trust & Proof: Authentication",
            worldTheme = "The Integrity Foundry", worldEmoji = "🛡️",
            themeColor = Color(0xFFB946FA), themeDarkColor = Color(0xFF8228BE), accentColor = Color(0xFFD884FD),
            nodes = generateNodes(81, listOf("Stage 1: Message Authentication Codes", "Stage 2: HMAC Construction & Inner/Outer Pads", "Stage 3: CMAC Standard", "Stage 4: Authenticated Encryption (GCM)", "Stage 5: MAC Review"))
        ),
        ChapterDefinition(
            id = 18, number = 18, track = CourseTrack.CORE,
            title = "Digital signatures", subtitle = "Trust & Proof: Signing & Verifying",
            worldTheme = "The Integrity Foundry", worldEmoji = "🛡️",
            themeColor = Color(0xFFB946FA), themeDarkColor = Color(0xFF8228BE), accentColor = Color(0xFFD884FD),
            nodes = generateNodes(86, listOf("Stage 1: Sign with Private, Verify with Public", "Stage 2: Non-Repudiation Guarantee", "Stage 3: RSA Signatures", "Stage 4: Digital Signature Standard (DSS/DSA)", "Stage 5: Signature Review"))
        ),
        ChapterDefinition(
            id = 19, number = 19, track = CourseTrack.CORE,
            title = "Proving who you are", subtitle = "Trust & Proof: Identity Proofs",
            worldTheme = "The Integrity Foundry", worldEmoji = "🛡️",
            themeColor = Color(0xFFB946FA), themeDarkColor = Color(0xFF8228BE), accentColor = Color(0xFFD884FD),
            nodes = generateNodes(91, listOf("Stage 1: Authentication Factors", "Stage 2: Password Hashing (Salt, Argon2, bcrypt)", "Stage 3: Challenge-Response Authentication", "Stage 4: Biometrics & Liveness", "Stage 5: Authentication Review"))
        ),
        ChapterDefinition(
            id = 20, number = 20, track = CourseTrack.CORE,
            title = "Kerberos and certificates", subtitle = "Trust & Proof: PKI & Trust Chains",
            worldTheme = "The Integrity Foundry", worldEmoji = "🛡️",
            themeColor = Color(0xFFB946FA), themeDarkColor = Color(0xFF8228BE), accentColor = Color(0xFFD884FD),
            bossSpec = DSABossSpec(
                id = "boss_unit_04",
                name = "The Collision Beast",
                title = "Devourer of Hash Digest Integrity",
                worldTheme = "The Integrity Foundry",
                avatarEmoji = "🛡️👹",
                bossHp = 160,
                timeLimitSeconds = 60,
                quote = "My birthday paradox will find two plaintexts with the identical digest!",
                defeatQuote = "Inconceivable! 256 bits of collision resistance held firm!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 11
            ),
            nodes = generateNodes(96, listOf("Stage 1: Kerberos Authentication Service & TGT", "Stage 2: Ticket Granting Server (TGS)", "Stage 3: X.509 Certificate Structure", "Stage 4: Public Key Infrastructure (PKI) & CRL/OCSP", "Stage 5: Boss: The Collision Beast"), isLastBoss = true)
        ),

        // ── REALM 5: REAL-WORLD DEFENSE (Chapters 21..25) ──
        ChapterDefinition(
            id = 21, number = 21, track = CourseTrack.CORE,
            title = "Email & IP security", subtitle = "Real-World Defense: Protocols",
            worldTheme = "The Firewall Citadel", worldEmoji = "⚡",
            themeColor = Color(0xFF10B981), themeDarkColor = Color(0xFF047857), accentColor = Color(0xFF34D399),
            nodes = generateNodes(106, listOf("Stage 1: PGP Web of Trust", "Stage 2: S/MIME Enterprise Certificates", "Stage 3: IPsec Architecture (AH vs ESP)", "Stage 4: Tunnel vs Transport Mode & IKE", "Stage 5: Email & IP Review"))
        ),
        ChapterDefinition(
            id = 22, number = 22, track = CourseTrack.CORE,
            title = "Web & wireless security", subtitle = "Real-World Defense: TLS & Wi-Fi",
            worldTheme = "The Firewall Citadel", worldEmoji = "⚡",
            themeColor = Color(0xFF10B981), themeDarkColor = Color(0xFF047857), accentColor = Color(0xFF34D399),
            nodes = generateNodes(111, listOf("Stage 1: TLS 1.3 1-RTT Handshake", "Stage 2: Forward Secrecy & ECDHE", "Stage 3: WPA2 4-Way Handshake & KRACK", "Stage 4: WPA3 SAE (Dragonfly) Defense", "Stage 5: Web & Wireless Review"))
        ),
        ChapterDefinition(
            id = 23, number = 23, track = CourseTrack.CORE,
            title = "Firewalls, IDS and intruders", subtitle = "Real-World Defense: Perimeters",
            worldTheme = "The Firewall Citadel", worldEmoji = "⚡",
            themeColor = Color(0xFF10B981), themeDarkColor = Color(0xFF047857), accentColor = Color(0xFF34D399),
            nodes = generateNodes(116, listOf("Stage 1: Packet Filters vs Stateful Inspection", "Stage 2: Application Firewalls & DMZs", "Stage 3: Intrusion Detection vs Prevention (IDS/IPS)", "Stage 4: Honeypots & Deception Tech", "Stage 5: Perimeter Review"))
        ),
        ChapterDefinition(
            id = 24, number = 24, track = CourseTrack.CORE,
            title = "Malware, web attacks, cloud & IoT", subtitle = "Real-World Defense: Threats",
            worldTheme = "The Firewall Citadel", worldEmoji = "⚡",
            themeColor = Color(0xFF10B981), themeDarkColor = Color(0xFF047857), accentColor = Color(0xFF34D399),
            nodes = generateNodes(121, listOf("Stage 1: Malware Taxonomy (Viruses, Worms, Ransomware)", "Stage 2: Denial of Service (DoS & DDoS Botnets)", "Stage 3: Web Injections (SQLi & XSS Sanitization)", "Stage 4: Cloud & IoT Security Fundamentals", "Stage 5: Threat Landscape Review"))
        ),
        ChapterDefinition(
            id = 25, number = 25, track = CourseTrack.CORE,
            title = "The future of security", subtitle = "Real-World Defense: Frontiers",
            worldTheme = "The Firewall Citadel", worldEmoji = "⚡",
            themeColor = Color(0xFF10B981), themeDarkColor = Color(0xFF047857), accentColor = Color(0xFF34D399),
            bossSpec = DSABossSpec(
                id = "boss_unit_05",
                name = "The Cyber Titan",
                title = "Sovereign of the Distributed Perimeter",
                worldTheme = "The Firewall Citadel",
                avatarEmoji = "⚡👹",
                bossHp = 200,
                timeLimitSeconds = 75,
                quote = "My distributed botnets and SYN floods will overwhelm your perimeter!",
                defeatQuote = "The Zero Trust defense held! You have mastered all 25 chapters of Cryptography & Network Security!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 15
            ),
            nodes = generateNodes(126, listOf("Stage 1: Post-Quantum Cryptography (ML-KEM & ML-DSA)", "Stage 2: Zero-Knowledge Proofs (Ali Baba Cave)", "Stage 3: Merkle Trees & Blockchain Integrity", "Stage 4: Passkeys & WebAuthn / FIDO2", "Stage 5: Boss: The Cyber Titan"), isLastBoss = true)
        )
    )

    // Core chapters (1..5) & Expansion chapters (6..25)
    val coreChapters: List<ChapterDefinition> get() = all25Chapters.take(5)
    val expansionChapters: List<ChapterDefinition> get() = all25Chapters.drop(5)

    private val allChaptersMap: Map<Int, ChapterDefinition> = all25Chapters.associateBy { it.id }

    // ── REGISTRY ACCESSORS ────────────────────────────────────────

    fun getAllChapters(): List<ChapterDefinition> = listOf(chapter0Basecamp) + all25Chapters

    fun getCoreUnits(): List<UnitModel> = coreChapters.map { it.toUnitModel() }

    fun getExpansionUnits(): List<UnitModel> = expansionChapters.map { it.toUnitModel() }

    fun getBasecampUnit(): UnitModel = chapter0Basecamp.toUnitModel()

    fun getChapterById(chapterId: Int): ChapterDefinition? = when (chapterId) {
        0 -> chapter0Basecamp
        in 1..25 -> allChaptersMap[chapterId]
        else -> null
    }

    fun getChapterForLevel(levelNumber: Int): ChapterDefinition? = when {
        isBasecampLevel(levelNumber) -> chapter0Basecamp
        levelNumber in 1..100 -> {
            val chapterId = ((levelNumber - 1) / 5) + 1
            allChaptersMap[chapterId]
        }
        levelNumber in 106..130 -> {
            val chapterId = ((levelNumber - 106) / 5) + 21
            allChaptersMap[chapterId]
        }
        else -> null
    }

    fun isCoreLevel(levelNumber: Int): Boolean = (levelNumber in 1..100) || (levelNumber in 106..130)

    fun isExpansionLevel(levelNumber: Int): Boolean = levelNumber in 26..50 // for legacy test backwards compat

    fun isBasecampLevel(levelNumber: Int): Boolean = levelNumber in 101..105

    fun getTrackForLevel(levelNumber: Int): CourseTrack = when {
        isBasecampLevel(levelNumber) -> CourseTrack.FOUNDATION
        else -> CourseTrack.CORE
    }
}
