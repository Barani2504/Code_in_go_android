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
import androidx.compose.ui.text.font.FontFamily
import com.simats.codeingo.ui.theme.AmberGold
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
                    LinkedListRoadLevel.LEVEL_1 -> LLLevel1Content(onComplete = { handleLevelCompleted(LinkedListRoadLevel.LEVEL_1) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    LinkedListRoadLevel.LEVEL_2 -> LLLevel2Content(onComplete = { handleLevelCompleted(LinkedListRoadLevel.LEVEL_2) })
                    LinkedListRoadLevel.LEVEL_3 -> LLLevel3Content(onComplete = { handleLevelCompleted(LinkedListRoadLevel.LEVEL_3) })
                    LinkedListRoadLevel.LEVEL_4 -> LLLevel4Content(onComplete = { handleLevelCompleted(LinkedListRoadLevel.LEVEL_4) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    LinkedListRoadLevel.LEVEL_5 -> LLLevel5Content(onComplete = { handleLevelCompleted(LinkedListRoadLevel.LEVEL_5) })
                    LinkedListRoadLevel.LEVEL_6 -> LLLevel6Content(onComplete = { handleLevelCompleted(LinkedListRoadLevel.LEVEL_6) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    LinkedListRoadLevel.BOSS_BATTLE -> LLBossBattleContent(onDefeatBoss = { handleLevelCompleted(LinkedListRoadLevel.BOSS_BATTLE) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
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
                    Text(text = "🧱 S-Box Citadel Codex", fontSize = 12.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
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
                    Text(text = "🧱 S-BOX ROUND SECURED!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
                    Text(text = currentLevel.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                    Duolingo3DButton(
                        title = "NEXT STAGE ➔",
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
                    Text(text = "👑 S-BOX CITADEL LIBERATED!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                    Text(text = "You mastered AES SubBytes, Feistel round swaps, and vanquished the Block Overlord in the S-Box Citadel!", fontSize = 13.sp, color = SubtextGray, textAlign = TextAlign.Center)
                    Duolingo3DButton(
                        title = "COMPLETE CITADEL",
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
                    Text(text = "🧱 S-Box Citadel Codex (Symmetric Block Ciphers)", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
                    Text(
                        text = "Symmetric Block Ciphers encrypt fixed-size data blocks (such as 64-bit in DES and 128-bit in AES) using identical keys for encryption and decryption.\n\nCore Transformations:\n1. SubBytes: Non-linear byte substitution using the AES S-Box in GF(2^8) providing confusion.\n2. ShiftRows & MixColumns: Byte permutation and Galois Field polynomial multiplication providing diffusion.\n3. AddRoundKey: XORing the state matrix with the round subkey.\n\nFeistel Networks (DES) divide plaintext into Left and Right 32-bit halves, applying round function F: L_{i+1} = R_i, R_{i+1} = L_i ⊕ F(R_i, K_i).",
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
                    Text(text = "💡 Citadel Hint: ${currentLevel.title}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
                    Text(
                        text = when (currentLevel) {
                                            LinkedListRoadLevel.LEVEL_1 -> "The non-linear S-Box replaces each input byte to resist linear cryptanalysis."
                LinkedListRoadLevel.LEVEL_2 -> "ShiftRows cyclically shifts row 1 by 1 byte, row 2 by 2 bytes, and row 3 by 3 bytes."
                LinkedListRoadLevel.LEVEL_3 -> "MixColumns mixes column bytes using matrix multiplication in GF(2^8) to achieve diffusion."
                LinkedListRoadLevel.LEVEL_4 -> "AddRoundKey combines each byte of the state with a subkey using bitwise XOR."
                LinkedListRoadLevel.LEVEL_5 -> "Feistel swaps: Left half becomes next Right, while Right half XORed with F becomes next Left."
                LinkedListRoadLevel.LEVEL_6 -> "A single bit change cascades into widespread diffusion: the avalanche effect!"
                LinkedListRoadLevel.BOSS_BATTLE -> "Overcome the non-linear substitution locks to bring down the Block Overlord!"
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
// 🧱 S-Box Citadel Levels Content
// ══════════════════════════════════════════════════════════════════
// Level Content Composables
// ══════════════════════════════════════════════════════════════════
@Composable
private fun LLLevel1Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    val candidates = listOf("0xED", "0x53", "0x63", "0x7F")
    var selectedByte by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF2B0E14)).border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(16.dp)
        ) {
            Text(
                text = "🧱 SubBytes Transformation: In the AES S-Box lookup table, input byte 0x53 maps to its multiplicative inverse in GF(2^8) followed by affine mapping. Select the output byte!",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = LocalDynamicThemeColors.current.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        if (feedback != null) {
            Text(text = feedback!!, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedByte == "0xED") DuolingoGreen else DuolingoRed)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            candidates.forEach { byteStr ->
                val isCorrect = byteStr == "0xED"
                val isSelected = selectedByte == byteStr
                Box(
                    modifier = Modifier
                        .size(68.dp, 56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF1E0A0E))
                        .border(1.5.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                        .clickable {
                            selectedByte = byteStr
                            if (isCorrect) {
                                feedback = "✓ S-Box(0x53) = 0xED! Non-linear confusion achieved!"
                                onComplete()
                            } else {
                                feedback = "❌ Incorrect S-box lookup. Try 0xED!"
                                onDeductHeart()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = byteStr, fontSize = 15.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun LLLevel2Content(onComplete: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val rowShifts = listOf("Row 0: Shift 0 bytes (no shift)", "Row 1: Cyclically shift 1 byte left", "Row 2: Cyclically shift 2 bytes left", "Row 3: Cyclically shift 3 bytes left")

    val matrixRows = listOf(
        listOf("s0,0", "s0,1", "s0,2", "s0,3"),
        listOf(if (step >= 1) "s1,1" else "s1,0", if (step >= 1) "s1,2" else "s1,1", if (step >= 1) "s1,3" else "s1,2", if (step >= 1) "s1,0" else "s1,3"),
        listOf(if (step >= 2) "s2,2" else "s2,0", if (step >= 2) "s2,3" else "s2,1", if (step >= 2) "s2,0" else "s2,2", if (step >= 2) "s2,1" else "s2,3"),
        listOf(if (step >= 3) "s3,3" else "s3,0", if (step >= 3) "s3,0" else "s3,1", if (step >= 3) "s3,1" else "s3,2", if (step >= 3) "s3,2" else "s3,3")
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "ShiftRows Permutation: Cyclically shifting 4x4 state matrix (${step}/4)", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold)

        // 4x4 Grid preview
        Column(
            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xFF2B0E14)).padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            matrixRows.forEachIndexed { r, row ->
                val isCurrentShift = step == r
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { cell ->
                        Box(
                            modifier = Modifier.size(52.dp, 36.dp).clip(RoundedCornerShape(6.dp))
                                .background(if (isCurrentShift) Color(0xFFEF4444).copy(alpha = 0.4f) else Color(0xFF1E0A0E))
                                .border(1.dp, if (isCurrentShift) Color(0xFFEF4444) else Color.White.copy(alpha = 0.1f), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = cell, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color.White)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(0.9f).clip(RoundedCornerShape(10.dp)).background(Color(0xFF330F16)).padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = rowShifts[step.coerceIn(0, 3)], fontSize = 13.sp, fontWeight = FontWeight.Black, color = AmberGold, fontFamily = FontFamily.Monospace)
        }

        Duolingo3DButton(
            title = if (step < 3) "🔄 EXECUTE ROW SHIFT (${step + 1}/4)" else "SHIFTROWS COMPLETE ✓",
            style = Duolingo3DButtonStyle.AMBER,
            onClick = {
                step++
                if (step >= 4) onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}

@Composable
private fun LLLevel3Content(onComplete: () -> Unit) {
    var isDiffused by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(text = "MixColumns Diffusion: Polynomial multiplication modulo x^4 + 1 in GF(2^8)", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "c(x) MATRIX", fontSize = 10.sp, color = AmberGold)
                Box(modifier = Modifier.size(70.dp, 60.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF2B0E14)), contentAlignment = Alignment.Center) {
                    Text(text = "[ 02 03 ]\n[ 01 01 ]", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }

            Text(text = "×", fontSize = 20.sp, color = AmberGold)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "INPUT COL", fontSize = 10.sp, color = Color(0xFFEF4444))
                Box(modifier = Modifier.size(54.dp, 60.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFEF4444)), contentAlignment = Alignment.Center) {
                    Text(text = "D4\nBF\n5D\n30", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }

            Text(text = "=", fontSize = 20.sp, color = Color.White)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "DIFFUSED", fontSize = 10.sp, color = DuolingoGreen)
                Box(modifier = Modifier.size(54.dp, 60.dp).clip(RoundedCornerShape(8.dp)).background(if (isDiffused) DuolingoGreen else Color(0xFF330F16)), contentAlignment = Alignment.Center) {
                    Text(text = if (isDiffused) "04\n66\n81\nE5" else "??\n??\n??\n??", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }
        }

        if (!isDiffused) {
            Duolingo3DButton(
                title = "⚡ MULTIPLY GF(2^8) MATRIX",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = {
                    isDiffused = true
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}

@Composable
private fun LLLevel4Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    val options = listOf("0x7F", "0x3F", "0x2B", "0x80")

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(text = "AddRoundKey: Compute bitwise XOR between State Byte and Round Key Byte", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "0x54 (01010100)", color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text(text = "⊕", color = Color(0xFFEF4444), fontSize = 18.sp)
            Text(text = "0x2B (00101011)", color = AmberGold, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { opt ->
                val isCorrect = opt == "0x7F"
                val isSelected = selectedOption == opt
                Box(
                    modifier = Modifier
                        .size(68.dp, 50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF2B0E14))
                        .border(1.5.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                        .clickable {
                            selectedOption = opt
                            if (isCorrect) onComplete() else onDeductHeart()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = opt, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun LLLevel5Content(onComplete: () -> Unit) {
    var isSwapped by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "16-Round Feistel Swap: L[i+1] = R[i] and R[i+1] = L[i] ⊕ F(R[i], K[i])", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(70.dp, 48.dp).clip(RoundedCornerShape(10.dp)).background(if (isSwapped) DuolingoGreen else Color(0xFFEF4444)), contentAlignment = Alignment.Center) {
                Text(text = if (isSwapped) "R[i]" else "L[i]", fontWeight = FontWeight.Black, color = Color.White)
            }
            Text(text = "⇄", fontSize = 24.sp, color = AmberGold)
            Box(modifier = Modifier.size(70.dp, 48.dp).clip(RoundedCornerShape(10.dp)).background(if (isSwapped) AmberGold else Color(0xFF330F16)), contentAlignment = Alignment.Center) {
                Text(text = if (isSwapped) "L[i]⊕F" else "R[i]", fontWeight = FontWeight.Black, color = Color.White)
            }
        }

        if (!isSwapped) {
            Duolingo3DButton(
                title = "🔄 SWAP FEISTEL HALVES",
                style = Duolingo3DButtonStyle.WHITE,
                onClick = {
                    isSwapped = true
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(0.75f)
            )
        }
    }
}

@Composable
private fun LLLevel6Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var selectedRound by remember { mutableStateOf<String?>(null) }
    val options = listOf("Round 1", "Round 2-3", "Round 10", "Never")

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(text = "⚡ Avalanche Sprint: Flipping 1 bit in the 128-bit input propagates to flip ~50% of the entire state matrix by which AES round?", fontSize = 13.5.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { opt ->
                val isCorrect = opt == "Round 2-3"
                val isSelected = selectedRound == opt
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF2B0E14))
                        .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(10.dp))
                        .clickable {
                            selectedRound = opt
                            if (isCorrect) onComplete() else onDeductHeart()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = opt, fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun LLBossBattleContent(onDefeatBoss: () -> Unit, onDeductHeart: () -> Unit) {
    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableIntStateOf(1) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("The Block Overlord locks down the AES Citadel! Break its cipher defenses!") }

    val phaseData = when (currentPhase) {
        1 -> Triple("SubBytes Defense: What is the S-Box value for input byte 0x00?", "0x63", listOf("0x63", "0x00", "0xFF", "0x53"))
        2 -> Triple("MixColumns Diffusion: In which round of AES-128 is MixColumns OMITTED?", "Round 10", listOf("Round 1", "Round 5", "Round 10", "Never"))
        else -> Triple("Key Expansion: AES-128 expands its 16-byte key into how many 32-bit round words?", "44 words", listOf("44 words", "40 words", "16 words", "64 words"))
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "👾 THE BLOCK OVERLORD", fontSize = 16.sp, color = DuolingoRed, fontWeight = FontWeight.Black)
            Text(text = "PHASE $currentPhase / 3", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AmberGold)
        }

        Box(modifier = Modifier.fillMaxWidth(0.85f).height(14.dp).clip(RoundedCornerShape(7.dp)).background(LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f))) {
            Box(modifier = Modifier.fillMaxWidth(bossHP / 100f).height(14.dp).clip(RoundedCornerShape(7.dp)).background(DuolingoRed))
        }
        Text(text = "$bossHP / 100 HP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFF2B0E14)).border(1.5.dp, Color(0xFFEF4444).copy(alpha = 0.6f), RoundedCornerShape(14.dp)).padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = phaseData.first, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                Text(text = feedback, fontSize = 11.sp, color = AmberGold, textAlign = TextAlign.Center)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            phaseData.third.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { opt ->
                        val isCorrect = opt == phaseData.second
                        val isSelected = selectedAnswer == opt
                        Box(
                            modifier = Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF1E0A0E))
                                .border(1.dp, if (isSelected) Color.White else Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedAnswer = opt
                                    if (isCorrect) {
                                        val newHP = maxOf(0, bossHP - 35)
                                        bossHP = newHP
                                        if (newHP <= 0 || currentPhase >= 3) {
                                            onDefeatBoss()
                                        } else {
                                            currentPhase++
                                            selectedAnswer = null
                                            feedback = "💥 Direct strike! The Overlord reshuffles round matrices!"
                                        }
                                    } else {
                                        onDeductHeart()
                                        feedback = "❌ Counterattacked! Review AES standard specifications!"
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = opt, fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}
