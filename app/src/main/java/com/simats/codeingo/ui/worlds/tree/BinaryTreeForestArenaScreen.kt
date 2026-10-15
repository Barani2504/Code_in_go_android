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
                    BinaryTreeForestLevel.LEVEL_2 -> TreeLevel2Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_2) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    BinaryTreeForestLevel.LEVEL_3 -> TreeLevel3Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_3) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    BinaryTreeForestLevel.LEVEL_4 -> TreeLevel4Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_4) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    BinaryTreeForestLevel.LEVEL_5 -> TreeLevel5Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_5) })
                    BinaryTreeForestLevel.LEVEL_6 -> TreeLevel6Content(onComplete = { handleLevelCompleted(BinaryTreeForestLevel.LEVEL_6) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    BinaryTreeForestLevel.BOSS_BATTLE -> TreeBossBattleContent(onDefeatBoss = { handleLevelCompleted(BinaryTreeForestLevel.BOSS_BATTLE) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
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
// Level Content Composables
// ══════════════════════════════════════════════════════════════════
@Composable
private fun TreeLevel1Content(onComplete: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val steps = listOf("ClientHello: Propose TLS 1.3 ciphers", "KeyShare: Send Ephemeral ECDH Share", "ServerHello: Handshake Keys Derived!")

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF0C2B1C)).border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(16.dp)
        ) {
            Text(
                text = "🌐 TLS 1.3 1-RTT Handshake: Client initiates connection with modern AEAD ciphers and ephemeral ECDH KeyShare.",
                fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary, textAlign = TextAlign.Center
            )
        }

        Box(modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(12.dp)).background(Color(0xFF0C3824)).padding(14.dp), contentAlignment = Alignment.Center) {
            Text(text = steps[step.coerceIn(0, 2)], fontSize = 13.sp, fontWeight = FontWeight.Black, color = AmberGold, fontFamily = FontFamily.Monospace)
        }

        Duolingo3DButton(
            title = if (step < 2) "🚀 NEXT PACKET (${step + 1}/3)" else "HANDSHAKE KEYS ESTABLISHED ✓",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = {
                step++
                if (step >= 3) onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}

@Composable
private fun TreeLevel2Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    val options = listOf("TLS_AES_256_GCM_SHA384", "TLS_RSA_WITH_RC4_128_MD5", "TLS_RSA_WITH_3DES_EDE_CBC_SHA", "SSL_NULL_WITH_NULL_NULL")
    var selectedCipher by remember { mutableStateOf<String?>(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF0C2B1C)).border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(16.dp)
        ) {
            Text(
                text = "🔒 Cipher Suite Negotiation: Which of the following is a valid, secure TLS 1.3 AEAD cipher suite?",
                fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary, textAlign = TextAlign.Center
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            options.forEach { opt ->
                val isCorrect = opt == "TLS_AES_256_GCM_SHA384"
                val isSelected = selectedCipher == opt
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF0C3824))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(10.dp))
                        .clickable {
                            selectedCipher = opt
                            if (isCorrect) onComplete() else onDeductHeart()
                        }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = opt, fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun TreeLevel3Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var selectedField by remember { mutableStateOf<String?>(null) }
    val options = listOf("Sequence Number", "Security Parameters Index (SPI)", "Next Header", "Padding")

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF0C2B1C)).border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(14.dp)
        ) {
            Text(
                text = "IPsec ESP (Encapsulating Security Payload): Which field in the ESP header provides anti-replay protection?",
                fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
            )
        }

        // ESP Packet Layout
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("New IP" to Color(0xFF0C3824), "SPI & Seq#" to AmberGold, "Encrypted Payload" to Color(0xFF10B981), "Padding" to Color(0xFF0C3824), "ICV Auth Tag" to Color(0xFF00CD9C)).forEach { (label, col) ->
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(col.copy(alpha = 0.25f)).border(1.dp, col, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 6.dp)) {
                    Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            options.forEach { opt ->
                val isCorrect = opt == "Sequence Number"
                val isSelected = selectedField == opt
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF0C3824))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(10.dp))
                        .clickable {
                            selectedField = opt
                            if (isCorrect) onComplete() else onDeductHeart()
                        }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = opt, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun TreeLevel4Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "Stateful Firewall Inspection: Standard HTTPS encrypted traffic operates on which TCP destination port?", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

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
                            if (isCorrect) onComplete() else onDeductHeart()
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
        Text(text = "X.509 PKI Chain of Trust: Root CA ➔ Intermediate CA ➔ Leaf Server Cert", fontSize = 13.5.sp, color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

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
private fun TreeLevel6Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    val options = listOf(
        "Encode TCP connection state into Initial Sequence Number (ISN)",
        "Block all port 443 incoming traffic",
        "Keep half-open sockets open indefinitely",
        "Drop 50% of packets randomly"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF0C2B1C)).border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(14.dp)
        ) {
            Text(
                text = "⚡ DDoS Defense Sprint: How do SYN Cookies mitigate SYN flood memory exhaustion without allocating socket buffer until ACK?",
                fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary, textAlign = TextAlign.Center
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            options.forEach { opt ->
                val isCorrect = opt.startsWith("Encode TCP connection state")
                val isSelected = selectedAnswer == opt
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF0C3824))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(10.dp))
                        .clickable {
                            selectedAnswer = opt
                            if (isCorrect) onComplete() else onDeductHeart()
                        }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = opt, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun TreeBossBattleContent(onDefeatBoss: () -> Unit, onDeductHeart: () -> Unit) {
    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableIntStateOf(1) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("The Cyber Titan launches multi-vector network assaults! Defend the Cyber Bastion!") }

    val phaseData = when (currentPhase) {
        1 -> Triple("SYN Flood: Inbound half-open connection backlog saturated! Deploy countermeasure!", "Deploy SYN Cookies (Stateless Handshake)", listOf("Deploy SYN Cookies (Stateless Handshake)", "Disable Ingress Firewall", "Accept all half-open sockets", "Send ICMP Redirect"))
        2 -> Triple("MITM Rogue Certificate: Compromised intermediate CA detected in path! Deploy defense!", "Enforce Certificate Transparency & OCSP", listOf("Enforce Certificate Transparency & OCSP", "Accept Self-Signed Certificate", "Disable TLS Verification", "Downgrade to HTTP"))
        else -> Triple("Data Exfiltration: Attacker probes open ports on perimeter! Deploy network policy!", "Stateful Deep Packet Inspection (DPI)", listOf("Stateful Deep Packet Inspection (DPI)", "Allow Any Any Traffic", "Disable WAF Rules", "Forward all traffic to DMZ"))
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "👾 THE CYBER TITAN", fontSize = 16.sp, color = DuolingoRed, fontWeight = FontWeight.Black)
            Text(text = "PHASE $currentPhase / 3", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AmberGold)
        }

        Box(modifier = Modifier.fillMaxWidth(0.85f).height(14.dp).clip(RoundedCornerShape(7.dp)).background(LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f))) {
            Box(modifier = Modifier.fillMaxWidth(bossHP / 100f).height(14.dp).clip(RoundedCornerShape(7.dp)).background(DuolingoRed))
        }
        Text(text = "$bossHP / 100 HP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFF0C2B1C)).border(1.5.dp, Color(0xFF10B981).copy(alpha = 0.6f), RoundedCornerShape(14.dp)).padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = phaseData.first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                Text(text = feedback, fontSize = 11.sp, color = AmberGold, textAlign = TextAlign.Center)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            phaseData.third.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { opt ->
                        val isCorrect = opt == phaseData.second
                        val isSelected = selectedAnswer == opt
                        Box(
                            modifier = Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed) else Color(0xFF0C3824))
                                .border(1.dp, if (isSelected) Color.White else Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedAnswer = opt
                                    if (isCorrect) {
                                        val newHP = maxOf(0, bossHP - 35)
                                        bossHP = newHP
                                        if (newHP <= 0 || currentPhase >= 3) {
                                            onDefeatBoss()
                                        } else {
                                            currentPhase++
                                            selectedAnswer = null
                                            feedback = "💥 Attack neutralized! The Cyber Titan switches exploit vectors!"
                                        }
                                    } else {
                                        onDeductHeart()
                                        feedback = "❌ Network breached! Deploy standard cyber security countermeasures."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = opt, fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
