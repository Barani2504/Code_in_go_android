package com.simats.codeingo.ui.arenas.integrity_foundry

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.simats.codeingo.ui.arenas.common.ArenaPalettes
import com.simats.codeingo.ui.arenas.common.ArenaParticleBurst
import com.simats.codeingo.ui.arenas.common.ArenaShockwaveEffect
import com.simats.codeingo.ui.arenas.common.ArenaTacticalTool
import com.simats.codeingo.ui.arenas.common.arenaShake
import com.simats.codeingo.ui.arenas.common.rememberArenaShakeController
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.BirthdayRadarLevel
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.BitFlipLabLevel
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.CollisionBeastBossLevel
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.DigestPressLevel
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.SignatureSealLevel
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.SpongeChamberLevel
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.WaxSealLevel
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntegrityFoundryArenaScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val dynamicColors = LocalDynamicThemeColors.current
    val reduceMotion = LocalReduceMotion.current

    val primary = ArenaPalettes.Arena4Primary
    val accent = ArenaPalettes.Arena4Accent

    // Screen Shake & Motion State
    val shakeController = rememberArenaShakeController()
    var shockwaveTrigger by remember { mutableStateOf<Any?>(null) }
    var particleTrigger by remember { mutableStateOf<Any?>(null) }

    // Hearts economy strictly observes GameManager (Max 10)
    val heartsCount by gameManager.heartsCount.collectAsState()
    val gemsCount by gameManager.gemsCount.collectAsState()

    var currentLevel by remember { mutableStateOf(IntegrityFoundryLevel.LEVEL_1) }
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
            shakeController.shake()
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

    fun handleLevelCompleted(lvl: IntegrityFoundryLevel) {
        val nextId = lvl.id + 1
        unlockedLevels = unlockedLevels + nextId
        val earnedStars = if (comboCount >= 3) 3 else 2
        levelStars[lvl.id] = earnedStars
        sessionXP += lvl.xpReward
        gameManager.addXP(lvl.xpReward)
        gameManager.addGems(lvl.coinReward)

        particleTrigger = System.currentTimeMillis()
        shockwaveTrigger = System.currentTimeMillis()

        debriefIsVictory = true
        showDebrief = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        com.simats.codeingo.ui.arenas.common.ArenaEnvironmentBackground(accentColor = primary)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .arenaShake(shakeController)
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
                                text = "INTEGRITY FOUNDRY",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = dynamicColors.textPrimary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currentLevel.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primary
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
                val foundryStations = remember {
                    IntegrityFoundryLevel.values().map { lvl ->
                        com.simats.codeingo.ui.arenas.common.ArenaStationNode(
                            id = lvl.id,
                            title = lvl.title,
                            shortName = when (lvl.id) {
                                1 -> "Digest"
                                2 -> "Avalanche"
                                3 -> "Sponge"
                                4 -> "HMAC"
                                5 -> "Signature"
                                6 -> "Birthday"
                                else -> "Collision"
                            },
                            iconEmoji = lvl.iconEmoji,
                            isBoss = lvl == IntegrityFoundryLevel.BOSS_BATTLE
                        )
                    }
                }

                com.simats.codeingo.ui.arenas.common.ArenaLevelTrack(
                    stations = foundryStations,
                    currentLevelId = currentLevel.id,
                    unlockedLevels = unlockedLevels,
                    levelStars = levelStars,
                    accentColor = primary,
                    onSelectLevel = { id ->
                        currentLevel = IntegrityFoundryLevel.fromId(id)
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
                        IntegrityFoundryLevel.LEVEL_1 -> DigestPressLevel(
                            onComplete = { handleLevelCompleted(IntegrityFoundryLevel.LEVEL_1) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(120) }
                        )
                        IntegrityFoundryLevel.LEVEL_2 -> BitFlipLabLevel(
                            onComplete = { handleLevelCompleted(IntegrityFoundryLevel.LEVEL_2) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(150) }
                        )
                        IntegrityFoundryLevel.LEVEL_3 -> SpongeChamberLevel(
                            onComplete = { handleLevelCompleted(IntegrityFoundryLevel.LEVEL_3) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(180) }
                        )
                        IntegrityFoundryLevel.LEVEL_4 -> WaxSealLevel(
                            onComplete = { handleLevelCompleted(IntegrityFoundryLevel.LEVEL_4) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(150) }
                        )
                        IntegrityFoundryLevel.LEVEL_5 -> SignatureSealLevel(
                            onComplete = { handleLevelCompleted(IntegrityFoundryLevel.LEVEL_5) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(200) }
                        )
                        IntegrityFoundryLevel.LEVEL_6 -> BirthdayRadarLevel(
                            onComplete = { handleLevelCompleted(IntegrityFoundryLevel.LEVEL_6) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(220) }
                        )
                        IntegrityFoundryLevel.BOSS_BATTLE -> CollisionBeastBossLevel(
                            onDefeatBoss = { handleLevelCompleted(IntegrityFoundryLevel.BOSS_BATTLE) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(300) },
                            onTriggerShake = { shakeController.shake() },
                            onTriggerShockwave = { shockwaveTrigger = System.currentTimeMillis() }
                        )
                    }
                }

            } // Close inner weighted column

            com.simats.codeingo.ui.arenas.common.ArenaBottomBar(
                onOpenCodex = { showTheoryCodex = true },
                onResetLevel = {
                    val lvl = currentLevel
                    currentLevel = IntegrityFoundryLevel.LEVEL_1
                    currentLevel = lvl
                },
                onSkipLevel = {
                    val nextId = minOf(7, currentLevel.id + 1)
                    unlockedLevels = unlockedLevels + nextId
                    currentLevel = IntegrityFoundryLevel.fromId(nextId)
                }
            )
        }

        // Motion FX Overlays
        ArenaShockwaveEffect(trigger = shockwaveTrigger, color = primary)
        ArenaParticleBurst(
            trigger = particleTrigger,
            colors = listOf(primary, accent, Color.White)
        )

        // Cinematic Briefing Overlay
        if (showBriefing) {
            val objective = when (currentLevel) {
                IntegrityFoundryLevel.LEVEL_1 -> "Assemble 512-bit message blocks with 0x80 delimiter, zero padding, and 64-bit length trailer before crushing."
                IntegrityFoundryLevel.LEVEL_2 -> "Validate the Strict Avalanche Criterion (SAC): a 1-bit input flip must invert ~50% of the 256 output bits."
                IntegrityFoundryLevel.LEVEL_3 -> "Master the SHA-3 Keccak-f[1600] permutation sponge across Rate (r=1088b) and Capacity (c=512b) lanes."
                IntegrityFoundryLevel.LEVEL_4 -> "Synthesize the RFC 2104 HMAC wax seal with inner pad (0x36) and outer pad (0x5C) to defeat length extension."
                IntegrityFoundryLevel.LEVEL_5 -> "Execute asymmetric digital signatures: sign digest h with private key d, verify with public key e."
                IntegrityFoundryLevel.LEVEL_6 -> "Analyze the Birthday Paradox square-root collision barrier: finding collisions takes O(2^(n/2)) work."
                IntegrityFoundryLevel.BOSS_BATTLE -> "Defend against the Collision Beast! Preserve Merkle tree roots, collision resistance, and HMAC envelopes!"
            }
            val mechanic = when (currentLevel) {
                IntegrityFoundryLevel.LEVEL_1 -> "Assemble the delimiter and length trailer correctly."
                IntegrityFoundryLevel.LEVEL_2 -> "Flip input bits and observe the live Hamming distance meter."
                IntegrityFoundryLevel.LEVEL_3 -> "Absorb message chunks into rate lanes and squeeze the digest."
                IntegrityFoundryLevel.LEVEL_4 -> "Execute the 4-stage RFC 2104 inner and outer pad sequence."
                IntegrityFoundryLevel.LEVEL_5 -> "Sign the message digest with private key d and verify with e."
                IntegrityFoundryLevel.LEVEL_6 -> "Select appropriate digest bit sizes to maintain 128-bit collision resistance."
                IntegrityFoundryLevel.BOSS_BATTLE -> "Neutralize 3 collision and tamper phases to vanquish the Beast."
            }

            ArenaBriefingView(
                arenaTitle = "Arena 4 • Integrity Foundry",
                levelName = currentLevel.title,
                cryptographicObjective = objective,
                mechanicsHint = mechanic,
                isBoss = currentLevel == IntegrityFoundryLevel.BOSS_BATTLE,
                onStartLevel = { showBriefing = false }
            )
        }

        // Post-Game Debrief View
        if (showDebrief) {
            val concepts = when (currentLevel) {
                IntegrityFoundryLevel.LEVEL_1 -> listOf("Merkle-Damgård Structure", "512-bit Block Padding", "Davies-Meyer Compression")
                IntegrityFoundryLevel.LEVEL_2 -> listOf("Strict Avalanche Criterion (SAC)", "Hamming Bit Distance", "Diffusion")
                IntegrityFoundryLevel.LEVEL_3 -> listOf("Keccak Sponge Construction", "Rate vs Capacity", "State Cube Permutation")
                IntegrityFoundryLevel.LEVEL_4 -> listOf("RFC 2104 HMAC", "Inner & Outer Pads (0x36 / 0x5C)", "Length Extension Immunity")
                IntegrityFoundryLevel.LEVEL_5 -> listOf("Hash-Then-Sign Paradigm", "Asymmetric Signatures", "Non-Repudiation")
                IntegrityFoundryLevel.LEVEL_6 -> listOf("Birthday Paradox", "O(2^(n/2)) Collision Complexity", "Preimage vs Collision")
                IntegrityFoundryLevel.BOSS_BATTLE -> listOf("Merkle Tree Cascading Integrity", "Collision Defense", "Keyed Hash Envelopes")
            }
            val pitfalls = if (!debriefIsVictory) listOf(
                "Plain H(Key ∥ Message) is vulnerable to Merkle-Damgård length extension; always use HMAC.",
                "Birthday collisions require only O(2^(n/2)) hash operations; 128-bit hashes offer only 64 bits of collision resistance.",
                "Digital signatures must always be generated with the private key and verified with the public key."
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
                        currentLevel = IntegrityFoundryLevel.fromId(nextId)
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
                            text = "THEORY CODEX • CRYPTOGRAPHIC INTEGRITY",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = primary
                        )
                        IconButton(onClick = { showTheoryCodex = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = dynamicColors.textSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Cryptographic Hashes & Integrity Primitives\n\n" +
                                "1. Merkle-Damgård Construction: Iterative hash design (MD5, SHA-1, SHA-2). Pads inputs to multiples of 512 bits with a trailing 64-bit length integer. Vulnerable to length extension when keyed naively.\n\n" +
                                "2. Keccak Sponge Construction (SHA-3): State width b = r + c (1600 bits). Message bits are XORed into rate r; capacity c provides security margin. Squeezes output exclusively from rate lanes, completely immune to length extension.\n\n" +
                                "3. HMAC (RFC 2104): Keyed message authentication code using nested hashing: H((K ⊕ opad) ∥ H((K ⊕ ipad) ∥ M)). Defeats length extension attacks and provides proof of authenticity and integrity.\n\n" +
                                "4. Digital Signatures: Hash-then-sign paradigm. Signer creates signature s = hᵈ mod n using private key d; verifier computes sᵉ mod n using public key e. Provides authenticity and non-repudiation.\n\n" +
                                "5. Birthday Paradox: Collisions H(m₁) = H(m₂) require only O(2^(n/2)) trials with 50% probability. A 256-bit hash provides 128 bits of collision resistance.",
                        fontSize = 13.sp,
                        color = dynamicColors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
