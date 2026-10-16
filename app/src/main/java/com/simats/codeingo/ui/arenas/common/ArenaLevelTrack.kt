package com.simats.codeingo.ui.arenas.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.pressScale

/**
 * Universal station representation for level selector ribbons across all 5 arenas.
 */
data class ArenaStationNode(
    val id: Int,
    val title: String,
    val shortName: String,
    val iconEmoji: String,
    val isBoss: Boolean = (id == 7)
)

/**
 * ⚡ Compact & High-Contrast Cyber Arena Level Track
 * Designed to maximize displayable screen space for interactive level content:
 * - Sleek 48.dp horizontal capsule pods (saves ~40dp vertical space)
 * - High-contrast light and dark mode colors (no washed out elements)
 * - Auto-scroll to current level
 * - Instant feedback with active aura pulse and star indicators
 */
@Composable
fun ArenaLevelTrack(
    stations: List<ArenaStationNode>,
    currentLevelId: Int,
    unlockedLevels: Set<Int>,
    levelStars: Map<Int, Int>,
    accentColor: Color,
    onSelectLevel: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val listState = rememberLazyListState()

    // High contrast accent for light mode readability
    val effectiveAccent = if (isDark) accentColor else {
        if (accentColor == AmberGold) Color(0xFFD97706) else accentColor
    }

    LaunchedEffect(currentLevelId) {
        val index = stations.indexOfFirst { it.id == currentLevelId }
        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pod_glow")
    val activeGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // High-tech circuit guide rail beneath the pods
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.Center)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            effectiveAccent.copy(alpha = 0.08f),
                            effectiveAccent.copy(alpha = if (isDark) 0.40f else 0.25f),
                            if (stations.any { it.isBoss }) DuolingoRed.copy(alpha = 0.45f) else effectiveAccent.copy(alpha = 0.40f),
                            effectiveAccent.copy(alpha = 0.08f)
                        )
                    )
                )
        )

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(stations) { _, station ->
                val isSelected = station.id == currentLevelId
                val isUnlocked = unlockedLevels.contains(station.id)
                val starsEarned = levelStars[station.id] ?: 0
                val isBoss = station.isBoss

                val podBorderColor by animateColorAsState(
                    targetValue = when {
                        isSelected && isBoss -> DuolingoRed
                        isSelected -> effectiveAccent
                        isBoss -> DuolingoRed.copy(alpha = if (isDark) 0.6f else 0.45f)
                        isUnlocked -> if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                        else -> if (isDark) Color(0xFF1E293B).copy(alpha = 0.4f) else Color(0xFFE2E8F0)
                    },
                    label = "border_color"
                )

                val podBg = when {
                    isSelected && isBoss -> DuolingoRed.copy(alpha = if (isDark) 0.22f else 0.12f)
                    isSelected -> effectiveAccent.copy(alpha = if (isDark) 0.20f else 0.12f)
                    isBoss -> DuolingoRed.copy(alpha = if (isDark) 0.08f else 0.05f)
                    isUnlocked -> if (isDark) Color(0xFF0F172A).copy(alpha = 0.88f) else Color.White
                    else -> if (isDark) Color(0xFF0F172A).copy(alpha = 0.40f) else Color(0xFFF8FAFC)
                }

                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .pressScale(0.95f)
                        .then(
                            if (isSelected) {
                                Modifier.shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(12.dp),
                                    ambientColor = if (isBoss) DuolingoRed else effectiveAccent,
                                    spotColor = if (isBoss) DuolingoRed else effectiveAccent
                                )
                            } else Modifier
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(podBg)
                        .border(
                            width = if (isSelected) 1.8.dp else 1.dp,
                            color = podBorderColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = isUnlocked) {
                            onSelectLevel(station.id)
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Left: Icon Emoji
                        Text(
                            text = station.iconEmoji,
                            fontSize = 17.sp,
                            modifier = Modifier.scale(if (isSelected) 1.1f else 1.0f)
                        )

                        // Center: Station Code + Codename
                        Column(verticalArrangement = Arrangement.Center) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (isBoss) "APEX" else "0${station.id}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected) {
                                        if (isBoss) DuolingoRed else effectiveAccent
                                    } else {
                                        if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                    }
                                )

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .scale(activeGlowAlpha)
                                            .clip(CircleShape)
                                            .background(if (isBoss) DuolingoRed else effectiveAccent)
                                    )
                                }
                            }

                            Text(
                                text = station.shortName,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) {
                                    if (isDark) Color.White else Color(0xFF0F172A)
                                } else {
                                    if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Right: Status indicator (Stars or Lock)
                        if (!isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                                modifier = Modifier.size(11.dp)
                            )
                        } else if (starsEarned > 0) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(minOf(3, starsEarned)) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (isDark) AmberGold else Color(0xFFD97706),
                                        modifier = Modifier.size(8.dp)
                                    )
                                }
                            }
                        } else if (isUnlocked && !isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1))
                            )
                        }
                    }
                }
            }
        }
    }
}
