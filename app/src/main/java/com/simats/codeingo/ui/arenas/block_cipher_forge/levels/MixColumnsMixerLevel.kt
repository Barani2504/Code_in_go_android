package com.simats.codeingo.ui.arenas.block_cipher_forge.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.delay

@Composable
fun MixColumnsMixerLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val view = LocalView.current
    val reduceMotion = LocalReduceMotion.current
    val isDark = dynamicColors.isDark

    val primary = ArenaPalettes.Arena2Primary
    val accent = ArenaPalettes.Arena2Accent
    val cardBg = if (isDark) ArenaPalettes.Arena2CardDark else dynamicColors.cardBackground

    // NIST FIPS-197 known column vector: [0xdb, 0x13, 0x53, 0x45] -> [0x8e, 0x4d, 0xa1, 0xbc]
    val inputCol = remember { intArrayOf(0xdb, 0x13, 0x53, 0x45) }
    val expectedOutputCol = remember { ArenaCryptoEngine.mixColumn(inputCol).toList() }

    // User chosen coefficient offsets for Row 1: target is [2, 3, 1, 1]
    var c0 by remember { mutableIntStateOf(1) }
    var c1 by remember { mutableIntStateOf(1) }
    var c2 by remember { mutableIntStateOf(2) }
    var c3 by remember { mutableIntStateOf(3) }

    val isCoefficientsAligned = (c0 == 2 && c1 == 3 && c2 == 1 && c3 == 1)
    var reactorRunning by remember { mutableStateOf(false) }

    LaunchedEffect(reactorRunning) {
        if (reactorRunning) {
            delay(400L)
            reactorRunning = false
            if (isCoefficientsAligned) {
                ArenaHaptics.success(view, reduceMotion)
                onActionSuccess()
                onComplete()
            } else {
                ArenaHaptics.error(view, reduceMotion)
                onDeductHeart()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mission Header with Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GALOIS FIELD GF(2⁸) MIXER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = accent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "MixColumns MDS Diffusion",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary
                )
            }
            ArenaStatusBadge(
                type = if (isCoefficientsAligned) ArenaBadgeType.SUCCESS else ArenaBadgeType.WARNING,
                label = if (isCoefficientsAligned) "MDS ROW ALIGNED" else "COEFFICIENTS UNBALANCED"
            )
        }

        // Educational Explanation Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Maximum Distance Separable (MDS) Property:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = primary
                )
                Text(
                    text = "MixColumns treats each 4-byte column as a polynomial over GF(2⁸) modulo x⁴+1, multiplied by the circulant MDS matrix. Row 1 coefficients must be {02, 03, 01, 01}.",
                    fontSize = 11.sp,
                    color = dynamicColors.textSecondary,
                    lineHeight = 16.sp
                )
                Text(
                    text = "Irreducible Polynomial: P(x) = x⁸ + x⁴ + x³ + x + 1 (0x11B)",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = accent
                )
            }
        }

        // Vessel Chamber: Input Column vs MDS Row vs Output Column
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .border(
                    1.dp,
                    if (isCoefficientsAligned) ArenaSuccessGreen.copy(alpha = 0.6f) else dynamicColors.cardBorder,
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Input Column
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "INPUT COL",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                inputCol.forEach { b ->
                    Text(
                        text = "0x" + b.toString(16).uppercase().padStart(2, '0'),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                }
            }

            Text(text = "⊗", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primary)

            // MDS Matrix coefficients Row 1
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "MDS ROW 1",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "[ $c0 , $c1 , $c2 , $c3 ]",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = if (isCoefficientsAligned) ArenaSuccessGreen else accent
                )
                Text(
                    text = if (isCoefficientsAligned) "✓ VALID MDS" else "TARGET: 2,3,1,1",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCoefficientsAligned) ArenaSuccessGreen else ArenaDangerRed
                )
            }

            Text(text = "=", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primary)

            // Output Column
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "OUTPUT COL",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCoefficientsAligned) ArenaSuccessGreen else dynamicColors.textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                expectedOutputCol.forEach { b ->
                    Text(
                        text = if (isCoefficientsAligned) "0x" + b.toString(16).uppercase().padStart(2, '0') else "0x??",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isCoefficientsAligned) ArenaSuccessGreen else dynamicColors.textSecondary
                    )
                }
            }
        }

        // MDS Matrix Row Dial Controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tap to Adjust MDS Mixing Coefficients for Row 1:",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = dynamicColors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("c0 (x·S₀)", c0) { c0 = if (c0 == 2) 1 else 2 },
                    Triple("c1 (x+1)·S₁", c1) { c1 = if (c1 == 3) 1 else 3 },
                    Triple("c2 (1·S₂)", c2) { c2 = if (c2 == 1) 2 else 1 },
                    Triple("c3 (1·S₃)", c3) { c3 = if (c3 == 1) 3 else 1 }
                ).forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .minTouchTarget48()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if ((item.first.startsWith("c0") && c0 == 2) ||
                                    (item.first.startsWith("c1") && c1 == 3) ||
                                    (item.first.startsWith("c2") && c2 == 1) ||
                                    (item.first.startsWith("c3") && c3 == 1)
                                ) ArenaSuccessGreen.copy(alpha = 0.15f)
                                else dynamicColors.cardBorder.copy(alpha = 0.25f)
                            )
                            .border(
                                1.5.dp,
                                if ((item.first.startsWith("c0") && c0 == 2) ||
                                    (item.first.startsWith("c1") && c1 == 3) ||
                                    (item.first.startsWith("c2") && c2 == 1) ||
                                    (item.first.startsWith("c3") && c3 == 1)
                                ) ArenaSuccessGreen else dynamicColors.cardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                ArenaHaptics.tap(view, reduceMotion)
                                item.third()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.first.substringBefore(" "),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = dynamicColors.textSecondary
                            )
                            Text(
                                text = "${item.second}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = primary
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isCoefficientsAligned,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ArenaSuccessGreen.copy(alpha = 0.15f))
                    .border(1.dp, ArenaSuccessGreen, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓ Valid AES MDS Matrix: {02, 03, 01, 01} locks Galois diffusion!",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArenaSuccessGreen,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        // Reactor Lever Button
        Tactile3DButton(
            title = if (reactorRunning) "Diffusing Columns in GF(2⁸)..." else if (isCoefficientsAligned) "Engage Galois Reactor ✓" else "Verify MDS Row {2,3,1,1}",
            style = if (isCoefficientsAligned) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                reactorRunning = true
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
