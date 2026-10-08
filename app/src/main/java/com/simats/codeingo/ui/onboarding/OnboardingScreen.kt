package com.simats.codeingo.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.R
import com.simats.codeingo.data.model.Language
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonStyle
import com.simats.codeingo.ui.phoenix.AnimatedGIFView
import com.simats.codeingo.ui.components.HeaderView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AmberGoldDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

/**
 * OnboardingScreen faithfully synchronized with iOS ContentView.swift (commit 2b2c2ab).
 * Features:
 * - PhoenixAtmosphericBackgroundView deep canvas
 * - HeaderView with dynamic mascot & language picker pill
 * - 4-Slide Duolingo-style Swipeable Carousel with AnimatedHomeMascotView
 * - Liquid glass pill indicators
 * - Pinned Duolingo3DButtons ("GET STARTED" -> BeginnerOnboardingFlowView, "I ALREADY HAVE AN ACCOUNT" -> LoginScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val localizationManager = LocalizationManager.instance
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showBeginnerOnboarding by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E17))
    ) {
        // Deep obsidian background with drifting embers & nebulae
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Navigation Bar
            HeaderView(
                onOpenLanguagePicker = { showLanguageSheet = true },
                onOpenLogin = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0D1C).copy(alpha = 0.95f))
            )

            // Main Content: Swipeable Hero & Features Carousel (Duolingo-style)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> HeroPageSlide()
                        1 -> FeatureSlide(
                            icon = Icons.Default.SportsEsports,
                            badgeText = "DSA ADVENTURE",
                            badgeColor = AmberGold,
                            title = "Gamified Roadmaps",
                            subtitle = "Level up step-by-step through interactive 3D worlds from Arrays to Dynamic Programming."
                        )
                        2 -> FeatureSlide(
                            icon = Icons.Default.AutoAwesome,
                            badgeText = "LIVE ALGORITHMS",
                            badgeColor = Color(0xFFFF731A),
                            title = "Interactive Visualizer",
                            subtitle = "Watch algorithms execute in real time with step-through pointer tracking and visual memory registers."
                        )
                        3 -> FeatureSlide(
                            icon = Icons.Default.EmojiEvents,
                            badgeText = "LEADERBOARDS & STREAKS",
                            badgeColor = Color(0xFFFFD700),
                            title = "Climb the Leagues",
                            subtitle = "Compete with coders worldwide, protect your daily streak, and unlock legendary Phoenix ranks."
                        )
                    }
                }
            }

            // Liquid Glass Paging Indicator Dots
            CarouselPageIndicator(
                currentPage = pagerState.currentPage,
                pageCount = 4,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 10.dp)
            )

            // Pinned Primary & Secondary Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // GET STARTED — Phoenix Primary 3D Button (Amber Gold)
                Duolingo3DButton(
                    title = localizationManager.string("get_started_caps"),
                    style = Duolingo3DButtonStyle.Amber,
                    onClick = {
                        showBeginnerOnboarding = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // I ALREADY HAVE AN ACCOUNT — Duolingo 3D Button (White / Liquid Glass)
                Duolingo3DButton(
                    title = localizationManager.string("already_have_account_caps"),
                    style = Duolingo3DButtonStyle.White,
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Language Picker Bottom Sheet
        if (showLanguageSheet) {
            ModalBottomSheet(
                onDismissRequest = { showLanguageSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF0F0E17)
            ) {
                val currentLangCode by localizationManager.selectedLanguageCode.collectAsState()
                LanguagePickerSheet(
                    selectedLanguageCode = currentLangCode,
                    onLanguageSelected = { selected: Language ->
                        localizationManager.selectLanguage(selected.code)
                        showLanguageSheet = false
                        coroutineScope.launch {
                            delay(250)
                            showBeginnerOnboarding = true
                        }
                    },
                    onDismiss = { showLanguageSheet = false }
                )
            }
        }

        // Fullscreen Beginner Onboarding Flow View
        AnimatedVisibility(
            visible = showBeginnerOnboarding,
            enter = fadeIn(tween(350)),
            exit = fadeOut(tween(300))
        ) {
            BeginnerOnboardingFlowView(
                onCompleteOnboarding = {
                    showBeginnerOnboarding = false
                    onGetStarted()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// MARK: - Slide 0: Main Hero Slide
@Composable
private fun HeroPageSlide() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Hero Mascot with Orbiting Badges & Squish Breathing
        AnimatedHomeMascotView(modifier = Modifier.size(220.dp))

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Rise with Code in Go",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = LocalDynamicThemeColors.current.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "The fiery, fun, and gamified way to master Data Structures & Algorithms!",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.70f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

// MARK: - Feature Slides (Gamified Roadmaps, Visualizer, Leagues)
@Composable
private fun FeatureSlide(
    icon: ImageVector,
    badgeText: String,
    badgeColor: Color,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illuminated Glass Feature Emblem
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(140.dp)
        ) {
            // Ambient Aura Blur
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(badgeColor.copy(alpha = 0.22f))
                    .blur(20.dp)
            )

            // Ultra Thin Glass Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(
                        2.dp,
                        Brush.linearGradient(
                            listOf(LocalDynamicThemeColors.current.textSecondary, badgeColor.copy(alpha = 0.5f))
                        ),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = badgeColor,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Badge Tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(badgeColor.copy(alpha = 0.18f))
                .border(1.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text = badgeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = badgeColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = LocalDynamicThemeColors.current.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.72f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

// MARK: - Liquid Glass Paging Indicator
@Composable
private fun CarouselPageIndicator(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pageCount) {
            val isSelected = i == currentPage
            val width = if (isSelected) 22.dp else 7.dp
            val color = if (isSelected) AmberGold else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.25f)

            Box(
                modifier = Modifier
                    .height(7.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

// MARK: - AnimatedHomeMascotView (Breathing Squish/Stretch + Orbiting Badges + Tap Confetti)
private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
    val alpha: Float
)

@Composable
fun AnimatedHomeMascotView(
    modifier: Modifier = Modifier
) {
    val badges = listOf("⚡", "📚", "🌲", "🏆", "💎")
    var orbitAngle by remember { mutableFloatStateOf(0f) }
    var isHighFiveTapped by remember { mutableStateOf(false) }
    val confettiParticles = remember { mutableStateListOf<ConfettiParticle>() }

    // Breathing loop
    val infiniteTransition = rememberInfiniteTransition(label = "HomeMascotBreathing")
    val breathingScaleX by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleX"
    )
    val breathingScaleY by infiniteTransition.animateFloat(
        initialValue = 1.04f,
        targetValue = 0.96f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleY"
    )

    // Orbit continuous angle updater
    LaunchedEffect(Unit) {
        while (isActive) {
            orbitAngle = (orbitAngle + 1.2f) % 360f
            delay(30)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            // Trigger high five burst
            isHighFiveTapped = true
            confettiParticles.clear()
            for (i in 0 until 14) {
                val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                val dist = Random.nextFloat() * 50f + 30f
                confettiParticles.add(
                    ConfettiParticle(
                        x = cos(angle) * dist,
                        y = sin(angle) * dist - 20f,
                        size = Random.nextFloat() * 4f + 3f,
                        color = listOf(AmberGold, Color(0xFFFF9500), Color(0xFFFFCC00), Color.White).random(),
                        alpha = 1.0f
                    )
                )
            }
        }
    ) {
        // Reset high five tap after 400ms
        LaunchedEffect(isHighFiveTapped) {
            if (isHighFiveTapped) {
                delay(400)
                isHighFiveTapped = false
                delay(400)
                confettiParticles.clear()
            }
        }

        // 1. Orbiting Badges Behind Mascot (when sin < 0)
        badges.forEachIndexed { index, badge ->
            val angle = orbitAngle + (index * (360f / badges.size))
            val radians = Math.toRadians(angle.toDouble())
            val x = (cos(radians) * 105.0).toFloat()
            val y = (sin(radians) * 30.0 - 8.0).toFloat()
            val isBehind = sin(radians) < 0

            if (isBehind) {
                Text(
                    text = badge,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .offset(x = x.dp, y = y.dp)
                        .scale(0.75f + (sin(radians).toFloat() + 1f) * 0.25f)
                )
            }
        }

        // 2. Central Phoenix Mascot
        val finalScaleX = if (isHighFiveTapped) 1.12f else breathingScaleX
        val finalScaleY = if (isHighFiveTapped) 1.18f else breathingScaleY
        val rotationDeg = if (isHighFiveTapped) -8f else 0f
        val offsetY = if (isHighFiveTapped) (-12).dp else 0.dp

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(190.dp)
                .offset(y = offsetY)
                .scale(scaleX = finalScaleX, scaleY = finalScaleY)
                .rotate(rotationDeg)
        ) {
            // Ambient Aura Glow
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF9500).copy(alpha = 0.25f))
                    .blur(22.dp)
            )

            AnimatedGIFView(
                resourceName = "phoenix_flying",
                size = 175.dp
            )
        }

        // 3. Orbiting Badges In Front of Mascot (when sin >= 0)
        badges.forEachIndexed { index, badge ->
            val angle = orbitAngle + (index * (360f / badges.size))
            val radians = Math.toRadians(angle.toDouble())
            val x = (cos(radians) * 105.0).toFloat()
            val y = (sin(radians) * 30.0 - 8.0).toFloat()
            val isBehind = sin(radians) < 0

            if (!isBehind) {
                Text(
                    text = badge,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .offset(x = x.dp, y = y.dp)
                        .scale(0.75f + (sin(radians).toFloat() + 1f) * 0.25f)
                )
            }
        }

        // 4. Confetti Particles Burst
        confettiParticles.forEach { particle ->
            Box(
                modifier = Modifier
                    .offset(x = particle.x.dp, y = particle.y.dp)
                    .size(particle.size.dp)
                    .clip(CircleShape)
                    .background(particle.color.copy(alpha = particle.alpha))
            )
        }
    }
}
