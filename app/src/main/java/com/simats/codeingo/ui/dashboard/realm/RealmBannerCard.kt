package com.simats.codeingo.ui.dashboard.realm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.CnsRealm
import com.simats.codeingo.ui.dashboard.PhoenixEggCompanionMascotView
import com.simats.codeingo.ui.theme.AshTheme
import com.simats.codeingo.ui.theme.accentContainer
import com.simats.codeingo.ui.theme.border
import com.simats.codeingo.ui.theme.subtle

/**
 * 👑 RealmBannerCard — Collapsible Realm Banner for each of the 5 Syllabus Units.
 *
 * Requirements:
 *  - Title & Emoji ("Realm 1: Secrets 101", etc.)
 *  - Progress ring showing chapters completed (e.g., 2/5).
 *  - Ash mascot line giving context on why this realm matters.
 *  - Boss gate preview at the end of the realm.
 *  - Future realms are visible but locked with a silhouette + "Unlock by finishing Realm X" banner.
 */
@Composable
fun RealmBannerCard(
    realm: CnsRealm,
    isUnlocked: Boolean,
    isExpanded: Boolean,
    completedChaptersCount: Int,
    totalChaptersInRealm: Int = 5,
    onToggleExpand: () -> Unit,
    onOpenBossArena: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val progress = (completedChaptersCount.toFloat() / totalChaptersInRealm.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "realmProgress")
    val chevronRotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "chevronRotate")

    val realmColor = when (realm.id) {
        1 -> Color(0xFFFF6E0F) // Secrets 101 (Orange)
        2 -> Color(0xFFF02D4B) // Symmetric Forge (Crimson)
        3 -> Color(0xFFFFA500) // Public-Key Citadel (Amber)
        4 -> Color(0xFFB946FA) // Trust & Proof (Purple)
        5 -> Color(0xFF10B981) // Real-World Defense (Emerald)
        else -> AshTheme.colors.brand
    }

    val ashQuote = when (realm.id) {
        1 -> "Master classical shifts and frequency analysis before opening the forge!"
        2 -> "Feel the heat of Feistel networks, DES rounds, and AES state matrices!"
        3 -> "Harness prime trapdoors, Diffie-Hellman paint, and elliptic curves!"
        4 -> "Forge collision-resistant hashes, HMAC seals, and digital signatures!"
        5 -> "Defend network borders with TLS 1.3, firewalls, and post-quantum shields!"
        else -> "Master every challenge on your path to cryptographic mastery!"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AshTheme.colors.surface)
            .border(
                1.5.dp,
                if (isUnlocked) realmColor.copy(alpha = 0.5f) else AshTheme.colors.border,
                RoundedCornerShape(16.dp)
            )
            .clickable { onToggleExpand() }
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(realmColor.copy(alpha = if (isUnlocked) 0.18f else 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            Text(text = realm.emoji, fontSize = 24.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = AshTheme.colors.subtle,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "REALM ${realm.id}: ${realm.title.uppercase()}",
                                style = AshTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) AshTheme.colors.ink else AshTheme.colors.subtle
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = realm.syllabusUnit,
                            style = AshTheme.typography.labelSmall,
                            color = AshTheme.colors.subtle,
                            fontSize = 12.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isUnlocked) {
                        // Circular Progress Indicator
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.size(34.dp),
                                color = realmColor,
                                trackColor = AshTheme.colors.border,
                                strokeWidth = 3.5.dp,
                                strokeCap = StrokeCap.Round
                            )
                            Text(
                                text = "$completedChaptersCount/$totalChaptersInRealm",
                                style = AshTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = AshTheme.colors.ink
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = AshTheme.colors.subtle,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(chevronRotation)
                    )
                }
            }

            // Unlocked Ash Line
            if (isUnlocked) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AshTheme.colors.accentContainer)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PhoenixEggCompanionMascotView(
                        levelNumber = realm.id * 5,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = ashQuote,
                        style = AshTheme.typography.bodySmall,
                        color = AshTheme.colors.ink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // Locked Realm silhouette text (Phase 5 requirement)
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AshTheme.colors.border.copy(alpha = 0.4f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "🔒 Realm ${realm.id} Locked — Unlock by finishing Realm ${realm.id - 1}",
                        style = AshTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AshTheme.colors.subtle,
                        fontSize = 13.sp
                    )
                }
            }

            // Collapsible Boss Gate Preview
            AnimatedVisibility(visible = isExpanded && isUnlocked) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        realmColor.copy(alpha = 0.12f),
                                        AshTheme.colors.surface
                                    )
                                )
                            )
                            .border(1.dp, realmColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .clickable { onOpenBossArena?.invoke() }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👹", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "CAPSTONE BOSS: ${realm.bossName.uppercase()}",
                                        style = AshTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = realmColor
                                    )
                                    Text(
                                        text = "Threat Arena: ${realm.arenaName}",
                                        style = AshTheme.typography.bodySmall,
                                        color = AshTheme.colors.ink,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            Text(
                                text = "Defeat Boss →",
                                style = AshTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = realmColor
                            )
                        }
                    }
                }
            }
        }
    }
}
