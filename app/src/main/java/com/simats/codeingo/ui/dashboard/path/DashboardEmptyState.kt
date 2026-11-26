package com.simats.codeingo.ui.dashboard.path

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.AshButton
import com.simats.codeingo.ui.components.AshButtonSize
import com.simats.codeingo.ui.components.AshButtonStyle
import com.simats.codeingo.ui.dashboard.PhoenixEggCompanionMascotView
import com.simats.codeingo.ui.theme.AshTheme
import com.simats.codeingo.ui.theme.background
import com.simats.codeingo.ui.theme.subtle

/**
 * 🪹 DashboardEmptyState — Reliable fallback state with Ash mascot and retry action.
 * Ensures the screen is never blank during network issues or cold sync delays.
 */
@Composable
fun DashboardEmptyState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Ash is gathering your path...",
    subtitle: String = "Could not sync curriculum right now. Tap below to reconnect."
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AshTheme.colors.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PhoenixEggCompanionMascotView(
                levelNumber = 1,
                modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = AshTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AshTheme.colors.ink,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = AshTheme.typography.bodyLarge,
                color = AshTheme.colors.subtle,
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            AshButton(
                text = "Retry Sync",
                onClick = onRetry,
                variant = AshButtonStyle.BRAND,
                size = AshButtonSize.REGULAR,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
    }
}
