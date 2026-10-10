package com.simats.codeingo.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.LessonNodeItem
import com.simats.codeingo.data.model.UnitModel
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AmberGoldDark
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixMotion
import com.simats.codeingo.ui.theme.liquidGlassCard
import com.simats.codeingo.ui.theme.pressScale

/**
 * LessonNodeButton — 3D Bevel Pushable Node Button Component.
 * Exact parity with iOS MainDashboardView.swift pushable3DNodeButton.
 * Features:
 * - 3D physical bevel shadow extrusion with animated compression ($7\text{dp} \to 2\text{dp}$)
 * - Glossy top-rim specular highlight
 * - Active target pulsing aura ring with infinite breath animation
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
    modifier: Modifier = Modifier,
    onCirclePositioned: ((LayoutCoordinates) -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val isBoss = node.isBoss
    val size = if (isBoss) 78.dp else 68.dp

    // Animated spring depth
    val animatedShadowOffset by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 7.dp,
        animationSpec = PhoenixMotion.PressSpringDp,
        label = "shadowDepth"
    )
    val animatedFaceOffset by animateDpAsState(
        targetValue = if (isPressed) 5.dp else 0.dp,
        animationSpec = PhoenixMotion.PressSpringDp,
        label = "faceOffset"
    )

    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val faceColor = when {
        isUnlocked && isBoss -> Color(0xFFFF3B30)
        isUnlocked -> unit.themeColor
        else -> dynamicColors.cardBackground
    }

    val shadowColor = when {
        isUnlocked && isBoss -> Color(0xFFA51919)
        isUnlocked -> unit.themeDarkColor
        else -> if (isDark) Color(0xFF141F28) else Color(0xFFC8D2E2)
    }

    // Infinite breathing scale for active target aura
    val infiniteTransition = rememberInfiniteTransition(label = "auraTransition")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraScale"
    )
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraAlpha"
    )

    Box(
        modifier = modifier
            .offset(x = xOffset)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. ACTIVE UNLOCKED TARGET TOOLTIP (5 Questions • START)
        AnimatedVisibility(
            visible = isActiveTarget && !isLockedSelected,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut() + scaleOut(targetScale = 0.85f),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-96).dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = (-6).dp)
                    .width(240.dp)
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
                        .border(1.2.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = unit.themeColor.copy(alpha = 0.4f))
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

                    // START Action 3D Button inside Tooltip
                    AppButton(
                        title = if (isBoss) "BOSS BATTLE ⚔️" else "START • 5 QUESTIONS",
                        style = if (isBoss) AppButtonStyle.DANGER_CRIMSON else AppButtonStyle.PRIMARY_AMBER,
                        onClick = onStartClick,
                        height = 38.dp,
                        fontSize = 12.5.sp,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 38.dp)
                    )
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
            exit = fadeOut() + scaleOut(targetScale = 0.85f),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-96).dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = (-6).dp)
                    .width(185.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassCard(cornerRadius = 16.dp)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = node.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = dynamicColors.textPrimary,
                        maxLines = 2
                    )

                    Text(
                        text = "Complete previous levels to unlock!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textSecondary
                    )

                    // 3D Pushable Disabled LOCKED Button
                    AppButton(
                        title = "🔒 LOCKED",
                        style = AppButtonStyle.DISABLED,
                        onClick = {},
                        isEnabled = false,
                        modifier = Modifier.fillMaxWidth().height(34.dp)
                    )
                }

                // Downward Pointer Tip
                Canvas(modifier = Modifier.size(width = 14.dp, height = 7.dp)) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(drawContext.size.width / 2f, drawContext.size.height)
                        lineTo(drawContext.size.width, 0f)
                        close()
                    }
                    drawPath(path, dynamicColors.cardBackground)
                }
            }
        }

        // 3. Central 3D Pushable Node Button with Target Ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size + 20.dp)
        ) {
            // Target Pulsing Aura Ring around Active Node
            if (isActiveTarget) {
                Box(
                    modifier = Modifier
                        .size(size + 14.dp)
                        .scale(auraScale)
                        .clip(CircleShape)
                        .border(
                            3.5.dp,
                            Brush.linearGradient(
                                listOf(
                                    AmberGold.copy(alpha = auraAlpha),
                                    unit.themeColor.copy(alpha = auraAlpha),
                                    Color(0xFFFF4026).copy(alpha = auraAlpha)
                                )
                            ),
                            CircleShape
                        )
                )
            }

            // 3D Pushable Button Container
            Box(
                modifier = Modifier
                    .size(width = size + 4.dp, height = size + 8.dp)
                    .onGloballyPositioned { coords ->
                        onCirclePositioned?.invoke(coords)
                    }
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onNodeClick() },
                contentAlignment = Alignment.TopCenter
            ) {
                // 3D Depth Shadow Extrusion
                Box(
                    modifier = Modifier
                        .offset(y = animatedShadowOffset)
                        .size(size)
                        .clip(CircleShape)
                        .background(shadowColor)
                )

                // 3D Top Surface Button Face with glossy top-rim highlight
                Box(
                    modifier = Modifier
                        .offset(y = animatedFaceOffset)
                        .size(size)
                        .clip(CircleShape)
                        .background(faceColor)
                        .drawBehind {
                            if (isUnlocked) {
                                // Glossy top-half highlight arc
                                drawRoundRect(
                                    color = Color.White.copy(alpha = 0.22f),
                                    topLeft = Offset(4.dp.toPx(), 2.dp.toPx()),
                                    size = Size(size.toPx() - 8.dp.toPx(), size.toPx() * 0.45f),
                                    cornerRadius = CornerRadius(size.toPx() / 2, size.toPx() / 2)
                                )
                            }
                        }
                        .border(
                            if (isBoss) 2.5.dp else 2.dp,
                            if (isUnlocked) {
                                if (isBoss) Color(0xFFFFD700).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.35f)
                            } else {
                                dynamicColors.inputBorder
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
                            !isUnlocked -> dynamicColors.textSecondary
                            isBoss -> Color(0xFFFFD700)
                            else -> Color.White
                        },
                        modifier = Modifier.size(if (isBoss) 34.dp else 28.dp)
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
