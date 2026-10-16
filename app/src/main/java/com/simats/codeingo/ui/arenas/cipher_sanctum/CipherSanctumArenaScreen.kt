package com.simats.codeingo.ui.arenas.cipher_sanctum

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.simats.codeingo.ui.arenas.common.ArenaGameSpeed
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonStyle
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CipherSanctumArenaScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val gemsCount by gameManager.gemsCount.collectAsState()

    var currentLevel by remember { mutableStateOf(CipherSanctumLevel.LEVEL_1) }
    var unlockedLevels by remember { mutableStateOf(setOf(1)) }
    var levelStars by remember { mutableStateOf(mutableMapOf(1 to 3)) }

    var hearts by remember { mutableIntStateOf(5) }
    var sessionXP by remember { mutableIntStateOf(0) }
    var speed by remember { mutableStateOf(ArenaGameSpeed.NORMAL) }

    var showTheoryCodex by remember { mutableStateOf(false) }
    var showHintSheet by remember { mutableStateOf(false) }
    var showVictoryModal by remember { mutableStateOf(false) }
    var showBossVictoryModal by remember { mutableStateOf(false) }

    fun handleLevelCompleted(lvl: CipherSanctumLevel) {
        val nextId = lvl.id + 1
        unlockedLevels = unlockedLevels + nextId
        levelStars[lvl.id] = 3
        sessionXP += lvl.xpReward
        gameManager.addXP(lvl.xpReward)
        gameManager.addGems(lvl.coinReward)

        if (lvl == CipherSanctumLevel.BOSS_BATTLE) {
            showBossVictoryModal = true
        } else {
            showVictoryModal = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalDynamicThemeColors.current.background)
    ) {
        CipherArenaEnvironmentView(isBoss = currentLevel == CipherSanctumLevel.BOSS_BATTLE)

        com.simats.codeingo.ui.arenas.common.ArenaEnvironmentBackground(accentColor = AmberGold)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
            ArenaTopHUD(
                hearts = hearts,
                sessionXP = sessionXP,
                gemsCount = gemsCount,
                speed = speed,
                onDismiss = onDismiss,
                onCycleSpeed = { speed = speed.next() },
                onOpenHint = { showHintSheet = true }
            )

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
                accentColor = AmberGold,
                onSelectLevel = { id ->
                    currentLevel = CipherSanctumLevel.fromId(id)
                }
            )

            // Dynamic Level Board Content with internal scroll view
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when (currentLevel) {
                        CipherSanctumLevel.LEVEL_1 -> CaesarDialLevel(onComplete = { handleLevelCompleted(CipherSanctumLevel.LEVEL_1) })
                        CipherSanctumLevel.LEVEL_2 -> FindTheShiftLevel(onComplete = { handleLevelCompleted(CipherSanctumLevel.LEVEL_2) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                        CipherSanctumLevel.LEVEL_3 -> FrequencyRadioLevel(onComplete = { handleLevelCompleted(CipherSanctumLevel.LEVEL_3) })
                        CipherSanctumLevel.LEVEL_4 -> VigenereRotorsLevel(onComplete = { handleLevelCompleted(CipherSanctumLevel.LEVEL_4) })
                        CipherSanctumLevel.LEVEL_5 -> RailFenceGridLevel(onComplete = { handleLevelCompleted(CipherSanctumLevel.LEVEL_5) })
                        CipherSanctumLevel.LEVEL_6 -> CryptanalysisSprintLevel(onComplete = { handleLevelCompleted(CipherSanctumLevel.LEVEL_6) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                        CipherSanctumLevel.BOSS_BATTLE -> SphinxBossBattleLevel(onDefeatBoss = { handleLevelCompleted(CipherSanctumLevel.BOSS_BATTLE) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    }
                }
            }

            } // Close inner weighted column

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

        // Victory Overlay
        if (showVictoryModal) {
            ArenaVictoryOverlay(
                level = currentLevel,
                onNext = {
                    showVictoryModal = false
                    val nextId = currentLevel.id + 1
                    if (nextId <= 7) {
                        currentLevel = CipherSanctumLevel.fromId(nextId)
                    }
                },
                onReplay = {
                    showVictoryModal = false
                }
            )
        }

        if (showBossVictoryModal) {
            ArenaBossVictoryOverlay(
                onDismiss = {
                    showBossVictoryModal = false
                    onDismiss()
                }
            )
        }

        if (showTheoryCodex) {
            CipherSanctumCodexSheet(onClose = { showTheoryCodex = false })
        }

        if (showHintSheet) {
            CipherSanctumHintSheet(currentLevel = currentLevel, onClose = { showHintSheet = false })
        }
    }
}

@Composable
private fun CipherArenaEnvironmentView(isBoss: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val ambientGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientGlow"
    )

    val isDark = LocalDynamicThemeColors.current.isDark
    val bgGradient = if (isBoss) {
        listOf(Color(0xFF3B0000), Color(0xFF1F0000), if (isDark) Color(0xFF0F0000) else Color(0xFFFFEBEE))
    } else {
        if (isDark) {
            listOf(Color(0xFF0F1A2E), Color(0xFF080F1E), Color(0xFF040810))
        } else {
            listOf(Color(0xFFFFF9E6), Color(0xFFFFF3D0), Color(0xFFF9F5EC))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(320.dp)
                .scale(ambientGlow)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            (if (isBoss) DuolingoRed else AmberGold).copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
private fun ArenaTopHUD(
    hearts: Int,
    sessionXP: Int,
    gemsCount: Int,
    speed: ArenaGameSpeed,
    onDismiss: () -> Unit,
    onCycleSpeed: () -> Unit,
    onOpenHint: () -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Exit", tint = dynamicColors.textPrimary.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(dynamicColors.cardBorder.copy(alpha = 0.3f))
                    .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(text = "❤️ $hearts", fontSize = 11.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(dynamicColors.cardBorder.copy(alpha = 0.3f))
                    .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(text = "⭐ +$sessionXP", fontSize = 11.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(dynamicColors.cardBorder.copy(alpha = 0.3f))
                    .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(text = "💎 $gemsCount", fontSize = 11.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .pressScale()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AmberGold.copy(alpha = 0.2f))
                    .border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .clickable(onClick = onCycleSpeed)
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(text = speed.label, fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = AmberGold)
            }

            Box(
                modifier = Modifier
                    .pressScale()
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(AmberGold.copy(alpha = 0.2f))
                    .clickable(onClick = onOpenHint),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = "Hint", tint = AmberGold, modifier = Modifier.size(15.dp))
            }
        }
    }
}


@Composable
private fun ArenaVictoryOverlay(
    level: CipherSanctumLevel,
    onNext: () -> Unit,
    onReplay: () -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .background(dynamicColors.cardBackground)
                .border(2.dp, AmberGold, RoundedCornerShape(20.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "🎉 LEVEL COMPLETED!", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AmberGold)
            Text(text = level.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
            Text(text = "+${level.xpReward} XP  •  +${level.coinReward} Gems", fontSize = 12.sp, color = DuolingoGreen, fontWeight = FontWeight.Bold)

            Duolingo3DButton(
                title = "Continue",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ArenaBossVictoryOverlay(onDismiss: () -> Unit) {
    val dynamicColors = LocalDynamicThemeColors.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .background(dynamicColors.cardBackground)
                .border(2.dp, AmberGold, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = "👑 SPHINX VANQUISHED!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = AmberGold)
            Text(text = "You have unlocked the inner sanctum of Classical Cryptanalysis!", fontSize = 13.sp, color = dynamicColors.textPrimary, textAlign = TextAlign.Center)

            Duolingo3DButton(
                title = "Claim Realm Key",
                style = Duolingo3DButtonStyle.AMBER,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CipherSanctumCodexSheet(onClose: () -> Unit) {
    val dynamicColors = LocalDynamicThemeColors.current
    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = dynamicColors.cardBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "📜 Cipher Sanctum Codex", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AmberGold)
            Text(
                text = "Classical ciphers manipulate symbols through substitution (swapping values) or transposition (permuting positions). In Caesar shift, C = (P + K) mod 26. Vigenère uses a polyalphabetic key stream, while frequency analysis exploits natural language letter distributions (E ~ 12.7%, T ~ 9.1%).",
                fontSize = 13.sp,
                color = dynamicColors.textPrimary,
                lineHeight = 18.sp
            )
            Duolingo3DButton(title = "Close Codex", style = Duolingo3DButtonStyle.GREEN, onClick = onClose, modifier = Modifier.fillMaxWidth())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CipherSanctumHintSheet(currentLevel: CipherSanctumLevel, onClose: () -> Unit) {
    val dynamicColors = LocalDynamicThemeColors.current
    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = dynamicColors.cardBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "💡 Tactical Hint", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AmberGold)
            val hintText = when (currentLevel) {
                CipherSanctumLevel.LEVEL_1 -> "In Caesar cipher mod 26, shifting letter P by key K gives C = (P + K) mod 26."
                CipherSanctumLevel.LEVEL_2 -> "Match the shifted letters along the modular ring to compute the ciphertext offset."
                CipherSanctumLevel.LEVEL_3 -> "Frequency analysis isolates high-frequency vowels and common consonants like E, T, and A."
                CipherSanctumLevel.LEVEL_4 -> "Align the repeating key characters against plaintext letters to scramble letter frequencies."
                CipherSanctumLevel.LEVEL_5 -> "Reorder transposition grid rows and columns to reconstruct the original plaintext stream."
                CipherSanctumLevel.LEVEL_6 -> "Speed is critical! Perform rapid substitution cryptanalysis before the timer elapses."
                CipherSanctumLevel.BOSS_BATTLE -> "Solve the modular cipher locks to deal devastating damage to the Sphinx of Shadows!"
            }
            Text(text = hintText, fontSize = 13.sp, color = dynamicColors.textPrimary, lineHeight = 18.sp)
            Duolingo3DButton(title = "Understood", style = Duolingo3DButtonStyle.GREEN, onClick = onClose, modifier = Modifier.fillMaxWidth())
        }
    }
}
