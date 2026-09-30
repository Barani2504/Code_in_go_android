package com.simats.duolingo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.R
import com.simats.duolingo.data.AppState
import com.simats.duolingo.ui.components.DuolingoButton
import com.simats.duolingo.ui.theme.*

@Composable
fun ShopScreen(
    onOpenCreateProfile: () -> Unit = {}
) {
    val isLoggedIn = AppState.isLoggedIn

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .then(
                    if (!isLoggedIn) Modifier.blur(3.dp) else Modifier
                ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (isLoggedIn) {
                // 1. Super Duolingo Hero Banner
                SuperTrialHeroBanner()
            }

            // 2. Hearts Section
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "HEARTS",
                    color = DuolingoSubtext,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                ShopRowCard(
                    icon = "❤️",
                    title = "Refill Hearts",
                    subtitle = "Get full hearts so you can worry less about making mistakes in lessons.",
                    actionText = "FULL",
                    isAvailable = AppState.heartsCount >= 5
                )

                ShopRowCard(
                    icon = "♾️",
                    title = "Unlimited Hearts",
                    subtitle = "Never run out of hearts with Super Duolingo!",
                    actionText = "FREE TRIAL",
                    actionColor = DuolingoBlue
                )
            }

            // 3. Power-Ups Section
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "POWER-UPS",
                    color = DuolingoSubtext,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                ShopRowCard(
                    icon = "❄️",
                    title = "Streak Freeze",
                    subtitle = "Streak Freeze allows your streak to remain in place for one full day of inactivity.",
                    actionText = "200 💎",
                    actionColor = DuolingoBlue
                )

                ShopRowCard(
                    icon = "🔥",
                    title = "Double or Nothing",
                    subtitle = "Double your 50 gem wager by maintaining a 7 day streak.",
                    actionText = "50 💎",
                    actionColor = Color(0xFFFF9600)
                )
            }

            Spacer(Modifier.height(40.dp))
        }

        // Center Popover Modal Banner when not logged in
        if (!isLoggedIn) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(DuolingoCardBg)
                        .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF9600).copy(0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.duo_backpack),
                            contentDescription = "Mascot",
                            modifier = Modifier.size(72.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Text(
                        text = "You earned 500 gems! Create a profile to spend them in the store!",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )

                    DuolingoButton(
                        text = "CREATE PROFILE",
                        backgroundColor = DuolingoGreen,
                        shadowColor = DuolingoGreenDark,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        onOpenCreateProfile()
                    }

                    // Demo Toggle Button matching iOS ShopView.swift line 307
                    Text(
                        text = if (isLoggedIn) "DEMO: LOG OUT" else "DEMO: SIMULATE LOGGED IN STORE",
                        color = DuolingoSubtext,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { AppState.isLoggedIn = !AppState.isLoggedIn }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SuperTrialHeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1E2E7B), Color(0xFF611E94))
                )
            )
            .border(2.dp, Color(0xFF3B82F6), RoundedCornerShape(24.dp))
            .shadow(10.dp, RoundedCornerShape(24.dp), spotColor = Color.Magenta.copy(0.35f))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF9600).copy(0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.duo_backpack),
                        contentDescription = "Super Mascot",
                        modifier = Modifier.size(52.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Cyan, Color.Blue)
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("SUPER", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }

            Text("Start a 2-Week Free Trial", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("Unlimited hearts, personalized practice, and no ads.", color = Color.White.copy(0.85f), fontSize = 13.sp)

            DuolingoButton(
                text = "START 2 WEEKS FREE",
                backgroundColor = Color.White,
                shadowColor = Color(200, 200, 200),
                textColor = Color(0xFF1E2E7B),
                modifier = Modifier.fillMaxWidth()
            ) {}
        }
    }
}

@Composable
private fun ShopRowCard(
    icon: String,
    title: String,
    subtitle: String,
    actionText: String,
    actionColor: Color = DuolingoGreen,
    isAvailable: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DuolingoCardBg)
            .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(icon, fontSize = 32.sp)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = DuolingoSubtext, fontSize = 12.sp, lineHeight = 16.sp)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isAvailable) actionColor else DuolingoInputBorder)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(actionText, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
    }
}
