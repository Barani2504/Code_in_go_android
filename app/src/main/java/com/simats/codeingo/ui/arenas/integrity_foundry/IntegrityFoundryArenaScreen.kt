package com.simats.codeingo.ui.arenas.integrity_foundry

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
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.BirthdayRadarLevel
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.BitFlipLabLevel
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.CollisionBeastBossLevel
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.DigestPressLevel
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.SignatureSealLevel
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.SpongeChamberLevel
import com.simats.codeingo.ui.arenas.integrity_foundry.levels.WaxSealLevel
import com.simats.codeingo.ui.theme.AmberGold
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

        debriefIsVictory = true
        showDebrief = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        com.simats.codeingo.ui.arenas.common.ArenaEnvironmentBackground(accentColor = Color(0xFF00B894))

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
            val foundryStations = remember {
                IntegrityFoundryLevel.values().map { lvl ->
                    com.simats.codeingo.ui.arenas.common.ArenaStationNode(
                        id = lvl.id,
                        title = lvl.title,
                        shortName = when (lvl.id) {
                            1 -> "Digest"
                            2 -> "Avalanche"
                            3 -> "Sponge"
                            4 -> "Merkle"
                            5 -> "HMAC"
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
                accentColor = Color(0xFF00B894),
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
                            onActionSuccess = { onActionSuccess(300) }
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

        // Cinematic Briefing Overlay
        if (showBriefing) {
            val objective = when (currentLevel) {
                IntegrityFoundryLevel.LEVEL_1 -> "Compress arbitrary message streams into irreversible, fixed-size 256-bit digests via Merkle-Damgård padding."
                IntegrityFoundryLevel.LEVEL_2 -> "Observe the strict avalanche criterion: verify that altering 1 input bit alters approximately 50% of the output hash."
                IntegrityFoundryLevel.LEVEL_3 -> "Operate the SHA-3 Keccak sponge construction: absorb message bits into the rate r and squeeze out the digest."
                IntegrityFoundryLevel.LEVEL_4 -> "Validate hierarchical integrity using Merkle tree root hashes over cryptographic leaf blocks."
                IntegrityFoundryLevel.LEVEL_5 -> "Calculate the square-root collision search boundary: 2^(n/2) for n-bit hash functions via the Birthday Paradox."
                IntegrityFoundryLevel.LEVEL_6 -> "Construct keyed-hash message authentication codes (HMAC) using inner/outer pads to defeat length extension attacks."
                IntegrityFoundryLevel.BOSS_BATTLE -> "Confront the Collision Beast! Defend certificate digests from birthday collision attacks and length extensions!"
            }
            val mechanic = when (currentLevel) {
                IntegrityFoundryLevel.LEVEL_1 -> "Identify the standard block chunk processing size for SHA-256."
                IntegrityFoundryLevel.LEVEL_2 -> "Flip 1 bit in the input message and confirm the avalanche cascade."
                IntegrityFoundryLevel.LEVEL_3 -> "Advance through Keccak absorb, 24-round permutation, and squeeze stages."
                IntegrityFoundryLevel.LEVEL_4 -> "Verify the root hash commitment from child node hashes."
                IntegrityFoundryLevel.LEVEL_5 -> "Determine the birthday bound trial space for a 256-bit digest."
                IntegrityFoundryLevel.LEVEL_6 -> "Select the cryptographic vulnerability eliminated by HMAC nested padding."
                IntegrityFoundryLevel.BOSS_BATTLE -> "Answer collision resistance bounds across 3 defensive boss phases."
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
                IntegrityFoundryLevel.LEVEL_1 -> listOf("Merkle-Damgård Construction", "512-bit Block Padding", "SHA-256 Compression")
                IntegrityFoundryLevel.LEVEL_2 -> listOf("Strict Avalanche Criterion", "One-Way Pre-image Resistance", "Diffusion in Hashes")
                IntegrityFoundryLevel.LEVEL_3 -> listOf("Keccak Sponge Construction", "Rate (r) and Capacity (c)", "Keccak-f[1600] Permutation")
                IntegrityFoundryLevel.LEVEL_4 -> listOf("Merkle Trees", "Hierarchical Digest Authentication", "Cryptographic Accumulators")
                IntegrityFoundryLevel.LEVEL_5 -> listOf("Birthday Paradox Collision Bound: 2^(n/2)", "Collision vs Pre-image Resistance")
                IntegrityFoundryLevel.LEVEL_6 -> listOf("HMAC Construction", "ipad (0x36) & opad (0x5C)", "Length Extension Attack Defense")
                IntegrityFoundryLevel.BOSS_BATTLE -> listOf("Full Digest Integrity", "Collision Beast Defeated", "Mastered Hash Foundations")
            }
            val pitfalls = if (!debriefIsVictory) listOf(
                "Never use plain H(K || M) with Merkle-Damgård hashes; use HMAC to avoid length extension attacks.",
                "MD5 and SHA-1 have broken collision resistance and must not be used for digital certificates.",
                "Collision resistance requires 2^(n/2) work, whereas pre-image resistance requires 2^n work."
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
                            text = "THEORY CODEX • HASH & INTEGRITY",
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
                        text = "Cryptographic Hash Functions & Integrity\n\n" +
                                "A cryptographic hash function H maps arbitrary-length inputs to fixed-length outputs satisfying:\n" +
                                "1. Pre-image Resistance: Given y, it is computationally infeasible to find x such that H(x) = y (2^n work).\n" +
                                "2. Second Pre-image Resistance: Given x, it is infeasible to find x' ≠ x such that H(x) = H(x') (2^n work).\n" +
                                "3. Collision Resistance: Infeasible to find any pair (x, x') such that H(x) = H(x') (2^(n/2) work via Birthday Paradox).\n\n" +
                                "HMAC (Keyed-Hash Message Authentication Code):\n" +
                                "HMAC(K, M) = H((K ⊕ opad) ∥ H((K ⊕ ipad) ∥ M))\n" +
                                "where ipad = 0x36 repeated and opad = 0x5C repeated. This inner/outer nested hashing structurally prevents length extension attacks that plague Merkle-Damgård hash functions.\n\n" +
                                "Keccak / SHA-3 Sponge Construction:\n" +
                                "Replaces Merkle-Damgård with an absorb-squeeze state machine over a 1600-bit permutation state (rate r + capacity c), providing inherent resistance against length extension without needing HMAC.",
                        fontSize = 13.sp,
                        color = dynamicColors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
