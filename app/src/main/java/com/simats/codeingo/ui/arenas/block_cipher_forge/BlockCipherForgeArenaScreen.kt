package com.simats.codeingo.ui.arenas.block_cipher_forge

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
import com.simats.codeingo.ui.arenas.block_cipher_forge.levels.AddRoundKeyLockLevel
import com.simats.codeingo.ui.arenas.block_cipher_forge.levels.AvalancheSprintLevel
import com.simats.codeingo.ui.arenas.block_cipher_forge.levels.BlockOverlordBossLevel
import com.simats.codeingo.ui.arenas.block_cipher_forge.levels.MixColumnsMixerLevel
import com.simats.codeingo.ui.arenas.block_cipher_forge.levels.ModeDetectiveLevel
import com.simats.codeingo.ui.arenas.block_cipher_forge.levels.SBoxForgeLevel
import com.simats.codeingo.ui.arenas.block_cipher_forge.levels.ShiftRowsSliderLevel
import com.simats.codeingo.ui.arenas.common.ArenaBottomBar
import com.simats.codeingo.ui.arenas.common.ArenaBriefingView
import com.simats.codeingo.ui.arenas.common.ArenaComboHUD
import com.simats.codeingo.ui.arenas.common.ArenaDebriefView
import com.simats.codeingo.ui.arenas.common.ArenaEnvironmentBackground
import com.simats.codeingo.ui.arenas.common.ArenaGameSpeed
import com.simats.codeingo.ui.arenas.common.ArenaLevelTrack
import com.simats.codeingo.ui.arenas.common.ArenaLoadoutSheet
import com.simats.codeingo.ui.arenas.common.ArenaPalettes
import com.simats.codeingo.ui.arenas.common.ArenaParticleBurst
import com.simats.codeingo.ui.arenas.common.ArenaShockwaveEffect
import com.simats.codeingo.ui.arenas.common.ArenaStationNode
import com.simats.codeingo.ui.arenas.common.ArenaTacticalTool
import com.simats.codeingo.ui.arenas.common.arenaShake
import com.simats.codeingo.ui.arenas.common.rememberArenaShakeController
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockCipherForgeArenaScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val dynamicColors = LocalDynamicThemeColors.current
    val reduceMotion = LocalReduceMotion.current
    val isDark = dynamicColors.isDark

    val primary = ArenaPalettes.Arena2Primary
    val accent = ArenaPalettes.Arena2Accent

    // Hearts economy strictly observes GameManager (Max 10)
    val heartsCount by gameManager.heartsCount.collectAsState()
    val gemsCount by gameManager.gemsCount.collectAsState()

    var currentLevel by remember { mutableStateOf(BlockCipherForgeLevel.LEVEL_1) }
    var unlockedLevels by remember { mutableStateOf(setOf(1, 2, 3, 4, 5, 6, 7)) }
    var levelStars by remember { mutableStateOf(mutableMapOf<Int, Int>()) }

    var sessionXP by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var comboCount by remember { mutableIntStateOf(0) }
    var speed by remember { mutableStateOf(ArenaGameSpeed.NORMAL) }

    // Screen Shake & Motion FX
    val shakeController = rememberArenaShakeController()
    var shockwaveTrigger by remember { mutableStateOf(0L) }
    var particleTrigger by remember { mutableStateOf(0L) }

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

    // Deduction with Second Chance support and screen shake
    fun onDeductHeart() {
        if (equippedTool == ArenaTacticalTool.SECOND_CHANCE && !toolUsedInLevel) {
            toolUsedInLevel = true
            // Shield absorbed mistake
        } else {
            gameManager.loseHeart()
            comboCount = 0
            shakeController.shake()
            if (heartsCount <= 1) { // Will reach 0
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

    fun handleLevelCompleted(lvl: BlockCipherForgeLevel) {
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
        ArenaEnvironmentBackground(accentColor = primary)

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
                // Top HUD Bar: Back, Arena Title, Hearts, Gems, Speed, Loadout
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
                                text = "BLOCK CIPHER FORGE",
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

                    // Economy Badges: Hearts, Gems, Tool
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

                        // Hearts HUD (Observes GameManager max 10)
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

                        // Theory Codex Button
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
                val forgeStations = remember {
                    BlockCipherForgeLevel.values().map { lvl ->
                        ArenaStationNode(
                            id = lvl.id,
                            title = lvl.title,
                            shortName = when (lvl.id) {
                                1 -> "S-Box"
                                2 -> "ShiftRows"
                                3 -> "MixCols"
                                4 -> "RoundKey"
                                5 -> "Modes"
                                6 -> "Avalanche"
                                else -> "Overlord"
                            },
                            iconEmoji = lvl.iconEmoji,
                            isBoss = lvl == BlockCipherForgeLevel.BOSS_BATTLE
                        )
                    }
                }

                ArenaLevelTrack(
                    stations = forgeStations,
                    currentLevelId = currentLevel.id,
                    unlockedLevels = unlockedLevels,
                    levelStars = levelStars,
                    accentColor = primary,
                    onSelectLevel = { id ->
                        currentLevel = BlockCipherForgeLevel.fromId(id)
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

                // Main Mini-Game Canvas Container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) ArenaPalettes.Arena2BackgroundDark else dynamicColors.cardBackground)
                        .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when (currentLevel) {
                        BlockCipherForgeLevel.LEVEL_1 -> SBoxForgeLevel(
                            toolActive = equippedTool == ArenaTacticalTool.HINT_LENS && !toolUsedInLevel,
                            onComplete = { handleLevelCompleted(BlockCipherForgeLevel.LEVEL_1) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(120) }
                        )
                        BlockCipherForgeLevel.LEVEL_2 -> ShiftRowsSliderLevel(
                            onComplete = { handleLevelCompleted(BlockCipherForgeLevel.LEVEL_2) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(150) }
                        )
                        BlockCipherForgeLevel.LEVEL_3 -> MixColumnsMixerLevel(
                            onComplete = { handleLevelCompleted(BlockCipherForgeLevel.LEVEL_3) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(180) }
                        )
                        BlockCipherForgeLevel.LEVEL_4 -> AddRoundKeyLockLevel(
                            onComplete = { handleLevelCompleted(BlockCipherForgeLevel.LEVEL_4) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(150) }
                        )
                        BlockCipherForgeLevel.LEVEL_5 -> ModeDetectiveLevel(
                            onComplete = { handleLevelCompleted(BlockCipherForgeLevel.LEVEL_5) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(200) }
                        )
                        BlockCipherForgeLevel.LEVEL_6 -> AvalancheSprintLevel(
                            onComplete = { handleLevelCompleted(BlockCipherForgeLevel.LEVEL_6) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(220) }
                        )
                        BlockCipherForgeLevel.BOSS_BATTLE -> BlockOverlordBossLevel(
                            timeFreezeActive = equippedTool == ArenaTacticalTool.TIME_FREEZE && toolUsedInLevel,
                            onTriggerShake = { shakeController.shake() },
                            onTriggerShockwave = { shockwaveTrigger = System.currentTimeMillis() },
                            onComplete = { handleLevelCompleted(BlockCipherForgeLevel.BOSS_BATTLE) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(300) }
                        )
                    }
                }

            } // Close inner weighted column

            ArenaBottomBar(
                onOpenCodex = { showTheoryCodex = true },
                onResetLevel = {
                    val lvl = currentLevel
                    currentLevel = BlockCipherForgeLevel.LEVEL_1
                    currentLevel = lvl
                },
                onSkipLevel = {
                    val nextId = minOf(7, currentLevel.id + 1)
                    unlockedLevels = unlockedLevels + nextId
                    currentLevel = BlockCipherForgeLevel.fromId(nextId)
                }
            )
        }

        // Particle Burst and Shockwave overlays
        ArenaParticleBurst(trigger = particleTrigger, colors = listOf(primary, accent, Color.White))
        ArenaShockwaveEffect(trigger = shockwaveTrigger, color = primary)

        // Cinematic Briefing Overlay
        if (showBriefing) {
            val objective = when (currentLevel) {
                BlockCipherForgeLevel.LEVEL_1 -> "Substitute input bytes through the GF(2^8) multiplicative inverse and affine transformation to destroy linear algebraic relations."
                BlockCipherForgeLevel.LEVEL_2 -> "Permute the 4x4 state matrix by cyclically shifting rows 0, 1, 2, and 3 left by their respective row offsets."
                BlockCipherForgeLevel.LEVEL_3 -> "Mix columns via matrix multiplication over Galois Field GF(2^8) with reduction polynomial 0x11B to attain maximum distance separability."
                BlockCipherForgeLevel.LEVEL_4 -> "Perform bitwise XOR between the cipher state and round key words. Verify the self-inverting property (S ⊕ K ⊕ K = S)."
                BlockCipherForgeLevel.LEVEL_5 -> "Examine ciphertext patterns across ECB, CBC, and CTR modes. Identify the ECB structural leak, CBC malleability, and CTR keystream reuse."
                BlockCipherForgeLevel.LEVEL_6 -> "Flip a single bit and observe complete diffusion. Confirm that about 50% of output bits flip on average under SAC."
                BlockCipherForgeLevel.BOSS_BATTLE -> "Infiltrate the Block Overlord's citadel! Reconstruct the AES round loop, disarm the Round 10 MixColumns trap, and contain the avalanche surge!"
            }
            val mechanic = when (currentLevel) {
                BlockCipherForgeLevel.LEVEL_1 -> "Adjust Row and Col hex dials to pinpoint the exact FIPS-197 S-Box coordinate, then forge the substituted byte."
                BlockCipherForgeLevel.LEVEL_2 -> "Tap shift arrows to cyclically rotate row bytes left until all 4 rows lock into alignment."
                BlockCipherForgeLevel.LEVEL_3 -> "Blend the Galois Field coefficients (2, 3, 1, 1) and engage the reactor to execute modular reduction."
                BlockCipherForgeLevel.LEVEL_4 -> "Toggle bit switches to calculate byte XOR outputs and unlock the master cryptographic vault."
                BlockCipherForgeLevel.LEVEL_5 -> "Flag ECB bitmap leakage, inject 1-bit CBC tampering, and demonstrate CTR keystream cancellation."
                BlockCipherForgeLevel.LEVEL_6 -> "Inject bit flips into state blocks, analyze diffusion rounds, and solve the Strict Avalanche Criterion challenge."
                BlockCipherForgeLevel.BOSS_BATTLE -> "Phase 1: Arrange operations. Phase 2: Disarm Round 10 MixColumns trap. Phase 3: Stabilize avalanche surge."
            }

            ArenaBriefingView(
                arenaTitle = "Arena 2 • Block Cipher Forge",
                levelName = currentLevel.title,
                cryptographicObjective = objective,
                mechanicsHint = mechanic,
                isBoss = currentLevel == BlockCipherForgeLevel.BOSS_BATTLE,
                onStartLevel = { showBriefing = false }
            )
        }

        // Post-Game Debrief View
        if (showDebrief) {
            val concepts = when (currentLevel) {
                BlockCipherForgeLevel.LEVEL_1 -> listOf("FIPS-197 AES S-Box", "GF(2^8) Multiplicative Inverse", "Affine Transformation Vector")
                BlockCipherForgeLevel.LEVEL_2 -> listOf("Cyclic Row Permutation", "Inter-column Diffusion", "AES State Array Indexing")
                BlockCipherForgeLevel.LEVEL_3 -> listOf("Galois Field Matrix Multiplication", "Irreducible Polynomial 0x11B", "MDS Branch Number 5")
                BlockCipherForgeLevel.LEVEL_4 -> listOf("Bitwise XOR Round Key Addition", "Self-inverting Involution", "Key Schedule Expansion")
                BlockCipherForgeLevel.LEVEL_5 -> listOf("ECB Penguin Image Leakage", "CBC Initialization Vectors", "CTR Nonce-Reuse Two-Time Pad Trap")
                BlockCipherForgeLevel.LEVEL_6 -> listOf("Avalanche Effect Criterion", "Strict Diffusion: ~50% bit flip on average")
                BlockCipherForgeLevel.BOSS_BATTLE -> listOf("AES-128 Complete 10-Round Structure", "Round 10 MixColumns Omission", "Full Citadel Defense")
            }
            val pitfalls = if (!debriefIsVictory) listOf(
                "In ECB mode, identical plaintext blocks always produce identical ciphertext blocks.",
                "In Round 10 of AES-128, MixColumns is OMITTED to enable symmetry and reduce silicon gate area.",
                "Never reuse an IV or Nonce with the same key in CTR/GCM mode."
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
                        currentLevel = BlockCipherForgeLevel.fromId(nextId)
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
            BlockCipherForgeTheoryCodexSheet(
                level = currentLevel,
                onDismiss = { showTheoryCodex = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlockCipherForgeTheoryCodexSheet(
    level: BlockCipherForgeLevel,
    onDismiss: () -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val primary = ArenaPalettes.Arena2Primary

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                    text = "THEORY CODEX • ${level.title.uppercase()}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = primary
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = dynamicColors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (level) {
                BlockCipherForgeLevel.LEVEL_1 -> {
                    Text(
                        text = "AES S-Box Substitution\n\n" +
                                "The S-Box (Substitution Box) provides non-linear confusion in AES. It acts on individual bytes through two steps:\n" +
                                "1. Multiplicative Inverse: Each byte 'a' is mapped to a⁻¹ in the Galois Field GF(2⁸) with irreducible polynomial P(x) = x⁸ + x⁴ + x³ + x + 1 (0x11B). Zero maps to itself.\n" +
                                "2. Affine Transformation: An invertible bit-matrix multiplication and addition with constant vector 0x63 destroys any algebraic relations over GF(2).\n\n" +
                                "This design prevents linear and differential cryptanalysis.",
                        fontSize = 13.sp,
                        color = dynamicColors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
                BlockCipherForgeLevel.LEVEL_2 -> {
                    Text(
                        text = "AES ShiftRows Transformation\n\n" +
                                "ShiftRows cyclically shifts the bytes in each row of the 4x4 state matrix by different offsets:\n" +
                                "• Row 0: Shifted by 0 bytes (unchanged)\n" +
                                "• Row 1: Cyclically shifted left by 1 byte\n" +
                                "• Row 2: Cyclically shifted left by 2 bytes\n" +
                                "• Row 3: Cyclically shifted left by 3 bytes\n\n" +
                                "This ensures that bytes across the same column are dispersed into 4 different columns in the next round.",
                        fontSize = 13.sp,
                        color = dynamicColors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
                BlockCipherForgeLevel.LEVEL_3 -> {
                    Text(
                        text = "AES MixColumns Transformation\n\n" +
                                "MixColumns operates on each 4-byte column of the state matrix independently as a 4-term polynomial over GF(2⁸).\n" +
                                "It multiplies the column by a Maximum Distance Separable (MDS) matrix with branch number 5:\n" +
                                "[ 02  03  01  01 ]\n" +
                                "[ 01  02  03  01 ]\n" +
                                "[ 01  01  02  03 ]\n" +
                                "[ 03  01  01  02 ]\n\n" +
                                "Multiplication by 2 (xtimes) shifts left and conditionally XORs with 0x1B if the MSB was 1.",
                        fontSize = 13.sp,
                        color = dynamicColors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
                BlockCipherForgeLevel.LEVEL_4 -> {
                    Text(
                        text = "AES AddRoundKey Transformation\n\n" +
                                "AddRoundKey combines each 128-bit state matrix with a 128-bit round subkey generated by the Rijndael Key Schedule.\n" +
                                "The operation is pure bitwise XOR (⊕).\n\n" +
                                "Because XOR is self-inverting (A ⊕ B ⊕ B = A), the decryption round key addition uses the exact same hardware circuit as encryption.",
                        fontSize = 13.sp,
                        color = dynamicColors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
                BlockCipherForgeLevel.LEVEL_5 -> {
                    Text(
                        text = "Block Cipher Modes & Vulnerabilities\n\n" +
                                "• ECB (Electronic Codebook): Each block is encrypted independently. Identical plaintext blocks produce identical ciphertext blocks, preserving visual patterns.\n" +
                                "• CBC (Cipher Block Chaining): Each block is XORed with previous ciphertext block before encryption. Requires random, unpredictable IV.\n" +
                                "• CTR (Counter Mode): Converts block cipher into stream cipher. Reusing an IV/Nonce with the same key allows attackers to XOR ciphertexts and recover plaintext.",
                        fontSize = 13.sp,
                        color = dynamicColors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
                BlockCipherForgeLevel.LEVEL_6 -> {
                    Text(
                        text = "The Avalanche Effect\n\n" +
                                "A desirable property of any cryptographic block cipher or hash function.\n" +
                                "Strict Avalanche Criterion: A single input bit flip must cause about 50% of the output bits to flip on average.\n" +
                                "In AES, after just 2 to 3 rounds of SubBytes, ShiftRows, and MixColumns, full diffusion is achieved across all 128 bits.",
                        fontSize = 13.sp,
                        color = dynamicColors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
                BlockCipherForgeLevel.BOSS_BATTLE -> {
                    Text(
                        text = "AES-128 Structure & Final Round Exception\n\n" +
                                "AES-128 consists of:\n" +
                                "• 1 Initial Round: AddRoundKey\n" +
                                "• 9 Standard Rounds: SubBytes → ShiftRows → MixColumns → AddRoundKey\n" +
                                "• 1 Final Round (Round 10): SubBytes → ShiftRows → AddRoundKey (MixColumns is OMITTED!)\n\n" +
                                "MixColumns is omitted in Round 10 so that inverse cipher operations can match the forward structure with optimal silicon footprint.",
                        fontSize = 13.sp,
                        color = dynamicColors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
