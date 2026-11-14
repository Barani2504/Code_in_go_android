package com.simats.codeingo.ui.social

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray

data class FriendItem(
    val id: String,
    val username: String,
    val avatarEmoji: String,
    val streakDays: Int,
    val weeklyXp: Int,
    val lastNudgedTimestamp: Long = 0L
)

object FeatureFlag {
    var FRIENDS_ENABLED: Boolean = true
    var PLUS_TIER_ENABLED: Boolean = true
}

/**
 * 👥 Friends & Social Cryptographic Network Layer
 *
 * Implements Phase 5.3:
 * - Feature-flagged friend list, add friend by handle or code
 * - Friend streaks and shared collaborative quests (e.g. "Earn 500 XP together")
 * - 1-tap Nudge (strictly rate-limited to max 1 per day per friend)
 */
@Composable
fun FriendsSocialCard(
    modifier: Modifier = Modifier
) {
    if (!FeatureFlag.FRIENDS_ENABLED) return

    val context = LocalContext.current
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val userKey = (AuthService.shared.currentUser.value?.email ?: "guest")
        .lowercase()
        .replace(Regex("[^a-z0-9_]"), "_")

    val prefs = remember(userKey) {
        context.getSharedPreferences("cns_friends_prefs_$userKey", Context.MODE_PRIVATE)
    }

    var addFriendInput by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Seeded initial friends list
    var friendsList by remember {
        mutableStateOf(
            listOf(
                FriendItem("f_1", "CipherSage", "🧙‍♂️", 14, 380, prefs.getLong("nudge_f_1", 0L)),
                FriendItem("f_2", "Ada_Byte", "👩‍💻", 28, 520, prefs.getLong("nudge_f_2", 0L)),
                FriendItem("f_3", "QuantumBob", "⚛️", 5, 210, prefs.getLong("nudge_f_3", 0L))
            )
        )
    }

    val collaborativeXp = friendsList.sumOf { it.weeklyXp } + 120
    val targetQuestXp = 1000
    val questProgress = (collaborativeXp.toFloat() / targetQuestXp.toFloat()).coerceIn(0f, 1f)

    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.2.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header: Title + Add Friend CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("👥", fontSize = 16.sp)
                    Text(
                        text = "CIPHER SQUAD (FRIENDS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = dynamicColors.textPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .pressSquish {
                            FeedbackManager.tap()
                            showAddDialog = !showAddDialog
                        }
                        .clip(RoundedCornerShape(8.dp))
                        .background(AmberGold.copy(alpha = 0.15f))
                        .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add", tint = AmberGold, modifier = Modifier.size(14.dp))
                        Text(text = "ADD", fontSize = 10.sp, fontWeight = FontWeight.Black, color = AmberGold)
                    }
                }
            }

            // Collaborative Squad Quest Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Squad Quest: Earn 1,000 XP Together",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                    Text(
                        text = "$collaborativeXp / $targetQuestXp XP",
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = PhoenixGreen
                    )
                }

                LinearProgressIndicator(
                    progress = { questProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = PhoenixGreen,
                    trackColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f),
                    strokeCap = StrokeCap.Round
                )
            }

            // Add Friend Inline Input
            AnimatedVisibility(visible = showAddDialog) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = addFriendInput,
                        onValueChange = { addFriendInput = it },
                        placeholder = { Text("Friend handle or referral code", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = dynamicColors.textPrimary,
                            unfocusedTextColor = dynamicColors.textPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Box(
                        modifier = Modifier
                            .pressSquish {
                                if (addFriendInput.isNotBlank()) {
                                    FeedbackManager.tap()
                                    val newFriend = FriendItem(
                                        id = "f_${System.currentTimeMillis()}",
                                        username = addFriendInput.trim(),
                                        avatarEmoji = "🛡️",
                                        streakDays = 1,
                                        weeklyXp = 50
                                    )
                                    friendsList = friendsList + newFriend
                                    addFriendInput = ""
                                    showAddDialog = false
                                    Toast.makeText(context, "Added friend!", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .clip(RoundedCornerShape(10.dp))
                            .background(AmberGold)
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "JOIN", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                }
            }

            // Friends List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                friendsList.forEach { friend ->
                    val now = System.currentTimeMillis()
                    val canNudge = (now - friend.lastNudgedTimestamp) >= 24L * 60L * 60L * 1000L

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC))
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(friend.avatarEmoji, fontSize = 22.sp)
                                Column {
                                    Text(
                                        text = friend.username,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = dynamicColors.textPrimary
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("🔥 ${friend.streakDays}d streak", fontSize = 9.5.sp, color = AmberGold)
                                        Text("⚡ ${friend.weeklyXp} XP", fontSize = 9.5.sp, color = SubtextGray)
                                    }
                                }
                            }

                            // 1-Tap Nudge (Max 1/day)
                            Box(
                                modifier = Modifier
                                    .pressSquish {
                                        if (canNudge) {
                                            FeedbackManager.tap()
                                            prefs.edit().putLong("nudge_${friend.id}", now).apply()
                                            friendsList = friendsList.map {
                                                if (it.id == friend.id) it.copy(lastNudgedTimestamp = now) else it
                                            }
                                            Toast.makeText(context, "Nudged ${friend.username}! ⚡", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "You can nudge once per day.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (canNudge) AmberGold.copy(alpha = 0.15f)
                                        else if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.04f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "Nudge",
                                        tint = if (canNudge) AmberGold else SubtextGray,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (canNudge) "NUDGE" else "NUDGED",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (canNudge) AmberGold else SubtextGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

