package com.simats.codeingo.ui.arenas.key_exchange_citadel.levels

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale

@Composable
fun CurveWalkerLevel(
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

    // ToyF17Curve: y² ≡ x³ + 2x + 2 (mod 17)
    // 1G = (5, 1)
    // 2G = (6, 3) [Tangent Doubling]
    // 3G = (10, 6) [Chord Addition]
    // 4G = (3, 1) [Public Key Q]
    var currentMultiplier by remember { mutableIntStateOf(1) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    val currentPoint: ArenaCryptoEngine.ECPoint = remember(currentMultiplier) {
        ArenaCryptoEngine.ToyF17Curve.multiply(currentMultiplier, ArenaCryptoEngine.ToyF17Curve.G)
    }

    // Points on the toy curve to render on the coordinate grid
    val curvePoints = remember {
        listOf(
            Offset(5f, 1f), Offset(5f, 16f),
            Offset(6f, 3f), Offset(6f, 14f),
            Offset(10f, 6f), Offset(10f, 11f),
            Offset(3f, 1f), Offset(3f, 16f),
            Offset(0f, 6f), Offset(0f, 11f),
            Offset(7f, 0f), Offset(9f, 1f), Offset(9f, 16f)
        )
    }

    val pointLabel = when (currentMultiplier) {
        1 -> "G = (5, 1)"
        2 -> "2G = (6, 3) [Point Doubled]"
        3 -> "3G = (10, 6) [Point Added]"
        else -> "4G = (3, 1) [Public Key Q]"
    }

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
                    Text(
                        text = "ECC POINT WALKER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary
                    )

                    ArenaStatusBadge(
                        type = if (currentMultiplier >= 4) ArenaBadgeType.SUCCESS else ArenaBadgeType.WARNING,
                        label = if (currentMultiplier >= 4) "ECDLP ESTABLISHED" else "POINT ${currentMultiplier}G"
                    )
                }

                Text(
                    text = "y² ≡ x³ + 2x + 2 (mod 17) • Group Order n = 19.\nWalk the curve via chord-and-tangent group law to compute public key Q = k·G.",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // Active Curve Coordinate Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (currentMultiplier >= 4) ArenaSuccessGreen.copy(alpha = 0.15f) else (if (isDark) ArenaPalettes.Arena3BackgroundDark else Color(0xFFF8FAFC)))
                .border(1.5.dp, if (currentMultiplier >= 4) ArenaSuccessGreen else primary, RoundedCornerShape(14.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "ACTIVE CURVE COORDINATE ($currentMultiplier · G):",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = SubtextGray
                )
                Text(
                    text = if (!currentPoint.isInfinity) "(${currentPoint.x}, ${currentPoint.y}) mod 17" else "O (Infinity)",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (currentMultiplier >= 4) ArenaSuccessGreen else accent
                )
                Text(
                    text = pointLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary
                )
            }
        }

        // Stylized 17x17 Finite Field Canvas
        Box(
            modifier = Modifier
                .size(240.dp, 160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) ArenaPalettes.Arena3BackgroundDark else Color(0xFF0F172A))
                .border(1.dp, primary.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                val gridW = size.width
                val gridH = size.height
                val stepX = gridW / 17f
                val stepY = gridH / 17f

                // Draw curve points
                curvePoints.forEach { pt ->
                    val isCurrent = !currentPoint.isInfinity && pt.x.toInt() == currentPoint.x && pt.y.toInt() == currentPoint.y
                    drawCircle(
                        color = if (isCurrent) accent else primary.copy(alpha = 0.45f),
                        radius = if (isCurrent) 6.5f else 3.5f,
                        center = Offset(pt.x * stepX, gridH - (pt.y * stepY))
                    )
                }

                // If current point exists, highlight with ripple
                if (!currentPoint.isInfinity) {
                    val cx = currentPoint.x * stepX
                    val cy = gridH - (currentPoint.y * stepY)
                    drawCircle(
                        color = accent.copy(alpha = 0.3f),
                        radius = 12f,
                        center = Offset(cx, cy)
                    )
                }
            }
        }

        // Stepper Breadcrumb Ribbon
        Row(
            modifier = Modifier.fillMaxWidth(0.95f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) { i ->
                val stepIdx = i + 1
                val isReached = currentMultiplier >= stepIdx
                val isCurrent = currentMultiplier == stepIdx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isCurrent) accent
                            else if (isReached) ArenaSuccessGreen.copy(alpha = 0.2f)
                            else dynamicColors.cardBorder.copy(alpha = 0.2f)
                        )
                        .border(
                            1.dp,
                            if (isCurrent) Color.White
                            else if (isReached) ArenaSuccessGreen
                            else dynamicColors.cardBorder,
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${stepIdx}G",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isCurrent) Color.Black else dynamicColors.textPrimary
                    )
                }
            }
        }

        // Interactive Cryptographic Questions for Each Step
        when (currentMultiplier) {
            1 -> {
                // Moving 1G -> 2G (Tangent Doubling)
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "HOW DO WE COMPUTE 2G = G + G AT POINT (5, 1)?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "Point Doubling: Tangent line slope λ = (3x² + a)/(2y)" to true,
                        "Chord Addition: Secant line between distinct points" to false,
                        "Scalar Division by 2" to false
                    ).forEach { (opt, isCorrect) ->
                        val isSelected = selectedOption == opt
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .minTouchTarget48()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) (if (isCorrect) ArenaSuccessGreen else ArenaDangerRed)
                                    else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                )
                                .border(1.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                                .pressScale()
                                .clickable {
                                    selectedOption = opt
                                    if (isCorrect) {
                                        ArenaHaptics.success(view, reduceMotion)
                                        currentMultiplier = 2
                                        selectedOption = null
                                        feedbackMessage = "✓ Tangent slope λ ≡ 13 mod 17 yields 2G = (6, 3)!"
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackMessage = "❌ When adding a point to itself, use the tangent slope formula."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected && isCorrect) Color.Black else dynamicColors.textPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                        }
                    }
                }
            }
            2 -> {
                // Moving 2G -> 3G (Chord Addition)
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "HOW DO WE ADD DISTINCT POINTS 2G(6, 3) + G(5, 1)?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "Chord Addition: Slope λ = (y₂ - y₁) / (x₂ - x₁)" to true,
                        "Point Doubling on 2G" to false,
                        "Direct Vector Addition (6+5, 3+1)" to false
                    ).forEach { (opt, isCorrect) ->
                        val isSelected = selectedOption == opt
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .minTouchTarget48()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) (if (isCorrect) ArenaSuccessGreen else ArenaDangerRed)
                                    else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                )
                                .border(1.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                                .pressScale()
                                .clickable {
                                    selectedOption = opt
                                    if (isCorrect) {
                                        ArenaHaptics.success(view, reduceMotion)
                                        currentMultiplier = 3
                                        selectedOption = null
                                        feedbackMessage = "✓ Chord line between (6, 3) and (5, 1) intersects at (10, 6)!"
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackMessage = "❌ For distinct points P ≠ Q, use the chord slope formula."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected && isCorrect) Color.Black else dynamicColors.textPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                        }
                    }
                }
            }
            3 -> {
                // Moving 3G -> 4G (ECDLP Intractability)
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "WHY IS EXTRACTING PRIVATE k FROM PUBLIC KEY Q = k·G HARD?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "ECDLP has no known sub-exponential algorithm on general curves" to true,
                        "Elliptic points cannot be added together" to false,
                        "Modulo 17 is impossible to compute" to false
                    ).forEach { (opt, isCorrect) ->
                        val isSelected = selectedOption == opt
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .minTouchTarget48()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) (if (isCorrect) ArenaSuccessGreen else ArenaDangerRed)
                                    else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                )
                                .border(1.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                                .pressScale()
                                .clickable {
                                    selectedOption = opt
                                    if (isCorrect) {
                                        ArenaHaptics.criticalStrike(view, reduceMotion)
                                        currentMultiplier = 4
                                        selectedOption = null
                                        feedbackMessage = "🎉 Correct! Public Q=4G=(3,1) forged! Finding k=4 from Q is the ECDLP."
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackMessage = "❌ ECDLP hardness protects ECC keys against classical attacks!"
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected && isCorrect) Color.Black else dynamicColors.textPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                        }
                    }
                }
            }
            else -> {
                // Multiplier 4: Advance
                Tactile3DButton(
                    title = "ECDLP MASTERED! ADVANCE CITADEL ➔",
                    style = Tactile3DButtonColor.GREEN,
                    onClick = {
                        ArenaHaptics.success(view, reduceMotion)
                        onComplete()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .minTouchTarget48()
                )
            }
        }

        if (feedbackMessage != null) {
            Text(
                text = feedbackMessage!!,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (feedbackMessage!!.startsWith("✓") || feedbackMessage!!.startsWith("🎉")) ArenaSuccessGreen else ArenaDangerRed,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
