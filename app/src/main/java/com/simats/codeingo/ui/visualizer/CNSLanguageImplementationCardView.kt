package com.simats.codeingo.ui.visualizer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Security
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
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale

@Composable
fun CNSLanguageImplementationCardView(
    structureName: String,
    modifier: Modifier = Modifier,
    initialLanguage: String = "Python"
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val context = LocalContext.current
    var activeLanguage by remember(initialLanguage) { mutableStateOf(initialLanguage) }
    var isExpanded by remember { mutableStateOf(true) }
    var copied by remember { mutableStateOf(false) }

    val supportedLanguages = listOf("Python", "Java", "C++", "Go", "JavaScript", "Kotlin", "Swift")

    val languageFlag = when (activeLanguage.lowercase()) {
        "python" -> "🐍"
        "java" -> "☕"
        "c++", "cpp" -> "⚙️"
        "go" -> "🐹"
        "javascript", "js" -> "📜"
        "kotlin" -> "📱"
        else -> "🦅"
    }

    val codeString = CNSLanguageCodeProvider.code(structureName, activeLanguage)
    val lines = codeString.lines()

    val s = structureName.lowercase()
    val securityChips = when {
        s.contains("caesar") || s.contains("shift") || s.contains("array") -> listOf(
            "Keyspace" to "26 shifts (4.7 bits)",
            "Attack" to "Frequency analysis O(1)",
            "Security" to "0 bits (Broken)",
            "Type" to "Monoalphabetic"
        )
        s.contains("des") || s.contains("feistel") || s.contains("stack") -> listOf(
            "Block Size" to "64 bits",
            "Key Size" to "56 bits",
            "Rounds" to "16 Feistel",
            "Status" to "Deprecated (Broken)"
        )
        s.contains("aes") || s.contains("matrix") || s.contains("queue") -> listOf(
            "Block Size" to "128 bits",
            "Key Size" to "128 / 192 / 256b",
            "Rounds" to "10 / 12 / 14 SPN",
            "Standard" to "FIPS-197 Active"
        )
        s.contains("vigenere") || s.contains("poly") || s.contains("linked") -> listOf(
            "Keyspace" to "26^L",
            "Attack" to "Kasiski & Friedman",
            "Security" to "Polyalphabetic",
            "Flaw" to "Periodic overlap"
        )
        s.contains("diffie") || s.contains("hellman") || (s.contains("tree") && !s.contains("avl") && !s.contains("trie")) -> listOf(
            "Security" to "DLP Hardness",
            "Key Size" to "2048+ bits (FFDH)",
            "PFS" to "Forward Secrecy",
            "Vulnerability" to "Unauthenticated MITM"
        )
        s.contains("rsa") || s.contains("avl") -> listOf(
            "Security" to "Integer Factoring",
            "Key Size" to "2048 - 4096 bits",
            "Trapdoor" to "phi(n) = (p-1)(q-1)",
            "Padding" to "OAEP required"
        )
        s.contains("elliptic") || s.contains("ecc") || s.contains("trie") -> listOf(
            "Security" to "ECDLP Hardness",
            "Key Size" to "256 bits (~RSA-3072)",
            "Standard" to "secp256r1 / X25519",
            "Advantage" to "High Perf & Small Keys"
        )
        s.contains("sha") || s.contains("hash") || s.contains("graph") -> listOf(
            "Output" to "256 bits",
            "Block Size" to "512 bits",
            "Structure" to "Merkle-Damgård",
            "Collision Bound" to "2^128 (Birthday)"
        )
        s.contains("hmac") || s.contains("collision") || s.contains("sorting") -> listOf(
            "Construction" to "RFC 2104 Nested",
            "KDF" to "PBKDF2 / Argon2",
            "Defense" to "Prevents Length-Ext",
            "Salt" to "Per-user Cryptographic"
        )
        else -> listOf(
            "Handshake" to "1-RTT ECDHE",
            "AEAD" to "AES-128-GCM",
            "Forward Secrecy" to "Mandatory",
            "Identity" to "X.509 PKI Chain"
        )
    }

    val cardShape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(if (isDark) CardBackground else Color.White)
            .border(1.5.dp, if (isDark) InputBorder else Color(0xFFE2E8F0), cardShape)
    ) {
        // --- Header Section ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DsaBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "CNS Security Parameters",
                        tint = DsaBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = "Cryptography Implementation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isDark) Color.White else Color(0xFF1E293B)
                    )
                    Text(
                        text = "$languageFlag $activeLanguage  •  ${lines.size} lines",
                        fontSize = 12.sp,
                        color = SubtextGray
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Copy Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) DarkBackground else Color(0xFFF1F5F9))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("code", codeString)
                            clipboard.setPrimaryClip(clip)
                            copied = true
                        }
                        .pressScale(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = if (copied) "Copied" else "Copy Code",
                        tint = if (copied) DuolingoGreen else (if (isDark) Color.White else Color(0xFF64748B)),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Collapse/Expand
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = SubtextGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // --- Security Parameter Chips ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(securityChips) { (label, value) ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) DarkBackground else Color(0xFFF8FAFC))
                        .border(1.dp, if (isDark) InputBorder.copy(alpha = 0.5f) else Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = SubtextGray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = value,
                        fontSize = 11.sp,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Language Selector Tabs ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(supportedLanguages) { lang ->
                val isSelected = lang.equals(activeLanguage, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) DsaBlue else (if (isDark) DarkBackground else Color(0xFFF1F5F9))
                        )
                        .clickable { activeLanguage = lang }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = lang,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else (if (isDark) Color.LightGray else Color(0xFF475569))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Code Block Area ---
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A)) // Sleek dark code background
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    // Line numbers
                    Column(
                        modifier = Modifier.padding(end = 16.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        lines.indices.forEach { index ->
                            Text(
                                text = "${index + 1}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF475569),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Code text
                    Column {
                        lines.forEach { line ->
                            Text(
                                text = line.ifEmpty { " " },
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFE2E8F0),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Backwards-compatible alias so existing callers don't break */
@Composable
fun DSALanguageImplementationCardView(
    structureName: String,
    modifier: Modifier = Modifier,
    initialLanguage: String = "Python"
) {
    CNSLanguageImplementationCardView(
        structureName = structureName,
        modifier = modifier,
        initialLanguage = initialLanguage
    )
}
