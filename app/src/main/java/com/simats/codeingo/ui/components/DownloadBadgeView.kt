package com.simats.codeingo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import com.simats.codeingo.ui.theme.CardBackground

enum class StorePlatform(val title: String, val subtitle: String, val icon: ImageVector) {
    AppStore("App Store", "Download on the", Icons.Default.Star),
    GooglePlay("Google Play", "Get it on", Icons.Default.PlayArrow)
}

@Composable
fun DownloadBadgeButton(
    platform: StorePlatform,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(CardBackground.copy(alpha = 0.8f))
            .border(1.5.dp, Color.White.copy(alpha = 0.2f), shape)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = platform.icon,
            contentDescription = platform.title,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = platform.subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = platform.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun DownloadBadgeView(
    onSelectPlatform: (StorePlatform) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        DownloadBadgeButton(
            platform = StorePlatform.AppStore,
            onClick = { onSelectPlatform(StorePlatform.AppStore) },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        DownloadBadgeButton(
            platform = StorePlatform.GooglePlay,
            onClick = { onSelectPlatform(StorePlatform.GooglePlay) },
            modifier = Modifier.weight(1f)
        )
    }
}
