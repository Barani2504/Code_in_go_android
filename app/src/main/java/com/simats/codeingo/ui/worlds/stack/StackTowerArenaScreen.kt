package com.simats.codeingo.ui.worlds.stack

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Refresh
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
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.worlds.array.KingdomGameSpeed
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StackTowerArenaScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val gemsCount by gameManager.gemsCount.collectAsState()

    var currentLevel by remember { mutableStateOf(StackTowerLevel.LEVEL_1) }
    var unlockedLevels by remember { mutableStateOf(setOf(1)) }
    var levelStars by remember { mutableStateOf(mutableMapOf(1 to 3)) }

    var hearts by remember { mutableIntStateOf(5) }
    var sessionXP by remember { mutableIntStateOf(0) }
    var speed by remember { mutableStateOf(KingdomGameSpeed.NORMAL) }

    var showTheoryCodex by remember { mutableStateOf(false) }
    var showHintSheet by remember { mutableStateOf(false) }
    var showVictoryModal by remember { mutableStateOf(false) }
    var showBossVictoryModal by remember { mutableStateOf(false) }

    fun handleLevelCompleted(lvl: StackTowerLevel) {
        val nextId = lvl.id + 1
        unlockedLevels = unlockedLevels + nextId
        levelStars[lvl.id] = 3
        sessionXP += lvl.xpReward
        gameManager.addXP(lvl.xpReward)
        gameManager.addGems(lvl.coinReward)

        if (lvl == StackTowerLevel.BOSS_BATTLE) {
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
        // Solar Ascent Background
        StackTowerEnvironmentView(isBoss = currentLevel == StackTowerLevel.BOSS_BATTLE)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Exit", tint = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(text = "❤️ $hearts", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(text = "⭐ +$sessionXP", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(text = "💎 $gemsCount", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .pressScale()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFF9500).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFFFF9500).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { speed = speed.next() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(text = speed.label, fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF9500))
                    }

                    Box(
                        modifier = Modifier
                            .pressScale()
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700).copy(alpha = 0.2f))
                            .clickable { showHintSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = "Hint", tint = Color(0xFFFFD700), modifier = Modifier.size(15.dp))
                    }
                }
            }

            // Level Track
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StackTowerLevel.entries.forEach { lvl ->
                    val isUnlocked = unlockedLevels.contains(lvl.id)
                    val isSelected = currentLevel == lvl

                    Box(
                        modifier = Modifier
                            .pressScale()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    isSelected && lvl == StackTowerLevel.BOSS_BATTLE -> DuolingoRed
                                    isSelected -> Color(0xFFFF9500)
                                    isUnlocked -> Color(0xFF1E1710)
                                    else -> Color.Black.copy(alpha = 0.35f)
                                }
                            )
                            .border(1.dp, if (isSelected) Color.White else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                            .clickable(enabled = isUnlocked) { currentLevel = lvl }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = if (isUnlocked) lvl.iconEmoji else "🔒", fontSize = 12.sp)
                            Text(
                                text = "L${lvl.id}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.Black else (if (isUnlocked) Color.White else SubtextGray)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Level Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (currentLevel) {
                    StackTowerLevel.LEVEL_1 -> StackLevel1Content(onComplete = { handleLevelCompleted(StackTowerLevel.LEVEL_1) })
                    StackTowerLevel.LEVEL_2 -> StackLevel2Content(onComplete = { handleLevelCompleted(StackTowerLevel.LEVEL_2) })
                    StackTowerLevel.LEVEL_3 -> StackLevel3Content(onComplete = { handleLevelCompleted(StackTowerLevel.LEVEL_3) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    StackTowerLevel.LEVEL_4 -> StackLevel4Content(onComplete = { handleLevelCompleted(StackTowerLevel.LEVEL_4) })
                    StackTowerLevel.LEVEL_5 -> StackLevel5Content(onComplete = { handleLevelCompleted(StackTowerLevel.LEVEL_5) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    StackTowerLevel.LEVEL_6 -> StackLevel6Content(onComplete = { handleLevelCompleted(StackTowerLevel.LEVEL_6) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    StackTowerLevel.BOSS_BATTLE -> StackBossBattleContent(onDefeatBoss = { handleLevelCompleted(StackTowerLevel.BOSS_BATTLE) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                }
            }

            // Bottom Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp)
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .pressScale()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1E1710))
                        .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .clickable { showTheoryCodex = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(text = "🥞 LIFO Spire Codex", fontSize = 12.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .pressScale()
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E1710))
                            .clickable {
                                val current = currentLevel
                                currentLevel = StackTowerLevel.LEVEL_1
                                currentLevel = current
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Replay", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    Box(
                        modifier = Modifier
                            .pressScale()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable {
                                val nextId = minOf(7, currentLevel.id + 1)
                                unlockedLevels = unlockedLevels + nextId
                                currentLevel = StackTowerLevel.fromId(nextId)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(text = "Skip ⏭", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.8f))
                    }
                }
            }
        }

        // Modals
        if (showVictoryModal) {
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
                        .background(Color(0xFF1A1208))
                        .border(2.dp, Color(0xFFFF9500), RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "🥞 TOWER ELEVATED!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF9500))
                    Text(text = currentLevel.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                    Duolingo3DButton(
                        title = "ASCEND NEXT SPIRE ➔",
                        style = Duolingo3DButtonStyle.AMBER,
                        onClick = {
                            showVictoryModal = false
                            val nextId = minOf(7, currentLevel.id + 1)
                            currentLevel = StackTowerLevel.fromId(nextId)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showBossVictoryModal) {
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
                        .background(Color(0xFF1A1208))
                        .border(2.dp, DuolingoGreen, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "👑 SPIRE SAVED!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                    Text(text = "You mastered LIFO Push, Pop & Peek, stabilizing the Solar Tower!", fontSize = 13.sp, color = SubtextGray, textAlign = TextAlign.Center)
                    Duolingo3DButton(
                        title = "COMPLETE TOWER",
                        style = Duolingo3DButtonStyle.GREEN,
                        onClick = {
                            showBossVictoryModal = false
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showTheoryCodex) {
            ModalBottomSheet(
                onDismissRequest = { showTheoryCodex = false },
                containerColor = DarkBackground
            ) {
                Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "🥞 Stack Tower Codex (LIFO)", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF9500))
                    Text(
                        text = "A Stack is a Last-In, First-Out (LIFO) structure where elements are added and removed only from the top. All operations:\n- PUSH(item): O(1) time\n- POP(): O(1) time\n- PEEK(): O(1) time\n\nStacks power function call stacks, undo/redo mechanisms, syntax bracket validation, and DFS traversals.",
                        fontSize = 13.sp,
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        if (showHintSheet) {
            ModalBottomSheet(
                onDismissRequest = { showHintSheet = false },
                containerColor = DarkBackground
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "💡 Spire Hint: ${currentLevel.title}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF9500))
                    Text(
                        text = when (currentLevel) {
                            StackTowerLevel.LEVEL_1 -> "Stacks only allow access at the very top. Think of a stack of golden plates!"
                            StackTowerLevel.LEVEL_2 -> "PUSH drops a new block onto the top of the tower."
                            StackTowerLevel.LEVEL_3 -> "POP removes whatever block is currently at the TOP."
                            StackTowerLevel.LEVEL_4 -> "PEEK looks at the top element without removing it."
                            StackTowerLevel.LEVEL_5 -> "The LAST element pushed is always the FIRST element popped!"
                            StackTowerLevel.LEVEL_6 -> "Watch the command sequence closely and execute quickly."
                            StackTowerLevel.BOSS_BATTLE -> "Spam POP to get rid of incoming unstable blocks before height hits 6!"
                        },
                        fontSize = 13.sp,
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 🌌 Environment & Levels
// ══════════════════════════════════════════════════════════════════
@Composable
private fun StackTowerEnvironmentView(isBoss: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isBoss) {
                        listOf(Color(0xFF280B04), Color(0xFF140602), Color(0xFF0A0201))
                    } else {
                        listOf(Color(0xFF2B1603), Color(0xFF140A02), Color(0xFF0A0501))
                    }
                )
            )
    )
}

@Composable
private fun StackLevel1Content(onComplete: () -> Unit) {
    val stack = remember { mutableStateListOf<Int>() }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF221307))
                .border(1.dp, Color(0xFFFF9500).copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "🥞 The Stack Tower: Elements stack vertically. The TOP pointer points to the most recently added item!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LocalDynamicThemeColors.current.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        // Vertical Tower Box
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.5.dp, Color(0xFFFF9500).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                stack.reversed().forEachIndexed { revIdx, v ->
                    val isTop = revIdx == 0
                    Box(
                        modifier = Modifier
                            .size(110.dp, 36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isTop) Color(0xFFFF9500) else Color(0xFFB35900))
                            .border(1.dp, LocalDynamicThemeColors.current.textSecondary, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (isTop) "$v (TOP)" else "$v", fontSize = 14.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                }
            }
        }

        if (stack.size < 3) {
            Duolingo3DButton(
                title = "⬇️ PUSH Item (${(stack.size + 1) * 10})",
                style = Duolingo3DButtonStyle.AMBER,
                onClick = {
                    stack.add((stack.size + 1) * 10)
                    if (stack.size == 3) {
                        onComplete()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }
    }
}

@Composable
private fun StackLevel2Content(onComplete: () -> Unit) {
    val stack = remember { mutableStateListOf(10, 20) }
    var pushedCount by remember { mutableIntStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "PUSH drops a new block onto the top of the stack.", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)

        Box(
            modifier = Modifier
                .width(140.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.5.dp, Color(0xFFFF9500), RoundedCornerShape(16.dp))
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                stack.reversed().forEachIndexed { idx, v ->
                    Box(
                        modifier = Modifier
                            .size(110.dp, 36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (idx == 0) Color(0xFFFF9500) else Color(0xFFB35900)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$v", fontSize = 14.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                }
            }
        }

        Duolingo3DButton(
            title = "⬇️ PUSH(99)",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = {
                stack.add(99)
                pushedCount++
                onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}

@Composable
private fun StackLevel3Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    val stack = remember { mutableStateListOf(10, 20, 30, 40) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "POP removes the topmost element (40).", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)

        Box(
            modifier = Modifier
                .width(140.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.5.dp, Color(0xFFFF9500), RoundedCornerShape(16.dp))
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                stack.reversed().forEachIndexed { idx, v ->
                    Box(
                        modifier = Modifier
                            .size(110.dp, 36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (idx == 0) DuolingoRed else Color(0xFFB35900)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$v", fontSize = 14.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                }
            }
        }

        Duolingo3DButton(
            title = "⬆️ POP()",
            style = Duolingo3DButtonStyle.WHITE,
            onClick = {
                if (stack.isNotEmpty()) {
                    stack.removeAt(stack.size - 1)
                    onComplete()
                }
            },
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}

@Composable
private fun StackLevel4Content(onComplete: () -> Unit) {
    val stack = remember { listOf(15, 25, 35) }
    var peekedValue by remember { mutableStateOf<Int?>(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "PEEK inspects the TOP element without removing it!", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)

        Box(
            modifier = Modifier
                .width(140.dp)
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.5.dp, Color(0xFFFF9500), RoundedCornerShape(16.dp))
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                stack.reversed().forEachIndexed { idx, v ->
                    val isPeeked = idx == 0 && peekedValue != null
                    Box(
                        modifier = Modifier
                            .size(110.dp, 36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isPeeked) Color.Cyan else Color(0xFFB35900)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$v", fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (isPeeked) Color.Black else Color.White)
                    }
                }
            }
        }

        if (peekedValue == null) {
            Duolingo3DButton(
                title = "👁️ PEEK()",
                style = Duolingo3DButtonStyle.BLUE,
                onClick = {
                    peekedValue = 35
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        } else {
            Text(text = "Top item is $peekedValue (Stack size unchanged = ${stack.size})", fontSize = 13.sp, color = Color.Cyan, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StackLevel5Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var answered by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(
            text = "🔮 We pushed: [1] then [2] then [3].\nWhich element will POP() return?",
            fontSize = 15.sp,
            color = LocalDynamicThemeColors.current.textPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf(1, 2, 3).forEach { opt ->
                Box(
                    modifier = Modifier
                        .size(64.dp, 54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF221307))
                        .border(1.5.dp, Color(0xFFFF9500), RoundedCornerShape(14.dp))
                        .clickable(enabled = !answered) {
                            if (opt == 3) {
                                answered = true
                                onComplete()
                            } else {
                                onDeductHeart()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$opt", fontSize = 20.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                }
            }
        }
    }
}

@Composable
private fun StackLevel6Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val requiredOps = listOf("PUSH", "PUSH", "POP")

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "⚡ Execute the sequence: PUSH ➔ PUSH ➔ POP", fontSize = 14.sp, color = AmberGold, fontWeight = FontWeight.Bold)

        Text(text = "Step ${step + 1} of 3: Need ${requiredOps.getOrNull(step) ?: "DONE"}", fontSize = 13.sp, color = LocalDynamicThemeColors.current.textPrimary)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Duolingo3DButton(
                title = "⬇️ PUSH",
                style = Duolingo3DButtonStyle.AMBER,
                onClick = {
                    if (requiredOps.getOrNull(step) == "PUSH") {
                        step++
                        if (step == 3) onComplete()
                    } else {
                        onDeductHeart()
                    }
                },
                modifier = Modifier.width(130.dp)
            )

            Duolingo3DButton(
                title = "⬆️ POP",
                style = Duolingo3DButtonStyle.WHITE,
                onClick = {
                    if (requiredOps.getOrNull(step) == "POP") {
                        step++
                        if (step == 3) onComplete()
                    } else {
                        onDeductHeart()
                    }
                },
                modifier = Modifier.width(130.dp)
            )
        }
    }
}

@Composable
private fun StackBossBattleContent(onDefeatBoss: () -> Unit, onDeductHeart: () -> Unit) {
    val tower = remember { mutableStateListOf(50, 40, 30, 20) }
    var bossHP by remember { mutableIntStateOf(100) }

    LaunchedEffect(Unit) {
        while (bossHP > 0 && tower.size < 6) {
            delay(1500)
            tower.add(Random.nextInt(60, 99))
            if (tower.size >= 6) {
                onDeductHeart()
                tower.clear()
                tower.addAll(listOf(50, 40, 30))
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "👾 TOWER OVERFLOW BOSS! (${bossHP} HP)", fontSize = 16.sp, color = DuolingoRed, fontWeight = FontWeight.Black)

        // Height warning
        Text(text = "Tower Height: ${tower.size}/6 (Don't let it overflow!)", fontSize = 12.sp, color = if (tower.size >= 5) DuolingoRed else AmberGold)

        Box(
            modifier = Modifier
                .width(140.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.5.dp, if (tower.size >= 5) DuolingoRed else Color(0xFFFF9500), RoundedCornerShape(16.dp))
                .padding(bottom = 6.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                tower.reversed().forEach { v ->
                    Box(
                        modifier = Modifier
                            .size(110.dp, 22.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFF5252)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$v", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                }
            }
        }

        Duolingo3DButton(
            title = "⬆️ QUICK POP TO STABILIZE!",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = {
                if (tower.isNotEmpty()) {
                    tower.removeAt(tower.size - 1)
                    bossHP = maxOf(0, bossHP - 20)
                    if (bossHP <= 0) {
                        onDefeatBoss()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}
