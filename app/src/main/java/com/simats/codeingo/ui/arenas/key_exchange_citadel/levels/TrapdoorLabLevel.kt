package com.simats.codeingo.ui.arenas.key_exchange_citadel.levels

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
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
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

@Composable
fun TrapdoorLabLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // Primes p = 5, q = 11. Modulus n = 55. phi(n) = (5-1)*(11-1) = 40
    // Stage 0: Forward encryption easy
    // Stage 1: Brute force reverse is blocked (exponential difficulty)
    // Stage 2: Trapdoor unlocked via phi(n) = 40!
    var stage by remember { mutableIntStateOf(0) }

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
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "ONE-WAY TRAPDOOR PERMUTATION LAB",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = when (stage) {
                        0 -> "A trapdoor function is easy to compute in the forward direction (M ➔ C = Mᵉ mod n), but computationally infeasible to invert without the private trapdoor."
                        1 -> "Notice the adversary's dilemma: Without prime factors p & q, inverting C mod 55 requires exhaustive trial factoring (O(2ⁿ))!"
                        else -> "🎉 Trapdoor Activated! Knowing p=5 & q=11 yields Euler's Totient φ(n)=(5-1)(11-1)=40, unlocking instant private key derivation d ≡ e⁻¹ mod 40!"
                    },
                    fontSize = 12.5.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        // Forward vs Reverse Interactive Valve
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDark) Color(0xFF090E17) else Color(0xFFF8FAFC))
                .border(1.5.dp, if (stage >= 2) DuolingoGreen else AmberGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Forward Lane (Easy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "FORWARD DIRECTION (ENCRYPT)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                        Text(text = "C = Mᵉ mod 55", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DuolingoGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "EASY: O(log e)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                    }
                }

                // Middle Trapdoor Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SubtextGray.copy(alpha = 0.3f))
                )

                // Reverse Lane (Adversary vs Trapdoor)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "REVERSE DIRECTION (DECRYPT)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (stage >= 2) DuolingoGreen else Color(0xFFEF4444)
                        )
                        Text(
                            text = if (stage >= 2) "d ≡ e⁻¹ mod 40 = 7 (FAST)" else "Factoring n=55 (HARD)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = dynamicColors.textPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (stage >= 2) DuolingoGreen.copy(alpha = 0.2f)
                                else Color(0xFFEF4444).copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (stage >= 2) "TRAPDOOR OPEN" else "BLOCKED (O(2ⁿ))",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (stage >= 2) DuolingoGreen else Color(0xFFEF4444)
                        )
                    }
                }
            }
        }

        // Totient Derivation Card (When Unlocked)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (stage >= 2) DuolingoGreen.copy(alpha = 0.12f)
                    else Color(0xFF3B82F6).copy(alpha = 0.12f)
                )
                .border(
                    1.dp,
                    if (stage >= 2) DuolingoGreen.copy(alpha = 0.5f)
                    else Color(0xFF3B82F6).copy(alpha = 0.4f),
                    RoundedCornerShape(16.dp)
                )
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "THE MATHEMATICAL SECRET TRAPDOOR:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (stage >= 2) DuolingoGreen else Color(0xFF60A5FA)
                )
                Text(
                    text = "φ(n) = (p - 1) × (q - 1) = (5 - 1) × (11 - 1) = 4 × 10 = 40. Knowing φ(n) allows the private key owner to calculate d with the Extended Euclidean Algorithm in milliseconds!",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 17.sp
                )
            }
        }

        // Action Stepper Button
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (stage >= 2) Brush.horizontalGradient(listOf(DuolingoGreen, Color(0xFF059669)))
                    else Brush.horizontalGradient(listOf(AmberGold, Color(0xFFD97706)))
                )
                .pressScale(targetScale = 0.96f)
                .clickable {
                    if (stage < 2) {
                        stage++
                        onActionSuccess()
                    } else {
                        onComplete()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (stage >= 2) Icons.Default.Check else Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = when (stage) {
                        0 -> "1. TEST FORWARD COMPUTATION →"
                        1 -> "2. UNLOCK TRAPDOOR VIA φ(n)=40 🔓"
                        else -> "TRAPDOOR MASTERED! ADVANCE →"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
