package com.simats.codeingo.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.LessonNodeItem
import com.simats.codeingo.data.model.UnitModel
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AmberGoldDark

/**
 * LessonNodeButton — 3D Bevel Pushable Node Button Component.
 * Exact parity with iOS MainDashboardView.swift pushable3DNodeButton.
 * Features:
 * - 3D physical bevel shadow extrusion with animated compression
 * - Active target pulsing aura ring
 * - Active target start tooltip with downward triangle pointer
 * - Locked node explanation popover with 3D disabled LOCKED button
 */
@Composable
fun LessonNodeButton(
    node: LessonNodeItem,
    unit: UnitModel,
    isUnlocked: Boolean,
    isActiveTarget: Boolean,
    isLockedSelected: Boolean,
    xOffset: Dp = 0.dp,
    onNodeClick: () -> Unit,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val isBoss = node.isBoss
    val size = if (isBoss) 76.dp else 68.dp
    val shadowOffset = if (isPressed) 2.dp else 7.dp
    val faceOffset = if (isPressed) 5.dp else 0.dp

    val faceColor = when {
        isUnlocked && isBoss -> Color(0xFFFF3B30)
        isUnlocked -> unit.themeColor
        else -> Color(0xFF1E2832)
    }

    val shadowColor = when {
        isUnlocked && isBoss -> Color(0xFFA51919)
        isUnlocked -> unit.themeDarkColor
        else -> Color(0xFF141F28)
    }

    Column(
        modifier = modifier
            .offset(x = xOffset)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. ACTIVE UNLOCKED TARGET TOOLTIP (5 Questions • START)
        AnimatedVisibility(
            visible = isActiveTarget && !isLockedSelected,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut() + scaleOut(targetScale = 0.85f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = (-6).dp)
                    .width(195.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(unit.themeColor, unit.themeDarkColor)
                            )
                        )
                        .border(1.2.dp, Color.White.copy(alpha = 0.40f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = node.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = if (node.levelNumber <= 2) "🥚" else "🔥", fontSize = 11.sp)
                        Text(
                            text = "5 Questions • +${unit.unitNumber * 5 + 5} XP • +1 Ember",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }

                    // START Action Button inside Tooltip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isBoss) Color.Red else Color.White)
                            .clickable { onStartClick() }
                            .padding(vertical = 7.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isBoss) Icons.Default.LocalFireDepartment else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isBoss) Color.White else unit.themeColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isBoss) "BOSS BATTLE ⚔️" else "START • 5 QUESTIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isBoss) Color.White else unit.themeColor
                        )
                    }
                }

                // Downward Pointer Tip Triangle
                Canvas(modifier = Modifier.size(width = 14.dp, height = 7.dp)) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(drawContext.size.width / 2f, drawContext.size.height)
                        lineTo(drawContext.size.width, 0f)
                        close()
                    }
                    drawPath(path, unit.themeDarkColor)
                }
            }
        }

        // 2. LOCKED LEVEL POPOVER TOOLTIP
        AnimatedVisibility(
            visible = isLockedSelected,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut() + scaleOut(targetScale = 0.85f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = (-6).dp)
                    .width(180.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E2832))
                        .border(1.2.dp, Color(0xFF2E3E4E), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = node.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 2
                    )

                    Text(
                        text = "Complete previous levels to unlock!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.6f)
                    )

                    // 3D Pushable Dark Gray Disabled LOCKED Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .offset(y = 2.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF141F26))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1C2B35))
                                .border(1.dp, Color(0xFF2E3E4E), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "LOCKED",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // Downward Pointer Tip
                Canvas(modifier = Modifier.size(width = 14.dp, height = 7.dp)) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(drawContext.size.width / 2f, drawContext.size.height)
                        lineTo(drawContext.size.width, 0f)
                        close()
                    }
                    drawPath(path, Color(0xFF1E2832))
                }
            }
        }

        // 3. Central 3D Pushable Node Button with Target Ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size + 16.dp)
        ) {
            // Target Aura Ring around Active Node
            if (isActiveTarget) {
                Box(
                    modifier = Modifier
                        .size(size + 14.dp)
                        .clip(CircleShape)
                        .border(
                            3.5.dp,
                            Brush.linearGradient(
                                listOf(AmberGold, unit.themeColor, Color(0xFFFF4026))
                            ),
                            CircleShape
                        )
                )
            }

            // 3D Pushable Button Container
            Box(
                modifier = Modifier
                    .size(width = size + 4.dp, height = size + 8.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onNodeClick() },
                contentAlignment = Alignment.TopCenter
            ) {
                // 3D Depth Shadow Extrusion
                Box(
                    modifier = Modifier
                        .offset(y = shadowOffset)
                        .size(size)
                        .clip(CircleShape)
                        .background(shadowColor)
                )

                // 3D Top Surface Button Face
                Box(
                    modifier = Modifier
                        .offset(y = faceOffset)
                        .size(size)
                        .clip(CircleShape)
                        .background(faceColor)
                        .border(
                            if (isBoss) 2.5.dp else 2.dp,
                            if (isUnlocked) {
                                if (isBoss) Color(0xFFFFD700).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.28f)
                            } else {
                                Color(0xFF2E3E4E)
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val iconVector = resolveNodeIcon(node.icon, isUnlocked, isBoss)
                    Icon(
                        imageVector = iconVector,
                        contentDescription = node.title,
                        tint = when {
                            !isUnlocked -> Color.White.copy(alpha = 0.35f)
                            isBoss -> Color(0xFFFFD700)
                            else -> Color.White
                        },
                        modifier = Modifier.size(if (isBoss) 32.dp else 26.dp)
                    )
                }
            }
        }
    }
}

private fun resolveNodeIcon(iconName: String, isUnlocked: Boolean, isBoss: Boolean): ImageVector {
    if (!isUnlocked) return Icons.Default.Lock
    if (isBoss) return Icons.Default.LocalFireDepartment
    return when {
        iconName.contains("grid", ignoreCase = true) -> Icons.Default.GridOn
        iconName.contains("search", ignoreCase = true) || iconName.contains("magnifyingglass", ignoreCase = true) -> Icons.Default.Search
        iconName.contains("link", ignoreCase = true) -> Icons.Default.Link
        iconName.contains("stack", ignoreCase = true) -> Icons.Default.ViewStream
        iconName.contains("tree", ignoreCase = true) || iconName.contains("crown", ignoreCase = true) -> Icons.Default.AccountTree
        iconName.contains("gauge", ignoreCase = true) -> Icons.Default.Speed
        else -> Icons.Default.AutoAwesome
    }
}
