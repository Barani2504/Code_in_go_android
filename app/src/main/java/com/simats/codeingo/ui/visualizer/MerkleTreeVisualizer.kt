package com.simats.codeingo.ui.visualizer

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.components.VisualizerPlaybackController

/**
 * 🌳 Merkle Tree Cryptographic Hash Verification Visualizer
 *
 * Implements Phase 4.1:
 * - Tree structure: 4 Leaves -> 2 Intermediate Hashes -> 1 Merkle Root
 * - Interactive Tampering Demonstration: Modifying 1 leaf triggers cascading invalidation along the Merkle audit path
 * - Used in Bitcoin, Git commit trees, Certificate Transparency, and ZK-rollups
 */
@Composable
fun MerkleTreeVisualizer(modifier: Modifier = Modifier) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    var isTampered by remember { mutableStateOf(false) }
    var currentStep by remember { mutableIntStateOf(1) } // 1: Leaf Hash, 2: Level 1 Combos, 3: Merkle Root
    var isPlaying by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }

    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    // Hash values simulation
    val tx1 = "Tx A: Alice->Bob $50"
    val tx2 = if (isTampered) "Tx B: Bob->Mallory $9999 [TAMPERED!]" else "Tx B: Bob->Carol $25"
    val tx3 = "Tx C: Dave->Eve $10"
    val tx4 = "Tx D: Frank->Grace $80"

    val hash1 = "0x4A8F"
    val hash2 = if (isTampered) "0xDEAD" else "0xB1C2"
    val hash3 = "0x9E7D"
    val hash4 = "0x3F1A"

    val hash12 = if (isTampered) "0xBAD1" else "0x7E3A"
    val hash34 = "0x5C89"

    val merkleRoot = if (isTampered) "0xINVALID_ROOT" else "0x89AC_01FE"

    val tamperedColor by animateColorAsState(
        targetValue = if (isTampered) PhoenixCrimson else PhoenixGreen,
        label = "tamperColor"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Control Card: Tamper Leaf Toggle & Overview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(cardBg)
                .border(1.2.dp, borderColor, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🌳", fontSize = 16.sp)
                        Text(
                            text = "MERKLE TREE AUDIT PATH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = dynamicColors.textPrimary
                        )
                    }

                    // Interactive Tamper Button
                    Box(
                        modifier = Modifier
                            .pressSquish {
                                FeedbackManager.tap()
                                isTampered = !isTampered
                            }
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isTampered) PhoenixCrimson.copy(alpha = 0.2f)
                                else AmberGold.copy(alpha = 0.15f)
                            )
                            .border(
                                1.dp,
                                if (isTampered) PhoenixCrimson else AmberGold,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isTampered) "🚨 RESTORE LEAF B" else "⚡ TAMPER LEAF B",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isTampered) PhoenixCrimson else AmberGold
                        )
                    }
                }

                Text(
                    text = if (isTampered)
                        "⚠️ TAMPER DETECTED: Modifying Leaf B instantly alters Hash(B) -> Hash(AB) -> Merkle Root. The audit proof fails in O(log n) time!"
                    else
                        "All cryptographic hashes match the consensus root. Any client can verify Leaf B using only its sibling Hash(A) and uncle Hash(CD).",
                    fontSize = 11.5.sp,
                    color = if (isTampered) PhoenixCrimson else dynamicColors.textSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        // Tree Visualization Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(cardBg)
                .border(1.2.dp, borderColor, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tier 1: Merkle Root
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "MERKLE ROOT H(H₁₂ + H₃₄)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(tamperedColor.copy(alpha = 0.15f))
                            .border(1.5.dp, tamperedColor, RoundedCornerShape(10.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (currentStep >= 3) merkleRoot else "PENDING…",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = tamperedColor
                        )
                    }
                }

                Text(text = "▲", fontSize = 12.sp, color = SubtextGray)

                // Tier 2: Intermediate Hashes (H12 and H34)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Left parent (H12) - Affected by Leaf B tampering!
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(tamperedColor.copy(alpha = 0.12f))
                            .border(1.2.dp, tamperedColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "H₁₂ = H(H₁ || H₂)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
                            Text(
                                text = if (currentStep >= 2) hash12 else "…",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = tamperedColor
                            )
                        }
                    }

                    // Right parent (H34) - Untouched!
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PhoenixGreen.copy(alpha = 0.12f))
                            .border(1.2.dp, PhoenixGreen, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "H₃₄ = H(H₃ || H₄)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
                            Text(
                                text = if (currentStep >= 2) hash34 else "…",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = PhoenixGreen
                            )
                        }
                    }
                }

                Text(text = "▲", fontSize = 12.sp, color = SubtextGray)

                // Tier 3: Leaf Hashes (H1, H2, H3, H4)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Triple("H₁", hash1, PhoenixGreen),
                        Triple("H₂", hash2, tamperedColor),
                        Triple("H₃", hash3, PhoenixGreen),
                        Triple("H₄", hash4, PhoenixGreen)
                    ).forEach { (label, hashVal, col) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(col.copy(alpha = 0.10f))
                                .border(1.dp, col, RoundedCornerShape(6.dp))
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = label, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
                                Text(text = hashVal, fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = col)
                            }
                        }
                    }
                }
            }
        }

        // Playback Controller
        VisualizerPlaybackController(
            currentStep = currentStep,
            totalSteps = 3,
            onStepChange = { currentStep = it },
            isPlaying = isPlaying,
            onPlayPauseToggle = { isPlaying = !isPlaying },
            playbackSpeed = playbackSpeed,
            onSpeedChange = { playbackSpeed = it },
            stepLabel = "TREE LEVEL",
            accentColor = AmberGold
        )
    }
}

