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
                    StackTowerLevel.LEVEL_1 -> StackLevel1Content(onComplete = { handleLevelCompleted(StackTowerLevel.LEVEL_1) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    StackTowerLevel.LEVEL_2 -> StackLevel2Content(onComplete = { handleLevelCompleted(StackTowerLevel.LEVEL_2) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    StackTowerLevel.LEVEL_3 -> StackLevel3Content(onComplete = { handleLevelCompleted(StackTowerLevel.LEVEL_3) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    StackTowerLevel.LEVEL_4 -> StackLevel4Content(onComplete = { handleLevelCompleted(StackTowerLevel.LEVEL_4) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
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
                    Text(text = "🔑 Key Keepers Codex", fontSize = 12.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
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
                    Text(text = "🔑 SPIRE KEY SYNTHESIZED!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF9500))
                    Text(text = currentLevel.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                    Duolingo3DButton(
                        title = "ASCEND NEXT CHAMBER ➔",
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
                    Text(text = "👑 KEY KEEPERS SPIRE SECURED!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                    Text(text = "You mastered Diffie-Hellman, RSA prime generation, and vanquished the Quantum Cryptanalyst in the Key Keepers Spire!", fontSize = 13.sp, color = SubtextGray, textAlign = TextAlign.Center)
                    Duolingo3DButton(
                        title = "COMPLETE SPIRE",
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
                    Text(text = "🔑 Key Keepers Codex (Asymmetric Cryptography)", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF9500))
                    Text(
                        text = "Asymmetric Cryptography relies on mathematically hard, one-way trapdoor functions that allow secure communication without pre-shared keys.\n\nKey Concepts:\n1. Diffie-Hellman: Parties negotiate shared secret g^(ab) mod p over insecure channels via the Discrete Logarithm Problem.\n2. RSA: Relies on the hardness of factoring large semiprimes n = p * q. Keys satisfy e * d ≡ 1 (mod φ(n)).\n3. Digital Signatures: A sender signs hash m with private key d (s = m^d mod n); anyone verifies with public key e (m = s^e mod n).",
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
                                            StackTowerLevel.LEVEL_1 -> "Modular exponentiation wraps powers around modulo p to prevent reverse computation."
                StackTowerLevel.LEVEL_2 -> "Alice sends g^a mod p; Bob sends g^b mod p. Both independently reach g^(ab) mod p!"
                StackTowerLevel.LEVEL_3 -> "Primes p and q must remain strictly private; only modulus n and public exponent e are revealed."
                StackTowerLevel.LEVEL_4 -> "The public key encrypts messages; only the corresponding private key d can invert and decrypt."
                StackTowerLevel.LEVEL_5 -> "Digital signatures provide non-repudiation and proof of authenticity using private key operations."
                StackTowerLevel.LEVEL_6 -> "Speed is crucial! Compute the modular inverses before quantum factorization catches up."
                StackTowerLevel.BOSS_BATTLE -> "Generate cryptographic key pairs rapidly to counter the Quantum Cryptanalyst's Shor attacks!"
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

// 🔑 Key Keepers Spire Levels Content (Asymmetric Cryptography)
// ══════════════════════════════════════════════════════════════════
// Level Content Composables
// ══════════════════════════════════════════════════════════════════
@Composable
private fun StackLevel1Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    val options = listOf(6, 1, 3, 2)

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF261805)).border(1.dp, Color(0xFFFF9500).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(16.dp)
        ) {
            Text(
                text = "🔑 Modular Exponentiation: In asymmetric key math, compute 3^3 mod 7 = 27 mod 7. Select the correct remainder!",
                fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary, textAlign = TextAlign.Center
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach { opt ->
                val isCorrect = opt == 6
                val isSelected = selectedAnswer == opt
                Box(
                    modifier = Modifier
                        .size(64.dp, 56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF1E1710))
                        .border(1.5.dp, Color(0xFFFF9500), RoundedCornerShape(12.dp))
                        .clickable {
                            selectedAnswer = opt
                            if (isCorrect) onComplete() else onDeductHeart()
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
private fun StackLevel2Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var selectedK by remember { mutableStateOf<Int?>(null) }
    val options = listOf(2, 8, 19, 7)

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF261805)).border(1.dp, Color(0xFFFF9500).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Diffie-Hellman: Prime p=23, Base g=5", fontSize = 13.sp, fontWeight = FontWeight.Black, color = AmberGold)
                Text(text = "Alice (a=6) sends A=8. Bob (b=15) sends B=19.\nAlice computes K = B^a mod p = 19^6 mod 23. What is K?", fontSize = 12.sp, color = Color.White, textAlign = TextAlign.Center)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(70.dp, 48.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFFF9500)), contentAlignment = Alignment.Center) {
                Text(text = "A = 8", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.Black, fontFamily = FontFamily.Monospace)
            }
            Text(text = "⇄", fontSize = 20.sp, color = AmberGold)
            Box(modifier = Modifier.size(70.dp, 48.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFB35900)), contentAlignment = Alignment.Center) {
                Text(text = "B = 19", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { opt ->
                val isCorrect = opt == 2
                val isSelected = selectedK == opt
                Box(
                    modifier = Modifier
                        .size(64.dp, 50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF1E1710))
                        .border(1.5.dp, Color(0xFFFF9500), RoundedCornerShape(12.dp))
                        .clickable {
                            selectedK = opt
                            if (isCorrect) onComplete() else onDeductHeart()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$opt", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun StackLevel3Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var selectedPhi by remember { mutableStateOf<Int?>(null) }
    // p = 5, q = 11 -> n = 55, phi(n) = (5-1)*(11-1) = 40
    val options = listOf(55, 40, 50, 44)

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "RSA Euler's Totient: For primes p=5 and q=11, what is φ(n) = (p-1)(q-1)?", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach { opt ->
                val isCorrect = opt == 40
                val isSelected = selectedPhi == opt
                Box(
                    modifier = Modifier
                        .size(64.dp, 56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF331A05))
                        .border(1.5.dp, Color(0xFFFF9500), RoundedCornerShape(12.dp))
                        .clickable {
                            selectedPhi = opt
                            if (isCorrect) onComplete() else onDeductHeart()
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
private fun StackLevel4Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    val options = listOf("Private key exponent d", "Public exponent e", "Modulus n", "Public KeyShare g^a")

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF261805)).border(1.dp, Color(0xFFFF9500).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(14.dp)
        ) {
            Text(
                text = "Public Key Distribution: Which parameter must be kept strictly CONFIDENTIAL and NEVER published?",
                fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary, textAlign = TextAlign.Center
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            options.forEach { opt ->
                val isCorrect = opt == "Private key exponent d"
                val isSelected = selectedAnswer == opt
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF1E1710))
                        .border(1.dp, Color(0xFFFF9500), RoundedCornerShape(10.dp))
                        .clickable {
                            selectedAnswer = opt
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
private fun StackLevel5Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var isSigned by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "Digital Signature: Sign message digest with private key d: s = m^d mod n", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Box(
            modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(12.dp)).background(Color(0xFF331A05)).padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (isSigned) "VALID SIGNATURE: 0x9F4C2E ✓" else "RAW DIGEST: 0x1A2B3C", fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (isSigned) DuolingoGreen else AmberGold, fontFamily = FontFamily.Monospace)
        }

        if (!isSigned) {
            Duolingo3DButton(
                title = "✍️ SIGN WITH PRIVATE KEY",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = {
                    isSigned = true
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}

@Composable
private fun StackLevel6Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "⚡ Modular Exponentiation Sprint: Binary Square-and-Multiply (${step}/3)", fontSize = 14.sp, color = Color(0xFFFF9500), fontWeight = FontWeight.Bold)

        Duolingo3DButton(
            title = "👉 SQUARE & MULTIPLY (${step}/3)",
            style = Duolingo3DButtonStyle.AMBER,
            onClick = {
                step++
                if (step == 3) onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        )
    }
}

@Composable
private fun StackBossBattleContent(onDefeatBoss: () -> Unit, onDeductHeart: () -> Unit) {
    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableIntStateOf(1) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("The Quantum Cryptanalyst prepares Shor's algorithm! Deploy quantum-safe defenses!") }

    val phaseData = when (currentPhase) {
        1 -> Triple("Shor's Factorization: Which widely deployed cipher is broken in polynomial time by Shor's algorithm?", "RSA-2048", listOf("RSA-2048", "AES-256", "SHA-256", "ChaCha20"))
        2 -> Triple("Shor's Discrete Log: Which key agreement protocol is broken by quantum computers?", "ECDH (Curve25519)", listOf("ECDH (Curve25519)", "AES-GCM", "HMAC-SHA256", "Argon2id"))
        else -> Triple("Post-Quantum Cryptography: Which NIST standardized lattice-based KEM provides Shor resistance?", "ML-KEM (Kyber)", listOf("ML-KEM (Kyber)", "RSA-4096", "Diffie-Hellman", "ECC-P384"))
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "👾 THE QUANTUM CRYPTANALYST", fontSize = 15.sp, color = DuolingoRed, fontWeight = FontWeight.Black)
            Text(text = "PHASE $currentPhase / 3", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AmberGold)
        }

        Box(modifier = Modifier.fillMaxWidth(0.85f).height(14.dp).clip(RoundedCornerShape(7.dp)).background(LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f))) {
            Box(modifier = Modifier.fillMaxWidth(bossHP / 100f).height(14.dp).clip(RoundedCornerShape(7.dp)).background(DuolingoRed))
        }
        Text(text = "$bossHP / 100 HP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFF261805)).border(1.5.dp, Color(0xFFFF9500).copy(alpha = 0.6f), RoundedCornerShape(14.dp)).padding(14.dp),
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
                                .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF1E1710))
                                .border(1.dp, if (isSelected) Color.White else Color(0xFFFF9500).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
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
                                            feedback = "💥 Quantum qubits decohered! Next quantum attack phase!"
                                        }
                                    } else {
                                        onDeductHeart()
                                        feedback = "❌ Quantum attack breached your defenses! Classical asymmetric ciphers are vulnerable."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = opt, fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
