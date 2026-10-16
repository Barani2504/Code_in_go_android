package com.simats.codeingo.ui.arenas.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.arenas.routing.ArenaRoute
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale

data class ArenaHubSpec(
    val routeKey: String,
    val unitNumber: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val iconEmoji: String,
    val interactiveModules: List<String>,
    val bossName: String
)

val CNS_ARENAS_SPECS = listOf(
    ArenaHubSpec(
        routeKey = ArenaRoute.CIPHER_SANCTUM,
        unitNumber = 1,
        title = "Cipher Sanctum",
        subtitle = "Classical Ciphers & Cryptanalysis",
        description = "Master modular ring arithmetic, polyalphabetic Vigenère rotors, rail-fence transpositions, and radio frequency spectrum analysis.",
        primaryColor = AmberGold,
        secondaryColor = Color(0xFFFF9800),
        iconEmoji = "🏛️",
        interactiveModules = listOf("Caesar Dial Wheel", "Vigenère Rotor Matrix", "Radio Spectrum Oscilloscope", "Rail-Fence Waves"),
        bossName = "Sphinx Cryptanalyst"
    ),
    ArenaHubSpec(
        routeKey = ArenaRoute.BLOCK_CIPHER_FORGE,
        unitNumber = 2,
        title = "Block Cipher Forge",
        subtitle = "Symmetric Encryption & AES-128",
        description = "Step inside the FIPS-197 AES engine: non-linear GF(2^8) S-Box reactors, ShiftRows permutations, MixColumns diffusion, and ECB vs CBC mode leak simulations.",
        primaryColor = Color(0xFFEF4444),
        secondaryColor = Color(0xFFDC2626),
        iconEmoji = "⚙️",
        interactiveModules = listOf("GF(2^8) S-Box Reactor", "ShiftRows Cycler", "MixColumns MDS Mixer", "ECB vs CBC Leak Simulator"),
        bossName = "Block Overlord"
    ),
    ArenaHubSpec(
        routeKey = ArenaRoute.KEY_EXCHANGE_CITADEL,
        unitNumber = 3,
        title = "Key Exchange Citadel",
        subtitle = "Asymmetric & Public-Key Cryptography",
        description = "Harness the discrete logarithm problem with interactive Diffie-Hellman paint mixers, RSA modular exponentiation clocks, and elliptic curve point addition secants.",
        primaryColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFFD97706),
        iconEmoji = "🏰",
        interactiveModules = listOf("DH Color Paint Mixer", "RSA Trapdoor Clock", "ECC Point Addition Board", "MITM Interceptor"),
        bossName = "Prime Sovereign"
    ),
    ArenaHubSpec(
        routeKey = ArenaRoute.INTEGRITY_FOUNDRY,
        unitNumber = 4,
        title = "Integrity Foundry",
        subtitle = "Hashes, Sponge Functions & Merkle Proofs",
        description = "Trigger strict avalanche criterion explosions (>50% bit flip), absorb and squeeze with the Keccak sponge chamber, and detect leaf tampering in real-time Merkle trees.",
        primaryColor = Color(0xFFA855F7),
        secondaryColor = Color(0xFF7E22CE),
        iconEmoji = "🛡️",
        interactiveModules = listOf("Avalanche Bit Explosion", "Keccak SHA-3 Sponge", "Merkle Tree Tamper Detector", "ECDSA Signature Seal"),
        bossName = "Collision Gorgon"
    ),
    ArenaHubSpec(
        routeKey = ArenaRoute.CYBER_BASTION,
        unitNumber = 5,
        title = "Cyber Bastion",
        subtitle = "Protocols, Firewalls & Network Defense",
        description = "Sequencing 1-RTT TLS 1.3 handshakes, configuring 5-tuple stateful firewall rules to drop SYN floods, and deploying anti-ARP cache poisoning countermeasures.",
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF059669),
        iconEmoji = "⚡",
        interactiveModules = listOf("TLS 1.3 1-RTT Handshake", "5-Tuple Stateful Firewall", "ARP Poisoning Radar", "IPsec Packet Builder"),
        bossName = "Net Warden Sentinel"
    )
)

@Composable
fun ArenasHubScreen(
    onOpenArena: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val totalXP by GameManager.instance.totalXP.collectAsState()
    val gemsCount by GameManager.instance.gemsCount.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Screen Title Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val headerAmber = if (isDark) AmberGold else Color(0xFFB45309)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = headerAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "CNS WARGAMES LAB",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = headerAmber,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Battle Arenas",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = dynamicColors.textPrimary
                        )
                    }

                    // Status Badge: All 5 Unlocked
                    val badgeAmber = if (isDark) AmberGold else Color(0xFFB45309)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isDark) AmberGold.copy(alpha = 0.15f)
                                else Color(0xFFB45309).copy(alpha = 0.10f)
                            )
                            .border(
                                1.2.dp,
                                if (isDark) AmberGold.copy(alpha = 0.45f) else Color(0xFFB45309).copy(alpha = 0.35f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "5/5 UNLOCKED ⚡",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = badgeAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gamified interactive cryptography simulations & security defenses. No quizzes — only hands-on cryptographic mechanics.",
                    fontSize = 13.sp,
                    color = SubtextGray,
                    lineHeight = 18.sp
                )
            }
        }

        // 2. Player Combat Stats HUD Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isDark) Brush.linearGradient(
                            listOf(Color(0xFF131D33), Color(0xFF0F172A))
                        ) else Brush.linearGradient(
                            listOf(Color.White, Color(0xFFF8FAFC))
                        )
                    )
                    .border(
                        1.2.dp,
                        if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                val statAmber = if (isDark) AmberGold else Color(0xFFB45309)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CombatStatColumn(label = "ACTIVE ARENAS", value = "5 / 5", color = DuolingoGreen)
                    Box(modifier = Modifier.height(32.dp).width(1.dp).background(SubtextGray.copy(alpha = 0.3f)))
                    CombatStatColumn(label = "INTERACTIVE LABS", value = "35 STATIONS", color = statAmber)
                    Box(modifier = Modifier.height(32.dp).width(1.dp).background(SubtextGray.copy(alpha = 0.3f)))
                    CombatStatColumn(label = "CRYPTO XP", value = "$totalXP XP", color = Color(0xFF60A5FA))
                }
            }
        }

        // 3. 5 Flagship Battle Arena Cards
        items(CNS_ARENAS_SPECS) { spec ->
            ArenaBattleHeroCard(
                spec = spec,
                onEnterArena = { onOpenArena(spec.routeKey) }
            )
        }
    }
}

@Composable
private fun CombatStatColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = color
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = SubtextGray,
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
private fun ArenaBattleHeroCard(
    spec: ArenaHubSpec,
    onEnterArena: () -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val effectivePrimary = if (spec.primaryColor == AmberGold && !isDark) Color(0xFFB45309) else spec.primaryColor
    val effectiveSecondary = if (spec.secondaryColor == Color(0xFFFF9800) && !isDark) Color(0xFFD97706) else spec.secondaryColor

    val cardShape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = cardShape,
                spotColor = effectivePrimary.copy(alpha = if (isDark) 0.30f else 0.18f)
            )
            .clip(cardShape)
            .background(
                if (isDark) Brush.verticalGradient(
                    listOf(
                        effectivePrimary.copy(alpha = 0.12f),
                        Color(0xFF0F172A)
                    )
                ) else Brush.verticalGradient(
                    listOf(
                        Color.White,
                        effectivePrimary.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        effectivePrimary.copy(alpha = 0.75f),
                        effectiveSecondary.copy(alpha = 0.35f)
                    )
                ),
                shape = cardShape
            )
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // 1. Top Meta Row: Unit Pill on left, Boss Badge on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Unit & Level Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(effectivePrimary.copy(alpha = if (isDark) 0.15f else 0.10f))
                        .border(1.dp, effectivePrimary.copy(alpha = if (isDark) 0.40f else 0.25f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "UNIT ${spec.unitNumber}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = effectivePrimary
                        )
                        Text(text = "•", fontSize = 10.sp, color = SubtextGray)
                        Text(
                            text = "7 INTERACTIVE LEVELS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SubtextGray
                        )
                    }
                }

                // Boss Badge (Clean, single-line pill)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEF4444).copy(alpha = if (isDark) 0.15f else 0.10f))
                        .border(1.dp, Color(0xFFEF4444).copy(alpha = if (isDark) 0.40f else 0.28f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "BOSS: ${spec.bossName}",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFEF4444),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // 2. Main Title Row: 52dp Icon + Title & Subtitle aligned together
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(effectivePrimary.copy(alpha = if (isDark) 0.20f else 0.12f))
                        .border(1.5.dp, effectivePrimary.copy(alpha = if (isDark) 0.6f else 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = spec.iconEmoji, fontSize = 26.sp)
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = spec.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = dynamicColors.textPrimary
                    )
                    Text(
                        text = spec.subtitle,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = effectivePrimary
                    )
                }
            }

            // 3. Educational Description
            Text(
                text = spec.description,
                fontSize = 12.5.sp,
                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                lineHeight = 17.5.sp
            )

            // 4. Interactive Gamified Lab Tags
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "INTERACTIVE LAB SIMULATIONS:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = SubtextGray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    spec.interactiveModules.take(2).forEach { module ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isDark) Color.White.copy(alpha = 0.06f)
                                    else Color(0xFFF1F5F9)
                                )
                                .border(
                                    1.dp,
                                    if (isDark) effectivePrimary.copy(alpha = 0.25f)
                                    else Color(0xFFCBD5E1),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🎮 $module",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = dynamicColors.textPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // 5. Action Launch Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(effectivePrimary, effectiveSecondary)
                        )
                    )
                    .pressScale(targetScale = 0.96f)
                    .clickable { onEnterArena() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Enter",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "ENTER ${spec.title.uppercase()} ARENA",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        letterSpacing = 0.6.sp
                    )
                }
            }
        }
    }
}
