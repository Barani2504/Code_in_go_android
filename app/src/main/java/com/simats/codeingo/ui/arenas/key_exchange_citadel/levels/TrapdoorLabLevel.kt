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
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun TrapdoorLabLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // Unified RSA Parameters (ArenaCryptoEngine.ToyRSA5_11):
    // p = 5, q = 11, n = 55, phi(n) = (5-1)*(11-1) = 40, e = 3, d = 27 (3*27 = 81 = 2*40 + 1)
    // Stage 0: Forward encryption easy (M=2 -> C = 2^3 mod 55 = 8)
    // Stage 1: Brute force reverse is hard (factoring n=55 without factors takes trial division O(sqrt(n)))
    // Stage 2: Trapdoor unlocked via phi(n) = 40 -> d = 27! Invert C=8: 8^27 mod 55 = 2!
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
                        0 -> "A trapdoor one-way function is easy to compute forward (C ≡ Mᵉ mod n), but computationally infeasible to invert without the mathematical trapdoor."
                        1 -> "Adversary Dilemma: Factoring modulus n=55 requires trial division (O(√n) arithmetic steps, exponential in bit-length k=log₂ n; GNFS is sub-exponential)."
                        else -> "🎉 Trapdoor Activated! Knowing prime factors p=5 & q=11 yields Euler's Totient φ(n)=(5-1)(11-1)=40, granting instant inverse d ≡ 3⁻¹ mod 40 = 27!"
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
                .border(1.5.dp, if (stage >= 2) ArenaSuccessGreen else AmberGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
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
                        Text(text = "FORWARD DIRECTION (ENCRYPT)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = ArenaSuccessGreen)
                        Text(text = "C = M³ mod 55 (e.g. 2³ = 8)", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ArenaSuccessGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "EASY: O(log e)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = ArenaSuccessGreen)
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
                            color = if (stage >= 2) ArenaSuccessGreen else ArenaDangerRed
                        )
                        Text(
                            text = if (stage >= 2) "d ≡ 3⁻¹ mod 40 = 27 (8²⁷ mod 55 = 2)" else "Factoring n=55: O(√n) trial",
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
                                if (stage >= 2) ArenaSuccessGreen.copy(alpha = 0.2f)
                                else ArenaDangerRed.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (stage >= 2) "TRAPDOOR OPEN" else "HARD: O(√n)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (stage >= 2) ArenaSuccessGreen else ArenaDangerRed
                        )
                    }
                }
            }
        }

        // Totient Derivation Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (stage >= 2) ArenaSuccessGreen.copy(alpha = 0.12f)
                    else Color(0xFF3B82F6).copy(alpha = 0.12f)
                )
                .border(
                    1.dp,
                    if (stage >= 2) ArenaSuccessGreen.copy(alpha = 0.5f)
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
                    color = if (stage >= 2) ArenaSuccessGreen else Color(0xFF60A5FA)
                )
                Text(
                    text = "φ(n) = (p - 1) × (q - 1) = (5 - 1) × (11 - 1) = 4 × 10 = 40. Knowing φ(n)=40 allows the owner to solve 3 × d ≡ 1 mod 40 ➔ d = 27 using the Extended Euclidean Algorithm!",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Tactile3DButton(
            title = when (stage) {
                0 -> "1. Test Forward Computation (C = Mᵉ mod n) →"
                1 -> "2. Unlock Trapdoor via φ(n)=40 🔓"
                else -> "Trapdoor Mastered! Advance →"
            },
            style = if (stage >= 2) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (stage < 2) {
                    stage++
                    onActionSuccess()
                } else {
                    onComplete()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
