package com.simats.codeingo.ui.worlds.queue

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
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueStationArenaScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val gemsCount by gameManager.gemsCount.collectAsState()

    var currentLevel by remember { mutableStateOf(QueueStationLevel.LEVEL_1) }
    var unlockedLevels by remember { mutableStateOf(setOf(1)) }
    var levelStars by remember { mutableStateOf(mutableMapOf(1 to 3)) }

    var hearts by remember { mutableIntStateOf(5) }
    var sessionXP by remember { mutableIntStateOf(0) }
    var speed by remember { mutableStateOf(KingdomGameSpeed.NORMAL) }

    var showTheoryCodex by remember { mutableStateOf(false) }
    var showHintSheet by remember { mutableStateOf(false) }
    var showVictoryModal by remember { mutableStateOf(false) }
    var showBossVictoryModal by remember { mutableStateOf(false) }

    fun handleLevelCompleted(lvl: QueueStationLevel) {
        val nextId = lvl.id + 1
        unlockedLevels = unlockedLevels + nextId
        levelStars[lvl.id] = 3
        sessionXP += lvl.xpReward
        gameManager.addXP(lvl.xpReward)
        gameManager.addGems(lvl.coinReward)

        if (lvl == QueueStationLevel.BOSS_BATTLE) {
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
        // Astral Railway Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF240E33), Color(0xFF13061C), Color(0xFF09020F))
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
                            .background(Color(0xFFB946FA).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFFB946FA).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { speed = speed.next() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(text = speed.label, fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = Color(0xFFB946FA))
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
                QueueStationLevel.entries.forEach { lvl ->
                    val isUnlocked = unlockedLevels.contains(lvl.id)
                    val isSelected = currentLevel == lvl

                    Box(
                        modifier = Modifier
                            .pressScale()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    isSelected && lvl == QueueStationLevel.BOSS_BATTLE -> DuolingoRed
                                    isSelected -> Color(0xFFB946FA)
                                    isUnlocked -> Color(0xFF221133)
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
                    QueueStationLevel.LEVEL_1 -> QueueLevel1Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_1) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    QueueStationLevel.LEVEL_2 -> QueueLevel2Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_2) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    QueueStationLevel.LEVEL_3 -> QueueLevel3Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_3) })
                    QueueStationLevel.LEVEL_4 -> QueueLevel4Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_4) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    QueueStationLevel.LEVEL_5 -> QueueLevel5Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_5) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    QueueStationLevel.LEVEL_6 -> QueueLevel6Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_6) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    QueueStationLevel.BOSS_BATTLE -> QueueBossBattleContent(onDefeatBoss = { handleLevelCompleted(QueueStationLevel.BOSS_BATTLE) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
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
                        .background(Color(0xFF221133))
                        .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .clickable { showTheoryCodex = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(text = "🛡️ Integrity Realm Codex", fontSize = 12.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .pressScale()
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF221133))
                            .clickable {
                                val current = currentLevel
                                currentLevel = QueueStationLevel.LEVEL_1
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
                                currentLevel = QueueStationLevel.fromId(nextId)
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
                    modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(24.dp)).background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF1E0E2B) else LocalDynamicThemeColors.current.cardBackground).border(2.dp, Color(0xFFB946FA), RoundedCornerShape(24.dp)).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "🎫 PASSENGERS DISPATCHED!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFB946FA))
                    Text(text = currentLevel.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                    Duolingo3DButton(
                        title = "NEXT TRAIN ➔",
                        style = Duolingo3DButtonStyle.GREEN,
                        onClick = {
                            showVictoryModal = false
                            val nextId = minOf(7, currentLevel.id + 1)
                            currentLevel = QueueStationLevel.fromId(nextId)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showBossVictoryModal) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(24.dp)).background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF1E0E2B) else LocalDynamicThemeColors.current.cardBackground).border(2.dp, DuolingoGreen, RoundedCornerShape(24.dp)).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "👑 GRIDLOCK BROKEN!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                    Text(text = "You mastered SHA-256 digests, HMAC authentication, and banished the Collision Beast in the Integrity Realm!", fontSize = 13.sp, color = SubtextGray, textAlign = TextAlign.Center)
                    Duolingo3DButton(
                        title = "COMPLETE REALM",
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
                    Text(text = "🛡️ Integrity Realm Codex (Cryptographic Hashes & HMAC)", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFB946FA))
                    Text(
                        text = "Cryptographic Hash Functions map arbitrary-length data to fixed-size bit strings (such as 256 bits in SHA-256) with mathematical guarantees.\n\nThree Critical Properties:\n1. Preimage Resistance: Computationally infeasible to find message m given hash H(m).\n2. Second Preimage Resistance: Hard to find m2 ≠ m1 such that H(m1) = H(m2).\n3. Collision Resistance: Infeasible to find any pair (m1, m2) with H(m1) = H(m2).\n\nHMAC (Hash-based Message Authentication Code) utilizes nested keys with inner and outer padding (ipad/opad) to defend against length extension attacks.",
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
                    Text(text = "💡 Realm Hint: ${currentLevel.title}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFB946FA))
                    Text(
                        text = when (currentLevel) {
                                            QueueStationLevel.LEVEL_1 -> "One-way hash functions are easy to compute forward, but practically impossible to invert."
                QueueStationLevel.LEVEL_2 -> "Even a single bit difference in input scrambles roughly half the output digest bits (avalanche effect)!"
                QueueStationLevel.LEVEL_3 -> "The Keccak sponge state absorbs message blocks and squeezes out pseudo-random hash output."
                QueueStationLevel.LEVEL_4 -> "Merkle trees combine leaf hashes into a single root hash, validating tamper-proof integrity."
                QueueStationLevel.LEVEL_5 -> "The Birthday Paradox shows collisions occur around 2^(n/2) trials rather than 2^n!"
                QueueStationLevel.LEVEL_6 -> "HMAC nests inner and outer hashes with secret keys to authenticate message integrity."
                QueueStationLevel.BOSS_BATTLE -> "Verify hash blocks rapidly to defeat the Collision Beast before it produces a collision!"
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
// 🛡️ Integrity Realm Levels Content (Hashes & Digital Signatures)
// ══════════════════════════════════════════════════════════════════
// Level Content Composables
// ══════════════════════════════════════════════════════════════════
@Composable
private fun QueueLevel1Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    val options = listOf("512 bits (64 bytes)", "256 bits (32 bytes)", "1024 bits", "128 bits")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF2B1238)).border(1.dp, Color(0xFFB946FA).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(16.dp)
        ) {
            Text(
                text = "🛡️ SHA-256 Merkle-Damgård Chunking: Input messages are padded with '1', zero bits, and 64-bit length. What is the standard block size processed by the compression function?",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = LocalDynamicThemeColors.current.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            options.forEach { opt ->
                val isCorrect = opt == "512 bits (64 bytes)"
                val isSelected = selectedOption == opt
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else if (LocalDynamicThemeColors.current.isDark) Color(0xFF1E0E28) else LocalDynamicThemeColors.current.inputBackground)
                        .border(1.dp, Color(0xFFB946FA), RoundedCornerShape(10.dp))
                        .clickable {
                            selectedOption = opt
                            if (isCorrect) onComplete() else onDeductHeart()
                        }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = opt, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun QueueLevel2Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var flipped by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(text = "⚡ Strict Avalanche Criterion: Flipping a single bit in input cascades through compression rounds", fontSize = 13.5.sp, color = Color(0xFFB946FA), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(110.dp, 44.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF4A1A6D)), contentAlignment = Alignment.Center) {
                Text(text = if (flipped) "M = 'cat!'" else "M = 'cat'", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
            }
            Text(text = "➔", fontSize = 20.sp, color = AmberGold)
            Box(modifier = Modifier.size(140.dp, 44.dp).clip(RoundedCornerShape(8.dp)).background(if (flipped) DuolingoGreen else Color(0xFF2B1238)), contentAlignment = Alignment.Center) {
                Text(text = if (flipped) "H = 7f83b165..." else "H = 77af778b...", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
            }
        }

        if (!flipped) {
            Duolingo3DButton(
                title = "💥 FLIP 1 BIT & OBSERVE AVALANCHE",
                style = Duolingo3DButtonStyle.AMBER,
                onClick = {
                    flipped = true
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }
    }
}

@Composable
private fun QueueLevel3Content(onComplete: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val phases = listOf("1. Absorb: XOR 1088-bit rate r into state", "2. Permute: Apply Keccak-f[1600] 24 rounds", "3. Squeeze: Extract 256-bit hash digest")

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "SHA-3 Keccak Sponge Construction (5×5 Lane State)", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        // 5x5 Keccak State Matrix preview
        Column(
            modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(Color(0xFF2B1238)).padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(5) { r ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(5) { c ->
                        val isRate = (r * 5 + c) < 17
                        Box(
                            modifier = Modifier.size(34.dp, 24.dp).clip(RoundedCornerShape(4.dp))
                                .background(if (isRate) Color(0xFFB946FA).copy(alpha = 0.4f) else AmberGold.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (isRate) "r" else "c", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(10.dp)).background(Color(0xFF4A1A6D)).padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = phases[step.coerceIn(0, 2)], fontSize = 12.sp, fontWeight = FontWeight.Black, color = AmberGold, fontFamily = FontFamily.Monospace)
        }

        Duolingo3DButton(
            title = if (step < 2) "🌊 SPONGE STEP (${step + 1}/3)" else "SPONGE COMPLETE ✓",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = {
                step++
                if (step >= 3) onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        )
    }
}

@Composable
private fun QueueLevel4Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var isVerified by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(text = "Merkle Tree Root Validation: H(Root) = H(H(A) ∥ H(B))", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Box(modifier = Modifier.size(180.dp, 50.dp).clip(RoundedCornerShape(10.dp)).background(if (isVerified) DuolingoGreen else Color(0xFFB946FA)), contentAlignment = Alignment.Center) {
            Text(text = if (isVerified) "ROOT VALIDATED ✓" else "ROOT: H(H(A) ∥ H(B))", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
        }

        if (!isVerified) {
            Duolingo3DButton(
                title = "🌿 VERIFY MERKLE ROOT",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = {
                    isVerified = true
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(0.75f)
            )
        }
    }
}

@Composable
private fun QueueLevel5Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    // 2^(n/2) for n=256 -> 2^128
    val options = listOf("2^256", "2^128", "2^64", "2^16")

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(text = "Birthday Paradox Bound: To find a hash collision in a 256-bit hash function, approximately how many trials are needed?", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { opt ->
                val isCorrect = opt == "2^128"
                val isSelected = selectedOption == opt
                Box(
                    modifier = Modifier
                        .size(68.dp, 50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF3B1556))
                        .border(1.5.dp, Color(0xFFB946FA), RoundedCornerShape(12.dp))
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
private fun QueueLevel6Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    val options = listOf("Prevent length extension attacks", "Speed up hashing", "Compress to 128 bits", "Encrypt the payload")

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF2B1238)).border(1.dp, Color(0xFFB946FA).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(14.dp)
        ) {
            Text(
                text = "HMAC Construction: Why does HMAC use two nested hashing passes with ipad (0x36) and opad (0x5C) instead of plain H(K || M)?",
                fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary, textAlign = TextAlign.Center
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            options.forEach { opt ->
                val isCorrect = opt == "Prevent length extension attacks"
                val isSelected = selectedAnswer == opt
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else if (LocalDynamicThemeColors.current.isDark) Color(0xFF1E0E28) else LocalDynamicThemeColors.current.inputBackground)
                        .border(1.dp, Color(0xFFB946FA), RoundedCornerShape(10.dp))
                        .clickable {
                            selectedAnswer = opt
                            if (isCorrect) onComplete() else onDeductHeart()
                        }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = opt, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun QueueBossBattleContent(onDefeatBoss: () -> Unit, onDeductHeart: () -> Unit) {
    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableIntStateOf(1) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("The Collision Beast attacks cryptographic digest integrity! Fortify hash defenses!") }

    val phaseData = when (currentPhase) {
        1 -> Triple("Birthday Paradox: Finding a collision in a 128-bit hash function requires how many hash computations?", "2^64", listOf("2^64", "2^128", "2^32", "2^256"))
        2 -> Triple("Security Bounds: Which security property requires FEWER trials to violate mathematically?", "Collision Resistance (2^(n/2))", listOf("Collision Resistance (2^(n/2))", "Pre-image Resistance (2^n)", "Second Pre-image (2^n)", "Both identical"))
        else -> Triple("Length Extension: Which hash family is naturally vulnerable to length extension attacks if not using HMAC?", "Merkle-Damgård (SHA-256)", listOf("Merkle-Damgård (SHA-256)", "Keccak Sponge (SHA-3)", "BLAKE3", "Ascon AEAD"))
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "👾 THE COLLISION BEAST", fontSize = 16.sp, color = DuolingoRed, fontWeight = FontWeight.Black)
            Text(text = "PHASE $currentPhase / 3", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AmberGold)
        }

        Box(modifier = Modifier.fillMaxWidth(0.85f).height(14.dp).clip(RoundedCornerShape(7.dp)).background(LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f))) {
            Box(modifier = Modifier.fillMaxWidth(bossHP / 100f).height(14.dp).clip(RoundedCornerShape(7.dp)).background(DuolingoRed))
        }
        Text(text = "$bossHP / 100 HP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFF2B1238)).border(1.5.dp, Color(0xFFB946FA).copy(alpha = 0.6f), RoundedCornerShape(14.dp)).padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = phaseData.first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
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
                                .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else if (LocalDynamicThemeColors.current.isDark) Color(0xFF1E0E28) else LocalDynamicThemeColors.current.inputBackground)
                                .border(1.dp, if (isSelected) Color.White else Color(0xFFB946FA).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
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
                                            feedback = "💥 Collision prevented! Digest integrity holds!"
                                        }
                                    } else {
                                        onDeductHeart()
                                        feedback = "❌ Collision storm hit! Check your cryptographic hash theorems."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = opt, fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = Color.White, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
