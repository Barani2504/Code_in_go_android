package com.simats.codeingo.ui.phoenix

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.PhoenixEmotion
import com.simats.codeingo.data.model.PhoenixEmotionCategory
import com.simats.codeingo.data.model.allPhoenixEmotions
import com.simats.codeingo.data.model.filtered
import com.simats.codeingo.domain.EmotionRule
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

// ══════════════════════════════════════════════════════════════════
// 📱 PhoenixEmotionPickerSheet — 28 Mascot Catalog & Rule Tester
// Exact Parity with iOS PhoenixEmotionPickerSheet.swift
// ══════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PhoenixEmotionPickerSheet(
    onDismiss: () -> Unit
) {
    val emotionManager = PhoenixEmotionManager.instance
    val currentEmotion by emotionManager.currentEmotion.collectAsState()
    val isAutoEnabled by emotionManager.isAutoEmotionEnabled.collectAsState()
    val activeRule by emotionManager.activeRule.collectAsState()

    var selectedCategory by remember { mutableStateOf(PhoenixEmotionCategory.ALL) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0D1626),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "App Icon & Emotions",
                    color = LocalDynamicThemeColors.current.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.6f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. Hero Active App Icon Showcase
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Radial Aura Glow
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .scale(glowScale)
                            .clip(CircleShape)
                            .background(currentEmotion.auraColor.copy(alpha = 0.25f))
                    )

                    // 3D Squircle Preview
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(112.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(Color(0xFF0D1A2D))
                            .border(1.5.dp, Color(0xFF2A3D59), RoundedCornerShape(26.dp))
                            .shadow(12.dp, RoundedCornerShape(26.dp), spotColor = currentEmotion.auraColor)
                    ) {
                        PhoenixMascotImage(
                            emotion = currentEmotion,
                            size = 88.dp
                        )
                    }
                }

                // Emotion Info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = currentEmotion.emoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentEmotion.title,
                            color = LocalDynamicThemeColors.current.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${currentEmotion.quote}\"",
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 2. Auto Emotion Mode Card
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF131F33))
                        .border(1.dp, Color(0xFF20324E), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Auto Dynamic Emotion",
                                color = LocalDynamicThemeColors.current.textPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mascot and logo automatically react to streaks, time of day, and quiz results.",
                            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                    Switch(
                        checked = isAutoEnabled,
                        onCheckedChange = { emotionManager.syncAutoEmotion() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = LocalDynamicThemeColors.current.textPrimary,
                            checkedTrackColor = AmberGold
                        )
                    )
                }

                // 3. Quick Simulation Interactive Tester Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF131F33))
                        .border(1.dp, Color(0xFF20324E), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = Color(0xFF00C8FF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Live Simulation Triggers",
                            color = LocalDynamicThemeColors.current.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val testTriggers = listOf(
                        "user_went_out_unpracticed" to "Left Without Practice 😒",
                        "user_went_out_goal_met" to "Goal Completed 😌",
                        "zero_hearts" to "0 Hearts Left 😭",
                        "one_heart_left" to "1 Heart Left 😮‍💨",
                        "perfect_quiz" to "100% Perfect Quiz 🤩",
                        "streak_risk" to "11 PM Streak Danger 💢",
                        "eureka_idea" to "5x Eureka Spark 💡",
                        "boss_battle" to "Boss Battle 🚩",
                        "hearts_refilled" to "Hearts Refilled 🥹"
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for ((triggerKey, label) in testTriggers) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color(0xFF1C2C45))
                                    .border(1.dp, Color(0xFF2D4263), RoundedCornerShape(100.dp))
                                    .clickable { emotionManager.simulateTrigger(triggerKey) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = LocalDynamicThemeColors.current.textPrimary,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // 4. Category Filter Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (category in PhoenixEmotionCategory.entries) {
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(if (isSelected) AmberGold else Color(0xFF182438))
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category.displayName,
                                color = if (isSelected) Color(0xFF1A1205) else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 5. Grid of 28 Emotion Icons
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 4
                ) {
                    for (emotion in selectedCategory.filtered()) {
                        val isCurrent = emotion.id == currentEmotion.id
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(74.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isCurrent) Color(0xFF1E314D) else Color(0xFF131E30))
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) emotion.auraColor else Color(0xFF243652),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    emotionManager.setEmotion(
                                        emotion = emotion,
                                        reason = "Manual: ${emotion.title}",
                                        updateAppIcon = true,
                                        showToast = true
                                    )
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp)
                        ) {
                            PhoenixMascotImage(
                                emotion = emotion,
                                size = 48.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = emotion.feeling,
                                color = if (isCurrent) emotion.auraColor else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}
