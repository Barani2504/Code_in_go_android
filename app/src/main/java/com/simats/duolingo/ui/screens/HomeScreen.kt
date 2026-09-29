package com.simats.duolingo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.simats.duolingo.data.AppState
import com.simats.duolingo.data.defaultLanguages
import com.simats.duolingo.ui.components.DuolingoButton
import com.simats.duolingo.ui.theme.*
import kotlin.math.*

// ─── Home / Onboarding Screen ─────────────────────────────────────────────────
@Composable
fun HomeScreen(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit,
    onPickLanguage: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Header bar ─────────────────────────────────────────────────────
            HomeHeaderBar(
                onLanguagePicker = onPickLanguage,
                onLogin = onLogin,
            )

            // ── Scrollable body ────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(10.dp))
                AnimatedHomeMascot()
                Spacer(Modifier.height(28.dp))
                HeroTextSection()
                Spacer(Modifier.height(28.dp))
                ActionButtonsSection(onGetStarted = onGetStarted, onLogin = onLogin)
                Spacer(Modifier.height(16.dp))
                FeaturesBanner()
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ─── Header Bar ───────────────────────────────────────────────────────────────
@Composable
private fun HomeHeaderBar(
    onLanguagePicker: () -> Unit,
    onLogin: () -> Unit,
) {
    val selected = AppState.selectedLanguage

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DuolingoBlue)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Logo
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("🐦", fontSize = 18.sp)
            Text(
                text = "Code in Go",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
            )
        }

        Spacer(Modifier.weight(1f))

        // Language chip
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(DuolingoCardBg)
                .border(1.dp, DuolingoInputBorder, RoundedCornerShape(12.dp))
                .clickable(onClick = onLanguagePicker)
                .padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (selected != null) {
                Text(selected.flagEmoji, fontSize = 12.sp)
                Text(selected.nativeName.uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
            } else {
                Text("🌐", fontSize = 11.sp)
                Text("CHOOSE LANGUAGE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
            }
            Text("▾", color = DuolingoSubtext, fontSize = 9.sp)
        }

        Spacer(Modifier.width(8.dp))

        // Login button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable(onClick = onLogin)
                .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
            Text("LOGIN", color = DuolingoBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Animated Mascot ──────────────────────────────────────────────────────────
@Composable
private fun AnimatedHomeMascot() {
    var orbitAngle by remember { mutableFloatStateOf(0f) }
    val breathAnim = rememberInfiniteTransition(label = "breath")
    val breathY by breathAnim.animateFloat(
        initialValue = 0f, targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ), label = "breathY"
    )

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(30)
            orbitAngle = (orbitAngle + 1.5f) % 360f
        }
    }

    val badges = listOf("🔥", "📚", "💡", "🏆", "💎")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        // Back orbiting badges
        badges.forEachIndexed { i, badge ->
            val angle = Math.toRadians((orbitAngle + i * (360f / badges.size)).toDouble())
            val x = (cos(angle) * 115.0).toFloat()
            val y = (sin(angle) * 35.0 - 10.0).toFloat()
            val isBehind = sin(angle) < 0
            if (isBehind) {
                Text(
                    text = badge,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .offset(x.dp, y.dp)
                        .alpha((0.4f + ((sin(angle).toFloat() + 1f) * 0.4f)).coerceIn(0f, 1f))
                )
            }
        }

        // Mascot
        Text(
            text = "🦜",
            fontSize = 100.sp,
            modifier = Modifier.offset(y = breathY.dp)
        )

        // Front orbiting badges
        badges.forEachIndexed { i, badge ->
            val angle = Math.toRadians((orbitAngle + i * (360f / badges.size)).toDouble())
            val x = (cos(angle) * 115.0).toFloat()
            val y = (sin(angle) * 35.0 - 10.0).toFloat()
            val isBehind = sin(angle) < 0
            if (!isBehind) {
                Text(
                    text = badge,
                    fontSize = 24.sp,
                    modifier = Modifier.offset(x.dp, y.dp)
                )
            }
        }
    }
}

// ─── Hero Text Section ────────────────────────────────────────────────────────
@Composable
private fun HeroTextSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Text(
            text = "Code in Go, every day",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Text(
            text = "Learn programming with short, fun lessons. Practice your skills every day.",
            color = DuolingoSubtext,
            fontSize = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

// ─── CTA Buttons Section ──────────────────────────────────────────────────────
@Composable
private fun ActionButtonsSection(onGetStarted: () -> Unit, onLogin: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        DuolingoButton(
            text = "GET STARTED",
            backgroundColor = DuolingoGreen,
            shadowColor = DuolingoGreenDark,
            modifier = Modifier.fillMaxWidth(),
            onClick = onGetStarted,
        )
        DuolingoButton(
            text = "I ALREADY HAVE AN ACCOUNT",
            backgroundColor = DuolingoCardBg,
            shadowColor = DuolingoInputBorder,
            textColor = DuolingoBlue,
            modifier = Modifier.fillMaxWidth(),
            onClick = onLogin,
        )
    }
}

// ─── Features Banner ──────────────────────────────────────────────────────────
@Composable
private fun FeaturesBanner() {
    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Why learn with us?",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FeatureCard(
                icon = "🎮",
                iconColor = DuolingoGreen,
                title = "Effective & Fun",
                subtitle = "Bite-sized coding challenges keep you engaged.",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                icon = "✨",
                iconColor = DuolingoBlue,
                title = "Personalized",
                subtitle = "Adapts to your coding level and pace.",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FeatureCard(
    icon: String,
    iconColor: Color,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DuolingoCardBg)
            .border(1.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.15f))
                .padding(8.dp)
        ) {
            Text(icon, fontSize = 20.sp)
        }
        Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = DuolingoSubtext, fontSize = 12.sp)
    }
}
