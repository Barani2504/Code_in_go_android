package com.simats.codeingo.ui.exercise.labs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/**
 * 🏕️ ModuloClockLab — Chapter 0 Micro-Lab
 * Interactive Modular Arithmetic Clock demonstrating:
 *  - Clock arithmetic wrap-around intuition
 *  - Modulus switching: Mod 12 (Clock), Mod 26 (Caesar Alphabet), Mod 7 (Days)
 *  - Step additions & subtractions (+1, +3, +10, -1, -5)
 *  - Negative modulo resolution: ((x % n) + n) % n
 *  - Live mathematical quotient & remainder breakdown
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModuloClockLab(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    val dynamicColors = LocalDynamicThemeColors.current

    var selectedModulus by remember { mutableIntStateOf(12) } // 12, 26, or 7
    var currentValue by remember { mutableIntStateOf(15) }

    // Mathematical positive remainder
    val remainder = ((currentValue % selectedModulus) + selectedModulus) % selectedModulus
    val quotient = floor(currentValue.toDouble() / selectedModulus).toInt()

    // Smooth angle animation for the clock pointer
    val animatedAngle = remember { Animatable(0f) }
    val targetAngle = (remainder.toFloat() / selectedModulus) * 360f

    LaunchedEffect(targetAngle) {
        animatedAngle.animateTo(
            targetValue = targetAngle,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    val slateAccent = Color(0xFF64748B)
    val amberAccent = Color(0xFFF59E0B)
    val cyanAccent = Color(0xFF06B6D4)
    val emeraldAccent = Color(0xFF10B981)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(dynamicColors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // ── TOP NAV HEADER ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = dynamicColors.textSecondary)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⏰", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "MODULO CLOCK LAB",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = dynamicColors.textPrimary
                    )
                }
                Text(
                    "Chapter 0: Foundations Primer",
                    fontSize = 11.sp,
                    color = slateAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ── SCROLLABLE LAB CONTENT ──────────────────────────────────
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── MODULUS SELECTOR SEGMENT ────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(dynamicColors.inputBackground)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ModulusTab(
                    label = "Mod 12 (Clock)",
                    isSelected = selectedModulus == 12,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedModulus = 12 }
                )
                ModulusTab(
                    label = "Mod 26 (A-Z)",
                    isSelected = selectedModulus == 26,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedModulus = 26 }
                )
                ModulusTab(
                    label = "Mod 7 (Days)",
                    isSelected = selectedModulus == 7,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedModulus = 7 }
                )
            }

            // ── CIRCULAR CLOCK CANVAS ───────────────────────────────
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .clip(CircleShape)
                    .background(dynamicColors.cardBackground)
                    .border(2.dp, slateAccent.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    val radius = size.minDimension / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Draw outer subtle track ring
                    drawCircle(
                        color = slateAccent.copy(alpha = 0.2f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Draw clock notches
                    for (i in 0 until selectedModulus) {
                        val angleRad = (i.toFloat() / selectedModulus) * 2 * PI - (PI / 2)
                        val innerRadius = radius - 14.dp.toPx()
                        val outerRadius = radius - 4.dp.toPx()

                        val start = Offset(
                            x = center.x + innerRadius * cos(angleRad).toFloat(),
                            y = center.y + innerRadius * sin(angleRad).toFloat()
                        )
                        val end = Offset(
                            x = center.x + outerRadius * cos(angleRad).toFloat(),
                            y = center.y + outerRadius * sin(angleRad).toFloat()
                        )

                        val isCurrent = i == remainder
                        drawLine(
                            color = if (isCurrent) amberAccent else slateAccent.copy(alpha = 0.5f),
                            start = start,
                            end = end,
                            strokeWidth = if (isCurrent) 3.dp.toPx() else 1.5.dp.toPx()
                        )
                    }

                    // Draw pointer hand
                    val pointerAngleRad = (animatedAngle.value * (PI / 180f)) - (PI / 2)
                    val pointerLength = radius - 20.dp.toPx()
                    val pointerTip = Offset(
                        x = center.x + pointerLength * cos(pointerAngleRad).toFloat(),
                        y = center.y + pointerLength * sin(pointerAngleRad).toFloat()
                    )

                    drawLine(
                        color = amberAccent,
                        start = center,
                        end = pointerTip,
                        strokeWidth = 3.5.dp.toPx()
                    )

                    // Center pivot knob
                    drawCircle(color = amberAccent, radius = 6.dp.toPx(), center = center)
                    drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = center)
                }

                // Center Remainder Hub
                val centerLabel = if (selectedModulus == 26) {
                    val letter = ('A'.code + remainder).toChar()
                    "'$letter'"
                } else {
                    "$remainder"
                }

                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(dynamicColors.cardBackground.copy(alpha = 0.95f))
                        .border(1.5.dp, amberAccent.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = centerLabel,
                            fontSize = if (selectedModulus == 26) 22.sp else 20.sp,
                            fontWeight = FontWeight.Black,
                            color = amberAccent,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                        Text(
                            text = "mod $selectedModulus",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textSecondary
                        )
                    }
                }
            }

            // ── MATHEMATICAL BREAKDOWN CARD ─────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(dynamicColors.cardBackground)
                    .border(1.5.dp, slateAccent.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "DIVISION THEOREM BREAKDOWN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = slateAccent,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("INPUT X", fontSize = 10.sp, color = dynamicColors.textSecondary, fontWeight = FontWeight.Bold)
                            Text("$currentValue", fontSize = 24.sp, fontWeight = FontWeight.Black, color = cyanAccent, fontFamily = FontFamily.Monospace)
                        }

                        Text("≡", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = slateAccent)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("REMAINDER (R)", fontSize = 10.sp, color = dynamicColors.textSecondary, fontWeight = FontWeight.Bold)
                            Text("$remainder", fontSize = 24.sp, fontWeight = FontWeight.Black, color = amberAccent, fontFamily = FontFamily.Monospace)
                        }

                        Text("(mod $selectedModulus)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = slateAccent)
                    }

                    // Equation string: X = Q * N + R
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(dynamicColors.inputBackground)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "$currentValue = ($quotient × $selectedModulus) + $remainder",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (selectedModulus == 26) {
                        val letterFrom = if (currentValue >= 0) ('A'.code + (currentValue % 26)).toChar() else '?'
                        val letterRemainder = ('A'.code + remainder).toChar()
                        Text(
                            text = "Caesar Alphabet: Position $remainder corresponds to letter '$letterRemainder'",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = emeraldAccent
                        )
                    }
                }
            }

            // ── STEPPER BUTTONS (+ / -) ──────────────────────────────
            Text(
                "ADJUST VALUE (EXPLORE WRAP-AROUND)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = slateAccent,
                letterSpacing = 0.5.sp
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StepperChip("-5") { currentValue -= 5 }
                StepperChip("-1") { currentValue -= 1 }
                StepperChip("+1") { currentValue += 1 }
                StepperChip("+3") { currentValue += 3 }
                StepperChip("+12") { currentValue += 12 }
                StepperChip("+26") { currentValue += 26 }
                StepperChip("Reset (0)") { currentValue = 0 }
            }

            // ── NEGATIVE MODULO LESSON CARD ──────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(dynamicColors.cardBackground)
                    .border(1.dp, slateAccent.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Negative Modulo in Cryptography",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textPrimary
                        )
                    }
                    Text(
                        text = "• In computer languages like Java/Kotlin/C, the remainder operator '%' on negative numbers returns a negative result (e.g. -3 % 12 = -3).\n\n" +
                                "• But in mathematics and cryptography, remainders must always be in the range [0, N-1]!\n\n" +
                                "• To fix this in code, we add N before the second modulo:\n" +
                                "   val r = ((x % N) + N) % N\n" +
                                "   -3 mod 12 ➔ (-3 + 12) = 9 ➔ 9 o'clock!\n" +
                                "   -1 mod 26 ➔ (-1 + 26) = 25 ➔ 'Z'!",
                        fontSize = 12.sp,
                        color = dynamicColors.textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ModulusTab(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val amberAccent = Color(0xFFF59E0B)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) amberAccent.copy(alpha = 0.2f) else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) amberAccent else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
            color = if (isSelected) amberAccent else dynamicColors.textSecondary
        )
    }
}

@Composable
private fun StepperChip(
    label: String,
    onClick: () -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(dynamicColors.inputBackground)
            .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = dynamicColors.textPrimary,
            fontFamily = FontFamily.Monospace
        )
    }
}
