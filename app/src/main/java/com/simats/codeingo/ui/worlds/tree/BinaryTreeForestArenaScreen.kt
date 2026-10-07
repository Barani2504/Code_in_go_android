package com.simats.codeingo.ui.worlds.tree

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
import com.simats.codeingo.ui.worlds.array.KingdomGameSpeed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinaryTreeForestArenaScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val gemsCount by gameManager.gemsCount.collectAsState()

    var currentLevel by remember { mutableStateOf(BinaryTreeForestLevel.LEVEL_1) }
    var unlockedLevels by remember { mutableStateOf(setOf(1)) }
    var levelStars by remember { mutableStateOf(mutableMapOf(1 to 3)) }

    var hearts by remember { mutableIntStateOf(5) }
    var sessionXP by remember { mutableIntStateOf(0) }
    var speed by remember { mutableStateOf(KingdomGameSpeed.NORMAL) }

    var showTheoryCodex by remember { mutableStateOf(false) }
    var showHintSheet by remember { mutableStateOf(false) }
    var showVictoryModal by remember { mutableStateOf(false) }
    var showBossVictoryModal by remember { mutableStateOf(false) }

    fun handleLevelCompleted(lvl: BinaryTreeForestLevel) {
        val nextId = lvl.id + 1
        unlockedLevels = unlockedLevels + nextId
        levelStars[lvl.id] = 3
        sessionXP += lvl.xpReward
        gameManager.addXP(lvl.xpReward)
        gameManager.addGems(lvl.coinReward)

        if (lvl == BinaryTreeForestLevel.BOSS_BATTLE) {
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
        // Emerald Forest Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF072418), Color(0xFF04140D), Color(0xFF020A06))
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
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { speed = speed.next() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(text = speed.label, fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                    }

                    Box(
                        modifier = Modifier
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
                BinaryTreeForestLevel.entries.forEach { lvl ->
                    val isUnlocked = unlockedLevels.contains(lvl.id)
                    val isSelected = currentLevel == lvl

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    isSelected && lvl == BinaryTreeForestLevel.BOSS_BATTLE -> DuolingoRed
                                    isSelected -> Color(0xFF10B981)
                                    isUnlocked -> Color(0xFF0C2B1D)
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
                    BinaryTreeForestLevel.LEVEL_1 -> TreeLevel1Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_1) })
                    BinaryTreeForestLevel.LEVEL_2 -> TreeLevel2Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_2) })
                    BinaryTreeForestLevel.LEVEL_3 -> TreeLevel3Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_3) })
                    BinaryTreeForestLevel.LEVEL_4 -> TreeLevel4Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_4) })
                    BinaryTreeForestLevel.LEVEL_5 -> TreeLevel5Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_5) })
                    BinaryTreeForestLevel.LEVEL_6 -> TreeLevel6Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_6) })
                    BinaryTreeForestLevel.BOSS_BATTLE -> TreeBossBattleContent(onDefeatBoss = { handleLevelCompleted(BinaryTreeForestLevel.BOSS_BATTLE) })
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
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0C2B1D))
                        .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .clickable { showTheoryCodex = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(text = "🌲 Binary Tree Codex", fontSize = 12.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0C2B1D))
                            .clickable {
                                val current = currentLevel
                                currentLevel = BinaryTreeForestLevel.LEVEL_1
                                currentLevel = current
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Replay", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable {
                                val nextId = minOf(7, currentLevel.id + 1)
                                unlockedLevels = unlockedLevels + nextId
                                currentLevel = BinaryTreeForestLevel.fromId(nextId)
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
                    modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(24.dp)).background(Color(0xFF0A2417)).border(2.dp, Color(0xFF10B981), RoundedCornerShape(24.dp)).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "🌲 CANOPY ASCENDED!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                    Text(text = currentLevel.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                    Duolingo3DButton(
                        title = "EXPLORE NEXT CANOPY ➔",
                        style = Duolingo3DButtonStyle.GREEN,
                        onClick = {
                            showVictoryModal = false
                            val nextId = minOf(7, currentLevel.id + 1)
                            currentLevel = BinaryTreeForestLevel.fromId(nextId)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showBossVictoryModal) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(24.dp)).background(Color(0xFF0A2417)).border(2.dp, DuolingoGreen, RoundedCornerShape(24.dp)).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "👑 ANCIENT ENT BALANCED!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                    Text(text = "You restored balance to the Binary Tree Forest and mastered BSTs!", fontSize = 13.sp, color = SubtextGray, textAlign = TextAlign.Center)
                    Duolingo3DButton(
                        title = "COMPLETE FOREST",
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
            ModalBottomSheet(onDismissRequest = { showTheoryCodex = false }, containerColor = Color(0xFF0A2417)) {
                Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "🌲 Binary Tree Codex", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                    Text(
                        text = "A Binary Tree is a hierarchical structure where each node has at most two children (left & right).\n\nBinary Search Tree (BST) Invariant:\n- Left subtree values < Root value\n- Right subtree values > Root value\n\nSearch / Insert in a balanced BST takes O(log n) time. In-order traversal (Left ➔ Root ➔ Right) yields elements in strictly sorted order!",
                        fontSize = 13.sp,
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        if (showHintSheet) {
            ModalBottomSheet(onDismissRequest = { showHintSheet = false }, containerColor = Color(0xFF0A2417)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "💡 Forest Hint: ${currentLevel.title}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                    Text(
                        text = when (currentLevel) {
                            BinaryTreeForestLevel.LEVEL_1 -> "The ROOT is the very top node from which all branches descend."
                            BinaryTreeForestLevel.LEVEL_2 -> "Children branch off downward into left and right subtrees."
                            BinaryTreeForestLevel.LEVEL_3 -> "Leaves are nodes at the bottom that have no children!"
                            BinaryTreeForestLevel.LEVEL_4 -> "BST rule: smaller goes left, larger goes right."
                            BinaryTreeForestLevel.LEVEL_5 -> "Compare 25 to 50 (go left), then to 30 (place left)."
                            BinaryTreeForestLevel.LEVEL_6 -> "In-order traversal always produces sorted ascending values."
                            BinaryTreeForestLevel.BOSS_BATTLE -> "Restore tree balance to defeat the Overgrown Ent!"
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
// 🌲 Forest Levels Content
// ══════════════════════════════════════════════════════════════════
@Composable
private fun TreeLevel1Content(onComplete: () -> Unit) {
    var selectedRoot by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF0D2E1F)).border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(16.dp)
        ) {
            Text(
                text = "👑 Tap the ROOT node at the top of the canopy!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LocalDynamicThemeColors.current.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        // Tree structure: Root (50) with left (30) and right (70)
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Root
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (selectedRoot) DuolingoGreen else Color(0xFF10B981))
                    .border(2.dp, Color.White, CircleShape)
                    .clickable {
                        selectedRoot = true
                        onComplete()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "50", fontSize = 16.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
            }

            // Branches
            Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                Text(text = "↙", fontSize = 20.sp, color = Color(0xFF10B981))
                Text(text = "↘", fontSize = 20.sp, color = Color(0xFF10B981))
            }

            // Children
            Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF0C3824)), contentAlignment = Alignment.Center) {
                    Text(text = "30", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                }
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF0C3824)), contentAlignment = Alignment.Center) {
                    Text(text = "70", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                }
            }
        }
    }
}

@Composable
private fun TreeLevel2Content(onComplete: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "Parent & Child: Node 50 is parent to Left (30) & Right (70).", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Duolingo3DButton(
            title = "UNDERSTOOD ➔",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}

@Composable
private fun TreeLevel3Content(onComplete: () -> Unit) {
    var leavesTapped by remember { mutableIntStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "🍃 Tap both LEAVES (nodes with zero children) ($leavesTapped/2):", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
                    .clickable {
                        leavesTapped++
                        if (leavesTapped >= 2) onComplete()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "10 🍃", fontSize = 12.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
            }

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
                    .clickable {
                        leavesTapped++
                        if (leavesTapped >= 2) onComplete()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "90 🍃", fontSize = 12.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
            }
        }
    }
}

@Composable
private fun TreeLevel4Content(onComplete: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "⚖️ BST Rule: Left < Root < Right.\nFor root 50, which value belongs in the left subtree?", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf(25, 75).forEach { opt ->
                Box(
                    modifier = Modifier
                        .size(70.dp, 50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0C3824))
                        .border(1.5.dp, Color(0xFF10B981), RoundedCornerShape(14.dp))
                        .clickable {
                            if (opt == 25) onComplete()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$opt", fontSize = 18.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                }
            }
        }
    }
}

@Composable
private fun TreeLevel5Content(onComplete: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "🌱 Insert 40 into the BST:", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)
        Text(text = "Root is 50. Since 40 < 50, go left to node 30. Since 40 > 30, place at right of 30.", fontSize = 13.sp, color = SubtextGray, textAlign = TextAlign.Center)

        Duolingo3DButton(
            title = "INSERT 40 AT 30.RIGHT",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}

@Composable
private fun TreeLevel6Content(onComplete: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "📜 In-Order Traversal:\nVisiting [Left ➔ Root ➔ Right] produces: 10, 20, 30, 40, 50!", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Duolingo3DButton(
            title = "EXECUTE IN-ORDER TRAVERSAL",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}

@Composable
private fun TreeBossBattleContent(onDefeatBoss: () -> Unit) {
    var bossHP by remember { mutableIntStateOf(100) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "👾 THE OVERGROWN ENT BOSS! (${bossHP} HP)", fontSize = 16.sp, color = DuolingoRed, fontWeight = FontWeight.Black)

        Duolingo3DButton(
            title = "🌲 RESTORE CANOPY BALANCE!",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = {
                bossHP = maxOf(0, bossHP - 35)
                if (bossHP <= 0) onDefeatBoss()
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}
