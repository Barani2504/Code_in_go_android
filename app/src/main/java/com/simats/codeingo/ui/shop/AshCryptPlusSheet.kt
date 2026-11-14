package com.simats.codeingo.ui.shop

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.social.FeatureFlag
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray

/**
 * 👑 AshCrypt Plus: Ethical Monetization & Transparency Comparison
 *
 * Implements Phase 5.7:
 * - Core curriculum is 100% free forever for all students
 * - Plus tier adds offline package caching, unlimited practice hearts, and certificate export
 * - No interstitial ads, no pay-to-win, clear pricing
 */
@Composable
fun AshCryptPlusSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!FeatureFlag.PLUS_TIER_ENABLED) return

    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val plusGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2E1065),
            Color(0xFF0F172A)
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(plusGradient)
                .border(1.5.dp, AmberGold, RoundedCornerShape(20.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("👑", fontSize = 42.sp)
                Text(
                    text = "ASHCRYPT PLUS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold
                )
                Text(
                    text = "Accelerate your security engineering mastery with premium offline & analysis superpowers.",
                    fontSize = 11.5.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Feature Comparison Rows
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PlusBenefitRow(
                title = "100% Free Core Curriculum",
                freeText = "✓ Included",
                plusText = "✓ Included",
                isPlusExclusive = false
            )
            PlusBenefitRow(
                title = "Unlimited Practice Hearts",
                freeText = "5 (Refills over time)",
                plusText = "∞ Unlimited",
                isPlusExclusive = true
            )
            PlusBenefitRow(
                title = "Full Offline Lab Downloads",
                freeText = "Online Only",
                plusText = "✓ Complete Offline Access",
                isPlusExclusive = true
            )
            PlusBenefitRow(
                title = "Verifiable PDF Mastery Certificates",
                freeText = "Digital View",
                plusText = "✓ Cryptographically Signed PDF",
                isPlusExclusive = true
            )
            PlusBenefitRow(
                title = "Zero Interstitial Ads",
                freeText = "✓ Zero Ads Always",
                plusText = "✓ Zero Ads Always",
                isPlusExclusive = false
            )
        }

        // Ethical Guarantee Note
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                .padding(12.dp)
        ) {
            Text(
                text = "🛡️ Ethical Pledge: AshCrypt has zero dark patterns, no pay-to-win league mechanics, and never locks conceptual learning behind paywalls.",
                fontSize = 10.sp,
                color = dynamicColors.textSecondary,
                lineHeight = 14.sp
            )
        }

        // Plan Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color.White)
                    .border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Monthly", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                    Text("$4.99 / mo", fontSize = 14.sp, fontWeight = FontWeight.Black, color = AmberGold)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AmberGold.copy(alpha = 0.15f))
                    .border(1.5.dp, AmberGold, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Annual (Save 33%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                    Text("$39.99 / yr", fontSize = 14.sp, fontWeight = FontWeight.Black, color = AmberGold)
                }
            }
        }

        // Dismiss / Subscribe Action Buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pressSquish {
                    FeedbackManager.tap()
                    onDismiss()
                }
                .clip(RoundedCornerShape(12.dp))
                .background(AmberGold)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "START 7-DAY FREE TRIAL",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun PlusBenefitRow(
    title: String,
    freeText: String,
    plusText: String,
    isPlusExclusive: Boolean
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary, modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = plusText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isPlusExclusive) AmberGold else PhoenixGreen
                )
                Text(text = "Free: $freeText", fontSize = 9.sp, color = SubtextGray)
            }
        }
    }
}

