package com.simats.codeingo.ui.worlds.array

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonStyle
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.SubtextGray
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArrayKingdomArenaScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val gemsCount by gameManager.gemsCount.collectAsState()

    var currentLevel by remember { mutableStateOf(ArrayKingdomLevel.LEVEL_1) }
    var unlockedLevels by remember { mutableStateOf(setOf(1)) }
    var levelStars by remember { mutableStateOf(mutableMapOf(1 to 3)) }

    var hearts by remember { mutableIntStateOf(5) }
    var sessionXP by remember { mutableIntStateOf(0) }
    var speed by remember { mutableStateOf(KingdomGameSpeed.NORMAL) }

    var showTheoryCodex by remember { mutableStateOf(false) }
    var showHintSheet by remember { mutableStateOf(false) }
    var showVictoryModal by remember { mutableStateOf(false) }
    var showBossVictoryModal by remember { mutableStateOf(false) }

    // Level completion handler
    fun handleLevelCompleted(lvl: ArrayKingdomLevel) {
        val nextId = lvl.id + 1
        unlockedLevels = unlockedLevels + nextId
        levelStars[lvl.id] = 3
        sessionXP += lvl.xpReward
        gameManager.addXP(lvl.xpReward)
        gameManager.addGems(lvl.coinReward)

        if (lvl == ArrayKingdomLevel.BOSS_BATTLE) {
            showBossVictoryModal = true
        } else {
            showVictoryModal = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // 3D Sorting Arena Ambient Background
        ArrayArenaEnvironmentView(isBoss = currentLevel == ArrayKingdomLevel.BOSS_BATTLE)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // 1. Top HUD Bar
            ArenaTopHUD(
                hearts = hearts,
                sessionXP = sessionXP,
                gemsCount = gemsCount,
                speed = speed,
                onDismiss = onDismiss,
                onCycleSpeed = { speed = speed.next() },
                onOpenHints = { showHintSheet = true }
            )

            // 2. Horizontal Level Track (L1 to L7)
            ArenaLevelTrack(
                currentLevel = currentLevel,
                unlockedLevels = unlockedLevels,
                levelStars = levelStars,
                onSelectLevel = { lvl -> currentLevel = lvl }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Active Level Content Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (currentLevel) {
                    ArrayKingdomLevel.LEVEL_1 -> ArrayLevel1Content(onComplete = { handleLevelCompleted(ArrayKingdomLevel.LEVEL_1) })
                    ArrayKingdomLevel.LEVEL_2 -> ArrayLevel2Content(onComplete = { handleLevelCompleted(ArrayKingdomLevel.LEVEL_2) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    ArrayKingdomLevel.LEVEL_3 -> ArrayLevel3Content(onComplete = { handleLevelCompleted(ArrayKingdomLevel.LEVEL_3) })
                    ArrayKingdomLevel.LEVEL_4 -> ArrayLevel4Content(onComplete = { handleLevelCompleted(ArrayKingdomLevel.LEVEL_4) })
                    ArrayKingdomLevel.LEVEL_5 -> ArrayLevel5Content(onComplete = { handleLevelCompleted(ArrayKingdomLevel.LEVEL_5) })
                    ArrayKingdomLevel.LEVEL_6 -> ArrayLevel6Content(onComplete = { handleLevelCompleted(ArrayKingdomLevel.LEVEL_6) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    ArrayKingdomLevel.BOSS_BATTLE -> ArrayBossBattleContent(onDefeatBoss = { handleLevelCompleted(ArrayKingdomLevel.BOSS_BATTLE) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                }
            }

            // 4. Bottom Controls Bar
            ArenaBottomBar(
                onOpenCodex = { showTheoryCodex = true },
                onReplay = {
                    val current = currentLevel
                    currentLevel = ArrayKingdomLevel.LEVEL_1
                    currentLevel = current
                },
                onSkip = {
                    val nextId = minOf(7, currentLevel.id + 1)
                    unlockedLevels = unlockedLevels + nextId
                    currentLevel = ArrayKingdomLevel.fromId(nextId)
                }
            )
        }

        // Modals
        if (showVictoryModal) {
            ArenaVictoryOverlay(
                level = currentLevel,
                onNextLevel = {
                    showVictoryModal = false
                    val nextId = minOf(7, currentLevel.id + 1)
                    currentLevel = ArrayKingdomLevel.fromId(nextId)
                },
                onDismiss = { showVictoryModal = false }
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
            ModalBottomSheet(
                onDismissRequest = { showTheoryCodex = false },
                containerColor = Color(0xFF0F1726)
            ) {
                ArrayKingdomCodexSheet(onClose = { showTheoryCodex = false })
            }
        }

        if (showHintSheet) {
            ModalBottomSheet(
                onDismissRequest = { showHintSheet = false },
                containerColor = Color(0xFF0F1726)
            ) {
                ArrayKingdomHintSheet(currentLevel = currentLevel, onClose = { showHintSheet = false })
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 🌌 3D Sorting Arena Environment
// ══════════════════════════════════════════════════════════════════
@Composable
private fun ArrayArenaEnvironmentView(isBoss: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "arenaGlow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arenaGlowScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isBoss) {
                        listOf(Color(0xFF2C0A0A), Color(0xFF140505), Color(0xFF090202))
                    } else {
                        listOf(Color(0xFF1F1604), Color(0xFF0F1422), Color(0xFF070A12))
                    }
                )
            )
    ) {
        // Radiant Amber Spotlight in background
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(340.dp)
                .scale(glowScale)
                .blur(50.dp)
                .clip(CircleShape)
                .background(
                    if (isBoss) Color(0xFFFF2200).copy(alpha = 0.22f) else AmberGold.copy(alpha = 0.18f)
                )
        )
    }
}

// ══════════════════════════════════════════════════════════════════
// 🎮 HUD & Navigation Track
// ══════════════════════════════════════════════════════════════════
@Composable
private fun ArenaTopHUD(
    hearts: Int,
    sessionXP: Int,
    gemsCount: Int,
    speed: KingdomGameSpeed,
    onDismiss: () -> Unit,
    onCycleSpeed: () -> Unit,
    onOpenHints: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color.White.copy(alpha = 0.8f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            // Hearts
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = "❤️ $hearts", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
            }

            // Session XP
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = "⭐ +$sessionXP", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
            }

            // Gems
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = "💎 $gemsCount", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            // Speed Toggle Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AmberGold.copy(alpha = 0.18f))
                    .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .clickable { onCycleSpeed() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = speed.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = AmberGold
                )
            }

            // Hint Button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD700).copy(alpha = 0.2f))
                    .clickable { onOpenHints() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = "Hint", tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun ArenaLevelTrack(
    currentLevel: ArrayKingdomLevel,
    unlockedLevels: Set<Int>,
    levelStars: Map<Int, Int>,
    onSelectLevel: (ArrayKingdomLevel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ArrayKingdomLevel.entries.forEach { lvl ->
            val isUnlocked = unlockedLevels.contains(lvl.id)
            val isSelected = currentLevel == lvl

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        when {
                            isSelected && lvl == ArrayKingdomLevel.BOSS_BATTLE -> DuolingoRed
                            isSelected -> AmberGold
                            isUnlocked -> Color(0xFF141E30)
                            else -> Color.Black.copy(alpha = 0.35f)
                        }
                    )
                    .border(
                        1.dp,
                        if (isSelected) Color.White else Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(enabled = isUnlocked) { onSelectLevel(lvl) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = if (isUnlocked) lvl.iconEmoji else "🔒", fontSize = 12.sp)
                    Text(
                        text = "L${lvl.id}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color.Black else (if (isUnlocked) Color.White else SubtextGray)
                    )
                    if (isUnlocked && (levelStars[lvl.id] ?: 0) > 0) {
                        Text(text = "⭐", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ArenaBottomBar(
    onOpenCodex: () -> Unit,
    onReplay: () -> Unit,
    onSkip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF141E30))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                .clickable { onOpenCodex() }
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "📖", fontSize = 14.sp)
                Text(
                    text = "Kingdom Lore & Big-O",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF141E30))
                    .clickable { onReplay() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Replay", tint = Color.White, modifier = Modifier.size(18.dp))
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable { onSkip() }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Skip ⏭", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 🧩 Level 1: What is an Array?
// ══════════════════════════════════════════════════════════════════
@Composable
private fun ArrayLevel1Content(onComplete: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    var selectedQuizOption by remember { mutableStateOf<Int?>(null) }
    val blocks = remember { mutableStateListOf<Int>() }

    LaunchedEffect(step) {
        blocks.clear()
        when (step) {
            1 -> blocks.addAll(listOf(10))
            2 -> blocks.addAll(listOf(10, 20))
            3 -> blocks.addAll(listOf(10, 20, 30))
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Speech Dialogue Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0F1A2E))
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Text(
                text = if (step < 3) {
                    "🏰 Welcome to the Sorting Arena! An Array stores items in continuous side-by-side memory boxes with zero-based indices [0, 1, 2...]."
                } else {
                    "🎯 Which value is placed at index 1?"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Visual 3D Blocks Platform
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(130.dp)
        ) {
            if (blocks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .size(160.dp, 60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Empty Array []", color = SubtextGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                blocks.forEachIndexed { index, value ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(64.dp, 64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(AmberGold, Color(0xFFD46800))
                                    )
                                )
                                .border(1.5.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                .shadow(8.dp, spotColor = AmberGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "$value", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }

                        Text(
                            text = "index $index",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGold
                        )
                    }
                }
            }
        }

        // Action controls
        if (step < 3) {
            Duolingo3DButton(
                title = if (step == 0) "➕ Insert First Element [10]" else "➕ Append Next Element",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = { step++ },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(10, 20, 30).forEach { opt ->
                    val isCorrect = opt == 20
                    val isSelected = selectedQuizOption == opt
                    Box(
                        modifier = Modifier
                            .size(72.dp, 50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) {
                                    if (isCorrect) DuolingoGreen else DuolingoRed
                                } else {
                                    Color(0xFF141E30)
                                }
                            )
                            .border(1.5.dp, if (isSelected) Color.White else Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                            .clickable {
                                selectedQuizOption = opt
                                if (isCorrect) {
                                    onComplete()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$opt", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 🎯 Level 2: Index Hunt
// ══════════════════════════════════════════════════════════════════
@Composable
private fun ArrayLevel2Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    val items = remember { listOf(42, 88, 15, 99, 23) }
    var targetIndex by remember { mutableIntStateOf(2) }
    var correctCount by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("Tap the element at index $targetIndex!") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0F1A2E))
                .border(1.dp, DuolingoBlue.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "🎯 INDEX HUNT (${correctCount}/3): $message",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = DuolingoBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            items.forEachIndexed { idx, valNum ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(58.dp, 60.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF14243C))
                            .border(1.5.dp, DuolingoBlue.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .clickable {
                                if (idx == targetIndex) {
                                    correctCount++
                                    if (correctCount >= 3) {
                                        onComplete()
                                    } else {
                                        targetIndex = (0 until items.size).filter { it != targetIndex }.random()
                                        message = "Correct! Now tap element at index $targetIndex!"
                                    }
                                } else {
                                    onDeductHeart()
                                    message = "Wrong! Index $idx has value $valNum. Try index $targetIndex."
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$valNum", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }

                    Text(text = "[$idx]", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 🔍 Level 3: Treasure Search (Linear Search Step-by-Step)
// ══════════════════════════════════════════════════════════════════
@Composable
private fun ArrayLevel3Content(onComplete: () -> Unit) {
    val items = remember { listOf(12, 45, 78, 34, 91, 56) }
    val targetVal = 34
    var scanIndex by remember { mutableIntStateOf(0) }
    var found by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0F1A2E))
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Text(
                text = if (!found) "🔍 Searching for Treasure: $targetVal. Inspecting index $scanIndex..." else "🎉 Treasure $targetVal FOUND at index $scanIndex!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = if (found) DuolingoGreen else AmberGold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            items.forEachIndexed { idx, v ->
                val isScanning = idx == scanIndex
                val isMatch = v == targetVal && found

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(50.dp, 56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    isMatch -> DuolingoGreen
                                    isScanning -> AmberGold
                                    idx < scanIndex -> Color(0xFF1B2433)
                                    else -> Color(0xFF101724)
                                }
                            )
                            .border(1.5.dp, if (isScanning) Color.White else Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$v", fontSize = 16.sp, fontWeight = FontWeight.Black, color = if (isScanning && !isMatch) Color.Black else Color.White)
                    }

                    Text(text = if (isScanning) "👆 i=$idx" else "[$idx]", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isScanning) AmberGold else SubtextGray)
                }
            }
        }

        if (!found) {
            Duolingo3DButton(
                title = "👉 Check Next Box (O(n) Step)",
                style = Duolingo3DButtonStyle.AMBER,
                onClick = {
                    if (items[scanIndex] == targetVal) {
                        found = true
                        onComplete()
                    } else if (scanIndex < items.size - 1) {
                        scanIndex++
                        if (items[scanIndex] == targetVal) {
                            found = true
                            onComplete()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 🔄 Level 4: Bubble Sort Intro
// ══════════════════════════════════════════════════════════════════
@Composable
private fun ArrayLevel4Content(onComplete: () -> Unit) {
    val items = remember { mutableStateListOf(50, 20, 40, 10, 30) }
    var currentPair by remember { mutableIntStateOf(0) }
    var isSorted by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0F1A2E))
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Text(
                text = if (!isSorted) "🔄 Bubble Sort: Compare adjacent pairs. If left > right, swap them!" else "✨ Pass completed! The largest value bubbled to the end!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEachIndexed { idx, v ->
                val isComparing = idx == currentPair || idx == currentPair + 1
                Box(
                    modifier = Modifier
                        .size(56.dp, 60.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isComparing) AmberGold else Color(0xFF14243C))
                        .border(1.5.dp, if (isComparing) Color.White else Color.Transparent, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$v", fontSize = 18.sp, fontWeight = FontWeight.Black, color = if (isComparing) Color.Black else Color.White)
                }
            }
        }

        if (!isSorted) {
            Duolingo3DButton(
                title = "⚡ Compare & Step",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = {
                    if (currentPair < items.size - 1) {
                        if (items[currentPair] > items[currentPair + 1]) {
                            val temp = items[currentPair]
                            items[currentPair] = items[currentPair + 1]
                            items[currentPair + 1] = temp
                        }
                        if (currentPair == items.size - 2) {
                            isSorted = true
                            onComplete()
                        } else {
                            currentPair++
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.75f)
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 👆 Level 5: Control the Sort
// ══════════════════════════════════════════════════════════════════
@Composable
private fun ArrayLevel5Content(onComplete: () -> Unit) {
    val items = remember { mutableStateListOf(30, 10, 50, 20, 40) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var swapsDone by remember { mutableIntStateOf(0) }

    val sortedList = remember { items.sorted() }
    val isFullySorted = items.toList() == sortedList

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0F1A2E))
                .border(1.dp, DuolingoGreen.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Text(
                text = if (isFullySorted) "🏆 ARRAY ORDER RESTORED! ($swapsDone swaps)" else "👆 Tap two adjacent elements to swap and sort in ascending order.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isFullySorted) DuolingoGreen else Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEachIndexed { idx, v ->
                val isSelected = selectedIndex == idx
                Box(
                    modifier = Modifier
                        .size(56.dp, 60.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            when {
                                isFullySorted -> DuolingoGreen
                                isSelected -> AmberGold
                                else -> Color(0xFF14243C)
                            }
                        )
                        .border(1.5.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(14.dp))
                        .clickable(enabled = !isFullySorted) {
                            if (selectedIndex == null) {
                                selectedIndex = idx
                            } else {
                                val first = selectedIndex!!
                                if (kotlin.math.abs(first - idx) == 1) {
                                    val temp = items[first]
                                    items[first] = items[idx]
                                    items[idx] = temp
                                    swapsDone++
                                    if (items.toList() == sortedList) {
                                        onComplete()
                                    }
                                }
                                selectedIndex = null
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$v", fontSize = 18.sp, fontWeight = FontWeight.Black, color = if (isSelected) Color.Black else Color.White)
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// ⏱️ Level 6: Speed Challenge
// ══════════════════════════════════════════════════════════════════
@Composable
private fun ArrayLevel6Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    val items = remember { mutableStateListOf(40, 10, 30, 20) }
    var secondsLeft by remember { mutableIntStateOf(20) }
    var isSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(isSuccess) {
        if (!isSuccess) {
            while (secondsLeft > 0) {
                delay(1000)
                secondsLeft--
            }
            if (secondsLeft == 0 && !isSuccess) {
                onDeductHeart()
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (secondsLeft < 5) DuolingoRed else AmberGold)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "⏱️ $secondsLeft SECONDS REMAINING",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEachIndexed { idx, v ->
                Box(
                    modifier = Modifier
                        .size(60.dp, 64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSuccess) DuolingoGreen else Color(0xFF14243C))
                        .border(1.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .clickable(enabled = !isSuccess) {
                            if (idx < items.size - 1 && items[idx] > items[idx + 1]) {
                                val t = items[idx]
                                items[idx] = items[idx + 1]
                                items[idx + 1] = t
                                if (items.toList() == items.sorted()) {
                                    isSuccess = true
                                    onComplete()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$v", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }

        Text(
            text = "Tap any inverted pair to swap quickly!",
            fontSize = 12.sp,
            color = SubtextGray
        )
    }
}

// ══════════════════════════════════════════════════════════════════
// 🔥 Boss Battle: The Chaos Array
// ══════════════════════════════════════════════════════════════════
@Composable
private fun ArrayBossBattleContent(onDefeatBoss: () -> Unit, onDeductHeart: () -> Unit) {
    var bossHP by remember { mutableIntStateOf(100) }
    val chaosBlocks = remember { mutableStateListOf(90, 30, 80, 10, 60) }
    var selectedIdx by remember { mutableStateOf<Int?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Boss Status Bar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "👾 THE CHAOS ARRAY (BOSS)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = DuolingoRed,
                letterSpacing = 1.sp
            )

            // Boss HP Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(bossHP / 100f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(DuolingoRed)
                )
            }
            Text(text = "$bossHP / 100 HP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
        }

        // Chaos Blocks
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            chaosBlocks.forEachIndexed { i, v ->
                val isSelected = selectedIdx == i
                Box(
                    modifier = Modifier
                        .size(54.dp, 58.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AmberGold else Color(0xFF2E1010))
                        .border(1.5.dp, if (isSelected) Color.White else DuolingoRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable {
                            if (selectedIdx == null) {
                                selectedIdx = i
                            } else {
                                val first = selectedIdx!!
                                if (kotlin.math.abs(first - i) == 1) {
                                    val temp = chaosBlocks[first]
                                    chaosBlocks[first] = chaosBlocks[i]
                                    chaosBlocks[i] = temp
                                    bossHP = maxOf(0, bossHP - 25)
                                    if (bossHP <= 0 || chaosBlocks.toList() == chaosBlocks.sorted()) {
                                        onDefeatBoss()
                                    }
                                } else {
                                    onDeductHeart()
                                }
                                selectedIdx = null
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$v", fontSize = 18.sp, fontWeight = FontWeight.Black, color = if (isSelected) Color.Black else Color.White)
                }
            }
        }

        Text(text = "\"You cannot order my chaos!\"", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AmberGold)
    }
}

// ══════════════════════════════════════════════════════════════════
// 🏆 Victory Overlays & Bottom Sheets
// ══════════════════════════════════════════════════════════════════
@Composable
private fun ArenaVictoryOverlay(
    level: ArrayKingdomLevel,
    onNextLevel: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0F1726))
                .border(2.dp, AmberGold, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "🎉 LEVEL COMPLETE!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = AmberGold)
            Text(text = level.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "⭐ +${level.xpReward} XP", fontSize = 14.sp, fontWeight = FontWeight.Black, color = AmberGold)
                Text(text = "💎 +${level.coinReward} GEMS", fontSize = 14.sp, fontWeight = FontWeight.Black, color = DuolingoBlue)
            }

            Duolingo3DButton(
                title = "CONTINUE ➔",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = onNextLevel,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ArenaBossVictoryOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0F1726))
                .border(2.dp, DuolingoGreen, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "👑 BOSS VANQUISHED!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
            Text(text = "You mastered contiguous memory and conquered Array Kingdom!", fontSize = 13.sp, color = SubtextGray, textAlign = TextAlign.Center)

            Duolingo3DButton(
                title = "CLAIM VICTORY",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ArrayKingdomCodexSheet(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(text = "📖 Array Kingdom Codex", fontSize = 20.sp, fontWeight = FontWeight.Black, color = AmberGold)
        Text(
            text = "An Array stores elements in contiguous memory slots. Accessing an element by index takes O(1) time because the memory address is calculated as: Base_Address + (Index * Element_Size).\n\nSearching takes O(n) because each box must be inspected linearly. Sorting with Bubble Sort repeatedly swaps adjacent inverted pairs until the array is fully sorted, which takes O(n²) comparisons in the worst case.",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.8f),
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun ArrayKingdomHintSheet(currentLevel: ArrayKingdomLevel, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(text = "💡 Level Hint: ${currentLevel.title}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AmberGold)
        Text(
            text = when (currentLevel) {
                ArrayKingdomLevel.LEVEL_1 -> "Arrays start at index 0. The second element is always at index 1!"
                ArrayKingdomLevel.LEVEL_2 -> "Count starting from 0 from left to right."
                ArrayKingdomLevel.LEVEL_3 -> "Linear search checks each element one after another from left to right."
                ArrayKingdomLevel.LEVEL_4 -> "Bubble sort pushes the largest unsorted element to the end in each pass."
                ArrayKingdomLevel.LEVEL_5 -> "Select two elements right next to each other to swap them."
                ArrayKingdomLevel.LEVEL_6 -> "Speed is key! Find any number that is bigger than the number to its right and swap."
                ArrayKingdomLevel.BOSS_BATTLE -> "Restore order to deal damage to the Chaos Boss!"
            },
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.85f),
            lineHeight = 18.sp
        )
    }
}
