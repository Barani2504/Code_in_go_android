package com.simats.duolingo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

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
            // Header bar
            HomeHeaderBar(
                onLanguagePicker = onPickLanguage,
                onLogin = onLogin,
            )

            // Scrollable body
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
            .statusBarsPadding()
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
                Text(selected.nativeName.uppercase(), color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.ExtraBold)
            } else {
                Text("🌐", fontSize = 11.sp)
                Text("CHOOSE LANGUAGE", color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.ExtraBold)
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

private data class HomeConfetti(
    val x: Float,
    val y: Float,
    val scale: Float,
    val opacity: Float,
    val size: Float,
    val symbol: String
)

// ─── Animated Mascot (Breathing + High-Five Tap + Orbiting Badges) ───────────
@Composable
private fun AnimatedHomeMascot() {
    var orbitAngle by remember { mutableFloatStateOf(0f) }
    var isHighFiveTapped by remember { mutableStateOf(false) }
    var confettiParticles by remember { mutableStateOf<List<HomeConfetti>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "breath")
    val breathProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathProgress"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(30)
            orbitAngle = (orbitAngle + 1.5f) % 360f
        }
    }

    val badges = listOf("🔥", "📚", "💡", "🏆", "💎")

    fun triggerHighFive() {
        if (isHighFiveTapped) return
        isHighFiveTapped = true

        val symbols = listOf("🎉", "🎊", "⚡", "🌟", "💫", "✨")
        val newParticles = mutableListOf<HomeConfetti>()
        for (i in 0 until 12) {
            val randomAngle = Random.nextDouble(0.0, 2.0 * Math.PI)
            val distance = Random.nextDouble(50.0, 130.0).toFloat()
            newParticles.add(
                HomeConfetti(
                    x = (cos(randomAngle) * distance).toFloat(),
                    y = (sin(randomAngle) * distance - 20).toFloat(),
                    scale = Random.nextDouble(0.8, 1.4).toFloat(),
                    opacity = 1f,
                    size = Random.nextDouble(18.0, 26.0).toFloat(),
                    symbol = symbols[i % symbols.size]
                )
            )
        }
        confettiParticles = newParticles

        coroutineScope.launch {
            delay(400)
            isHighFiveTapped = false
            confettiParticles = confettiParticles.map { it.copy(opacity = 0f, scale = 0.3f) }
            delay(400)
            confettiParticles = emptyList()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. Back Orbiting Badges (depth < 0)
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

        // 2. Duo Cutout Image Mascot with Breathing Animation & Tap High-Five
        val scaleX = if (isHighFiveTapped) 1.12f else (0.97f + breathProgress * 0.06f)
        val scaleY = if (isHighFiveTapped) 1.18f else (1.04f - breathProgress * 0.08f)
        val rotationDeg = if (isHighFiveTapped) -12f else (2f - breathProgress * 4f)
        val offsetY = if (isHighFiveTapped) -20f else (-6f + breathProgress * 10f)

        Box(
            modifier = Modifier
                .size(240.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { triggerHighFive() })
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.duo_backpack),
                contentDescription = "Code in Go Mascot",
                modifier = Modifier
                    .size(220.dp)
                    .graphicsLayer {
                        this.scaleX = scaleX
                        this.scaleY = scaleY
                        this.rotationZ = rotationDeg
                        this.translationY = offsetY
                    }
                    .shadow(16.dp, CircleShape, spotColor = Color(0xFFFF9600).copy(0.4f)),
                contentScale = ContentScale.Fit
            )
        }

        // 3. Front Orbiting Badges (depth >= 0)
        badges.forEachIndexed { i, badge ->
            val angle = Math.toRadians((orbitAngle + i * (360f / badges.size)).toDouble())
            val x = (cos(angle) * 115.0).toFloat()
            val y = (sin(angle) * 35.0 - 10.0).toFloat()
            val isBehind = sin(angle) < 0
            if (!isBehind) {
                Text(
                    text = badge,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .offset(x.dp, y.dp)
                        .scale((0.9f + (sin(angle).toFloat() + 1f) * 0.2f).coerceIn(0.8f, 1.3f))
                )
            }
        }

        // 4. Tap Confetti Particles
        confettiParticles.forEach { p ->
            Text(
                text = p.symbol,
                fontSize = p.size.sp,
                modifier = Modifier
                    .offset(p.x.dp, p.y.dp)
                    .scale(p.scale)
                    .alpha(p.opacity)
            )
        }
    }
}

// ─── Hero Text Section ────────────────────────────────────────────────────────
@Composable
private fun HeroTextSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Duolingo on the go",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "The free, fun, and effective way to learn a language!",
            color = DuolingoSubtext,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

// ─── Action Buttons Section ───────────────────────────────────────────────────
@Composable
private fun ActionButtonsSection(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        DuolingoButton(
            text = "GET STARTED",
            backgroundColor = DuolingoGreen,
            shadowColor = DuolingoGreenDark,
            modifier = Modifier.fillMaxWidth(),
            onClick = onGetStarted
        )

        DuolingoButton(
            text = "I ALREADY HAVE AN ACCOUNT",
            backgroundColor = DuolingoCardBg,
            shadowColor = DuolingoInputBorder,
            textColor = DuolingoBlue,
            modifier = Modifier.fillMaxWidth(),
            onClick = onLogin
        )
    }
}

// ─── Features / Highlights Banner ─────────────────────────────────────────────
@Composable
private fun FeaturesBanner() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Why learn with Duolingo?",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FeatureCard(
                icon = "🎮",
                color = DuolingoGreen,
                title = "Effective & Fun",
                subtitle = "Gamified lessons keep you motivated.",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                icon = "✨",
                color = DuolingoBlue,
                title = "Personalized",
                subtitle = "AI tailored to your learning pace.",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FeatureCard(
    icon: String,
    color: Color,
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
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 20.sp)
        }
        Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = DuolingoSubtext, fontSize = 12.sp, lineHeight = 16.sp)
    }
}
