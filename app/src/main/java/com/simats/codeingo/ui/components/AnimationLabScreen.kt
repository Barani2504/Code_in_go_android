package com.simats.codeingo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.BehindPhoenixGlow
import com.simats.codeingo.ui.theme.BossTimerRing
import com.simats.codeingo.ui.theme.EffectTier
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalEffectTier
import com.simats.codeingo.ui.theme.MotionController
import com.simats.codeingo.ui.theme.ParticleBurstCanvas
import com.simats.codeingo.ui.theme.ParticlePool
import com.simats.codeingo.ui.theme.TypewriterText
import com.simats.codeingo.ui.theme.correctPop
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.pulse
import com.simats.codeingo.ui.theme.shake

/**
 * Debug-only Animation Lab (BuildConfig.DEBUG).
 * Replays every motion layer, effect tier, particle burst, notch HUD event,
 * and haptic pattern for fine-grained calibration.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationLabScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val feedback = remember { FeedbackManager(context) }
    val particlePool = remember { ParticlePool(60) }

    val currentTier by MotionController.effectTier.collectAsState()
    var bounceTrigger by remember { mutableStateOf(false) }
    var shakeCounter by remember { mutableIntStateOf(0) }
    var typewriterSample by remember { mutableStateOf("🎮 Phoenix Cheat Code: Block ciphers process fixed 128-bit blocks!") }
    var timerCountdown by remember { mutableIntStateOf(45) }

    CompositionLocalProvider(LocalEffectTier provides currentTier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF070D17))
        ) {
            // Background Particle Canvas
            ParticleBurstCanvas(pool = particlePool)

            // Dynamic Notch Pill HUD
            DynamicPillHUD()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "🔬 ANIMATION LAB",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Effect Tier Switcher
                LabSectionCard("EFFECT TIER (B0)") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EffectTier.values().forEach { tier ->
                            val isSelected = currentTier == tier
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) Color(0xFFFFB300) else Color(0xFF131D33)
                                    )
                                    .clickable {
                                        MotionController.setEffectTier(tier)
                                        feedback.tap(tier)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    tier.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) Color(0xFF1A1205) else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Dynamic Notch Pill HUD Events (C2)
                LabSectionCard("DYNAMIC PILL HUD (C2)") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LabButton("+25 XP", Modifier.weight(1f)) {
                                DynamicPillController.postEvent(DynamicPillEvent.XP(25))
                                DynamicPillController.triggerAmbientGlow(Color(0xFF00E676))
                                feedback.correct(1, currentTier)
                            }
                            LabButton("Combo x3", Modifier.weight(1f)) {
                                DynamicPillController.postEvent(DynamicPillEvent.Combo(3))
                                DynamicPillController.triggerAmbientGlow(Color(0xFFFFB300))
                                feedback.correct(3, currentTier)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LabButton("Heart Lost", Modifier.weight(1f)) {
                                DynamicPillController.postEvent(DynamicPillEvent.HeartLost)
                                DynamicPillController.triggerAmbientGlow(Color(0xFFFF3B30))
                                DynamicPillController.triggerEdgeLighting(Color(0xFFFF3B30))
                                feedback.wrong(currentTier)
                            }
                            LabButton("Stage Clear", Modifier.weight(1f)) {
                                DynamicPillController.postEvent(DynamicPillEvent.LevelComplete())
                                DynamicPillController.triggerEdgeLighting(Color(0xFFAA00FF))
                                feedback.correct(5, currentTier)
                            }
                        }
                        LabButton("Boss Banner (Cutout Morph)", Modifier.fillMaxWidth()) {
                            DynamicPillController.postEvent(DynamicPillEvent.BossIntro("BOSS: THE CIPHER BREAKER"))
                            DynamicPillController.triggerAmbientGlow(Color(0xFFFF1744))
                            DynamicPillController.triggerEdgeLighting(Color(0xFFFF1744))
                            feedback.bossWarning(currentTier)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: Particles & Game Feel (B0)
                LabSectionCard("PARTICLES & GATED SPRINGS (B0)") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LabButton("Burst (60 Max)", Modifier.weight(1f)) {
                            particlePool.spawnBurst(
                                origin = Offset(500f, 600f),
                                colors = listOf(Color(0xFFFFD700), Color(0xFFFF9100), Color(0xFF00E676)),
                                count = 28
                            )
                            feedback.correct(2, currentTier)
                        }
                        LabButton("Snappy Pop", Modifier.weight(1f)) {
                            bounceTrigger = !bounceTrigger
                            feedback.tap(currentTier)
                        }
                        LabButton("Shake", Modifier.weight(1f)) {
                            shakeCounter++
                            feedback.wrong(currentTier)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Interactive Target Tile
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .correctPop(bounceTrigger)
                            .shake(shakeTrigger = if (shakeCounter > 0) shakeCounter else null)
                            .pressScale()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF131D33))
                            .border(1.dp, Color(0xFF223555), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Tap to Test Pressed Scale (0.96) + Pop/Shake",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 4: Mascot Dialogue & Typewriter Reveal (B1)
                LabSectionCard("MASCOT TYPEWRITER REVEAL (~25ms/char) (B1)") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0C1424))
                            .border(1.dp, Color(0xFF1F2F4E), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        TypewriterText(
                            fullText = typewriterSample,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LabButton("Restart Typewriter", Modifier.fillMaxWidth()) {
                        typewriterSample = if (typewriterSample.contains("128-bit")) {
                            "🎮 Phoenix Cheat Code: RSA relies on the computational hardness of factoring large primes!"
                        } else {
                            "🎮 Phoenix Cheat Code: Block ciphers process fixed 128-bit blocks!"
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 5: Behind-Phoenix Halo & Boss Timer Ring (B1)
                LabSectionCard("BEHIND GLOW & BOSS TIMER RING (B1)") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Halo beneath
                        Box(contentAlignment = Alignment.Center) {
                            BehindPhoenixGlow(color = Color(0xFFFFB300), size = 80.dp)
                            Text("HALO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Countdown ring around
                        BossTimerRing(
                            timeRemainingSeconds = timerCountdown,
                            totalTimeSeconds = 60,
                            modifier = Modifier.size(90.dp)
                        ) {
                            Text(
                                "${timerCountdown}s",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LabButton("Step Timer (-5s)", Modifier.fillMaxWidth()) {
                        timerCountdown = if (timerCountdown > 5) timerCountdown - 5 else 60
                        if (timerCountdown <= 10) feedback.bossWarning(currentTier)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LabSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFFFB300),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun LabButton(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .pressScale()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
