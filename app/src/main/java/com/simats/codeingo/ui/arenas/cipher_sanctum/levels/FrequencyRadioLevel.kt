package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import kotlin.math.roundToInt

@Composable
fun FrequencyRadioLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val view = LocalView.current
    val reduceMotion = LocalReduceMotion.current
    val isDark = dynamicColors.isDark

    val primary = ArenaPalettes.Arena1Primary
    val accent = ArenaPalettes.Arena1Accent
    val cardBg = if (isDark) ArenaPalettes.Arena1CardDark else dynamicColors.cardBackground

    // Seeded English text (97 characters):
    // E appears 14 times (~14.4%), T appears 11 times (~11.3%), A appears 7 times (~7.2%)
    val originalPlaintext = "THE ANCIENT PHOENIX RISES FROM THE ASHES TO RESTORE THE CRYPTOGRAPHIC BALANCE AND PROTECT ALL REALMS"
    val actualShift = 3
    val ciphertext = remember { ArenaCryptoEngine.caesarEncrypt(originalPlaintext, actualShift) }

    // Slider controls candidate decryption shift k (0..25)
    var sliderShift by remember { mutableFloatStateOf(10f) }
    val currentKey = sliderShift.roundToInt().coerceIn(0, 25)
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var lastCheckSuccess by remember { mutableStateOf<Boolean?>(null) }

    // Decrypt ciphertext with current key
    val candidatePlaintext = remember(currentKey) {
        ArenaCryptoEngine.caesarDecrypt(ciphertext, currentKey)
    }

    // Top expected English letters and natural distribution percentages
    val referenceEnglishFreq = remember {
        listOf(
            'E' to 12.7f,
            'T' to 9.1f,
            'A' to 8.2f,
            'O' to 7.5f,
            'I' to 7.0f,
            'N' to 6.7f
        )
    }

    // Compute frequency of letters in candidate plaintext
    val candidateFrequencies = remember(candidatePlaintext) {
        val letterOnly = candidatePlaintext.filter { it.isLetter() }
        val total = letterOnly.length.toFloat().coerceAtLeast(1f)
        val counts = letterOnly.groupingBy { it }.eachCount()
        referenceEnglishFreq.map { (char, _) ->
            val count = counts[char] ?: 0
            char to (count / total) * 100f
        }
    }

    val isLocked = currentKey == actualShift

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
                    text = "FREQUENCY ANALYSIS SPECTROGRAPH",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Statistical Signature Calibration",
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
            ArenaStatusBadge(
                type = if (isLocked) ArenaBadgeType.SUCCESS else ArenaBadgeType.INFO,
                label = if (isLocked) "PEAK 'E' ALIGNED (K=3)" else "K = $currentKey"
            )
        }

        // Mission Briefing Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.dp, primary.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Letter Frequency Preservation:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )
                }
                Text(
                    text = "Monoalphabetic ciphers preserve the underlying frequency signature of the language. In English, 'E' accounts for ~12.7% of letters and 'T' ~9.1%. Adjust the shift key until the highest frequency peak aligns with 'E'!",
                    fontSize = 11.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 15.sp
                )
            }
        }

        // Letter Histogram Spectrum Visualizer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) Color(0xFF0D0A02) else Color(0xFF1E1705))
                .border(1.5.dp, if (isLocked) ArenaSuccessGreen else primary.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(10.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barWidth = size.width / (referenceEnglishFreq.size * 2f + 1f)
                val maxExpectedPct = 16f

                referenceEnglishFreq.forEachIndexed { i, (letter, expPct) ->
                    val candPct = candidateFrequencies.find { it.first == letter }?.second ?: 0f

                    val xBase = (i * 2 + 1) * barWidth
                    val expBarHeight = (expPct / maxExpectedPct) * (size.height - 24.dp.toPx())
                    val candBarHeight = (candPct / maxExpectedPct).coerceIn(0f, 1f) * (size.height - 24.dp.toPx())

                    // Reference bar (Ghost outline in Amber)
                    drawRoundRect(
                        color = primary.copy(alpha = 0.3f),
                        topLeft = Offset(xBase - barWidth * 0.4f, size.height - expBarHeight - 14.dp.toPx()),
                        size = Size(barWidth * 0.8f, expBarHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    // Candidate observed bar (Filled Green if locked, Accent Amber if not)
                    val barColor = if (isLocked) ArenaSuccessGreen else accent
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(xBase - barWidth * 0.4f, size.height - candBarHeight - 14.dp.toPx()),
                        size = Size(barWidth * 0.8f, candBarHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }

            // Labels row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                referenceEnglishFreq.forEach { (letter, _) ->
                    Text(
                        text = "$letter",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = if (isLocked && letter == 'E') ArenaSuccessGreen else primary
                    )
                }
            }
        }

        // Frequency Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "■ Ghost Bar: Expected English Freq", fontSize = 9.sp, color = SubtextGray)
            Text(
                text = if (isLocked) "■ Solid Bar: MATCHED (K=3) ✓" else "■ Solid Bar: Observed Stream",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLocked) ArenaSuccessGreen else accent
            )
        }

        // Live Key Calibration Slider Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(cardBg)
                .border(1.2.dp, primary.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DECRYPTION SHIFT KEY (K)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                    Text(
                        text = "K = $currentKey",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isLocked) ArenaSuccessGreen else primary
                    )
                }

                Slider(
                    value = sliderShift,
                    onValueChange = {
                        sliderShift = it
                        feedbackMessage = null
                    },
                    valueRange = 0f..25f,
                    steps = 24,
                    colors = SliderDefaults.colors(
                        thumbColor = if (isLocked) ArenaSuccessGreen else primary,
                        activeTrackColor = if (isLocked) ArenaSuccessGreen else primary,
                        inactiveTrackColor = SubtextGray.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.minTouchTarget48()
                )

                // Decrypted Preview
                Text(
                    text = "DECRYPTED PREVIEW: \"${candidatePlaintext.take(38)}...\"",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (isLocked) ArenaSuccessGreen else dynamicColors.textPrimary
                )
            }
        }

        // Live Feedback Message
        AnimatedVisibility(visible = feedbackMessage != null, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (lastCheckSuccess == true) ArenaSuccessGreen.copy(alpha = 0.12f)
                        else ArenaDangerRed.copy(alpha = 0.12f)
                    )
                    .border(
                        1.dp,
                        if (lastCheckSuccess == true) ArenaSuccessGreen else ArenaDangerRed,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = feedbackMessage ?: "",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (lastCheckSuccess == true) ArenaSuccessGreen else ArenaDangerRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Active Submission Button
        Tactile3DButton(
            title = if (isLocked) "Shatter Frequency Ward (K=3) ✓" else "Lock Frequency Key (K=$currentKey)",
            style = if (isLocked) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (isLocked) {
                    ArenaHaptics.success(view, reduceMotion)
                    lastCheckSuccess = true
                    feedbackMessage = "✓ Verified: Decryption shift K=3 perfectly aligns ciphertext frequency with the standard English profile ('E' peak ~14%)!"
                    onActionSuccess()
                    onComplete()
                } else {
                    ArenaHaptics.error(view, reduceMotion)
                    lastCheckSuccess = false
                    feedbackMessage = "❌ Chi-Squared Mismatch: At K=$currentKey, letter 'E' frequency is near 0%. Real English text has 'E' as the highest frequency letter!"
                    onDeductHeart()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .minTouchTarget48()
        )
    }
}
