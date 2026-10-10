package com.simats.codeingo.ui.worlds.linkedlist

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonStyle
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.worlds.array.KingdomGameSpeed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkedListRoadArenaScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val gemsCount by gameManager.gemsCount.collectAsState()

    var currentLevel by remember { mutableStateOf(LinkedListRoadLevel.LEVEL_1) }
    var unlockedLevels by remember { mutableStateOf(setOf(1)) }
    var levelStars by remember { mutableStateOf(mutableMapOf(1 to 3)) }

    var hearts by remember { mutableIntStateOf(5) }
    var sessionXP by remember { mutableIntStateOf(0) }
    var speed by remember { mutableStateOf(KingdomGameSpeed.NORMAL) }

    var showTheoryCodex by remember { mutableStateOf(false) }
    var showHintSheet by remember { mutableStateOf(false) }
    var showVictoryModal by remember { mutableStateOf(false) }
    var showBossVictoryModal by remember { mutableStateOf(false) }

    fun handleLevelCompleted(lvl: LinkedListRoadLevel) {
        val nextId = lvl.id + 1
        unlockedLevels = unlockedLevels + nextId
        levelStars[lvl.id] = 3
        sessionXP += lvl.xpReward
        gameManager.addXP(lvl.xpReward)
        gameManager.addGems(lvl.coinReward)

        if (lvl == LinkedListRoadLevel.BOSS_BATTLE) {
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
        // Crimson Pointer Road Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2E0A0E), Color(0xFF170407), Color(0xFF0A0203))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top HUD
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
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.45f)).border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                        Text(text = "❤️ $hearts", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.45f)).border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                        Text(text = "⭐ +$sessionXP", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.45f)).border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                        Text(text = "💎 $gemsCount", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .pressScale()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { speed = speed.next() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(text = speed.label, fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
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
                LinkedListRoadLevel.entries.forEach { lvl ->
                    val isUnlocked = unlockedLevels.contains(lvl.id)
                    val isSelected = currentLevel == lvl

                    Box(
                        modifier = Modifier
                            .pressScale()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    isSelected && lvl == LinkedListRoadLevel.BOSS_BATTLE -> DuolingoRed
                                    isSelected -> Color(0xFFEF4444)
                                    isUnlocked -> Color(0xFF260D12)
                                    else -> Color.Black.copy(alpha = 0.35f)
                                }
                            )
                            .border(1.dp, if (isSelected) Color.White else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                            .clickable(enabled = isUnlocked) { currentLevel = lvl }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = if (isUnlocked) lvl.iconEmoji else "🔒", fontSize = 12.sp)
                            Text(text = "L${lvl.id}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (isSelected) Color.Black else Color.White)
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
                    LinkedListRoadLevel.LEVEL_1 -> LLLevel1Content(onComplete = { handleLevelCompleted(LinkedListRoadLevel.LEVEL_1) })
                    LinkedListRoadLevel.LEVEL_2 -> LLLevel2Content(onComplete = { handleLevelCompleted(LinkedListRoadLevel.LEVEL_2) })
                    LinkedListRoadLevel.LEVEL_3 -> LLLevel3Content(onComplete = { handleLevelCompleted(LinkedListRoadLevel.LEVEL_3) })
                    LinkedListRoadLevel.LEVEL_4 -> LLLevel4Content(onComplete = { handleLevelCompleted(LinkedListRoadLevel.LEVEL_4) })
                    LinkedListRoadLevel.LEVEL_5 -> LLLevel5Content(onComplete = { handleLevelCompleted(LinkedListRoadLevel.LEVEL_5) })
                    LinkedListRoadLevel.LEVEL_6 -> LLLevel6Content(onComplete = { handleLevelCompleted(LinkedListRoadLevel.LEVEL_6) })
                    LinkedListRoadLevel.BOSS_BATTLE -> LLBossBattleContent(onDefeatBoss = { handleLevelCompleted(LinkedListRoadLevel.BOSS_BATTLE) })
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
                        .background(Color(0xFF260D12))
                        .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .clickable { showTheoryCodex = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(text = "🔗 Pointer Chain Codex", fontSize = 12.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .pressScale()
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF260D12))
                            .clickable {
                                val current = currentLevel
                                currentLevel = LinkedListRoadLevel.LEVEL_1
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
                                currentLevel = LinkedListRoadLevel.fromId(nextId)
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
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(24.dp)).background(Color(0xFF240A0F)).border(2.dp, Color(0xFFEF4444), RoundedCornerShape(24.dp)).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "🔗 POINTERS LINKED!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
                    Text(text = currentLevel.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                    Duolingo3DButton(
                        title = "NEXT ROAD ➔",
                        style = Duolingo3DButtonStyle.GREEN,
                        onClick = {
                            showVictoryModal = false
                            val nextId = minOf(7, currentLevel.id + 1)
                            currentLevel = LinkedListRoadLevel.fromId(nextId)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showBossVictoryModal) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(24.dp)).background(Color(0xFF240A0F)).border(2.dp, DuolingoGreen, RoundedCornerShape(24.dp)).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "👑 POINTER BRIDGE RESTORED!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                    Text(text = "You reconnected the severed pointer bridge and mastered Linked Lists!", fontSize = 13.sp, color = SubtextGray, textAlign = TextAlign.Center)
                    Duolingo3DButton(
                        title = "COMPLETE ROAD",
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
            ModalBottomSheet(onDismissRequest = { showTheoryCodex = false }, containerColor = DarkBackground) {
                Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "🔗 Linked List Codex (Pointers)", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
                    Text(
                        text = "A Linked List consists of separate Node objects allocated dynamically in heap memory. Each node has:\n1. data (value)\n2. next (memory pointer to the next node)\n\nAdvantages:\n- Dynamic resizing without memory relocation.\n- O(1) insertion/deletion at HEAD.\n\nDisadvantages:\n- O(n) access time (cannot index directly like array[i]).",
                        fontSize = 13.sp,
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        if (showHintSheet) {
            ModalBottomSheet(onDismissRequest = { showHintSheet = false }, containerColor = DarkBackground) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "💡 Road Hint: ${currentLevel.title}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
                    Text(
                        text = when (currentLevel) {
                            LinkedListRoadLevel.LEVEL_1 -> "A node has two parts: the data payload and the next reference pointer."
                            LinkedListRoadLevel.LEVEL_2 -> "Follow each pointer from HEAD until you reach null!"
                            LinkedListRoadLevel.LEVEL_3 -> "Link node A to node B by setting A.next = B."
                            LinkedListRoadLevel.LEVEL_4 -> "To insert in between, connect new.next to right, then left.next to new!"
                            LinkedListRoadLevel.LEVEL_5 -> "To delete, point around the node: prev.next = curr.next."
                            LinkedListRoadLevel.LEVEL_6 -> "Jump along the pointers to reach the destination."
                            LinkedListRoadLevel.BOSS_BATTLE -> "Tap the broken pointer to bridge the gap and defeat the boss!"
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
// 🔗 Levels Content
// ══════════════════════════════════════════════════════════════════
@Composable
private fun LLLevel1Content(onComplete: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF2B0E14)).border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(16.dp)
        ) {
            Text(
                text = "🔗 Meet the Node: A memory container with [ DATA | NEXT POINTER ➔ ].",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LocalDynamicThemeColors.current.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        // 3D Split Node
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E0A0E))
                .border(2.dp, Color(0xFFEF4444), RoundedCornerShape(16.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(60.dp).background(Color(0xFFEF4444).copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "42", fontSize = 20.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
            }
            Box(
                modifier = Modifier.size(50.dp, 60.dp).background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "• ➔", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
            }
        }

        Duolingo3DButton(
            title = "UNDERSTOOD ➔",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}

@Composable
private fun LLLevel2Content(onComplete: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val chain = listOf(10, 20, 30)

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "Follow the Chain: Traversing step $step of 3", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            chain.forEachIndexed { idx, v ->
                val isCurrent = idx == step
                Box(
                    modifier = Modifier
                        .size(54.dp, 50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCurrent) Color(0xFFEF4444) else Color(0xFF330F16)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$v", fontSize = 16.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                }
                if (idx < chain.size - 1) {
                    Text(text = "➔", fontSize = 14.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Black)
                }
            }
            Text(text = "➔ null", fontSize = 12.sp, color = SubtextGray)
        }

        Duolingo3DButton(
            title = "👉 STEP TO NEXT (curr = curr.next)",
            style = Duolingo3DButtonStyle.AMBER,
            onClick = {
                step++
                if (step == 3) onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}

@Composable
private fun LLLevel3Content(onComplete: () -> Unit) {
    var isLinked by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "Build the Chain: Connect Node [A] to Node [B]", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFEF4444)), contentAlignment = Alignment.Center) {
                Text(text = "A (10)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
            }

            Text(text = if (isLinked) "════➔" else "- - - ❓", fontSize = 14.sp, color = if (isLinked) DuolingoGreen else Color.White)

            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFEF4444)), contentAlignment = Alignment.Center) {
                Text(text = "B (20)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
            }
        }

        if (!isLinked) {
            Duolingo3DButton(
                title = "🔗 A.next = B",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = {
                    isLinked = true
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }
    }
}

@Composable
private fun LLLevel4Content(onComplete: () -> Unit) {
    var isInserted by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "Insert Node [15] between [10] and [20].", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "[10]", color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)
            Text(text = "➔", color = Color(0xFFEF4444))
            if (isInserted) {
                Text(text = "[15]", color = DuolingoGreen, fontWeight = FontWeight.Black)
                Text(text = "➔", color = Color(0xFFEF4444))
            }
            Text(text = "[20]", color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)
        }

        if (!isInserted) {
            Duolingo3DButton(
                title = "➕ INSERT NODE [15]",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = {
                    isInserted = true
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(0.75f)
            )
        }
    }
}

@Composable
private fun LLLevel5Content(onComplete: () -> Unit) {
    var isDeleted by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "Delete Node [20]: Bypass with 10.next = 30.", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "[10]", color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)
            Text(text = "➔", color = Color(0xFFEF4444))
            if (!isDeleted) {
                Text(text = "[20]", color = DuolingoRed, fontWeight = FontWeight.Black)
                Text(text = "➔", color = Color(0xFFEF4444))
            }
            Text(text = "[30]", color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)
        }

        if (!isDeleted) {
            Duolingo3DButton(
                title = "✂️ BYPASS & DELETE [20]",
                style = Duolingo3DButtonStyle.WHITE,
                onClick = {
                    isDeleted = true
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(0.75f)
            )
        }
    }
}

@Composable
private fun LLLevel6Content(onComplete: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "⚡ Pointer Sprint: Advance pointer 3 times!", fontSize = 14.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)

        Duolingo3DButton(
            title = "👉 JUMP POINTER (${step}/3)",
            style = Duolingo3DButtonStyle.AMBER,
            onClick = {
                step++
                if (step == 3) onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.75f)
        )
    }
}

@Composable
private fun LLBossBattleContent(onDefeatBoss: () -> Unit) {
    var bossHP by remember { mutableIntStateOf(100) }
    var bridgeBroken by remember { mutableStateOf(true) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "👾 THE BROKEN CHAIN BOSS! (${bossHP} HP)", fontSize = 16.sp, color = DuolingoRed, fontWeight = FontWeight.Black)

        Text(
            text = if (bridgeBroken) "⚠️ SEVERED POINTER DETECTED! Reconnect it!" else "✨ Bridge intact! Dealing critical damage!",
            fontSize = 13.sp,
            color = if (bridgeBroken) DuolingoRed else DuolingoGreen
        )

        Duolingo3DButton(
            title = "🔗 RECONNECT POINTER!",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = {
                bossHP = maxOf(0, bossHP - 35)
                bridgeBroken = false
                if (bossHP <= 0) onDefeatBoss()
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}
