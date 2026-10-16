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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mission Briefing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (isDark) Color(0xFF10192A) else dynamicColors.cardBackground)
                .border(1.2.dp, AmberGold.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RotateRight,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "MODULAR ARITHMETIC CLOCK LAB",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = "Step through powers of Base 3 in finite field ℤ/7ℤ. Watch the clock hand cycle around the 7-node ring: 3¹ ➔ 3² ➔ 3³ mod 7.",
                    fontSize = 12.5.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        // Circular Modular Clock Canvas (Mod 7)
        Box(
            modifier = Modifier
                .size(230.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0xFF090E17) else Color(0xFFF8FAFC))
                .border(2.dp, AmberGold.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(210.dp)) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width * 0.40f

                // Draw 7 clock positions around circle
                for (i in 0 until 7) {
                    val angle = (2 * Math.PI / 7 * i) - (Math.PI / 2)
                    val x = centerOffset.x + (radius * cos(angle)).toFloat()
                    val y = centerOffset.y + (radius * sin(angle)).toFloat()

                    val isCurrentPosition = i == remainder
                    val nodeColor = if (isCurrentPosition) {
                        if (isTargetReached) DuolingoGreen else AmberGold
                    } else {
                        Color(0xFF64748B).copy(alpha = 0.4f)
                    }

                    drawCircle(
                        color = nodeColor,
                        radius = if (isCurrentPosition) 16f else 10f,
                        center = Offset(x, y)
                    )
                }

                // Draw clock hand to current remainder position
                val handAngle = (2 * Math.PI / 7 * remainder) - (Math.PI / 2)
                val handX = centerOffset.x + (radius * 0.75f * cos(handAngle)).toFloat()
                val handY = centerOffset.y + (radius * 0.75f * sin(handAngle)).toFloat()

                drawLine(
                    color = if (isTargetReached) DuolingoGreen else AmberGold,
                    start = centerOffset,
                    end = Offset(handX, handY),
                    strokeWidth = 6f
                )

                drawCircle(
                    color = if (isTargetReached) DuolingoGreen else AmberGold,
                    radius = 8f,
                    center = centerOffset
                )
            }

            // Center Exponent & Value HUD
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "3^$currentExponent mod 7",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = SubtextGray
                )
                Text(
                    text = "= $remainder",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isTargetReached) DuolingoGreen else AmberGold
                )
            }
        }

        // Modular Equivalence Step Ribbon
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) Color(0xFF131D33) else Color(0xFFF1F5F9))
                .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
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
                color = if (isTargetReached) DuolingoGreen else AmberGold
            )
        }

        // Interactive Stepper Controls
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(1 to "3¹ = 3", 2 to "3² = 2", 3 to "3³ = 6").forEach { (exp, label) ->
                val isSelected = currentExponent == exp
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) AmberGold.copy(alpha = 0.25f)
                            else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                        )
                        .border(
                            1.5.dp,
                            if (isSelected) AmberGold else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .pressScale()
                        .clickable {
                            currentExponent = exp
                            onActionSuccess()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSelected) AmberGold else dynamicColors.textPrimary
                    )
                }
            }
        }

        // Advance Completion Button
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isTargetReached) Brush.horizontalGradient(listOf(DuolingoGreen, Color(0xFF059669)))
                    else Brush.horizontalGradient(listOf(Color(0xFF64748B), Color(0xFF475569)))
                )
                .pressScale(targetScale = 0.96f)
                .clickable(enabled = isTargetReached) {
                    onComplete()
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isTargetReached) "CLOCK CYCLED (3³ ≡ 6)! ADVANCE →" else "STEP TO EXPONENT 3³",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
