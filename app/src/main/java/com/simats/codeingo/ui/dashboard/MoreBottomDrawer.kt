package com.simats.codeingo.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun MoreBottomDrawer(
    onOpenPracticeHub: () -> Unit = {},
    onOpenShop: () -> Unit = {},
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(dynamicColors.cardBackground)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        val cardShape = RoundedCornerShape(20.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(CardBackground)
                .border(1.5.dp, InputBorder, cardShape)
        ) {
            MoreDrawerRow(
                icon = Icons.Default.Build,
                title = "PRACTICE HUB",
                subtitle = "Mistake Vault · Daily Review · Interview Mode",
                accentColor = DuolingoGreen,
                onClick = onOpenPracticeHub
            )
            DrawerDivider()
            MoreDrawerRow(
                icon = Icons.Default.ShoppingBag,
                title = "GEM SHOP",
                subtitle = "Streak Freezes · Hearts · Phoenix Skins",
                accentColor = AmberGold,
                onClick = onOpenShop
            )
            DrawerDivider()
            MoreDrawerRow(
                icon = Icons.Default.Settings,
                title = "SETTINGS",
                subtitle = "Theme · Audio · Profile & Data",
                accentColor = DuolingoBlue,
                onClick = onOpenSettings
            )
            DrawerDivider()
            MoreDrawerRow(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "LOG OUT",
                subtitle = "Sign out of active session",
                accentColor = DuolingoRed,
                titleColor = DuolingoRed,
                onClick = onLogout
            )
        }
    }
}

@Composable
private fun DrawerDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(InputBorder.copy(alpha = 0.6f))
    )
}

@Composable
private fun MoreDrawerRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    titleColor: Color = LocalDynamicThemeColors.current.textPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = titleColor
            )
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = SubtextGray
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = if (titleColor == DuolingoRed) DuolingoRed.copy(alpha = 0.6f) else SubtextGray
        )
    }
}
