package com.simats.codeingo.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.motion.NodeIgniteBurst
import com.simats.codeingo.ui.motion.SoundEffectsManager
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray
import kotlin.random.Random

enum class ChestStage {
    READY,
    UNSEALING,
    OPENED
}

data class ChestDrop(
    val emoji: String,
    val title: String,
    val amountText: String
)

/**
 * 🎁 3-Stage Daily Reward Chest Opening Ceremony
 *
 * Implements Phase 5.4:
 * - Stage 1: Closed chest with ambient pulse
 * - Stage 2: Shaking gold resonance with anticipation sound
 * - Stage 3: Burst open with particle celebration
 * - Shows transparent mathematical probability odds in the UI
 */
@Composable
fun ChestRewardCeremonyView(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val gameManager = GameManager.instance
    val scope = rememberCoroutineScope()
    var stage by remember { mutableStateOf(ChestStage.READY) }
    var awardedDrops by remember { mutableStateOf<List<ChestDrop>>(emptyList()) }

    val shakeRotation = remember { Animatable(0f) }
    val chestScale = remember { Animatable(1f) }

    // Roll rewards transparently on unsealing
    fun rollDrops(): List<ChestDrop> {
        val drops = mutableListOf<ChestDrop>()
        val rng = Random.nextInt(100)

        // Always award baseline XP
        val xpGain = Random.nextInt(20, 45)
        drops.add(ChestDrop("⚡", "$xpGain XP", "+$xpGain XP Added"))
        gameManager.addXP(xpGain)

        // 45% chance for Gems
        if (rng < 45) {
            val gems = Random.nextInt(15, 30)
            drops.add(ChestDrop("💎", "$gems Gems", "+$gems Cryptographic Crystals"))
            gameManager.addGems(gems)
        } else if (rng < 75) {
            // 30% chance for Hearts
            drops.add(ChestDrop("❤️", "1 Heart Refill", "Shield Matrix Restored"))
            gameManager.refillHearts()
        } else {
            // 25% chance for Streak Freeze
            drops.add(ChestDrop("🛡️", "Streak Freeze", "Flame Protection Token"))
        }

        return drops
    }

    LaunchedEffect(stage) {
        if (stage == ChestStage.UNSEALING) {
            SoundEffectsManager.getInstance(context).playChest()
            // Dynamic shake animation
            shakeRotation.animateTo(
                targetValue = 12f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 300
                        -12f at 75
                        12f at 150
                        -8f at 225
                        0f at 300
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        } else if (stage == ChestStage.OPENED) {
            shakeRotation.snapTo(0f)
            chestScale.animateTo(1.25f, tween(150, easing = FastOutSlowInEasing))
            chestScale.animateTo(1.0f, tween(200, easing = FastOutSlowInEasing))
            FeedbackManager.levelComplete()
        }
    }

    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(cardBg)
            .border(1.5.dp, AmberGold.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Text(
                text = when (stage) {
                    ChestStage.READY -> "DAILY DEFENSE CHEST"
                    ChestStage.UNSEALING -> "DECRYPTING VAULT KEY…"
                    ChestStage.OPENED -> "REWARD DECRYPTED! 🎉"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold
            )

            // Interactive Chest Visual
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .pressSquish {
                        if (stage == ChestStage.READY) {
                            FeedbackManager.tap()
                            stage = ChestStage.UNSEALING
                            scope.launch {
                                delay(800)
                                awardedDrops = rollDrops()
                                stage = ChestStage.OPENED
                            }
                        }
                    }
            ) {
                if (stage == ChestStage.OPENED) {
                    NodeIgniteBurst(trigger = 1, accentColor = AmberGold)
                }

                Text(
                    text = when (stage) {
                        ChestStage.READY -> "🎁"
                        ChestStage.UNSEALING -> "📦"
                        ChestStage.OPENED -> "✨"
                    },
                    fontSize = 54.sp,
                    modifier = Modifier
                        .scale(chestScale.value)
                        .rotate(shakeRotation.value)
                )
            }

            // Subtitle Guidance
            if (stage == ChestStage.READY) {
                Text(
                    text = "Tap the chest to unseal today's reward drops.",
                    fontSize = 11.5.sp,
                    color = dynamicColors.textSecondary
                )
            }

            // Awarded Drops List
            if (stage == ChestStage.OPENED) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    awardedDrops.forEach { drop ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PhoenixGreen.copy(alpha = 0.12f))
                                .border(1.dp, PhoenixGreen, RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(drop.emoji, fontSize = 20.sp)
                                    Column {
                                        Text(text = drop.title, fontSize = 12.sp, fontWeight = FontWeight.Black, color = PhoenixGreen)
                                        Text(text = drop.amountText, fontSize = 10.sp, color = dynamicColors.textSecondary)
                                    }
                                }
                                Text("CLAIMED ✓", fontSize = 10.sp, fontWeight = FontWeight.Black, color = PhoenixGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Claim Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressSquish {
                                FeedbackManager.tap()
                                onDismiss()
                            }
                            .clip(RoundedCornerShape(12.dp))
                            .background(AmberGold)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CONTINUE JOURNEY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }

            // Transparent Drop Rates Table
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "TRANSPARENT REWARD PROBABILITIES",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = SubtextGray
                )
                Text("• 100% Guaranteed: +20 to +45 XP", fontSize = 9.5.sp, color = dynamicColors.textSecondary)
                Text("• 45% Probability: +15 to +30 Gems 💎", fontSize = 9.5.sp, color = dynamicColors.textSecondary)
                Text("• 30% Probability: +1 Heart Refill ❤️", fontSize = 9.5.sp, color = dynamicColors.textSecondary)
                Text("• 25% Probability: Streak Freeze Token 🛡️", fontSize = 9.5.sp, color = dynamicColors.textSecondary)
            }
        }
    }
}

