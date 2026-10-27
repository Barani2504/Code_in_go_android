package com.simats.codeingo.ui.arenas.key_exchange_citadel.levels

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.arenas.common.ArenaBadgeType
import com.simats.codeingo.ui.arenas.common.ArenaHaptics
import com.simats.codeingo.ui.arenas.common.ArenaPalettes
import com.simats.codeingo.ui.arenas.common.ArenaStatusBadge
import com.simats.codeingo.ui.arenas.common.minTouchTarget48
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ClockArithmeticLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current

    val primary = ArenaPalettes.Arena3Primary
    val accent = ArenaPalettes.Arena3Accent

    // Base = 3, Modulo = 7. Exponents: 3^1=3, 3^2=2, 3^3=6
    var currentExponent by remember { mutableIntStateOf(1) }
    val modulus = 7
    val base = 3

    val remainder = remember(currentExponent) {
        var res = 1
        repeat(currentExponent) {
            res = (res * base) % modulus
        }
        res
    }

    val isTargetReached = currentExponent >= 3 && remainder == 6

    val animatedRemainder by animateFloatAsState(
        targetValue = remainder.toFloat(),
        animationSpec = if (reduceMotion) spring(stiffness = 500f) else spring(dampingRatio = 0.7f, stiffness = 200f),
        label = "clockHandAnim"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mission Briefing & Status Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (isDark) ArenaPalettes.Arena3CardDark else dynamicColors.cardBackground)
                .border(1.2.dp, primary.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RotateRight,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "MODULAR CLOCK ARITHMETIC",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = primary
                        )
                    }

                    ArenaStatusBadge(
                        type = if (isTargetReached) ArenaBadgeType.SUCCESS else ArenaBadgeType.INFO,
                        label = if (isTargetReached) "RESIDUE 3³ ≡ 6 ✓" else "EXP 3^$currentExponent mod 7"
                    )
                }

                Text(
                    text = "Observe modular exponentiation over finite ring ℤ/7ℤ. In Diffie-Hellman and RSA, discrete exponentiation wraps values cyclically, creating a hard-to-invert trapdoor.",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // Circular Modular Clock Canvas (Mod 7)
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(if (isDark) ArenaPalettes.Arena3BackgroundDark else Color(0xFFF1F5F9))
                .border(2.dp, primary.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width * 0.38f

                // Draw 7 clock positions around circle (0..6)
                for (i in 0 until 7) {
                    val angle = (2 * Math.PI / 7 * i) - (Math.PI / 2)
                    val x = centerOffset.x + (radius * cos(angle)).toFloat()
                    val y = centerOffset.y + (radius * sin(angle)).toFloat()

                    val isCurrentPosition = i == remainder
                    val nodeColor = if (isCurrentPosition) {
                        if (isTargetReached) ArenaSuccessGreen else accent
                    } else {
                        primary.copy(alpha = 0.35f)
                    }

                    drawCircle(
                        color = nodeColor,
                        radius = if (isCurrentPosition) 16f else 9f,
                        center = Offset(x, y)
                    )
                }

                // Draw animated clock hand to current remainder position
                val handAngle = (2 * Math.PI / 7 * animatedRemainder) - (Math.PI / 2)
                val handX = centerOffset.x + (radius * 0.72f * cos(handAngle)).toFloat()
                val handY = centerOffset.y + (radius * 0.72f * sin(handAngle)).toFloat()

                drawLine(
                    color = if (isTargetReached) ArenaSuccessGreen else accent,
                    start = centerOffset,
                    end = Offset(handX, handY),
                    strokeWidth = 6f
                )

                drawCircle(
                    color = if (isTargetReached) ArenaSuccessGreen else primary,
                    radius = 9f,
                    center = centerOffset
                )
            }

            // Center Exponent & Value HUD
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "3^$currentExponent mod 7",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = SubtextGray
                )
                Text(
                    text = "= $remainder",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isTargetReached) ArenaSuccessGreen else accent
                )
            }
        }

        // Modular Equivalence Step Ribbon
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) ArenaPalettes.Arena3CardDark else Color(0xFFF1F5F9))
                .border(1.dp, primary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = when (currentExponent) {
                    1 -> "Step 1: 3¹ = 3  ➔  3 mod 7 = 3"
                    2 -> "Step 2: 3² = 9  ➔  9 mod 7 = 2 (Wraps past 7)"
                    else -> "Step 3: 3³ = 27 = (3 × 7) + 6  ➔  27 mod 7 = 6 ✓"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = if (isTargetReached) ArenaSuccessGreen else accent,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Interactive Stepper Controls (Enforced 48dp+ touch targets)
        Row(
            modifier = Modifier.fillMaxWidth(0.95f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(1 to "3¹ = 3", 2 to "3² = 2", 3 to "3³ = 6").forEach { (exp, label) ->
                val isSelected = currentExponent == exp
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .minTouchTarget48()
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) primary.copy(alpha = 0.25f)
                            else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                        )
                        .border(
                            1.5.dp,
                            if (isSelected) accent else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .pressScale()
                        .clickable {
                            currentExponent = exp
                            if (exp == 3) {
                                ArenaHaptics.criticalStrike(view, reduceMotion)
                            } else {
                                ArenaHaptics.success(view, reduceMotion)
                            }
                            onActionSuccess()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSelected) accent else dynamicColors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Advance Completion 3D Tactile Button
        Tactile3DButton(
            title = if (isTargetReached) "CLOCK CYCLED (3³ ≡ 6)! ADVANCE ➔" else "STEP TO EXPONENT 3³",
            style = if (isTargetReached) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.DISABLED,
            isEnabled = isTargetReached,
            onClick = {
                if (isTargetReached) {
                    ArenaHaptics.success(view, reduceMotion)
                    onComplete()
                } else {
                    ArenaHaptics.tap(view, reduceMotion)
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .minTouchTarget48()
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}
