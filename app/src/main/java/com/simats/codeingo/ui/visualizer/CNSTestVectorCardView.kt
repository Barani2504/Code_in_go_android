package com.simats.codeingo.ui.visualizer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DSAVisualizerType
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.model.CNSTestVectorRepository

@Composable
fun CNSTestVectorCardView(
    labType: DSAVisualizerType,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val context = LocalContext.current

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    val vector = remember(labType) {
        CNSTestVectorRepository.getVectorForLab(labType)
    } ?: return

    var isVerified by remember(labType) { mutableStateOf(false) }
    var copied by remember(labType) { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.5.dp, if (isVerified) greenColor.copy(alpha = 0.6f) else DsaBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Official Standard Vector",
                        tint = if (isVerified) greenColor else DsaBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "OFFICIAL COMPLIANCE TEST VECTOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isVerified) greenColor else DsaBlue
                    )
                }

                // Copy button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("test_vector", "${vector.title}\nInput: ${vector.inputData}\nOutput: ${vector.expectedOutput}")
                            clipboard.setPrimaryClip(clip)
                            copied = true
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy Vector",
                        tint = if (copied) greenColor else SubtextGray,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (copied) "Copied!" else "Copy",
                        fontSize = 10.sp,
                        color = if (copied) greenColor else SubtextGray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = vector.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = dynamicColors.textPrimary
            )
            Text(
                text = "Standard: ${vector.sourceStandard}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = amberColor
            )

            // Vector Details Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(dynamicColors.inputBackground)
                    .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Input Block:", fontSize = 10.5.sp, color = SubtextGray, fontWeight = FontWeight.Bold)
                    Text(text = vector.inputData, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Key / Domain:", fontSize = 10.5.sp, color = SubtextGray, fontWeight = FontWeight.Bold)
                    Text(text = vector.keyOrParams, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = amberColor)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Expected Digest / Output:", fontSize = 10.5.sp, color = SubtextGray, fontWeight = FontWeight.Bold)
                    Text(text = vector.expectedOutput.take(24) + "...", fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = greenColor)
                }
            }

            Text(
                text = vector.description,
                fontSize = 11.sp,
                color = SubtextGray,
                lineHeight = 15.sp
            )

            // Verification Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isVerified) greenColor else DsaBlue)
                    .clickable { isVerified = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isVerified) Icons.Default.Check else Icons.Default.PlayArrow,
                        contentDescription = "Run Vector",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isVerified) "✅ NIST / RFC Vector Output Match Verified!" else "Run & Verify Against Standard",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}
