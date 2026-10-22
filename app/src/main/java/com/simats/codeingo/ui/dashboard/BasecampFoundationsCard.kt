package com.simats.codeingo.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

/**
 * 🏕️ BasecampFoundationsCard — Chapter 0: Foundations Primer Banner
 * Displays atop the Learn tab:
 *  - Diagnostic placement launcher
 *  - 3 Micro-Labs quick launch (BitToggler, ModuloClock, XorBoard)
 *  - 5 sequential foundation stages (Levels 101..105)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BasecampFoundationsCard(
    completedStages: Set<String>,
    diagnosticTaken: Boolean,
    recommendedChapter: Int,
    onOpenDiagnostic: () -> Unit,
    onOpenBitLab: () -> Unit,
    onOpenModuloLab: () -> Unit,
    onOpenXorLab: () -> Unit,
    onStartStage: (levelNumber: Int, isBoss: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    var isExpanded by remember { mutableStateOf(true) }

    val slateTheme = Color(0xFF64748B)     // Titanium Slate
    val darkSlate = Color(0xFF334155)
    val amberAccent = Color(0xFFF59E0B)
    val emeraldAccent = Color(0xFF10B981)
    val cyanAccent = Color(0xFF06B6D4)
    val purpleAccent = Color(0xFFA855F7)

    val completedCount = completedStages.size.coerceAtMost(5)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    if (dynamicColors.isDark) {
                        listOf(Color(0xFF1E293B).copy(alpha = 0.95f), Color(0xFF0F172A).copy(alpha = 0.98f))
                    } else {
                        listOf(Color(0xFFF8FAFC), Color(0xFFF1F5F9))
                    }
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(slateTheme.copy(alpha = 0.6f), Color(0xFF94A3B8).copy(alpha = 0.4f))
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // ── TOP HEADER ROW ──────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(slateTheme.copy(alpha = 0.2f))
                            .border(1.dp, slateTheme.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏕️", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CHAPTER 0: BASECAMP",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = dynamicColors.textPrimary
                            )
                        }
                        Text(
                            text = "Foundations Primer • $completedCount/5 Complete",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = slateTheme
                        )
                    }
                }

                // Expand / Collapse Chevron
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(dynamicColors.inputBackground)
                        .clickable { isExpanded = !isExpanded },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle",
                        tint = dynamicColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ── DIAGNOSTIC BANNER ───────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (diagnosticTaken) emeraldAccent.copy(alpha = 0.12f)
                        else amberAccent.copy(alpha = 0.12f)
                    )
                    .border(
                        1.dp,
                        if (diagnosticTaken) emeraldAccent.copy(alpha = 0.4f)
                        else amberAccent.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable(onClick = onOpenDiagnostic)
                    .padding(horizontal = 12.dp, vertical = 9.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (diagnosticTaken) "✅" else "🧭", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (diagnosticTaken) "Diagnostic: Recommended Chapter $recommendedChapter" else "Take Diagnostic Placement Quiz",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (diagnosticTaken) emeraldAccent else amberAccent
                            )
                            Text(
                                text = if (diagnosticTaken) "Tap to retake placement test (0 XP / 0 Hearts)" else "12 quick questions to find your optimal starting point",
                                fontSize = 10.sp,
                                color = dynamicColors.textSecondary
                            )
                        }
                    }
                    Text("START ›", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (diagnosticTaken) emeraldAccent else amberAccent)
                }
            }

            // ── 3 MICRO-LABS SHORTCUT CHIPS ─────────────────────────
            Text(
                text = "INTERACTIVE FOUNDATION LABS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = slateTheme,
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MicroLabChip(
                    icon = "0101",
                    title = "Bit Lab",
                    accentColor = cyanAccent,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenBitLab
                )
                MicroLabChip(
                    icon = "⏰",
                    title = "Modulo",
                    accentColor = amberAccent,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenModuloLab
                )
                MicroLabChip(
                    icon = "⚡",
                    title = "XOR Board",
                    accentColor = purpleAccent,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenXorLab
                )
            }

            // ── EXPANDABLE STAGES LIST (101..105) ───────────────────
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "BASECAMP STAGES (LEVELS 101 - 105)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = slateTheme,
                        letterSpacing = 0.5.sp
                    )

                    BasecampStageRow(
                        stageNumber = 1,
                        levelNumber = 101,
                        title = "Bits, Bytes & Hex",
                        subtitle = "8-bit weights, nibbles & ASCII",
                        isCompleted = completedStages.contains("c0_l1"),
                        isBoss = false,
                        onClick = { onStartStage(101, false) }
                    )
                    BasecampStageRow(
                        stageNumber = 2,
                        levelNumber = 102,
                        title = "Clock Math (Modulo)",
                        subtitle = "Wrap-around rings & negative remainders",
                        isCompleted = completedStages.contains("c0_l2"),
                        isBoss = false,
                        onClick = { onStartStage(102, false) }
                    )
                    BasecampStageRow(
                        stageNumber = 3,
                        levelNumber = 103,
                        title = "XOR Magic",
                        subtitle = "Truth tables, involution & key reuse",
                        isCompleted = completedStages.contains("c0_l3"),
                        isBoss = false,
                        onClick = { onStartStage(103, false) }
                    )
                    BasecampStageRow(
                        stageNumber = 4,
                        levelNumber = 104,
                        title = "Who Attacks What? CIA Triad",
                        subtitle = "Confidentiality, Integrity & Availability",
                        isCompleted = completedStages.contains("c0_l4"),
                        isBoss = false,
                        onClick = { onStartStage(104, false) }
                    )
                    BasecampStageRow(
                        stageNumber = 5,
                        levelNumber = 105,
                        title = "Boss: Basecamp Trial",
                        subtitle = "Timed synthesis challenge",
                        isCompleted = completedStages.contains("c0_boss") || completedStages.contains("c0_l5"),
                        isBoss = true,
                        onClick = { onStartStage(105, true) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MicroLabChip(
    icon: String,
    title: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.12f))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 13.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = dynamicColors.textPrimary
            )
        }
    }
}

@Composable
private fun BasecampStageRow(
    stageNumber: Int,
    levelNumber: Int,
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    isBoss: Boolean,
    onClick: () -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val emeraldAccent = Color(0xFF10B981)
    val amberAccent = Color(0xFFF59E0B)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(dynamicColors.inputBackground)
            .border(
                1.dp,
                if (isCompleted) emeraldAccent.copy(alpha = 0.4f) else dynamicColors.inputBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) emeraldAccent.copy(alpha = 0.2f)
                        else if (isBoss) amberAccent.copy(alpha = 0.2f)
                        else dynamicColors.cardBackground
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = emeraldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                } else if (isBoss) {
                    Text("🔥", fontSize = 13.sp)
                } else {
                    Text("$stageNumber", fontSize = 12.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "Stage $stageNumber: $title",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isCompleted) emeraldAccent.copy(alpha = 0.15f) else dynamicColors.cardBackground)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isCompleted) "COMPLETED ✓" else "START ▶",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = if (isCompleted) emeraldAccent else dynamicColors.textPrimary
            )
        }
    }
}
