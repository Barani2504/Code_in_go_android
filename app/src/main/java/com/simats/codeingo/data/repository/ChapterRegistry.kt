package com.simats.codeingo.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simats.codeingo.data.model.DSABossSpec
import com.simats.codeingo.data.model.LessonNodeItem
import com.simats.codeingo.data.model.UnitCharacterType
import com.simats.codeingo.data.model.UnitModel

// ══════════════════════════════════════════════════════════════════
// 🗺️ ChapterRegistry — Data-Driven Course & Expansion Catalog
// Architecture:
//  - Track 1: FOUNDATION (Chapter 0: Basecamp, Levels 101..105)
//  - Track 2: CORE (Chapters 1..5, Levels 1..25, Frozen iOS Parity)
//  - Track 3: EXPANSION (Chapters 6..10, Levels 26..50, Android-only)
// ══════════════════════════════════════════════════════════════════

enum class CourseTrack(val displayName: String, val badgeEmoji: String) {
    FOUNDATION("Foundations Primer", "🏕️"),
    CORE("Core Curriculum (25 Levels)", "🛡️"),
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

    // ── CHAPTER 0: BASECAMP (Foundations Primer, Non-Blocking) ────
    val chapter0Basecamp: ChapterDefinition = ChapterDefinition(
        id = 0,
        number = 0,
        track = CourseTrack.FOUNDATION,
        title = "Basecamp",
        subtitle = "Cryptography Foundations & Binary Primer",
        worldTheme = "The Foundations Basecamp",
        worldEmoji = "🏕️",
        themeColor = Color(0xFF64748B),     // Titanium Slate
        themeDarkColor = Color(0xFF334155), // Dark Slate
        accentColor = Color(0xFF94A3B8),    // Platinum Silver
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

    // ── CORE CHAPTERS 1..5 (Frozen Parity Invariants) ─────────────
    val coreChapters: List<ChapterDefinition> = listOf(
        // Chapter 1: Classical Ciphers (Levels 1..5)
        ChapterDefinition(
            id = 1, number = 1,
            track = CourseTrack.CORE,
            title = "Classical Ciphers",
            subtitle = "The Cipher Sanctum",
            worldTheme = "The Cipher Sanctum",
            worldEmoji = "🔥",
            themeColor = Color(0xFFFF6E0F),
            themeDarkColor = Color(0xFFC84105),
            accentColor = Color(0xFFFFC800),
            nodes = listOf(
                LessonNodeItem(1, 1, "lock", 0.dp, "Stage 1: Meet the Shift Cipher"),
                LessonNodeItem(2, 2, "key", (-45).dp, "Stage 2: Substitution Ciphers"),
                LessonNodeItem(3, 3, "arrow.triangle.swap", 45.dp, "Stage 3: Transposition Ciphers"),
                LessonNodeItem(4, 4, "chart.bar", (-20).dp, "Stage 4: Frequency Cryptanalysis"),
                LessonNodeItem(5, 5, "flame", 0.dp, "Stage 5: Boss: The Cipher Breaker", isBoss = true)
            ),
            bossSpec = DSABossSpec(
                id = "boss_unit_01",
                name = "The Sphinx of Shadows",
                title = "Guardian of the Cipher Sanctum",
                worldTheme = "The Cipher Sanctum",
                avatarEmoji = "🔐👹",
                bossHp = 100,
                timeLimitSeconds = 45,
                quote = "All your classical ciphers will shatter under my frequency analysis!",
                defeatQuote = "Impossible! Your layered substitution and transposition defied all cryptanalysis!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 3
            )
        ),
        // Chapter 2: Symmetric Encryption (Levels 6..10)
        ChapterDefinition(
            id = 2, number = 2,
            track = CourseTrack.CORE,
            title = "Symmetric Encryption",
            subtitle = "The Block Fortress",
            worldTheme = "The Block Fortress",
            worldEmoji = "🏰",
            themeColor = Color(0xFFF02D4B),
            themeDarkColor = Color(0xFFB41432),
            accentColor = Color(0xFFFF4D6D),
            nodes = listOf(
                LessonNodeItem(6, 6, "shield", 0.dp, "Stage 1: DES & Feistel Networks"),
                LessonNodeItem(7, 7, "square.grid.3x3", 45.dp, "Stage 2: AES Architecture"),
                LessonNodeItem(8, 8, "link", (-45).dp, "Stage 3: Block Cipher Modes (CBC/CTR)"),
                LessonNodeItem(9, 9, "checkmark.shield", 20.dp, "Stage 4: Authenticated Encryption (GCM)"),
                LessonNodeItem(10, 10, "flame", 0.dp, "Stage 5: Boss: The Block Overlord", isBoss = true)
            ),
            bossSpec = DSABossSpec(
                id = "boss_unit_02",
                name = "The Block Overlord",
                title = "Master of the S-Box Citadel",
                worldTheme = "The Block Fortress",
                avatarEmoji = "⚙️👹",
                bossHp = 120,
                timeLimitSeconds = 50,
                quote = "Feel the crushing diffusion of my 16-round Feistel matrix!",
                defeatQuote = "Curse your Galoise field arithmetic and inverse S-boxes!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 5
            )
        ),
        // Chapter 3: Asymmetric Cryptography (Levels 11..15)
        ChapterDefinition(
            id = 3, number = 3,
            track = CourseTrack.CORE,
            title = "Asymmetric Cryptography",
            subtitle = "The Asymmetric Spire",
            worldTheme = "The Asymmetric Spire",
            worldEmoji = "🔑",
            themeColor = Color(0xFFFFA500),
            themeDarkColor = Color(0xFFD26E00),
            accentColor = Color(0xFFFFD166),
            nodes = listOf(
                LessonNodeItem(11, 11, "number", 0.dp, "Stage 1: Modular Math & Euler's Totient"),
                LessonNodeItem(12, 12, "arrow.triangle.2.circlepath", (-45).dp, "Stage 2: Diffie-Hellman Key Exchange"),
                LessonNodeItem(13, 13, "key.horizontal", 45.dp, "Stage 3: RSA Public-Key Encryption"),
                LessonNodeItem(14, 14, "waveform.path", (-20).dp, "Stage 4: Elliptic Curve Cryptography"),
                LessonNodeItem(15, 15, "flame", 0.dp, "Stage 5: Boss: The Quantum Cryptanalyst", isBoss = true)
            ),
            bossSpec = DSABossSpec(
                id = "boss_unit_03",
                name = "The Quantum Cryptanalyst",
                title = "Breaker of Asymmetric Keys",
                worldTheme = "The Asymmetric Spire",
                avatarEmoji = "🏰👹",
                bossHp = 140,
                timeLimitSeconds = 55,
                quote = "Shor's algorithm will factor your primes into dust!",
                defeatQuote = "Your discrete log trapdoor held against my quantum simulation!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 8
            )
        ),
        // Chapter 4: Hashes & Signatures (Levels 16..20)
        ChapterDefinition(
            id = 4, number = 4,
            track = CourseTrack.CORE,
            title = "Hashes & Signatures",
            subtitle = "The Signature Bastion",
            worldTheme = "The Signature Bastion",
            worldEmoji = "🛡️",
            themeColor = Color(0xFFB946FA),
            themeDarkColor = Color(0xFF8228BE),
            accentColor = Color(0xFFD884FD),
            nodes = listOf(
                LessonNodeItem(16, 16, "fingerprint", 0.dp, "Stage 1: Cryptographic Hash Functions"),
                LessonNodeItem(17, 17, "arrow.down.doc", 45.dp, "Stage 2: SHA-2 & SHA-3 Families"),
                LessonNodeItem(18, 18, "lock.badge.clock", (-45).dp, "Stage 3: Message Authentication Codes (HMAC)"),
                LessonNodeItem(19, 19, "doc.plaintext", 20.dp, "Stage 4: Digital Signatures & PKI"),
                LessonNodeItem(20, 20, "flame", 0.dp, "Stage 5: Boss: The Collision Beast", isBoss = true)
            ),
            bossSpec = DSABossSpec(
                id = "boss_unit_04",
                name = "The Collision Beast",
                title = "Devourer of Hash Digest Integrity",
                worldTheme = "The Signature Bastion",
                avatarEmoji = "🛡️👹",
                bossHp = 160,
                timeLimitSeconds = 60,
                quote = "My birthday paradox will find two plaintexts with the identical digest!",
                defeatQuote = "Inconceivable! 256 bits of collision resistance held firm!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 11
            )
        ),
        // Chapter 5: Network Security (Levels 21..25)
        ChapterDefinition(
            id = 5, number = 5,
            track = CourseTrack.CORE,
            title = "Network Security",
            subtitle = "The Firewall Citadel",
            worldTheme = "The Firewall Citadel",
            worldEmoji = "🌐",
            themeColor = Color(0xFFFF4623),
            themeDarkColor = Color(0xFFC31E0A),
            accentColor = Color(0xFFFF7A59),
            nodes = listOf(
                LessonNodeItem(21, 21, "network", 0.dp, "Stage 1: TLS 1.3 Handshake"),
                LessonNodeItem(22, 22, "lock.shield", (-45).dp, "Stage 2: IPsec & Secure VPNs"),
                LessonNodeItem(23, 23, "flame.fill", 45.dp, "Stage 3: Firewalls & Zero Trust"),
                LessonNodeItem(24, 24, "exclamationmark.shield", (-20).dp, "Stage 4: Network Attacks & Defenses"),
                LessonNodeItem(25, 25, "flame", 0.dp, "Stage 5: Boss: The Cyber Titan", isBoss = true)
            ),
            bossSpec = DSABossSpec(
                id = "boss_unit_05",
                name = "The Cyber Titan",
                title = "Sovereign of the Distributed Perimeter",
                worldTheme = "The Firewall Citadel",
                avatarEmoji = "⚡👹",
                bossHp = 200,
                timeLimitSeconds = 75,
                quote = "My distributed botnets and SYN floods will overwhelm your perimeter!",
                defeatQuote = "The Zero Trust defense held! You have mastered the core realms of cryptography!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 15
            )
        )
    )

    // ── EXPANSION CHAPTERS 6..10 (Android-Only Advanced Defense) ──
    val expansionChapters: List<ChapterDefinition> = listOf(
        // Chapter 6: Identity Vault (Levels 26..30)
        ChapterDefinition(
            id = 6, number = 6,
            track = CourseTrack.EXPANSION,
            title = "Identity Vault",
            subtitle = "Authentication & Key Management",
            worldTheme = "The Identity Vault",
            worldEmoji = "🛂",
            themeColor = Color(0xFF4F46E5),     // Deep Indigo
            themeDarkColor = Color(0xFF3730A3),
            accentColor = Color(0xFF818CF8),
            description = "Master identity verification: Multi-factor authentication, TOTP/HOTP algorithms, Kerberos ticket granting, and KMS key lifecycles.",
            bossSpec = DSABossSpec(
                id = "boss_unit_06",
                name = "The Gatekeeper",
                title = "Warden of Identity & Delegation",
                worldTheme = "The Identity Vault",
                avatarEmoji = "🛂👹",
                bossHp = 180,
                timeLimitSeconds = 60,
                quote = "Without valid claims and signed proof, none shall pass my token inspection!",
                defeatQuote = "Your cryptographic identity proofs and PKCE delegation flows are flawless!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 16
            ),
            nodes = listOf(
                LessonNodeItem(26, 26, "person.badge.key", 0.dp, "Stage 1: Prove It's You (Factors & Passwords)"),
                LessonNodeItem(27, 27, "clock.badge.checkmark", 45.dp, "Stage 2: One-Time Codes (HOTP & TOTP)"),
                LessonNodeItem(28, 28, "ticket", (-45).dp, "Stage 3: Ticket Booth (Kerberos & TGT)"),
                LessonNodeItem(29, 29, "key.viewfinder", 20.dp, "Stage 4: Key Lifecycle & KMS"),
                LessonNodeItem(30, 30, "flame", 0.dp, "Stage 5: Boss: The Gatekeeper (OAuth & OIDC)", isBoss = true)
            )
        ),
        // Chapter 7: Web Fortress (Levels 31..35)
        ChapterDefinition(
            id = 7, number = 7,
            track = CourseTrack.EXPANSION,
            title = "Web Fortress",
            subtitle = "Web & Application Security",
            worldTheme = "The Web Fortress",
            worldEmoji = "🏰",
            themeColor = Color(0xFF0EA5E9),     // Cyber Cyan
            themeDarkColor = Color(0xFF0369A1),
            accentColor = Color(0xFF38BDF8),
            description = "Defend modern web applications: Same-origin policy, parameterized query defense, XSS output encoding, and secure build checklists.",
            bossSpec = DSABossSpec(
                id = "boss_unit_07",
                name = "The Bug Bounty Warden",
                title = "Architect of Zero-Day Exploits",
                worldTheme = "The Web Fortress",
                avatarEmoji = "🏰👹",
                bossHp = 190,
                timeLimitSeconds = 60,
                quote = "Every unescaped string in your application will become my injection payload!",
                defeatQuote = "Clean sanitization, strict CSP, and parameterized queries foiled every probe!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 16
            ),
            nodes = listOf(
                LessonNodeItem(31, 31, "globe", 0.dp, "Stage 1: Trust Boundaries (HTTPS, Cookies & SOP)"),
                LessonNodeItem(32, 32, "curlybraces", (-45).dp, "Stage 2: Injection Alley (SQLi & Input Sanitization)"),
                LessonNodeItem(33, 33, "shield.slash", 45.dp, "Stage 3: Script Storm (XSS & CSRF Defenses)"),
                LessonNodeItem(34, 34, "hammer.fill", (-20).dp, "Stage 4: Secure Build (OWASP Top 10 & CI/CD)"),
                LessonNodeItem(35, 35, "flame", 0.dp, "Stage 5: Boss: Bug Bounty (Vulnerability Triage)", isBoss = true)
            )
        ),
        // Chapter 8: Wireless, Mail & Remote Access (Levels 36..40)
        ChapterDefinition(
            id = 8, number = 8,
            track = CourseTrack.EXPANSION,
            title = "Wireless, Mail & Remote",
            subtitle = "Secure Channels & Communication",
            worldTheme = "The Spectrum Citadel",
            worldEmoji = "📡",
            themeColor = Color(0xFF2563EB),     // Signal Blue
            themeDarkColor = Color(0xFF1D4ED8),
            accentColor = Color(0xFF60A5FA),
            description = "Safeguard edge communications: Wi-Fi encryption evolution, PGP vs S/MIME email signing, anti-phishing hygiene, and SSH host key validation.",
            bossSpec = DSABossSpec(
                id = "boss_unit_08",
                name = "The Road Warrior Guardian",
                title = "Infiltrator of Untrusted Networks",
                worldTheme = "The Spectrum Citadel",
                avatarEmoji = "📡👹",
                bossHp = 200,
                timeLimitSeconds = 60,
                quote = "An evil twin access point and lookalike domain are all I need to capture your credentials!",
                defeatQuote = "WPA3 SAE handshakes, DMARC validation, and SSH key verification held unbreached!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 17
            ),
            nodes = listOf(
                LessonNodeItem(36, 36, "wifi", 0.dp, "Stage 1: Wi-Fi Evolution (WEP to WPA3 SAE)"),
                LessonNodeItem(37, 37, "envelope.badge.shieldhalf.filled", 45.dp, "Stage 2: Secure Mail (PGP, S/MIME, DMARC)"),
                LessonNodeItem(38, 38, "exclamationmark.bubble", (-45).dp, "Stage 3: Phishing Lab (Deception & Red Flags)"),
                LessonNodeItem(39, 39, "terminal", 20.dp, "Stage 4: Remote Access (SSH & VPN Architectures)"),
                LessonNodeItem(40, 40, "flame", 0.dp, "Stage 5: Boss: Road Warrior (Public Wi-Fi Survival)", isBoss = true)
            )
        ),
        // Chapter 9: Defense Ops (Levels 41..45)
        ChapterDefinition(
            id = 9, number = 9,
            track = CourseTrack.EXPANSION,
            title = "Defense Ops",
            subtitle = "Network Defense Operations",
            worldTheme = "The Defense Operations Center",
            worldEmoji = "🛰️",
            themeColor = Color(0xFF059669),     // Sentinel Emerald
            themeDarkColor = Color(0xFF047857),
            accentColor = Color(0xFF34D399),
            description = "Operate like a modern SOC: Intrusion detection vs prevention, layered perimeters, malware taxonomy, and NIST incident response playbooks.",
            bossSpec = DSABossSpec(
                id = "boss_unit_09",
                name = "The SOC Commander",
                title = "Coordinator of Threat Containment",
                worldTheme = "The Defense Operations Center",
                avatarEmoji = "🛰️👹",
                bossHp = 220,
                timeLimitSeconds = 60,
                quote = "A targeted advanced persistent threat has landed inside your DMZ. Can you isolate it in time?",
                defeatQuote = "Rapid triage, signature-based isolation, and post-incident recovery achieved!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 17
            ),
            nodes = listOf(
                LessonNodeItem(41, 41, "eye", 0.dp, "Stage 1: Watchtowers (IDS vs IPS & Anomaly Detection)"),
                LessonNodeItem(42, 42, "square.stack.3d.down.right", (-45).dp, "Stage 2: Perimeter Layers (DMZ, WAF & Honeypots)"),
                LessonNodeItem(43, 43, "ant", 45.dp, "Stage 3: Malware Zoo (Viruses, Ransomware & Rootkits)"),
                LessonNodeItem(44, 44, "list.clipboard", (-20).dp, "Stage 4: Incident Response (NIST SP 800-61 Lifecycle)"),
                LessonNodeItem(45, 45, "flame", 0.dp, "Stage 5: Boss: SOC Night Shift (Live Alert Triage)", isBoss = true)
            )
        ),
        // Chapter 10: Crypto in the Wild (Levels 46..50)
        ChapterDefinition(
            id = 10, number = 10,
            track = CourseTrack.EXPANSION,
            title = "Crypto in the Wild",
            subtitle = "Applied Pitfalls & Post-Quantum",
            worldTheme = "The Cryptographic Wilds",
            worldEmoji = "🌌",
            themeColor = Color(0xFF9333EA),     // Quantum Violet
            themeDarkColor = Color(0xFF7E22CE),
            accentColor = Color(0xFFC084FC),
            description = "Analyze historic cryptography failures: Weak PRNGs, padding oracles, timing side-channels, Heartbleed, and the post-quantum transition.",
            bossSpec = DSABossSpec(
                id = "boss_unit_10",
                name = "The Crypto Auditor",
                title = "Examiner of Cryptographic Flaws",
                worldTheme = "The Cryptographic Wilds",
                avatarEmoji = "🌌👹",
                bossHp = 250,
                timeLimitSeconds = 60,
                quote = "One weak random seed or timing leakage in your implementation will expose the entire master key!",
                defeatQuote = "Constant-time logic, secure entropy, and post-quantum agility verified. You are an Elite Cryptographer!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 18
            ),
            nodes = listOf(
                LessonNodeItem(46, 46, "die.face.5", 0.dp, "Stage 1: Randomness Matters (CSPRNG vs Pseudo-Random)"),
                LessonNodeItem(47, 47, "timer", 45.dp, "Stage 2: Padding & Timing (Side-Channel Vulnerabilities)"),
                LessonNodeItem(48, 48, "doc.text.magnifyingglass", (-45).dp, "Stage 3: Case Files (Heartbleed, Debian RNG, Dual_EC)"),
                LessonNodeItem(49, 49, "sparkles", 20.dp, "Stage 4: Future-Proofing (Post-Quantum & FIPS 203/204)"),
                LessonNodeItem(50, 50, "flame", 0.dp, "Stage 5: Boss: Crypto Auditor (Final Security Sign-Off)", isBoss = true)
            )
        )
    )

    // ── REGISTRY ACCESSORS ────────────────────────────────────────

    fun getAllChapters(): List<ChapterDefinition> = listOf(chapter0Basecamp) + coreChapters + expansionChapters

    fun getCoreUnits(): List<UnitModel> = coreChapters.map { it.toUnitModel() }

    fun getExpansionUnits(): List<UnitModel> = expansionChapters.map { it.toUnitModel() }

    fun getBasecampUnit(): UnitModel = chapter0Basecamp.toUnitModel()

    fun getChapterById(chapterId: Int): ChapterDefinition? = when (chapterId) {
        0 -> chapter0Basecamp
        in 1..5 -> coreChapters.firstOrNull { it.id == chapterId }
        in 6..10 -> expansionChapters.firstOrNull { it.id == chapterId }
        else -> null
    }

    fun getChapterForLevel(levelNumber: Int): ChapterDefinition? = when {
        isBasecampLevel(levelNumber) -> chapter0Basecamp
        isCoreLevel(levelNumber) -> {
            val chapterId = ((levelNumber - 1) / 5) + 1
            coreChapters.firstOrNull { it.id == chapterId }
        }
        isExpansionLevel(levelNumber) -> {
            val chapterId = ((levelNumber - 26) / 5) + 6
            expansionChapters.firstOrNull { it.id == chapterId }
        }
        else -> null
    }

    fun isCoreLevel(levelNumber: Int): Boolean = levelNumber in 1..25

    fun isExpansionLevel(levelNumber: Int): Boolean = levelNumber in 26..50

    fun isBasecampLevel(levelNumber: Int): Boolean = levelNumber in 101..105

    fun getTrackForLevel(levelNumber: Int): CourseTrack = when {
        isBasecampLevel(levelNumber) -> CourseTrack.FOUNDATION
        isExpansionLevel(levelNumber) -> CourseTrack.EXPANSION
        else -> CourseTrack.CORE
    }
}
