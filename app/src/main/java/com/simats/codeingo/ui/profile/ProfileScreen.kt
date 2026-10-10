package com.simats.codeingo.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.ui.auth.LoginScreen
import com.simats.codeingo.ui.auth.ProfileCreationScreen
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.phoenix.PhoenixEmotionPickerSheet
import com.simats.codeingo.ui.phoenix.AnimatedGIFView
import com.simats.codeingo.ui.phoenix.PhoenixMascotImage
import com.simats.codeingo.ui.shop.CosmeticsCatalog
import com.simats.codeingo.ui.shop.GemShopSheet
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.liquidGlassCard
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.tilt3D

@Composable
fun ProfileScreen(
    onOpenSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val localizationManager = LocalizationManager.instance
    val emotionManager = PhoenixEmotionManager.instance
    val gameManager = GameManager.instance

    val isLoggedIn by localizationManager.isLoggedIn.collectAsState()
    val userName by localizationManager.userName.collectAsState()
    val userHandle by localizationManager.userHandle.collectAsState()
    val selectedLanguage by localizationManager.selectedLanguage.collectAsState()
    val currentEmotion by emotionManager.currentEmotion.collectAsState()
    val streakDays by gameManager.streakDays.collectAsState()
    val totalXP by gameManager.totalXP.collectAsState()
    val gemsCount by gameManager.gemsCount.collectAsState()

    var showEmotionSheet by remember { mutableStateOf(false) }
    var showCreateAccountSheet by remember { mutableStateOf(false) }
    var showSignInSheet by remember { mutableStateOf(false) }
    var showSignOutAlert by remember { mutableStateOf(false) }
    var showGemShopSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalDynamicThemeColors.current.background)
    ) {
        PhoenixAtmosphericBackgroundView()

        if (isLoggedIn) {
            AuthenticatedProfileBody(
                userName = userName,
                userHandle = userHandle,
                flagEmoji = selectedLanguage?.flagEmoji ?: "🇮🇳",
                streakDays = streakDays,
                gemsCount = gemsCount,
                totalXP = totalXP,
                currentEmotion = currentEmotion,
                onOpenEmotionSheet = { showEmotionSheet = true },
                onOpenEditProfile = { showCreateAccountSheet = true },
                onOpenGemShop = { showGemShopSheet = true },
                onSignOutClick = { showSignOutAlert = true }
            )
        } else {
            UnauthenticatedProfileBody(
                flagEmoji = selectedLanguage?.flagEmoji ?: "🇮🇳",
                currentEmotion = currentEmotion,
                onCreateAccount = { showCreateAccountSheet = true },
                onSignIn = { showSignInSheet = true },
                onQuickDemoSignIn = {
                    localizationManager.setLoggedIn(true, name = "Vishal Rao", handle = "VishalRao3454")
                }
            )
        }

        // Emotion Picker Modal Sheet
        if (showEmotionSheet) {
            PhoenixEmotionPickerSheet(
                onDismiss = { showEmotionSheet = false }
            )
        }

        // Create Account Modal Flow
        if (showCreateAccountSheet) {
            Dialog(
                onDismissRequest = { showCreateAccountSheet = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                ProfileCreationScreen(
                    onProfileCreated = { _, _ -> showCreateAccountSheet = false },
                    onLoginClick = {
                        showCreateAccountSheet = false
                        showSignInSheet = true
                    },
                    onDismiss = { showCreateAccountSheet = false }
                )
            }
        }

        // Sign In Modal
        if (showSignInSheet) {
            Dialog(
                onDismissRequest = { showSignInSheet = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                LoginScreen(
                    onLoginSuccess = { _, _ -> showSignInSheet = false },
                    onDismiss = { showSignInSheet = false },
                    onCreateAccountClick = {
                        showSignInSheet = false
                        showCreateAccountSheet = true
                    }
                )
            }
        }

        // Sign Out Confirmation Alert
        if (showSignOutAlert) {
            AlertDialog(
                onDismissRequest = { showSignOutAlert = false },
                containerColor = Color(0xFF141A29),
                title = {
                    Text(
                        text = "Sign Out",
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontWeight = FontWeight.Black
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to sign out of your Phoenix profile?",
                        color = SubtextGray
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSignOutAlert = false
                            localizationManager.setLoggedIn(false)
                            onLogout()
                        }
                    ) {
                        Text("Sign Out", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSignOutAlert = false }) {
                        Text("Cancel", color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.7f))
                    }
                }
            )
        }

        // Duolingo Gem Shop Modal
        if (showGemShopSheet) {
            GemShopSheet(
                onDismiss = { showGemShopSheet = false }
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 👤 Authenticated Profile Content
// ══════════════════════════════════════════════════════════════════
@Composable
private fun AuthenticatedProfileBody(
    userName: String,
    userHandle: String,
    flagEmoji: String,
    streakDays: Int,
    gemsCount: Int,
    totalXP: Int,
    currentEmotion: com.simats.codeingo.data.model.PhoenixEmotion,
    onOpenEmotionSheet: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onOpenGemShop: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Top Action Bar with Sign Out
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "👤", fontSize = 18.sp)
                Text(
                    text = "PROFILE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary
                )
            }

            val isDark = LocalDynamicThemeColors.current.isDark
            val pillBg = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(pillBg)
                    .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                    .clickable { onSignOutClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "↪", fontSize = 12.sp, color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.8f))
                    Text(
                        text = "Sign Out",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // 1. User Header & Phoenix Mascot Card
        UserHeaderCard(
            userName = userName,
            userHandle = userHandle,
            flagEmoji = flagEmoji,
            currentEmotion = currentEmotion,
            onOpenEmotionSheet = onOpenEmotionSheet,
            onOpenEditProfile = onOpenEditProfile
        )

        // 2. Level & XP Progress Card (Matches iOS ProfileView)
        LevelProgressCard(
            currentLevel = 8,
            totalXP = totalXP,
            maxLevelXP = 1000
        )

        // 3. Customization: YOUR PHOENIX Section (Matches iOS ProfileView)
        YourPhoenixCustomizationSection(
            onOpenGemShop = onOpenGemShop
        )

        // 4. Statistics Section (2x2 Grid)
        StatisticsSection(
            streakDays = streakDays,
            gemsCount = gemsCount,
            totalXP = totalXP
        )

        // 5. Learning Journey Section (Matches iOS ProfileView)
        LearningJourneySection()

        // 6. Achievements Section
        AchievementsSection(
            streakDays = streakDays,
            totalXP = totalXP,
            onViewAllClick = {}
        )

        // 7. Add Friends Section
        AddFriendsSection()

        AppButton(
            title = "🚪 SIGN OUT OF PROFILE",
            style = AppButtonStyle.DANGER_CRIMSON,
            onClick = onSignOutClick,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        )

        Spacer(modifier = Modifier.height(72.dp))
    }
}

// ══════════════════════════════════════════════════════════════════
// 🦅 User Header Card with Living Emotion Mascot
// ══════════════════════════════════════════════════════════════════
@Composable
private fun UserHeaderCard(
    userName: String,
    userHandle: String,
    flagEmoji: String,
    currentEmotion: com.simats.codeingo.data.model.PhoenixEmotion,
    onOpenEmotionSheet: () -> Unit,
    onOpenEditProfile: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Phoenix Avatar Box (Liquid Glass)
        val isDark = LocalDynamicThemeColors.current.isDark
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .liquidGlassCard(accentGlow = AmberGold, cornerRadius = 24.dp)
        ) {
            // Edit Pencil in Top Right
            val editBtnBg = if (LocalDynamicThemeColors.current.isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(editBtnBg)
                    .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.15f), CircleShape)
                    .clickable { onOpenEditProfile() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✏️", fontSize = 14.sp)
            }

            // Centered Emotion Avatar & Badge
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Mascot Circle with Aura Blur
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { onOpenEmotionSheet() },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .blur(14.dp)
                            .clip(CircleShape)
                            .background(currentEmotion.auraColor.copy(alpha = 0.35f))
                    )

                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF0D1426) else Color.White)
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        currentEmotion.auraColor,
                                        currentEmotion.auraColor.copy(alpha = 0.4f)
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedGIFView(resourceName = "phoenix_flying", size = 66.dp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Emotion Badge Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (LocalDynamicThemeColors.current.isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f))
                        .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable { onOpenEmotionSheet() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = currentEmotion.emoji, fontSize = 14.sp)
                        Text(
                            text = currentEmotion.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = LocalDynamicThemeColors.current.textPrimary
                        )
                        Text(text = "✨", fontSize = 11.sp)
                    }
                }
            }
        }

        // Name, Handle, Join Date & Flag Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = userName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary
                )

                Text(
                    text = "@$userHandle",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold
                )

                Text(
                    text = "Joined September 2026",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.4f)
                )

                // Following / Followers Row
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "0 Following",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold
                    )
                    Text(
                        text = "0 Followers",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold
                    )
                }
            }

            // Flag badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (LocalDynamicThemeColors.current.isDark) Color.White.copy(alpha = 0.07f) else Color.Black.copy(alpha = 0.04f))
                    .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Text(text = flagEmoji, fontSize = 28.sp)
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// ⚡ Level & XP Progress Card (Matches iOS ProfileView)
// ══════════════════════════════════════════════════════════════════
@Composable
private fun LevelProgressCard(
    currentLevel: Int = 8,
    totalXP: Int = 860,
    maxLevelXP: Int = 1000
) {
    val remainingXP = (maxLevelXP - totalXP).coerceAtLeast(0)
    val progress = (totalXP.toFloat() / maxLevelXP.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlassCard(accentGlow = AmberGold, cornerRadius = 20.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "👑", fontSize = 16.sp)
                Text(
                    text = "LEVEL $currentLevel",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary
                )
            }
            Text(
                text = "$totalXP / $maxLevelXP XP",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold
            )
        }

        // Smooth Capsule Progress Bar (16dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (LocalDynamicThemeColors.current.isDark) Color.White.copy(alpha = 0.08f)
                    else Color.Black.copy(alpha = 0.06f)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFFB300), Color(0xFFFF8C00))
                        )
                    )
            )
        }

        // Bottom Info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = "⚡", fontSize = 12.sp)
            Text(
                text = "$remainingXP XP to Level ${currentLevel + 1}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.65f)
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 🦅 Customization: YOUR PHOENIX Section (Matches iOS ProfileView)
// ══════════════════════════════════════════════════════════════════
@Composable
private fun YourPhoenixCustomizationSection(
    onOpenGemShop: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("dsa_cosmetics_prefs", Context.MODE_PRIVATE) }
    val headId = prefs.getString("dsaEquippedHeadwear", "cap_grad") ?: "cap_grad"
    val outfitId = prefs.getString("dsaEquippedOutfit", "none_outfit") ?: "none_outfit"
    val accId = prefs.getString("dsaEquippedAccessory", "glasses_nerd") ?: "glasses_nerd"

    val headItem = CosmeticsCatalog.item(headId)
    val outfitItem = CosmeticsCatalog.item(outfitId)
    val accItem = CosmeticsCatalog.item(accId)

    val headEmoji = headItem?.emoji ?: "🎓"
    val outfitEmoji = outfitItem?.emoji ?: ""
    val accEmoji = accItem?.emoji ?: "👓"

    val headName = headItem?.name ?: "Natural"
    val accName = accItem?.name ?: "None"
    val outfitName = outfitItem?.name

    val subtitle = buildString {
        append(headName)
        if (outfitName != null && outfitId != "none_outfit") {
            append(" • ")
            append(outfitName)
        }
        append(" • ")
        append(accName)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "YOUR PHOENIX",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.6f),
            letterSpacing = 0.5.sp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlassCard(accentGlow = Color(0xFF58CC02), cornerRadius = 20.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Preview Icon Circle
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(AmberGold.copy(alpha = 0.18f))
                        .border(1.dp, AmberGold.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$headEmoji$outfitEmoji$accEmoji",
                        fontSize = 18.sp
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "Equipped Cosmetics",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocalDynamicThemeColors.current.textPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }
            }

            // Prominent "Customize Phoenix" Button (Duolingo 3D Button Aesthetic)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .pressScale()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color(0xFF46A302),
                        spotColor = Color(0xFF46A302)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF58CC02))
                    .border(1.5.dp, Color(0xFF46A302), RoundedCornerShape(16.dp))
                    .clickable { onOpenGemShop() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "✨", fontSize = 16.sp)
                    Text(
                        text = "Customize Phoenix",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 📊 Statistics Section (2x2 Grid)
// ══════════════════════════════════════════════════════════════════
@Composable
private fun StatisticsSection(
    streakDays: Int,
    gemsCount: Int,
    totalXP: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = "📊", fontSize = 16.sp)
            Text(
                text = "STATISTICS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = LocalDynamicThemeColors.current.textPrimary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileStatCard(
                icon = "🔥",
                value = "$streakDays days",
                label = "STREAK",
                accentColor = Color(0xFFFF9500),
                modifier = Modifier.weight(1f)
            )
            ProfileStatCard(
                icon = "💎",
                value = "$gemsCount",
                label = "GEMS",
                accentColor = Color(0xFF1CA6FF),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileStatCard(
                icon = "⚡",
                value = "$totalXP XP",
                label = "TOTAL XP",
                accentColor = AmberGold,
                modifier = Modifier.weight(1f)
            )
            ProfileStatCard(
                icon = "🏆",
                value = "#1 Diamond",
                label = "CURRENT LEAGUE",
                accentColor = Color(0xFF8CE036),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 🗺️ Learning Journey Section (Matches iOS ProfileView)
// ══════════════════════════════════════════════════════════════════
private data class LearningJourneyTopic(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val color: Color,
    val progress: Float,
    val isLocked: Boolean
) {
    val progressText: String
        get() = if (isLocked) "LOCKED" else "${(progress * 100).toInt()}%"
}

@Composable
private fun LearningJourneySection() {
    val topics = remember {
        listOf(
            LearningJourneyTopic("array", "Array", "📦", Color(0xFF2B70CA), 0.85f, false),
            LearningJourneyTopic("stack", "Stack", "🥞", Color(0xFFF5A623), 0.70f, false),
            LearningJourneyTopic("queue", "Queue", "🚶‍♂️", Color(0xFF58CC02), 0.55f, false),
            LearningJourneyTopic("linked_list", "Linked List", "🔗", Color(0xFFA659F2), 0.30f, false),
            LearningJourneyTopic("binary_tree", "Binary Tree", "🌲", Color(0xFFEB4747), 0.0f, true)
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "LEARNING JOURNEY",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.6f),
            letterSpacing = 0.5.sp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlassCard(accentGlow = Color(0xFF22D3EE), cornerRadius = 20.dp)
                .padding(vertical = 4.dp)
        ) {
            topics.forEachIndexed { index, topic ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Topic Icon Badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (topic.isLocked) Color.White.copy(alpha = 0.05f)
                                else topic.color.copy(alpha = 0.18f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (topic.isLocked) "🔒" else topic.iconEmoji, fontSize = 20.sp)
                    }

                    // Topic Name & Progress Bar
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = topic.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (topic.isLocked) LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.5f)
                            else LocalDynamicThemeColors.current.textPrimary
                        )

                        if (!topic.isLocked) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (LocalDynamicThemeColors.current.isDark) Color.White.copy(alpha = 0.08f)
                                        else Color.Black.copy(alpha = 0.06f)
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(topic.progress)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(topic.color)
                                )
                            }
                        }
                    }

                    // Progress % or LOCKED pill
                    if (topic.isLocked) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = topic.progressText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        Text(
                            text = topic.progressText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = topic.color
                        )
                    }
                }

                if (index < topics.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 74.dp)
                            .height(1.dp)
                            .background(
                                if (LocalDynamicThemeColors.current.isDark) Color.White.copy(alpha = 0.08f)
                                else Color.Black.copy(alpha = 0.06f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStatCard(
    icon: String,
    value: String,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .liquidGlassCard(accentGlow = accentColor, cornerRadius = 16.dp)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f))
                .border(1.dp, accentColor.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 20.sp)
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = LocalDynamicThemeColors.current.textPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.5f)
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 🏆 Achievements Section
// ══════════════════════════════════════════════════════════════════
@Composable
private fun AchievementsSection(
    streakDays: Int,
    totalXP: Int,
    onViewAllClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "🏆", fontSize = 16.sp)
                Text(
                    text = "ACHIEVEMENTS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary
                )
            }

            Text(
                text = "VIEW ALL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AchievementRow(
                icon = "🔥",
                level = 1,
                title = "Wildfire",
                subtitle = "Reach a 3-day streak",
                progress = minOf(streakDays, 3),
                maxProgress = 3,
                barColor = Color(0xFFFF9500),
                gradient = listOf(Color(0xFFD9381E), Color(0xFF8B1800))
            )

            AchievementRow(
                icon = "🧙‍♂️",
                level = 1,
                title = "Sage",
                subtitle = "Earn 100 XP in DSA courses",
                progress = minOf(totalXP, 100),
                maxProgress = 100,
                barColor = Color(0xFF2CB84B),
                gradient = listOf(Color(0xFF1E823C), Color(0xFF0F4D22))
            )

            AchievementRow(
                icon = "🛡️",
                level = 1,
                title = "Champion",
                subtitle = "Unlock leaderboards & compete",
                progress = 1,
                maxProgress = 1,
                barColor = Color(0xFF9E47FF),
                gradient = listOf(Color(0xFF6B2FB8), Color(0xFF3F1970))
            )
        }
    }
}

@Composable
private fun AchievementRow(
    icon: String,
    level: Int,
    title: String,
    subtitle: String,
    progress: Int,
    maxProgress: Int,
    barColor: Color,
    gradient: List<Color>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlassCard(accentGlow = barColor, cornerRadius = 18.dp)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Level Badge
        Box(
            modifier = Modifier
                .size(50.dp, 56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(gradient)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = icon, fontSize = 22.sp)
                Text(
                    text = "LVL $level",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary
                )
                Text(
                    text = "$progress/$maxProgress",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.45f)
                )
            }

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (LocalDynamicThemeColors.current.isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f))
            ) {
                val fraction = if (maxProgress > 0) (progress.toFloat() / maxProgress).coerceIn(0f, 1f) else 0f
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(barColor)
                )
            }

            Text(
                text = subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.45f)
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 👥 Add Friends Section
// ══════════════════════════════════════════════════════════════════
@Composable
private fun AddFriendsSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = "👥", fontSize = 16.sp)
            Text(
                text = "ADD FRIENDS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = LocalDynamicThemeColors.current.textPrimary
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Find Friends Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(accentGlow = DuolingoBlue, cornerRadius = 16.dp)
                    .clickable { }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(DuolingoBlue.copy(alpha = 0.18f))
                        .border(1.dp, DuolingoBlue.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔍", fontSize = 20.sp)
                }

                Text(
                    text = "Find Friends",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "›",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.35f)
                )
            }

            // Invite Friends Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(accentGlow = AmberGold, cornerRadius = 16.dp)
                    .clickable { }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AmberGold.copy(alpha = 0.18f))
                        .border(1.dp, AmberGold.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📩", fontSize = 20.sp)
                }

                Text(
                    text = "Invite Friends",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "›",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.35f)
                )
            }
        }

        Spacer(modifier = Modifier.height(56.dp))
    }
}

// ══════════════════════════════════════════════════════════════════
// 🐣 Unauthenticated Guest Profile Content
// ══════════════════════════════════════════════════════════════════
@Composable
private fun UnauthenticatedProfileBody(
    flagEmoji: String,
    currentEmotion: com.simats.codeingo.data.model.PhoenixEmotion,
    onCreateAccount: () -> Unit,
    onSignIn: () -> Unit,
    onQuickDemoSignIn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🔥", fontSize = 18.sp)
                Text(
                    text = "PROFILE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (LocalDynamicThemeColors.current.isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f))
                    .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                    .padding(6.dp)
            ) {
                Text(text = flagEmoji, fontSize = 22.sp)
            }
        }

        // Hero Card: Phoenix Mascot & Guest Call-To-Action
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(LocalDynamicThemeColors.current.cardBackground)
                .border(1.dp, AmberGold.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = AmberGold.copy(alpha = 0.25f))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Mascot with Radiant Flame Glow
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .blur(18.dp)
                        .clip(CircleShape)
                        .background(AmberGold.copy(alpha = 0.28f))
                )

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF73260D), Color(0xFF1A1438))
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(AmberGold, Color(0xFFFF7219))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedGIFView(resourceName = "phoenix_flying", size = 72.dp)
                }
            }

            // Explorer Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AmberGold.copy(alpha = 0.12f))
                    .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "✨", fontSize = 10.sp)
                    Text(
                        text = "GUEST EXPLORER • NOT SIGNED IN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Create a Profile to Save Your Progress",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Join thousands of engineers leveling up Data Structures, saving daily streaks, and climbing the global Obsidian League.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            // Action Buttons: CREATE ACCOUNT & SIGN IN
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppButton(
                    title = "🔥 CREATE ACCOUNT",
                    style = AppButtonStyle.SUCCESS_GREEN,
                    onClick = onCreateAccount,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                )

                AppButton(
                    title = "👤 SIGN IN",
                    style = AppButtonStyle.SECONDARY_GLASS,
                    onClick = onSignIn,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )
            }
        }

        // Why Join the Phoenix Realm?
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "⭐", fontSize = 14.sp)
                Text(
                    text = "WHY JOIN THE PHOENIX REALM?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold,
                    letterSpacing = 0.8.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BenefitCard(
                    icon = "🔥",
                    title = "Daily Fire Streaks",
                    subtitle = "Keep your algorithm flame alive without losing progress.",
                    accentColor = Color(0xFFFF9500),
                    modifier = Modifier.weight(1f)
                )
                BenefitCard(
                    icon = "🛡️",
                    title = "Obsidian League",
                    subtitle = "Compete against engineers worldwide every week.",
                    accentColor = AmberGold,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BenefitCard(
                    icon = "⚡",
                    title = "Save Solved DSA",
                    subtitle = "Synchronize solved Trees, Graphs & Dynamic Programming.",
                    accentColor = Color(0xFF4DB0FF),
                    modifier = Modifier.weight(1f)
                )
                BenefitCard(
                    icon = "🦅",
                    title = "28 Mascot Emotions",
                    subtitle = "Unlock living Phoenix expressions that react to your code.",
                    accentColor = Color(0xFFE666CC),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick One-Tap Demo Sign In
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (LocalDynamicThemeColors.current.isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f))
                .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                .clickable { onQuickDemoSignIn() }
                .padding(vertical = 10.dp, horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "⚡", fontSize = 12.sp)
                Text(
                    text = "Demo: One-Tap Sign In as Vishal Rao",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(56.dp))
    }
}

@Composable
private fun BenefitCard(
    icon: String,
    title: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F1726))
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f))
                .border(1.dp, accentColor.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 18.sp)
        }

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = LocalDynamicThemeColors.current.textPrimary
        )

        Text(
            text = subtitle,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.55f),
            lineHeight = 15.sp
        )
    }
}
