package com.simats.codeingo.ui.arenas.cyber_bastion

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
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MilitaryTech
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.arenas.common.ArenaBriefingView
import com.simats.codeingo.ui.arenas.common.ArenaComboHUD
import com.simats.codeingo.ui.arenas.common.ArenaDebriefView
import com.simats.codeingo.ui.arenas.common.ArenaEnvironmentBackground
import com.simats.codeingo.ui.arenas.common.ArenaGameSpeed
import com.simats.codeingo.ui.arenas.common.ArenaLoadoutSheet
import com.simats.codeingo.ui.arenas.common.ArenaPalettes
import com.simats.codeingo.ui.arenas.common.ArenaParticleBurst
import com.simats.codeingo.ui.arenas.common.ArenaShockwaveEffect
import com.simats.codeingo.ui.arenas.common.ArenaTacticalTool
import com.simats.codeingo.ui.arenas.common.arenaShake
import com.simats.codeingo.ui.arenas.common.rememberArenaShakeController
import com.simats.codeingo.ui.arenas.cyber_bastion.levels.CipherSuiteLabLevel
import com.simats.codeingo.ui.arenas.cyber_bastion.levels.CyberTitanBossLevel
import com.simats.codeingo.ui.arenas.cyber_bastion.levels.DnssecChainLevel
import com.simats.codeingo.ui.arenas.cyber_bastion.levels.IpsecTunnelLevel
import com.simats.codeingo.ui.arenas.cyber_bastion.levels.StatefulFirewallLevel
import com.simats.codeingo.ui.arenas.cyber_bastion.levels.SynCookieDefenseLevel
import com.simats.codeingo.ui.arenas.cyber_bastion.levels.TlsHandshakeLevel
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberBastionArenaScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val dynamicColors = LocalDynamicThemeColors.current
    val reduceMotion = LocalReduceMotion.current

    val primary = ArenaPalettes.Arena5Primary
    val accent = ArenaPalettes.Arena5Accent

    // Hearts economy strictly observes GameManager (Max 10)
    val heartsCount by gameManager.heartsCount.collectAsState()
    val gemsCount by gameManager.gemsCount.collectAsState()

    var currentLevel by remember { mutableStateOf(CyberBastionLevel.LEVEL_1) }
    var unlockedLevels by remember { mutableStateOf(setOf(1, 2, 3, 4, 5, 6, 7)) }
    var levelStars by remember { mutableStateOf(mutableMapOf<Int, Int>()) }

    var sessionXP by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var comboCount by remember { mutableIntStateOf(0) }
    var speed by remember { mutableStateOf(ArenaGameSpeed.NORMAL) }

    // Screen Shake & Motion FX
    val shakeController = rememberArenaShakeController()
    var shockwaveTrigger by remember { mutableStateOf(0L) }
    var particleTrigger by remember { mutableStateOf(0L) }

    // Tactical Loadout & Tool State
    var equippedTool by remember { mutableStateOf<ArenaTacticalTool?>(ArenaTacticalTool.HINT_LENS) }
    var toolUsedInLevel by remember { mutableStateOf(false) }
    var showLoadoutSheet by remember { mutableStateOf(false) }

    // Cinematic & Feedback Dialogs
    var showBriefing by remember { mutableStateOf(false) }
    var showDebrief by remember { mutableStateOf(false) }
    var debriefIsVictory by remember { mutableStateOf(false) }
    var showTheoryCodex by remember { mutableStateOf(false) }

    // Reset tool status on level change
    LaunchedEffect(currentLevel) {
        toolUsedInLevel = false
    }

    fun onDeductHeart() {
        if (equippedTool == ArenaTacticalTool.SECOND_CHANCE && !toolUsedInLevel) {
            toolUsedInLevel = true
        } else {
            gameManager.loseHeart()
            comboCount = 0
            shakeController.shake()
            if (heartsCount <= 1) {
                debriefIsVictory = false
                showDebrief = true
            }
        }
    }

    fun onActionSuccess(points: Int = 100) {
        comboCount++
        val multiplier = when {
            comboCount >= 8 -> 3.0f
            comboCount >= 5 -> 2.0f
            comboCount >= 3 -> 1.5f
            else -> 1.0f
        }
        score += (points * multiplier).toInt()
    }

    fun handleLevelCompleted(lvl: CyberBastionLevel) {
        val nextId = lvl.id + 1
        unlockedLevels = unlockedLevels + nextId
        val earnedStars = if (comboCount >= 3) 3 else 2
        levelStars[lvl.id] = earnedStars
        sessionXP += lvl.xpReward
        gameManager.addXP(lvl.xpReward)
        gameManager.addGems(lvl.coinReward)

        particleTrigger = System.currentTimeMillis()
        shockwaveTrigger = System.currentTimeMillis()

        debriefIsVictory = true
        showDebrief = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        ArenaEnvironmentBackground(accentColor = primary)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .arenaShake(shakeController)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
                // Top HUD Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(dynamicColors.cardBorder.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit",
                                tint = dynamicColors.textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = "CYBER BASTION",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = dynamicColors.textPrimary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currentLevel.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primary
                            )
                        }
                    }

                    // Economy Badges
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Tactical Tool Icon
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    equippedTool?.accentColor?.copy(alpha = 0.15f)
                                        ?: dynamicColors.cardBorder.copy(alpha = 0.2f)
                                )
                                .border(
                                    1.dp,
                                    equippedTool?.accentColor ?: dynamicColors.cardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { showLoadoutSheet = true }
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = equippedTool?.icon ?: Icons.Default.MilitaryTech,
                                    contentDescription = "Tool",
                                    tint = equippedTool?.accentColor ?: dynamicColors.textSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (toolUsedInLevel) "USED" else "READY",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (toolUsedInLevel) dynamicColors.textSecondary else (equippedTool?.accentColor ?: dynamicColors.textPrimary)
                                )
                            }
                        }

                        // Hearts HUD
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DuolingoRed.copy(alpha = 0.12f))
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Hearts",
                                tint = DuolingoRed,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$heartsCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = DuolingoRed
                            )
                        }

                        // Gems HUD
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF00C3FF).copy(alpha = 0.12f))
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = "Gems",
                                tint = Color(0xFF00C3FF),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$gemsCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00C3FF)
                            )
                        }

                        // Codex Button
                        IconButton(
                            onClick = { showTheoryCodex = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "Theory",
                                tint = dynamicColors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Enhanced Cyber Level Selector Ribbon
                val bastionStations = remember {
                    CyberBastionLevel.values().map { lvl ->
                        com.simats.codeingo.ui.arenas.common.ArenaStationNode(
                            id = lvl.id,
                            title = lvl.title,
                            shortName = when (lvl.id) {
                                1 -> "TLS 1.3"
                                2 -> "CipherSuite"
                                3 -> "IPsec"
                                4 -> "Firewall"
                                5 -> "DNSSEC"
                                6 -> "SYN Cookie"
                                else -> "Titan"
                            },
                            iconEmoji = lvl.iconEmoji,
                            isBoss = lvl == CyberBastionLevel.BOSS_BATTLE
                        )
                    }
                }

                com.simats.codeingo.ui.arenas.common.ArenaLevelTrack(
                    stations = bastionStations,
                    currentLevelId = currentLevel.id,
                    unlockedLevels = unlockedLevels,
                    levelStars = levelStars,
                    accentColor = primary,
                    onSelectLevel = { id ->
                        currentLevel = CyberBastionLevel.fromId(id)
                    }
                )

                // Combo & Tactical HUD
                ArenaComboHUD(
                    comboCount = comboCount,
                    score = score,
                    equippedTool = equippedTool,
                    toolUsed = toolUsedInLevel,
                    onActivateTool = {
                        toolUsedInLevel = true
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Main Level Canvas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(dynamicColors.cardBackground)
                        .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when (currentLevel) {
                        CyberBastionLevel.LEVEL_1 -> TlsHandshakeLevel(
                            onComplete = { handleLevelCompleted(CyberBastionLevel.LEVEL_1) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(120) }
                        )
                        CyberBastionLevel.LEVEL_2 -> CipherSuiteLabLevel(
                            onComplete = { handleLevelCompleted(CyberBastionLevel.LEVEL_2) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(150) }
                        )
                        CyberBastionLevel.LEVEL_3 -> IpsecTunnelLevel(
                            onComplete = { handleLevelCompleted(CyberBastionLevel.LEVEL_3) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(180) }
                        )
                        CyberBastionLevel.LEVEL_4 -> StatefulFirewallLevel(
                            onComplete = { handleLevelCompleted(CyberBastionLevel.LEVEL_4) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(150) }
                        )
                        CyberBastionLevel.LEVEL_5 -> DnssecChainLevel(
                            onComplete = { handleLevelCompleted(CyberBastionLevel.LEVEL_5) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(200) }
                        )
                        CyberBastionLevel.LEVEL_6 -> SynCookieDefenseLevel(
                            onComplete = { handleLevelCompleted(CyberBastionLevel.LEVEL_6) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(220) }
                        )
                        CyberBastionLevel.BOSS_BATTLE -> CyberTitanBossLevel(
                            onDefeatBoss = { handleLevelCompleted(CyberBastionLevel.BOSS_BATTLE) },
                            onDeductHeart = { onDeductHeart() },
                            onActionSuccess = { onActionSuccess(300) },
                            onTriggerShake = { shakeController.shake() },
                            onTriggerShockwave = { shockwaveTrigger = System.currentTimeMillis() }
                        )
                    }
                }

            } // Close inner weighted Column

            com.simats.codeingo.ui.arenas.common.ArenaBottomBar(
                onOpenCodex = { showTheoryCodex = true },
                onResetLevel = {
                    val lvl = currentLevel
                    currentLevel = CyberBastionLevel.LEVEL_1
                    currentLevel = lvl
                },
                onSkipLevel = {
                    val nextId = minOf(7, currentLevel.id + 1)
                    unlockedLevels = unlockedLevels + nextId
                    currentLevel = CyberBastionLevel.fromId(nextId)
                }
            )
        }

        // Motion FX Overlays
        ArenaShockwaveEffect(trigger = shockwaveTrigger, color = primary)
        ArenaParticleBurst(
            trigger = particleTrigger,
            colors = listOf(primary, accent, Color.White)
        )

        // Cinematic Briefing Overlay
        if (showBriefing) {
            val objective = when (currentLevel) {
                CyberBastionLevel.LEVEL_1 -> "Negotiate a TLS 1.3 1-RTT connection by offering modern AEAD cipher suites and ephemeral ECDH KeyShares."
                CyberBastionLevel.LEVEL_2 -> "Verify and select modern AEAD cipher suites that guarantee forward secrecy and eliminate obsolete static RSA key transport."
                CyberBastionLevel.LEVEL_3 -> "Construct an IPsec ESP packet and verify anti-replay sequence numbering to prevent duplicate packet injection."
                CyberBastionLevel.LEVEL_4 -> "Configure stateful firewall 5-tuple inspection to track connection transitions from SYN_SENT to ESTABLISHED on HTTPS port 443."
                CyberBastionLevel.LEVEL_5 -> "Traverse and authenticate the X.509 PKI / DNSSEC chain of trust from root trust anchors down to leaf records."
                CyberBastionLevel.LEVEL_6 -> "Mitigate TCP SYN flood resource exhaustion using stateless cryptographic SYN Cookies encoded in the Initial Sequence Number (ISN)."
                CyberBastionLevel.BOSS_BATTLE -> "Stand firm against the Cyber Titan! Mitigate SYN floods, intercept rogue intermediate certificates, and enforce deep packet inspection!"
            }
            val mechanic = when (currentLevel) {
                CyberBastionLevel.LEVEL_1 -> "Transmit ClientHello, ephemeral KeyShare, and ServerHello packets."
                CyberBastionLevel.LEVEL_2 -> "Select the authenticated encryption cipher suite supported in TLS 1.3."
                CyberBastionLevel.LEVEL_3 -> "Identify the ESP header field responsible for anti-replay verification."
                CyberBastionLevel.LEVEL_4 -> "Target the standard TCP port allocated for encrypted TLS transport."
                CyberBastionLevel.LEVEL_5 -> "Validate root and intermediate certificate authority signatures."
                CyberBastionLevel.LEVEL_6 -> "Identify the mechanism by which SYN Cookies achieve stateless connection tracking."
                CyberBastionLevel.BOSS_BATTLE -> "Deploy the correct countermeasure across 3 adversarial battle phases."
            }

            ArenaBriefingView(
                arenaTitle = "Arena 5 • Cyber Bastion",
                levelName = currentLevel.title,
                cryptographicObjective = objective,
                mechanicsHint = mechanic,
                isBoss = currentLevel == CyberBastionLevel.BOSS_BATTLE,
                onStartLevel = { showBriefing = false }
            )
        }

        // Post-Game Debrief View
        if (showDebrief) {
            val concepts = when (currentLevel) {
                CyberBastionLevel.LEVEL_1 -> listOf("TLS 1.3 Protocol", "1-RTT KeyShare Negotiation", "Perfect Forward Secrecy")
                CyberBastionLevel.LEVEL_2 -> listOf("AEAD Ciphers", "AES-GCM & ChaCha20-Poly1305", "Obsolete Cipher Deprecation")
                CyberBastionLevel.LEVEL_3 -> listOf("IPsec ESP Mode", "Anti-Replay Protection", "SPI & Security Associations")
                CyberBastionLevel.LEVEL_4 -> listOf("Stateful 5-Tuple Firewalls", "TCP State Table Tracking", "Port 443 Encrypted Traffic")
                CyberBastionLevel.LEVEL_5 -> listOf("X.509 PKI Hierarchy", "DNSSEC RRSIG Records", "Trust Anchor Verification")
                CyberBastionLevel.LEVEL_6 -> listOf("SYN Flood DDoS Defense", "Cryptographic SYN Cookies", "Stateless TCP Handshakes")
                CyberBastionLevel.BOSS_BATTLE -> listOf("Multi-Layer Defense", "Cyber Titan Defeated", "Bastion Perimeter Secured")
            }
            val pitfalls = if (!debriefIsVictory) listOf(
                "Never allow unauthenticated cipher suites or static RSA key exchange in production TLS.",
                "In IPsec ESP, anti-replay sequence counters prevent attackers from re-transmitting captured valid packets.",
                "SYN Cookies eliminate server-side state allocation until the client returns a valid ACK packet."
            ) else emptyList()

            ArenaDebriefView(
                isVictory = debriefIsVictory,
                levelName = currentLevel.title,
                starsEarned = levelStars[currentLevel.id] ?: 2,
                xpEarned = if (debriefIsVictory) currentLevel.xpReward else 0,
                gemsEarned = if (debriefIsVictory) currentLevel.coinReward else 0,
                conceptsMastered = if (debriefIsVictory) concepts else emptyList(),
                missedConcepts = pitfalls,
                onContinue = {
                    showDebrief = false
                    val nextId = currentLevel.id + 1
                    if (nextId <= 7) {
                        currentLevel = CyberBastionLevel.fromId(nextId)
                    } else {
                        onDismiss()
                    }
                },
                onRetry = {
                    showDebrief = false
                },
                onOpenVisualizer = {
                    showDebrief = false
                    onDismiss()
                }
            )
        }

        // Tactical Loadout Modal
        if (showLoadoutSheet) {
            ArenaLoadoutSheet(
                selectedTool = equippedTool,
                onSelectTool = { tool ->
                    equippedTool = tool
                },
                onDismiss = { showLoadoutSheet = false }
            )
        }

        // Theory Codex Sheet
        if (showTheoryCodex) {
            ModalBottomSheet(
                onDismissRequest = { showTheoryCodex = false },
                containerColor = dynamicColors.cardBackground,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .navigationBarsPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "THEORY CODEX • NETWORK SECURITY",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = primary
                        )
                        IconButton(onClick = { showTheoryCodex = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = dynamicColors.textSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Network Security Protocols & Perimeter Defense\n\n" +
                                "1. TLS 1.3 (RFC 8446):\n" +
                                "Reduces handshake latency to 1-RTT (or 0-RTT for resumed sessions). Mandates Ephemeral Diffie-Hellman (ECDHE) for Perfect Forward Secrecy and removes legacy non-AEAD ciphers.\n\n" +
                                "2. IPsec (ESP & AH):\n" +
                                "Provides network-layer confidentiality and integrity. ESP encrypts the inner packet and attaches a sequence number to defeat replay attacks using a sliding verification window.\n\n" +
                                "3. Stateful Firewalls:\n" +
                                "Maintains connection state tables across 5-tuple attributes (Source IP, Destination IP, Source Port, Destination Port, Protocol) to ensure packets belong to legitimately established flows.\n\n" +
                                "4. SYN Cookies:\n" +
                                "When a server's half-open TCP SYN backlog fills up, it encodes the client's connection parameters into the 32-bit Initial Sequence Number (ISN) with a secret cryptographic key, postponing socket buffer allocation until the final ACK arrives.",
                        fontSize = 13.sp,
                        color = dynamicColors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
