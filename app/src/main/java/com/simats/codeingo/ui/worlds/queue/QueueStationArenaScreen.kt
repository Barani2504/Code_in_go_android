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
                    QueueStationLevel.LEVEL_1 -> QueueLevel1Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_1) })
                    QueueStationLevel.LEVEL_2 -> QueueLevel2Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_2) })
                    QueueStationLevel.LEVEL_3 -> QueueLevel3Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_3) })
                    QueueStationLevel.LEVEL_4 -> QueueLevel4Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_4) })
                    QueueStationLevel.LEVEL_5 -> QueueLevel5Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_5) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    QueueStationLevel.LEVEL_6 -> QueueLevel6Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_6) })
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
                    modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(24.dp)).background(Color(0xFF1E0E2B)).border(2.dp, Color(0xFFB946FA), RoundedCornerShape(24.dp)).padding(24.dp),
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
                    modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(24.dp)).background(Color(0xFF1E0E2B)).border(2.dp, DuolingoGreen, RoundedCornerShape(24.dp)).padding(24.dp),
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
@Composable
private fun QueueLevel1Content(onComplete: () -> Unit) {
    val chunks = remember { mutableStateListOf("Chunk 0", "Chunk 1", "Chunk 2") }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF261036)).border(1.dp, Color(0xFFB946FA).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(16.dp)
        ) {
            Text(
                text = "🛡️ SHA-256 Compression: Process 512-bit message blocks through 64 rounds to produce an irreversible 256-bit digest.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LocalDynamicThemeColors.current.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            chunks.forEachIndexed { idx, name ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(80.dp, 56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (idx == 0) Color(0xFFB946FA) else Color(0xFF4A1A6D))
                            .border(1.dp, LocalDynamicThemeColors.current.placeholder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary, fontFamily = FontFamily.Monospace)
                    }
                    Text(
                        text = if (idx == 0) "ACTIVE ⚙️" else "[$idx]",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (idx == 0) Color(0xFFB946FA) else SubtextGray
                    )
                }
            }
        }

        Duolingo3DButton(
            title = "🛡️ COMPRESS CHUNK (Process Hash)",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = {
                if (chunks.isNotEmpty()) {
                    chunks.removeAt(0)
                    if (chunks.size == 1) onComplete()
                }
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}

@Composable
private fun QueueLevel2Content(onComplete: () -> Unit) {
    var flipped by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "Avalanche Effect: 1-bit input change flips ~50% of hash digest bits!", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(130.dp, 60.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF3B1556)).padding(8.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "H('cat')", fontSize = 12.sp, color = AmberGold)
                    Text(text = "0x77AF...3E", fontSize = 11.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }

            Box(modifier = Modifier.size(130.dp, 60.dp).clip(RoundedCornerShape(10.dp)).background(if (flipped) DuolingoGreen.copy(alpha = 0.2f) else Color(0xFF3B1556)).padding(8.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "H('dat')", fontSize = 12.sp, color = AmberGold)
                    Text(text = if (flipped) "0x2C4B...9F" else "???", fontSize = 11.sp, color = if (flipped) DuolingoGreen else Color.White, fontFamily = FontFamily.Monospace)
                }
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
    val phases = listOf("Absorb 64 bytes", "Permute State f", "Squeeze 32 bytes")

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "SHA-3 Keccak Sponge Construction: Absorbing & Squeezing", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Box(
            modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(12.dp)).background(Color(0xFF4A1A6D)).padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = phases[step.coerceIn(0, 2)], fontSize = 15.sp, fontWeight = FontWeight.Black, color = AmberGold, fontFamily = FontFamily.Monospace)
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
private fun QueueLevel4Content(onComplete: () -> Unit) {
    var isVerified by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "Merkle Tree Root Validation: H(Root) = H(H(A) ∥ H(B))", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Box(modifier = Modifier.size(160.dp, 50.dp).clip(RoundedCornerShape(10.dp)).background(if (isVerified) DuolingoGreen else Color(0xFFB946FA)), contentAlignment = Alignment.Center) {
            Text(text = if (isVerified) "ROOT VALIDATED ✓" else "ROOT: H(A+B)", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
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

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
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
                            if (isCorrect) onComplete()
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
private fun QueueLevel6Content(onComplete: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "HMAC Keyed Authentication Sprint: H((K ⊕ opad) ∥ H((K ⊕ ipad) ∥ M))", fontSize = 13.sp, color = Color(0xFFB946FA), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Duolingo3DButton(
            title = "👉 NESTED HASH PASS (${step}/2)",
            style = Duolingo3DButtonStyle.AMBER,
            onClick = {
                step++
                if (step == 2) onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}

@Composable
private fun QueueBossBattleContent(onDefeatBoss: () -> Unit, onDeductHeart: () -> Unit) {
    val hashes = remember { mutableStateListOf("0x7F2A", "0x89C1", "0x33B4") }
    var bossHP by remember { mutableIntStateOf(100) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "👾 THE COLLISION BEAST! (${bossHP} HP)", fontSize = 16.sp, color = DuolingoRed, fontWeight = FontWeight.Black)

        Text(text = "Active Collisions: ${hashes.size}/6 (Verify before collision storm!)", fontSize = 12.sp, color = Color(0xFFB946FA))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            hashes.forEach { h ->
                Box(modifier = Modifier.size(64.dp, 40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFB946FA)), contentAlignment = Alignment.Center) {
                    Text(text = h, fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }
        }

        Duolingo3DButton(
            title = "🛡️ RAPID INTEGRITY VERIFICATION!",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = {
                if (hashes.isNotEmpty()) {
                    hashes.removeAt(0)
                    bossHP = maxOf(0, bossHP - 25)
                    if (bossHP <= 0) onDefeatBoss()
                }
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}
