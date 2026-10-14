package com.simats.codeingo.ui.worlds.tree

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.simats.codeingo.ui.theme.AmberGold
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonStyle
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.worlds.array.KingdomGameSpeed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinaryTreeForestArenaScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val gemsCount by gameManager.gemsCount.collectAsState()

    var currentLevel by remember { mutableStateOf(BinaryTreeForestLevel.LEVEL_1) }
    var unlockedLevels by remember { mutableStateOf(setOf(1)) }
    var levelStars by remember { mutableStateOf(mutableMapOf(1 to 3)) }

    var hearts by remember { mutableIntStateOf(5) }
    var sessionXP by remember { mutableIntStateOf(0) }
    var speed by remember { mutableStateOf(KingdomGameSpeed.NORMAL) }

    var showTheoryCodex by remember { mutableStateOf(false) }
    var showHintSheet by remember { mutableStateOf(false) }
    var showVictoryModal by remember { mutableStateOf(false) }
    var showBossVictoryModal by remember { mutableStateOf(false) }

    fun handleLevelCompleted(lvl: BinaryTreeForestLevel) {
        val nextId = lvl.id + 1
        unlockedLevels = unlockedLevels + nextId
        levelStars[lvl.id] = 3
        sessionXP += lvl.xpReward
        gameManager.addXP(lvl.xpReward)
        gameManager.addGems(lvl.coinReward)

        if (lvl == BinaryTreeForestLevel.BOSS_BATTLE) {
            showBossVictoryModal = true
        } else {
            showVictoryModal = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Emerald Forest Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF072418), Color(0xFF04140D), Color(0xFF020A06))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top HUD
            // Top HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Exit", tint = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.45f)).border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                        Text(text = "❤️ $hearts", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.45f)).border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                        Text(text = "⭐ +$sessionXP", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.45f)).border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                        Text(text = "💎 $gemsCount", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .pressScale()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { speed = speed.next() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(text = speed.label, fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                    }

                    Box(
                        modifier = Modifier
                            .pressScale()
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700).copy(alpha = 0.2f))
                            .clickable { showHintSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = "Hint", tint = Color(0xFFFFD700), modifier = Modifier.size(15.dp))
                    }
                }
            }

            // Level Track
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BinaryTreeForestLevel.entries.forEach { lvl ->
                    val isUnlocked = unlockedLevels.contains(lvl.id)
                    val isSelected = currentLevel == lvl

                    Box(
                        modifier = Modifier
                            .pressScale()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    isSelected && lvl == BinaryTreeForestLevel.BOSS_BATTLE -> DuolingoRed
                                    isSelected -> Color(0xFF10B981)
                                    isUnlocked -> Color(0xFF0C2B1D)
                                    else -> Color.Black.copy(alpha = 0.35f)
                                }
                            )
                            .border(1.dp, if (isSelected) Color.White else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                            .clickable(enabled = isUnlocked) { currentLevel = lvl }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = if (isUnlocked) lvl.iconEmoji else "🔒", fontSize = 12.sp)
                            Text(text = "L${lvl.id}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (isSelected) Color.Black else Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Level Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (currentLevel) {
                    BinaryTreeForestLevel.LEVEL_1 -> TreeLevel1Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_1) })
                    BinaryTreeForestLevel.LEVEL_2 -> TreeLevel2Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_2) })
                    BinaryTreeForestLevel.LEVEL_3 -> TreeLevel3Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_3) })
                    BinaryTreeForestLevel.LEVEL_4 -> TreeLevel4Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_4) })
                    BinaryTreeForestLevel.LEVEL_5 -> TreeLevel5Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_5) })
                    BinaryTreeForestLevel.LEVEL_6 -> TreeLevel6Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_6) })
                    BinaryTreeForestLevel.BOSS_BATTLE -> TreeBossBattleContent(onDefeatBoss = { handleLevelCompleted(BinaryTreeForestLevel.BOSS_BATTLE) })
                }
            }

            // Bottom Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp)
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .pressScale()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0C2B1D))
                        .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .clickable { showTheoryCodex = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(text = "🛡️ Cyber Bastion Codex", fontSize = 12.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .pressScale()
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0C2B1D))
                            .clickable {
                                val current = currentLevel
                                currentLevel = BinaryTreeForestLevel.LEVEL_1
                                currentLevel = current
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Replay", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    Box(
                        modifier = Modifier
                            .pressScale()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable {
                                val nextId = minOf(7, currentLevel.id + 1)
                                unlockedLevels = unlockedLevels + nextId
                                currentLevel = BinaryTreeForestLevel.fromId(nextId)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(text = "Skip ⏭", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.8f))
                    }
                }
            }
        }

        // Modals
        if (showVictoryModal) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(24.dp)).background(Color(0xFF0A2417)).border(2.dp, Color(0xFF10B981), RoundedCornerShape(24.dp)).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "🌲 CANOPY ASCENDED!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                    Text(text = currentLevel.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                    Duolingo3DButton(
                        title = "EXPLORE NEXT CANOPY ➔",
                        style = Duolingo3DButtonStyle.GREEN,
                        onClick = {
                            showVictoryModal = false
                            val nextId = minOf(7, currentLevel.id + 1)
                            currentLevel = BinaryTreeForestLevel.fromId(nextId)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showBossVictoryModal) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(24.dp)).background(Color(0xFF0A2417)).border(2.dp, DuolingoGreen, RoundedCornerShape(24.dp)).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "👑 ANCIENT ENT BALANCED!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                    Text(text = "You defended network boundaries, validated TLS 1.3 sessions, and defeated the Cyber Titan in the Cyber Bastion!", fontSize = 13.sp, color = SubtextGray, textAlign = TextAlign.Center)
                    Duolingo3DButton(
                        title = "COMPLETE BASTION",
                        style = Duolingo3DButtonStyle.GREEN,
                        onClick = {
                            showBossVictoryModal = false
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showTheoryCodex) {
            ModalBottomSheet(onDismissRequest = { showTheoryCodex = false }, containerColor = DarkBackground) {
                Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "🛡️ Cyber Bastion Codex (Network Security & Protocols)", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                    Text(
                        text = "Network Security Protocols safeguard distributed data in transit against eavesdropping, tampering, and spoofing attacks.\n\nCore Pillars:\n1. TLS 1.3: Fast 1-RTT handshake negotiating ephemeral (EC)DHE keys, eliminating outdated insecure ciphers, and enforcing forward secrecy.\n2. IPsec ESP: Encapsulating Security Payload providing origin authentication, payload confidentiality, and anti-replay protection at Layer 3.\n3. Stateful Firewalls: Inspect TCP connection states (SYN, ESTABLISHED) and enforce access-control rules to block unauthorized packet ingress.",
                        fontSize = 13.sp,
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        if (showHintSheet) {
            ModalBottomSheet(onDismissRequest = { showHintSheet = false }, containerColor = DarkBackground) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "💡 Bastion Hint: ${currentLevel.title}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                    Text(
                        text = when (currentLevel) {
                                            BinaryTreeForestLevel.LEVEL_1 -> "TLS 1.3 sends key shares inside the initial ClientHello, completing encryption setup in just 1-RTT."
                BinaryTreeForestLevel.LEVEL_2 -> "Modern AEAD cipher suites (like AES-GCM and ChaCha20-Poly1305) provide both encryption and authentication."
                BinaryTreeForestLevel.LEVEL_3 -> "IPsec ESP encrypts inner IP packet payloads and guards against replay attacks using sequence numbers."
                BinaryTreeForestLevel.LEVEL_4 -> "Stateful packet inspection tracks 3-way TCP handshakes to permit valid returning traffic."
                BinaryTreeForestLevel.LEVEL_5 -> "X.509 validation walks up the public key infrastructure (PKI) hierarchy until reaching a trusted Root CA."
                BinaryTreeForestLevel.LEVEL_6 -> "Speed is vital! Filter malicious spoofed packets before server buffer queues overflow."
                BinaryTreeForestLevel.BOSS_BATTLE -> "Coordinate defense protocols and firewall rules to neutralize the Cyber Titan!"
                        },
                        fontSize = 13.sp,
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 🛡️ Cyber Bastion Levels Content (Network Security Protocols)
// ══════════════════════════════════════════════════════════════════
@Composable
private fun TreeLevel1Content(onComplete: () -> Unit) {
    var handshakeCompleted by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF0D2E1F)).border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(16.dp)
        ) {
            Text(
                text = "🛡️ TLS 1.3 1-RTT Handshake: Speculative (EC)DHE KeyShare is sent directly in ClientHello, reducing connection latency to 1 round-trip!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LocalDynamicThemeColors.current.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(100.dp, 50.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF10B981)), contentAlignment = Alignment.Center) {
                Text(text = "CLIENT", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
            }
            Text(text = if (handshakeCompleted) "════ 1-RTT ════➔" else "- - - 1-RTT - - - ➔", fontSize = 11.sp, color = if (handshakeCompleted) DuolingoGreen else AmberGold)
            Box(modifier = Modifier.size(100.dp, 50.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF0C3824)), contentAlignment = Alignment.Center) {
                Text(text = "SERVER", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }

        if (!handshakeCompleted) {
            Duolingo3DButton(
                title = "⚡ SEND CLIENTHELLO + KEYSHARE",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = {
                    handshakeCompleted = true
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }
    }
}

@Composable
private fun TreeLevel2Content(onComplete: () -> Unit) {
    var selectedSuite by remember { mutableStateOf<String?>(null) }
    val suites = listOf("TLS_AES_256_GCM_SHA384", "TLS_RSA_WITH_RC4_128_MD5", "TLS_DES_CBC_SHA")

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "Cipher Suite Negotiation: Which modern AEAD cipher suite is approved in TLS 1.3?", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth(0.9f)) {
            suites.forEach { suite ->
                val isCorrect = suite.contains("AES_256_GCM")
                val isSelected = selectedSuite == suite
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF0C3824))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                        .clickable {
                            selectedSuite = suite
                            if (isCorrect) onComplete()
                        }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = suite, fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun TreeLevel3Content(onComplete: () -> Unit) {
    var isTunnelActive by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "IPsec ESP Tunnel Mode: Encapsulates entire IP packet with SPI, Sequence Number, and ICV auth tag.", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Box(
            modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(12.dp)).background(Color(0xFF0C3824)).padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (isTunnelActive) "ESP TUNNEL ENCRYPTED ✓" else "[NEW IP HEADER | ESP | INNER IP | DATA]", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = if (isTunnelActive) DuolingoGreen else AmberGold, fontFamily = FontFamily.Monospace)
        }

        if (!isTunnelActive) {
            Duolingo3DButton(
                title = "🔒 ESTABLISH IPSEC ESP TUNNEL",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = {
                    isTunnelActive = true
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }
    }
}

@Composable
private fun TreeLevel4Content(onComplete: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "Stateful Firewall Inspection: Standard HTTPS encrypted traffic operates on which TCP destination port?", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf(80, 443, 21, 23).forEach { port ->
                val isCorrect = port == 443
                Box(
                    modifier = Modifier
                        .size(68.dp, 50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0C3824))
                        .border(1.5.dp, Color(0xFF10B981), RoundedCornerShape(14.dp))
                        .clickable {
                            if (isCorrect) onComplete()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$port", fontSize = 18.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun TreeLevel5Content(onComplete: () -> Unit) {
    var isChainValidated by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "X.509 PKI Chain of Trust: Root CA ➔ Intermediate CA ➔ Leaf Server Cert", fontSize = 14.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Box(
            modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(12.dp)).background(Color(0xFF0C3824)).padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (isChainValidated) "CERTIFICATE CHAIN VALIDATED ✓" else "ROOT CA SIGNATURE VERIFIED", fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (isChainValidated) DuolingoGreen else AmberGold)
        }

        if (!isChainValidated) {
            Duolingo3DButton(
                title = "📜 VERIFY CERTIFICATE CHAIN",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = {
                    isChainValidated = true
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }
    }
}

@Composable
private fun TreeLevel6Content(onComplete: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "⚡ DDoS Defense Sprint: Filter spoofed SYN flood packets (${step}/3)", fontSize = 14.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)

        Duolingo3DButton(
            title = "🛡️ APPLY SYN COOKIE FILTER (${step}/3)",
            style = Duolingo3DButtonStyle.AMBER,
            onClick = {
                step++
                if (step == 3) onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}

@Composable
private fun TreeBossBattleContent(onDefeatBoss: () -> Unit) {
    var bossHP by remember { mutableIntStateOf(100) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "👾 THE CYBER TITAN BOSS! (${bossHP} HP)", fontSize = 16.sp, color = DuolingoRed, fontWeight = FontWeight.Black)

        Text(text = "Your packet firewalls will crumble before my distributed botnet!", fontSize = 12.sp, color = AmberGold)

        Duolingo3DButton(
            title = "🏰 DEPLOY FIREWALL DEFENSE POLICY!",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = {
                bossHP = maxOf(0, bossHP - 35)
                if (bossHP <= 0) onDefeatBoss()
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}
