package com.simats.codeingo.ui.arenas.key_exchange_citadel.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun CurveWalkerLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // Verified with ArenaCryptoEngine.ToyF17Curve:
    // Curve: y² ≡ x³ + 2x + 2 (mod 17)
    // Base Generator G = (5, 1)
    // 2G = (6, 3) via point doubling
    // 3G = (10, 6) via point addition
    // 4G = (3, 1) via point addition
    var currentMultiplier by remember { mutableIntStateOf(1) }

    val currentPoint: ArenaCryptoEngine.ECPoint = remember(currentMultiplier) {
        ArenaCryptoEngine.ToyF17Curve.multiply(currentMultiplier, ArenaCryptoEngine.ToyF17Curve.G)
    }

    val pointLabel = when (currentMultiplier) {
        1 -> "G = (5, 1)"
        2 -> "2G = (6, 3) [Point Doubling]"
        3 -> "3G = (10, 6) [Chord Addition]"
        else -> "4G = (3, 1) [Public Key Q]"
    }

    val stepExplanation = when (currentMultiplier) {
        1 -> "Base generator G = (5, 1) satisfies 1² ≡ 5³ + 2(5) + 2 ≡ 137 ≡ 1 (mod 17)."
        2 -> "Point Doubling (P + P = 2G): Slope λ = (3x² + a) / (2y) mod 17. Tangent line intersects at (6, 3)!"
        3 -> "Point Addition (2G + G = 3G): Chord line between (6, 3) and (5, 1) yields (10, 6) mod 17."
        else -> "🎉 Scalar Multiplier k=4 reached! Public Key Q = 4G = (3, 1). Finding k from Q is the intractable ECDLP!"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ELLIPTIC CURVE POINT WALKER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "y² ≡ x³ + 2x + 2 (mod 17) • Group Order n = 19",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        // Educational Explanation Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Geometric Point Walking (Chord & Tangent):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = AmberGold
                )
                Text(
                    text = stepExplanation,
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 17.sp
                )
            }
        }

        // Active Curve Coordinate Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (currentMultiplier >= 4) ArenaSuccessGreen.copy(alpha = 0.15f) else dynamicColors.cardBorder.copy(alpha = 0.25f))
                .border(1.5.dp, if (currentMultiplier >= 4) ArenaSuccessGreen else AmberGold, RoundedCornerShape(14.dp))
                .padding(16.dp),
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (currentMultiplier >= 4) ArenaSuccessGreen else AmberGold
                )
                Text(
                    text = pointLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary
                )
            }
        }

        // Interactive Point Multiplier Stepper
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) { i ->
                val stepIdx = i + 1
                val isReached = currentMultiplier >= stepIdx
                val isCurrent = currentMultiplier == stepIdx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isCurrent) AmberGold
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

        Spacer(modifier = Modifier.height(4.dp))

        Tactile3DButton(
            title = when (currentMultiplier) {
                1 -> "Double Point (2G = G + G) ➔"
                2 -> "Add Point (3G = 2G + G) ➔"
                3 -> "Multiply to Key (4G = Q) ➔"
                else -> "ECDLP Mastered! Advance ➔"
            },
            style = if (currentMultiplier >= 4) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (currentMultiplier < 4) {
                    currentMultiplier++
                    onActionSuccess()
                } else {
                    onComplete()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
