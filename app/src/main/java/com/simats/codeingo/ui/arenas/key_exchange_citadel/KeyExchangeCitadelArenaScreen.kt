package com.simats.codeingo.ui.arenas.key_exchange_citadel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.arenas.common.ArenaBriefingView
import com.simats.codeingo.ui.arenas.common.ArenaComboHUD
import com.simats.codeingo.ui.arenas.common.ArenaDebriefView
import com.simats.codeingo.ui.arenas.common.ArenaGameSpeed
import com.simats.codeingo.ui.arenas.common.ArenaLoadoutSheet
import com.simats.codeingo.ui.arenas.common.ArenaTacticalTool
import com.simats.codeingo.ui.arenas.key_exchange_citadel.levels.ClockArithmeticLevel
import com.simats.codeingo.ui.arenas.key_exchange_citadel.levels.CurveWalkerLevel
import com.simats.codeingo.ui.arenas.key_exchange_citadel.levels.KeyPairForgeLevel
import com.simats.codeingo.ui.arenas.key_exchange_citadel.levels.KeySizeGauntletLevel
import com.simats.codeingo.ui.arenas.key_exchange_citadel.levels.PaintMixHandshakeLevel
import com.simats.codeingo.ui.arenas.key_exchange_citadel.levels.QuantumCryptanalystBossLevel
import com.simats.codeingo.ui.arenas.key_exchange_citadel.levels.TrapdoorLabLevel
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyExchangeCitadelArenaScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val dynamicColors = LocalDynamicThemeColors.current
    val reduceMotion = LocalReduceMotion.current

    // Hearts economy strictly observes GameManager (Max 10)
    val heartsCount by gameManager.heartsCount.collectAsState()
    val gemsCount by gameManager.gemsCount.collectAsState()

    var currentLevel by remember { mutableStateOf(KeyExchangeCitadelLevel.LEVEL_1) }
    var unlockedLevels by remember { mutableStateOf(setOf(1, 2, 3, 4, 5, 6, 7)) }
    var levelStars by remember { mutableStateOf(mutableMapOf<Int, Int>()) }

    var sessionXP by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var comboCount by remember { mutableIntStateOf(0) }
    var speed by remember { mutableStateOf(ArenaGameSpeed.NORMAL) }

    // Tactical Loadout & Tool State
    var equippedTool by remember { mutableStateOf<ArenaTacticalTool?>(ArenaTacticalTool.HINT_LENS) }
    var toolUsedInLevel by remember { mutableStateOf(false) }
    var showLoadoutSheet by remember { mutableStateOf(false) }

    // Cinematic & Feedback Dialogs
    var showBriefing by remember { mutableStateOf(false) }
    var showDebrief by remember { mutableStateOf(false) }
    var debriefIsVictory by remember { mutableStateOf(false) }
    var showTheoryCodex by remember { mutableStateOf(false) }

    // Reset tool status on level change
    LaunchedEffect(currentLevel) {
        toolUsedInLevel = false
    }

    fun onDeductHeart() {
        if (equippedTool == ArenaTacticalTool.SECOND_CHANCE && !toolUsedInLevel) {
            toolUsedInLevel = true
        } else {
            gameManager.loseHeart()
            comboCount = 0
            if (heartsCount <= 1) {
                debriefIsVictory = false
                showDebrief = true
            }
        }
    }

    fun onActionSuccess(points: Int = 100) {
        comboCount++
        val multiplier = when {
            comboCount >= 8 -> 3.0f
            comboCount >= 5 -> 2.0f
            comboCount >= 3 -> 1.5f
            else -> 1.0f
        }
        score += (points * multiplier).toInt()
    }

    fun handleLevelCompleted(lvl: KeyExchangeCitadelLevel) {
        val nextId = lvl.id + 1
        unlockedLevels = unlockedLevels + nextId
        val earnedStars = if (comboCount >= 3) 3 else 2
        levelStars[lvl.id] = earnedStars
        sessionXP += lvl.xpReward
        gameManager.addXP(lvl.xpReward)
        gameManager.addGems(lvl.coinReward)

        debriefIsVictory = true
        showDebrief = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        com.simats.codeingo.ui.arenas.common.ArenaEnvironmentBackground(accentColor = Color(0xFF6C5CE7))

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
                // Top HUD Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(dynamicColors.cardBorder.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit",
                            tint = dynamicColors.textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "KEY EXCHANGE CITADEL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = dynamicColors.textPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = currentLevel.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGold
                        )
                    }
                }

                // Economy Badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Tactical Tool Icon
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                equippedTool?.accentColor?.copy(alpha = 0.15f)
                                    ?: dynamicColors.cardBorder.copy(alpha = 0.2f)
                            )
                            .border(
                                1.dp,
                                equippedTool?.accentColor ?: dynamicColors.cardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { showLoadoutSheet = true }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = equippedTool?.icon ?: Icons.Default.MilitaryTech,
                                contentDescription = "Tool",
                                tint = equippedTool?.accentColor ?: dynamicColors.textSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (toolUsedInLevel) "USED" else "READY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (toolUsedInLevel) dynamicColors.textSecondary else (equippedTool?.accentColor ?: dynamicColors.textPrimary)
                            )
                        }
                    }

                    // Hearts HUD
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DuolingoRed.copy(alpha = 0.12f))
                            .padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Hearts",
                            tint = DuolingoRed,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$heartsCount",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = DuolingoRed
                        )
                    }

                    // Gems HUD
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00C3FF).copy(alpha = 0.12f))
                            .padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "Gems",
                            tint = Color(0xFF00C3FF),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$gemsCount",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00C3FF)
                        )
                    }

                    // Codex Button
                    IconButton(
                        onClick = { showTheoryCodex = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Theory",
                            tint = dynamicColors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Enhanced Cyber Level Selector Ribbon
            val citadelStations = remember {
                KeyExchangeCitadelLevel.values().map { lvl ->
                    com.simats.codeingo.ui.arenas.common.ArenaStationNode(
                        id = lvl.id,
                        title = lvl.title,
                        shortName = when (lvl.id) {
                            1 -> "Clock"
                            2 -> "PaintMix"
                            3 -> "Trapdoor"
                            4 -> "KeyPair"
                            5 -> "ECC Curve"
                            6 -> "Gauntlet"
                            else -> "Quantum"
                        },
                        iconEmoji = lvl.iconEmoji,
                        isBoss = lvl == KeyExchangeCitadelLevel.BOSS_BATTLE
                    )
                }
            }

            com.simats.codeingo.ui.arenas.common.ArenaLevelTrack(
                stations = citadelStations,
                currentLevelId = currentLevel.id,
                unlockedLevels = unlockedLevels,
                levelStars = levelStars,
                accentColor = Color(0xFF6C5CE7),
                onSelectLevel = { id ->
                    currentLevel = KeyExchangeCitadelLevel.fromId(id)
                }
            )

            // Combo & Tactical HUD
            ArenaComboHUD(
                comboCount = comboCount,
                score = score,
                equippedTool = equippedTool,
                toolUsed = toolUsedInLevel,
                onActivateTool = {
                    toolUsedInLevel = true
                },
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Main Level Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(dynamicColors.cardBackground)
                    .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                when (currentLevel) {
                        KeyExchangeCitadelLevel.LEVEL_1 -> ClockArithmeticLevel(
                            onComplete = { handleLevelCompleted(KeyExchangeCitadelLevel.LEVEL_1) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(120) }
                        )
                        KeyExchangeCitadelLevel.LEVEL_2 -> PaintMixHandshakeLevel(
                            onComplete = { handleLevelCompleted(KeyExchangeCitadelLevel.LEVEL_2) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(150) }
                        )
                        KeyExchangeCitadelLevel.LEVEL_3 -> TrapdoorLabLevel(
                            onComplete = { handleLevelCompleted(KeyExchangeCitadelLevel.LEVEL_3) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(180) }
                        )
                        KeyExchangeCitadelLevel.LEVEL_4 -> KeyPairForgeLevel(
                            onComplete = { handleLevelCompleted(KeyExchangeCitadelLevel.LEVEL_4) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(150) }
                        )
                        KeyExchangeCitadelLevel.LEVEL_5 -> CurveWalkerLevel(
                            onComplete = { handleLevelCompleted(KeyExchangeCitadelLevel.LEVEL_5) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(200) }
                        )
                        KeyExchangeCitadelLevel.LEVEL_6 -> KeySizeGauntletLevel(
                            onComplete = { handleLevelCompleted(KeyExchangeCitadelLevel.LEVEL_6) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(220) }
                        )
                        KeyExchangeCitadelLevel.BOSS_BATTLE -> QuantumCryptanalystBossLevel(
                            onDefeatBoss = { handleLevelCompleted(KeyExchangeCitadelLevel.BOSS_BATTLE) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(300) }
                        )
                    }
            }

            } // Close inner weighted column

            com.simats.codeingo.ui.arenas.common.ArenaBottomBar(
                onOpenCodex = { showTheoryCodex = true },
                onResetLevel = {
                    val lvl = currentLevel
                    currentLevel = KeyExchangeCitadelLevel.LEVEL_1
                    currentLevel = lvl
                },
                onSkipLevel = {
                    val nextId = minOf(7, currentLevel.id + 1)
                    unlockedLevels = unlockedLevels + nextId
                    currentLevel = KeyExchangeCitadelLevel.fromId(nextId)
                }
            )
        }

        // Cinematic Briefing Overlay
        if (showBriefing) {
            val objective = when (currentLevel) {
                KeyExchangeCitadelLevel.LEVEL_1 -> "Master modular exponentiation in finite cyclic rings to wrap powers and prevent easy reversal."
                KeyExchangeCitadelLevel.LEVEL_2 -> "Simulate the Diffie-Hellman color-mix handshake. Negotiate shared secret g^(ab) mod p over public wires."
                KeyExchangeCitadelLevel.LEVEL_3 -> "Unlock RSA's one-way trapdoor function by computing Euler's totient phi(n) = (p-1)(q-1) from secret primes."
                KeyExchangeCitadelLevel.LEVEL_4 -> "Synthesize public and private key components. Ensure private exponent d is kept strictly secret."
                KeyExchangeCitadelLevel.LEVEL_5 -> "Execute digital signatures over elliptic curve points and verify non-repudiation and authenticity."
                KeyExchangeCitadelLevel.LEVEL_6 -> "Sprint through binary square-and-multiply operations to perform fast modular exponentiation."
                KeyExchangeCitadelLevel.BOSS_BATTLE -> "Defend against Shor's algorithm! Deploy lattice-based post-quantum cryptography (ML-KEM) to vanquish the Quantum Cryptanalyst!"
            }
            val mechanic = when (currentLevel) {
                KeyExchangeCitadelLevel.LEVEL_1 -> "Select the correct modular residue for exponentiation."
                KeyExchangeCitadelLevel.LEVEL_2 -> "Calculate the matching shared secret K from Alice and Bob's exchanged public shares."
                KeyExchangeCitadelLevel.LEVEL_3 -> "Compute the totient phi(n) for the secret prime factors."
                KeyExchangeCitadelLevel.LEVEL_4 -> "Identify which key parameters must remain strictly confidential."
                KeyExchangeCitadelLevel.LEVEL_5 -> "Generate and seal the digital signature with private key d."
                KeyExchangeCitadelLevel.LEVEL_6 -> "Execute square and multiply rounds to rapidly calculate the high-power modular exponent."
                KeyExchangeCitadelLevel.BOSS_BATTLE -> "Counter Shor's factoring and discrete-log attacks across 3 quantum battle phases."
            }

            ArenaBriefingView(
                arenaTitle = "Arena 3 • Key Exchange Citadel",
                levelName = currentLevel.title,
                cryptographicObjective = objective,
                mechanicsHint = mechanic,
                isBoss = currentLevel == KeyExchangeCitadelLevel.BOSS_BATTLE,
                onStartLevel = { showBriefing = false }
            )
        }

        // Post-Game Debrief View
        if (showDebrief) {
            val concepts = when (currentLevel) {
                KeyExchangeCitadelLevel.LEVEL_1 -> listOf("Modular Rings ℤ/nℤ", "Modular Exponentiation", "Coprimality")
                KeyExchangeCitadelLevel.LEVEL_2 -> listOf("Diffie-Hellman Key Exchange", "Discrete Logarithm Problem", "Shared Secret Negotiation")
                KeyExchangeCitadelLevel.LEVEL_3 -> listOf("Euler's Totient φ(n)", "Trapdoor One-Way Permutations", "Semiprime Factorization")
                KeyExchangeCitadelLevel.LEVEL_4 -> listOf("RSA Key Distribution", "Public (e, n) vs Private d", "Confidentiality Boundaries")
                KeyExchangeCitadelLevel.LEVEL_5 -> listOf("Elliptic Curve Cryptography", "Point Addition & Multiplication", "Digital Signatures (ECDSA)")
                KeyExchangeCitadelLevel.LEVEL_6 -> listOf("Square-and-Multiply Algorithm", "O(log e) Exponentiation Speed", "Key-Length Equivalence")
                KeyExchangeCitadelLevel.BOSS_BATTLE -> listOf("Shor's Quantum Factorization", "Quantum Discrete Log Vulnerability", "Lattice-Based Post-Quantum (ML-KEM)")
            }
            val pitfalls = if (!debriefIsVictory) listOf(
                "Never disclose private key exponent d or prime factors p and q.",
                "Classical asymmetric algorithms (RSA, DH, ECC) are vulnerable to polynomial-time quantum attacks via Shor's algorithm.",
                "Always verify public key shares to prevent Man-in-the-Middle attacks."
            ) else emptyList()

            ArenaDebriefView(
                isVictory = debriefIsVictory,
                levelName = currentLevel.title,
                starsEarned = levelStars[currentLevel.id] ?: 2,
                xpEarned = if (debriefIsVictory) currentLevel.xpReward else 0,
                gemsEarned = if (debriefIsVictory) currentLevel.coinReward else 0,
                conceptsMastered = if (debriefIsVictory) concepts else emptyList(),
                missedConcepts = pitfalls,
                onContinue = {
                    showDebrief = false
                    val nextId = currentLevel.id + 1
                    if (nextId <= 7) {
                        currentLevel = KeyExchangeCitadelLevel.fromId(nextId)
                    } else {
                        onDismiss()
                    }
                },
                onRetry = {
                    showDebrief = false
                },
                onOpenVisualizer = {
                    showDebrief = false
                    onDismiss()
                }
            )
        }

        // Tactical Loadout Modal
        if (showLoadoutSheet) {
            ArenaLoadoutSheet(
                selectedTool = equippedTool,
                onSelectTool = { tool ->
                    equippedTool = tool
                },
                onDismiss = { showLoadoutSheet = false }
            )
        }

        // Theory Codex Sheet
        if (showTheoryCodex) {
            ModalBottomSheet(
                onDismissRequest = { showTheoryCodex = false },
                containerColor = dynamicColors.cardBackground,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .navigationBarsPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "THEORY CODEX • ASYMMETRIC CRYPTOGRAPHY",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                        IconButton(onClick = { showTheoryCodex = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = dynamicColors.textSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Asymmetric Cryptography & Key Exchange\n\n" +
                                "Unlike symmetric ciphers which share a single secret key, asymmetric cryptography employs pairs of keys: a public key for encryption/verification and a private key for decryption/signing.\n\n" +
                                "1. Diffie-Hellman: Allows two parties over an unencrypted channel to arrive at a shared secret key without an adversary discovering it, based on the hardness of the Discrete Logarithm Problem.\n\n" +
                                "2. RSA: Leverages the integer factorization problem of semiprimes n = p * q. The public exponent e and private exponent d satisfy e * d ≡ 1 (mod φ(n)).\n\n" +
                                "3. Elliptic Curves (ECC): Provides equivalent security with significantly smaller key sizes (e.g., 256-bit ECC ≈ 3072-bit RSA) by operating over algebraic structures of elliptic curves over finite fields.\n\n" +
                                "4. Post-Quantum Cryptography: Shor's algorithm will break RSA and ECC on sufficiently large quantum computers. NIST has standardized lattice-based alternatives (ML-KEM, ML-DSA) that resist quantum cryptanalysis.",
                        fontSize = 13.sp,
                        color = dynamicColors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
