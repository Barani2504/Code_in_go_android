package com.simats.codeingo.ui.arenas.cyber_bastion.levels

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale

enum class TlsSuite(
    val token: String,
    val aead: String,
    val hkdfHash: String,
    val isTls13Standard: Boolean,
    val note: String
) {
    AES_128_GCM_SHA256("TLS_AES_128_GCM_SHA256", "AES-128-GCM", "SHA-256", true, "Standard TLS 1.3 AEAD"),
    AES_256_GCM_SHA384("TLS_AES_256_GCM_SHA384", "AES-256-GCM", "SHA-384", true, "High-Security TLS 1.3 AEAD"),
    CHACHA20_POLY1305_SHA256("TLS_CHACHA20_POLY1305_SHA256", "ChaCha20-Poly1305", "SHA-256", true, "Fast Mobile TLS 1.3 AEAD"),
    LEGACY_RC4("TLS_RSA_WITH_RC4_128_SHA", "RC4-128", "SHA-1", false, "Broken stream cipher; forbidden in TLS 1.3"),
    LEGACY_3DES("TLS_RSA_WITH_3DES_EDE_CBC_SHA", "3DES-CBC", "SHA-1", false, "Sweet32 64-bit block collision; forbidden")
}

enum class SupportedGroup(
    val groupName: String,
    val curveType: String,
    val hasForwardSecrecy: Boolean
) {
    X25519("X25519", "Curve25519 ECDHE", true),
    SECP256R1("secp256r1", "NIST P-256 ECDHE", true),
    STATIC_RSA("Static RSA (Legacy)", "No Forward Secrecy", false)
}

@Composable
fun CipherSuiteLabLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current

    val primary = ArenaPalettes.Arena5Primary
    val accent = ArenaPalettes.Arena5Accent

    var selectedSuite by remember { mutableStateOf(TlsSuite.AES_128_GCM_SHA256) }
    var selectedGroup by remember { mutableStateOf(SupportedGroup.X25519) }

    val isSuiteSecure = selectedSuite.isTls13Standard
    val isGroupSecure = selectedGroup.hasForwardSecrecy
    val isHandshakeCompliant = isSuiteSecure && isGroupSecure

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
                .background(if (isDark) ArenaPalettes.Arena5CardDark else dynamicColors.cardBackground)
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
                        text = "TLS 1.3 CIPHER SUITE & GROUPS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary
                    )

                    ArenaStatusBadge(
                        type = if (isHandshakeCompliant) ArenaBadgeType.SUCCESS else ArenaBadgeType.DANGER,
                        label = if (isHandshakeCompliant) "RFC 8446 COMPLIANT ✓" else "INSECURE SUITE"
                    )
                }

                Text(
                    text = "In TLS 1.3 (RFC 8446), the cipher suite specifies ONLY the AEAD encryption and HKDF hash. Key exchange is negotiated separately via Supported Groups! Obsolete ciphers (RC4, 3DES, static RSA) are completely banned.",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // Live Negotiated Suite Badge
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isHandshakeCompliant) ArenaSuccessGreen.copy(alpha = 0.12f) else ArenaDangerRed.copy(alpha = 0.12f))
                .border(1.5.dp, if (isHandshakeCompliant) ArenaSuccessGreen else ArenaDangerRed, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(text = "NEGOTIATED TLS 1.3 PARAMETERS", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = selectedSuite.token,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = if (isSuiteSecure) ArenaSuccessGreen else ArenaDangerRed
                )
                Text(
                    text = "Key Share Group: ${selectedGroup.groupName} (${selectedGroup.curveType})",
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isGroupSecure) ArenaSuccessGreen else ArenaDangerRed
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isHandshakeCompliant) "✓ Valid TLS 1.3 AEAD • Forward Secrecy Guaranteed" else "⚠️ Insecure / Forbidden in RFC 8446!",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isHandshakeCompliant) ArenaSuccessGreen else ArenaDangerRed
                )
            }
        }

        // Suite Selector (Enforce 48dp+ touch target per row)
        Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "1. SELECT AEAD CIPHER SUITE:",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Black,
                color = primary
            )

            TlsSuite.values().forEach { suite ->
                val isSelected = selectedSuite == suite
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .minTouchTarget48()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) {
                                if (suite.isTls13Standard) ArenaSuccessGreen.copy(alpha = 0.2f) else ArenaDangerRed.copy(alpha = 0.2f)
                            } else dynamicColors.cardBorder.copy(alpha = 0.15f)
                        )
                        .border(
                            1.dp,
                            if (isSelected) (if (suite.isTls13Standard) ArenaSuccessGreen else ArenaDangerRed) else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                        .pressScale()
                        .clickable {
                            selectedSuite = suite
                            if (!suite.isTls13Standard) {
                                ArenaHaptics.error(view, reduceMotion)
                                onDeductHeart()
                            } else {
                                ArenaHaptics.tap(view, reduceMotion)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = suite.token, fontSize = 11.5.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                            Text(text = suite.note, fontSize = 9.5.sp, color = if (suite.isTls13Standard) SubtextGray else ArenaDangerRed)
                        }
                        Text(
                            text = if (suite.isTls13Standard) "TLS 1.3" else "FORBIDDEN",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = if (suite.isTls13Standard) ArenaSuccessGreen else ArenaDangerRed
                        )
                    }
                }
            }
        }

        // Supported Groups Selector (Enforce 48dp+ touch target)
        Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "2. SELECT SUPPORTED GROUP (KEY EXCHANGE EXTENSION):",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Black,
                color = accent
            )

            SupportedGroup.values().forEach { group ->
                val isSelected = selectedGroup == group
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .minTouchTarget48()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) {
                                if (group.hasForwardSecrecy) ArenaSuccessGreen.copy(alpha = 0.2f) else ArenaDangerRed.copy(alpha = 0.2f)
                            } else dynamicColors.cardBorder.copy(alpha = 0.15f)
                        )
                        .border(
                            1.dp,
                            if (isSelected) (if (group.hasForwardSecrecy) ArenaSuccessGreen else ArenaDangerRed) else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                        .pressScale()
                        .clickable {
                            selectedGroup = group
                            if (!group.hasForwardSecrecy) {
                                ArenaHaptics.error(view, reduceMotion)
                                onDeductHeart()
                            } else {
                                ArenaHaptics.tap(view, reduceMotion)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = group.groupName, fontSize = 11.5.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                            Text(text = group.curveType, fontSize = 9.5.sp, color = SubtextGray)
                        }
                        Text(
                            text = if (group.hasForwardSecrecy) "FORWARD SECRECY ✓" else "NO PFS ❌",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = if (group.hasForwardSecrecy) ArenaSuccessGreen else ArenaDangerRed
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Tactile3DButton(
            title = if (isHandshakeCompliant) "LOCK TLS 1.3 SUITE & ADVANCE ➔" else "SELECT SECURE AEAD & ECDHE GROUP",
            style = if (isHandshakeCompliant) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.DISABLED,
            isEnabled = isHandshakeCompliant,
            onClick = {
                if (isHandshakeCompliant) {
                    ArenaHaptics.success(view, reduceMotion)
                    onActionSuccess()
                    onComplete()
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .minTouchTarget48()
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}
