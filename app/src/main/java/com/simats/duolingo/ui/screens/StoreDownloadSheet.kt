package com.simats.duolingo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.ui.components.DuolingoButton
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class StorePlatform(val title: String, val badgeText: String, val icon: String) {
    APP_STORE("iOS (iPhone & iPad)", "App Store", "🍎"),
    GOOGLE_PLAY("Android", "Google Play", "▶")
}

@Composable
fun StoreDownloadSheet(
    platform: StorePlatform = StorePlatform.GOOGLE_PLAY,
    onDismiss: () -> Unit
) {
    var copiedToast by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Dismiss button
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onDismiss) {
                    Text("✕", color = DuolingoSubtext, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Platform Icon
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(DuolingoCardBg)
                    .border(1.dp, DuolingoInputBorder, RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(platform.icon, fontSize = 44.sp)
            }

            Text(
                text = "Code in Go for ${platform.title}",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Star Rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("4.8", color = Color(0xFFFF9600), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(5) {
                        Text("★", color = Color(0xFFFF9600), fontSize = 14.sp)
                    }
                }
                Text("• 12M+ Ratings", color = DuolingoSubtext, fontSize = 13.sp)
            }

            HorizontalDivider(color = DuolingoInputBorder)

            // Details
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🏆", fontSize = 24.sp)
                    Column {
                        Text("Top Coding Education App", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Best of the Best on ${platform.badgeText}", color = DuolingoSubtext, fontSize = 12.sp)
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥", fontSize = 24.sp)
                    Column {
                        Text("Bite-sized Coding Challenges", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Learn 12+ programming languages with fun streak goals.", color = DuolingoSubtext, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            DuolingoButton(
                text = "GET APP LINK",
                backgroundColor = DuolingoGreen,
                shadowColor = DuolingoGreenDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                copiedToast = true
                coroutineScope.launch {
                    delay(2000)
                    copiedToast = false
                }
            }
        }

        AnimatedVisibility(
            visible = copiedToast,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DuolingoGreen)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text("Download link copied to clipboard!", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
