package com.simats.codeingo.ui.arenas.cipher_sanctum

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
import com.simats.codeingo.ui.arenas.cipher_sanctum.levels.CaesarDialLevel
import com.simats.codeingo.ui.arenas.cipher_sanctum.levels.CryptanalysisSprintLevel
import com.simats.codeingo.ui.arenas.cipher_sanctum.levels.FindTheShiftLevel
import com.simats.codeingo.ui.arenas.cipher_sanctum.levels.FrequencyRadioLevel
import com.simats.codeingo.ui.arenas.cipher_sanctum.levels.RailFenceGridLevel
import com.simats.codeingo.ui.arenas.cipher_sanctum.levels.SphinxBossBattleLevel
import com.simats.codeingo.ui.arenas.cipher_sanctum.levels.VigenereRotorsLevel
import com.simats.codeingo.ui.arenas.common.ArenaBriefingView
import com.simats.codeingo.ui.arenas.common.ArenaComboHUD
import com.simats.codeingo.ui.arenas.common.ArenaDebriefView
import com.simats.codeingo.ui.arenas.common.ArenaEnvironmentBackground
import com.simats.codeingo.ui.arenas.common.ArenaGameSpeed
import com.simats.codeingo.ui.arenas.common.ArenaLoadoutSheet
import com.simats.codeingo.ui.arenas.common.ArenaPalettes
import com.simats.codeingo.ui.arenas.common.ArenaParticleBurst
import com.simats.codeingo.ui.arenas.common.ArenaShockwaveEffect
import com.simats.codeingo.ui.arenas.common.ArenaTacticalTool
import com.simats.codeingo.ui.arenas.common.arenaShake
import com.simats.codeingo.ui.arenas.common.rememberArenaShakeController
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CipherSanctumArenaScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val dynamicColors = LocalDynamicThemeColors.current
    val reduceMotion = LocalReduceMotion.current

    val primary = ArenaPalettes.Arena1Primary
    val accent = ArenaPalettes.Arena1Accent

    // Hearts economy strictly observes GameManager (Max 10)
    val heartsCount by gameManager.heartsCount.collectAsState()
    val gemsCount by gameManager.gemsCount.collectAsState()

    var currentLevel by remember { mutableStateOf(CipherSanctumLevel.LEVEL_1) }
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

    fun handleLevelCompleted(lvl: CipherSanctumLevel) {
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
                                text = "CIPHER SANCTUM",
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

                // Enhanced Sanctum Level Selector Ribbon
                val sanctumStations = remember {
                    CipherSanctumLevel.entries.map { lvl ->
                        com.simats.codeingo.ui.arenas.common.ArenaStationNode(
                            id = lvl.id,
                            title = lvl.title,
                            shortName = when (lvl.id) {
                                1 -> "Caesar"
                                2 -> "Shift"
                                3 -> "Freq"
                                4 -> "Vigenere"
                                5 -> "RailFence"
                                6 -> "Sprint"
                                else -> "Sphinx"
                            },
                            iconEmoji = lvl.iconEmoji,
                            isBoss = lvl == CipherSanctumLevel.BOSS_BATTLE
                        )
                    }
                }

                com.simats.codeingo.ui.arenas.common.ArenaLevelTrack(
                    stations = sanctumStations,
                    currentLevelId = currentLevel.id,
                    unlockedLevels = unlockedLevels,
                    levelStars = levelStars,
                    accentColor = primary,
                    onSelectLevel = { id ->
                        currentLevel = CipherSanctumLevel.fromId(id)
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
                        CipherSanctumLevel.LEVEL_1 -> CaesarDialLevel(
                            onComplete = { handleLevelCompleted(CipherSanctumLevel.LEVEL_1) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(100) }
                        )
                        CipherSanctumLevel.LEVEL_2 -> FindTheShiftLevel(
                            onComplete = { handleLevelCompleted(CipherSanctumLevel.LEVEL_2) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(120) }
                        )
                        CipherSanctumLevel.LEVEL_3 -> FrequencyRadioLevel(
                            onComplete = { handleLevelCompleted(CipherSanctumLevel.LEVEL_3) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(150) }
                        )
                        CipherSanctumLevel.LEVEL_4 -> VigenereRotorsLevel(
                            onComplete = { handleLevelCompleted(CipherSanctumLevel.LEVEL_4) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(180) }
                        )
                        CipherSanctumLevel.LEVEL_5 -> RailFenceGridLevel(
                            onComplete = { handleLevelCompleted(CipherSanctumLevel.LEVEL_5) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(180) }
                        )
                        CipherSanctumLevel.LEVEL_6 -> CryptanalysisSprintLevel(
                            onComplete = { handleLevelCompleted(CipherSanctumLevel.LEVEL_6) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(200) }
                        )
                        CipherSanctumLevel.BOSS_BATTLE -> SphinxBossBattleLevel(
                            onDefeatBoss = { handleLevelCompleted(CipherSanctumLevel.BOSS_BATTLE) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(300) },
                            onTriggerShake = { shakeController.shake() },
                            onTriggerShockwave = { shockwaveTrigger = System.currentTimeMillis() }
                        )
                    }
                }

            } // Close inner weighted Column

            com.simats.codeingo.ui.arenas.common.ArenaBottomBar(
                onOpenCodex = { showTheoryCodex = true },
                onResetLevel = {
                    val lvl = currentLevel
                    currentLevel = CipherSanctumLevel.LEVEL_1
                    currentLevel = lvl
                },
                onSkipLevel = {
                    val nextId = minOf(7, currentLevel.id + 1)
                    unlockedLevels = unlockedLevels + nextId
                    currentLevel = CipherSanctumLevel.fromId(nextId)
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
                CipherSanctumLevel.LEVEL_1 -> "Calibrate the Caesar shift dial to decrypt intercepted military orders."
                CipherSanctumLevel.LEVEL_2 -> "Analyze known plaintext-ciphertext letter pairs to deduce shift key K."
                CipherSanctumLevel.LEVEL_3 -> "Leverage English natural letter frequencies to break single-substitution ciphers."
                CipherSanctumLevel.LEVEL_4 -> "Align repeated keystream rotors in the polyalphabetic Vigenère cipher."
                CipherSanctumLevel.LEVEL_5 -> "Reconstruct the 3-rail zigzag transposition grid to unscramble message bytes."
                CipherSanctumLevel.LEVEL_6 -> "Perform rapid modular arithmetic shifts under extreme time constraints."
                CipherSanctumLevel.BOSS_BATTLE -> "Confront the Sphinx of Shadows! Decrypt ancient Caesar wards across 3 adversarial battle phases."
            }
            val mechanic = when (currentLevel) {
                CipherSanctumLevel.LEVEL_1 -> "Rotate wheel until DWWDFN maps to ATTACK."
                CipherSanctumLevel.LEVEL_2 -> "Align all 4 test pairs simultaneously."
                CipherSanctumLevel.LEVEL_3 -> "Shift the key until the highest peak aligns with 'E'."
                CipherSanctumLevel.LEVEL_4 -> "Compute (P + K) mod 26 across 7 rotor positions."
                CipherSanctumLevel.LEVEL_5 -> "Swap rail segments into the correct top-to-bottom sequence."
                CipherSanctumLevel.LEVEL_6 -> "Select correct backwards shift symbols before 20s expires."
                CipherSanctumLevel.BOSS_BATTLE -> "Decrypt the Sphinx riddles under Caesar shift K=3."
            }

            ArenaBriefingView(
                arenaTitle = "Arena 1 • Cipher Sanctum",
                levelName = currentLevel.title,
                cryptographicObjective = objective,
                mechanicsHint = mechanic,
                isBoss = currentLevel == CipherSanctumLevel.BOSS_BATTLE,
                onStartLevel = { showBriefing = false }
            )
        }

        // Post-Game Debrief View
        if (showDebrief) {
            val concepts = when (currentLevel) {
                CipherSanctumLevel.LEVEL_1 -> listOf("Caesar Shift Cipher", "Modular Arithmetic (mod 26)", "Symmetric Key Calibration")
                CipherSanctumLevel.LEVEL_2 -> listOf("Known-Plaintext Cryptanalysis", "Linear Shift Equations", "Key Space Exhaustion")
                CipherSanctumLevel.LEVEL_3 -> listOf("Frequency Analysis", "Natural Language Signatures", "Monoalphabetic Vulnerability")
                CipherSanctumLevel.LEVEL_4 -> listOf("Polyalphabetic Substitution", "Vigenère Tabula Recta", "Keystream Periodicity")
                CipherSanctumLevel.LEVEL_5 -> listOf("Transposition Ciphers", "Rail-Fence Zigzag Routing", "Permutation Preservation")
                CipherSanctumLevel.LEVEL_6 -> listOf("Speed Cryptanalysis", "Modular Inverse Operations", "Rapid Problem Solving")
                CipherSanctumLevel.BOSS_BATTLE -> listOf("Classical Cryptanalysis", "Sphinx Ward Shattered", "Sanctum Mastery")
            }
            val pitfalls = if (!debriefIsVictory) listOf(
                "Monoalphabetic substitution ciphers fail because they preserve single-character frequency distributions.",
                "Vigenère ciphers defeat frequency analysis by using multiple shift keys cyclically.",
                "Transposition ciphers rearrange character positions without altering their individual identities."
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
                        currentLevel = CipherSanctumLevel.fromId(nextId)
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
                            text = "THEORY CODEX • CLASSICAL CIPHERS",
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
                        text = "Classical Cryptographic Foundations\n\n" +
                                "1. Caesar Cipher:\n" +
                                "A monoalphabetic substitution cipher where each letter in the plaintext is shifted by a fixed number of positions K down the alphabet: C = (P + K) mod 26. With only 25 non-trivial keys, it is trivial to break by exhaustive search.\n\n" +
                                "2. Frequency Analysis:\n" +
                                "In natural human languages, letters appear with predictable frequencies (e.g. in English, 'E' ~12.7%, 'T' ~9.1%, 'A' ~8.2%). Since monoalphabetic ciphers preserve letter counts, identifying the most frequent ciphertext symbol often reveals the shift.\n\n" +
                                "3. Vigenère Cipher:\n" +
                                "A polyalphabetic cipher invented to flatten letter frequency spikes. A repeating keyword dictates varying shift amounts for each successive letter: C_i = (P_i + K_i) mod 26.\n\n" +
                                "4. Rail-Fence Transposition:\n" +
                                "Unlike substitution, transposition retains the original characters but permutes their spatial order by writing text in a zigzag wave across parallel rails and reading them row-by-row.",
                        fontSize = 13.sp,
                        color = dynamicColors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
