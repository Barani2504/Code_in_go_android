package com.simats.codeingo.ui.debug

import androidx.compose.runtime.mutableFloatStateOf
import com.simats.codeingo.ui.components.motion.BossAuraEffect
import com.simats.codeingo.ui.components.motion.CelebrationGlowField
import com.simats.codeingo.ui.components.motion.AvalancheHeatmapRipple
import com.simats.codeingo.ui.components.motion.MascotAuraEffect
import com.simats.codeingo.ui.components.motion.PacketTravelTrails
import com.simats.codeingo.ui.components.motion.ShockwaveEffect
import com.simats.codeingo.ui.mascot.MascotEmotion
import com.simats.codeingo.ui.mascot.PhoenixStage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.motion.AshMotion
import com.simats.codeingo.ui.components.motion.CardFace
import com.simats.codeingo.ui.components.motion.ConfettiHost
import com.simats.codeingo.ui.components.motion.EffectBudgetManager
import com.simats.codeingo.ui.components.motion.ExplodeHost
import com.simats.codeingo.ui.components.motion.FlipCard
import com.simats.codeingo.ui.components.motion.MotionTier
import com.simats.codeingo.ui.components.motion.NumberRollText
import com.simats.codeingo.ui.components.motion.bounce
import com.simats.codeingo.ui.components.motion.flash
import com.simats.codeingo.ui.components.motion.glowBorder
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.components.motion.pulse
import com.simats.codeingo.ui.components.motion.rubberBand
import com.simats.codeingo.ui.components.motion.shake
import com.simats.codeingo.ui.components.motion.skeletonShimmer
import com.simats.codeingo.ui.components.motion.slide
import com.simats.codeingo.ui.components.motion.tada
import com.simats.codeingo.ui.components.motion.wobble
import com.simats.codeingo.ui.components.motion.zoom
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixGreen

/**
 * AshCrypt Motion Lab — Debug Gallery for Designers & Engineers.
 *
 * Provides an interactive testing studio to audit, replay, and calibrate:
 * - All Motion Tiers (T1 Micro, T2 Answer, T3 Complete, T4 Milestone)
 * - All 11 Spring Modifiers (bounce, shake, pulse, tada, wobble, rubberBand, flash, slide, zoom, pressSquish, glowBorder)
 * - Native 3D FlipCard (Front/Back)
 * - ExplodeHost Particle Shatter
 * - Konfetti Host
 * - Number Roll Digit Animation
 * - Calm Mode & Sensory Safety Overrides
 */
@Composable
fun MotionLabScreen(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dynamicColors = LocalDynamicThemeColors.current
    val feedback = remember { FeedbackManager(context) }

    val isCalm by EffectBudgetManager.isCalmMode.collectAsState()
    val isQuietHours by EffectBudgetManager.isQuietHoursEnabled.collectAsState()

    var activeTier by remember { mutableStateOf(MotionTier.T1_MICRO) }
    var xpRollTarget by remember { mutableIntStateOf(100) }

    // Modifier triggers
    var bounceTrigger by remember { mutableIntStateOf(0) }
    var shakeTrigger by remember { mutableIntStateOf(0) }
    var tadaTrigger by remember { mutableIntStateOf(0) }
    var wobbleTrigger by remember { mutableIntStateOf(0) }
    var rubberTrigger by remember { mutableIntStateOf(0) }
    var flashTrigger by remember { mutableIntStateOf(0) }
    var slideTrigger by remember { mutableIntStateOf(0) }
    var zoomActive by remember { mutableStateOf(false) }

    // Systems
    var showConfetti by remember { mutableStateOf(false) }
    var showExplode by remember { mutableStateOf(false) }
    var labAvalancheTrigger by remember { mutableStateOf(false) }
    var labShockwaveTrigger by remember { mutableStateOf(false) }
    var labPacketProgress by remember { mutableFloatStateOf(0.5f) }
    var cardFace by remember { mutableStateOf(CardFace.FRONT) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dynamicColors.background)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = dynamicColors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "⚡ MOTION LAB GALLERY",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = AmberGold
                        )
                        Text(
                            text = "AshCrypt Physics, Tiers & Sensory Safety",
                            fontSize = 11.sp,
                            color = dynamicColors.textSecondary
                        )
                    }
                }
            }

            // 1. Accessibility & Safety Controls
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = dynamicColors.cardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "🛡️ SENSORY SAFETY & OVERRIDES",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Calm / Relaxed Mode", fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary, fontSize = 13.sp)
                                Text("Removes shakes, flashes, and particles", fontSize = 10.sp, color = dynamicColors.textSecondary)
                            }
                            Switch(
                                checked = isCalm,
                                onCheckedChange = { EffectBudgetManager.setCalmMode(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = AmberGold)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Quiet Hours (22:00 - 07:00)", fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary, fontSize = 13.sp)
                                Text("Attenuates haptic amplitude automatically", fontSize = 10.sp, color = dynamicColors.textSecondary)
                            }
                            Switch(
                                checked = isQuietHours,
                                onCheckedChange = { EffectBudgetManager.setQuietHoursEnabled(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = AmberGold)
                            )
                        }
                    }
                }
            }

            // 2. Motion Tier Demonstrator
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = dynamicColors.cardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "🎯 MOTION TIERS (T1 - T4)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MotionTier.entries.forEach { tier ->
                                val isSelected = activeTier == tier
                                Button(
                                    onClick = { activeTier = tier },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) AmberGold else dynamicColors.inputBackground,
                                        contentColor = if (isSelected) Color.Black else dynamicColors.textPrimary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("T${tier.level}", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                        Text(
                            text = "${activeTier.name}: ${activeTier.description}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = dynamicColors.textSecondary
                        )

                        // Tier Action Triggers
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    feedback.tick()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = dynamicColors.inputBackground)
                            ) {
                                Text("Tick (T1)", fontSize = 11.sp, color = dynamicColors.textPrimary)
                            }
                            Button(
                                onClick = {
                                    feedback.success(2)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PhoenixGreen)
                            ) {
                                Text("Correct (T2)", fontSize = 11.sp, color = Color.White)
                            }
                            Button(
                                onClick = {
                                    feedback.error()
                                    shakeTrigger++
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PhoenixCrimson)
                            ) {
                                Text("Error (T2)", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            // 3. The 11 Spring Modifiers Studio
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = dynamicColors.cardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "🌀 11 SPRING MODIFIERS STUDIO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .bounce(bounceTrigger)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AmberGold)
                                    .pressSquish { bounceTrigger++ },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Bounce", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .shake(shakeTrigger)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PhoenixCrimson)
                                    .pressSquish { shakeTrigger++ },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Shake", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .pulse()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF00C85A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Pulse", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .tada(tadaTrigger)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF38BDF8))
                                    .pressSquish { tadaTrigger++ },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Tada", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .wobble(wobbleTrigger)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFA855F7))
                                    .pressSquish { wobbleTrigger++ },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Wobble", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .rubberBand(rubberTrigger)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFF5712))
                                    .pressSquish { rubberTrigger++ },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Rubber", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .flash(flashTrigger)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE2E8F0))
                                    .pressSquish { flashTrigger++ },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Flash (<=3Hz)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .slide(slideTrigger)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0284C7))
                                    .pressSquish { slideTrigger++ },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Slide", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .zoom(1.15f, zoomActive)
                                    .glowBorder(AmberGold)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E293B))
                                    .pressSquish { zoomActive = !zoomActive },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Zoom/Glow", fontSize = 10.sp, fontWeight = FontWeight.Black, color = AmberGold)
                            }
                        }
                    }
                }
            }

            // 4. Native 3D FlipCard Showcase
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = dynamicColors.cardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "🃏 NATIVE 3D FLIPCARD (Tap to Flip)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                        FlipCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            cardFace = cardFace,
                            onFlip = { cardFace = it },
                            frontContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF1E293B))
                                        .border(1.5.dp, AmberGold, RoundedCornerShape(14.dp))
                                        .padding(14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("🔐 RSA Trapdoor Function", fontSize = 14.sp, fontWeight = FontWeight.Black, color = AmberGold)
                                        Text("Is modular exponentiation easily reversible?", fontSize = 11.sp, color = Color.White)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Tap to reveal answer →", fontSize = 9.sp, color = Color.Gray)
                                    }
                                }
                            },
                            backContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(PhoenixGreen.copy(alpha = 0.2f))
                                        .border(1.5.dp, PhoenixGreen, RoundedCornerShape(14.dp))
                                        .padding(14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("✅ Discrete Logarithm Hardness", fontSize = 13.sp, fontWeight = FontWeight.Black, color = PhoenixGreen)
                                        Text("Calculating m^e mod n is easy; recovering m without d is computationally infeasible.", fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // 5. Number Roll & Particle Explosions
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = dynamicColors.cardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "🔢 NUMBER ROLL & PARTICLE SYSTEMS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NumberRollText(
                                targetValue = xpRollTarget,
                                prefix = "+",
                                suffix = " XP EARNED"
                            )
                            Button(
                                onClick = { xpRollTarget += 25 },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                            ) {
                                Text("+25", color = Color.Black, fontWeight = FontWeight.Black)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    feedback.milestone()
                                    showConfetti = true
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PhoenixGreen)
                            ) {
                                Text("Konfetti (T3)", fontSize = 11.sp, color = Color.White)
                            }
                            Button(
                                onClick = {
                                    showExplode = true
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PhoenixCrimson)
                            ) {
                                Text("Explode Token", fontSize = 11.sp, color = Color.White)
                            }
                        }

                        // Skeleton Shimmer Box
                        Text("Skeleton Shimmer Preview:", fontSize = 11.sp, color = dynamicColors.textSecondary)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .skeletonShimmer()
                        )
                    }
                }
            }

            // 5. Part 4: Native Effects Studio (AGSL API 33+ & Canvas Fallback)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = dynamicColors.cardBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "5. NATIVE EFFECTS STUDIO (AGSL & CANVAS)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = AmberGold
                        )

                        Text(
                            text = "Dual-engine visual effects with < 1.5 Hz photosensitivity safety and Calm Mode compatibility.",
                            fontSize = 11.sp,
                            color = dynamicColors.textSecondary
                        )

                        // 1. Boss Aura & Mascot Aura Previews
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Boss Aura", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PhoenixCrimson)
                                Box(
                                    modifier = Modifier.size(90.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    BossAuraEffect(size = 80.dp, color = PhoenixCrimson)
                                    Box(
                                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF1E293B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("?", fontSize = 18.sp)
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Mascot Aura", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                                Box(
                                    modifier = Modifier.size(90.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    MascotAuraEffect(
                                        size = 75.dp,
                                        stage = PhoenixStage.DIVINE_PHOENIX,
                                        emotion = MascotEmotion.CELEBRATE,
                                        color = AmberGold
                                    )
                                    Box(
                                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF1E293B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("?", fontSize = 18.sp)
                                    }
                                }
                            }
                        }

                        // 2. Packet Travel Trails Preview
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Packet Travel Trails (TLS / Protocols):", fontSize = 11.sp, color = dynamicColors.textSecondary)
                                Text("${(labPacketProgress * 100).toInt()}%", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = AmberGold)
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F172A))
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                PacketTravelTrails(
                                    progress = labPacketProgress,
                                    packetColor = AmberGold,
                                    modifier = Modifier.fillMaxWidth().height(44.dp)
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { labPacketProgress = (labPacketProgress - 0.2f).coerceAtLeast(0.1f) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                                ) {
                                    Text("? Reverse", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { labPacketProgress = (labPacketProgress + 0.2f).coerceAtMost(0.95f) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                                ) {
                                    Text("Advance ?", fontSize = 10.sp)
                                }
                            }
                        }

                        // 3. One-Shot Interactive Triggers
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { labAvalancheTrigger = !labAvalancheTrigger },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                            ) {
                                Text("Avalanche Ripple", fontSize = 10.5.sp, color = Color.White)
                            }

                            Button(
                                onClick = { labShockwaveTrigger = !labShockwaveTrigger },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PhoenixCrimson)
                            ) {
                                Text("Shockwave Burst", fontSize = 10.5.sp, color = Color.White)
                            }
                        }

                        // Avalanche & Shockwave Preview Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            AvalancheHeatmapRipple(
                                trigger = labAvalancheTrigger,
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0xFF6366F1)
                            )
                            ShockwaveEffect(
                                trigger = labShockwaveTrigger,
                                modifier = Modifier.fillMaxSize(),
                                color = Color.White
                            )
                            Text(
                                text = "TAP BUTTONS ABOVE TO TEST AGSL / CANVAS RIPPLE",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }

        // Active Particle Layers
        if (showExplode) {
            ExplodeHost(
                trigger = true,
                origin = Offset(540f, 960f),
                onComplete = { showExplode = false }
            )
        }

        if (showConfetti) {
            ConfettiHost(
                trigger = true,
                onComplete = { showConfetti = false }
            )
        }
    }
}

