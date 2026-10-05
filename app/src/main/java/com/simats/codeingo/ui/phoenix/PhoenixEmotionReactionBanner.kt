package com.simats.codeingo.ui.phoenix

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppShortcut
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.PhoenixEmotionManager
import kotlin.math.roundToInt

// ══════════════════════════════════════════════════════════════════
// ⚡ PhoenixEmotionReactionBanner — Floating Reactive HUD Banner
// Exact Parity with iOS PhoenixEmotionReactionBanner.swift
// ══════════════════════════════════════════════════════════════════

@Composable
fun PhoenixEmotionReactionBanner(
    modifier: Modifier = Modifier
) {
    val emotionManager = PhoenixEmotionManager.instance
    val showToast by emotionManager.showReactionToast.collectAsState()
    val emotion by emotionManager.currentEmotion.collectAsState()
    val lastReason by emotionManager.lastTriggerReason.collectAsState()
    val isAutoEnabled by emotionManager.isAutoEmotionEnabled.collectAsState()

    var showPickerSheet by remember { mutableStateOf(false) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    AnimatedVisibility(
        visible = showToast,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .offset { IntOffset(0, offsetY.roundToInt()) }
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        if (delta < 0 || offsetY < 0) {
                            offsetY += delta
                        }
                    },
                    onDragStopped = {
                        if (offsetY < -40f) {
                            emotionManager.dismissToast()
                        }
                        offsetY = 0f
                    }
                )
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF121B2C).copy(alpha = 0.96f))
                .border(1.5.dp, emotion.auraColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.4f))
                .clickable { showPickerSheet = true }
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Mini 3D Squircle Mascot Avatar
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0D1A2D))
                        .border(1.5.dp, emotion.auraColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = emotion.auraColor.copy(alpha = 0.5f))
                ) {
                    PhoenixMascotImage(
                        emotion = emotion,
                        size = 40.dp,
                        modifier = Modifier.scale(pulseScale)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Emotion Detail & Quote
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = emotion.emoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = emotion.title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "•",
                            color = emotion.auraColor.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = emotion.feeling,
                            color = emotion.auraColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        if (isAutoEnabled) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(emotion.auraColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AppShortcut,
                                    contentDescription = null,
                                    tint = emotion.auraColor,
                                    modifier = Modifier.size(9.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Logo Synced",
                                    color = emotion.auraColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "\"${emotion.quote}\"",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(1.dp))

                    Text(
                        text = lastReason,
                        color = emotion.auraColor.copy(alpha = 0.9f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }

    if (showPickerSheet) {
        PhoenixEmotionPickerSheet(
            onDismiss = { showPickerSheet = false }
        )
    }
}
