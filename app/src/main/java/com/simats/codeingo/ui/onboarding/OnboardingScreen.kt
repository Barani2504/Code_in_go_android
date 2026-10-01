package com.simats.codeingo.ui.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.Language
import com.simats.codeingo.ui.components.DownloadBadgeView
import com.simats.codeingo.ui.components.DuolingoButton
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoGreenDark
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLanguageSheet by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(Language.defaultLanguages.first()) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Infinite breathing animation for hero mascot
    val infiniteTransition = rememberInfiniteTransition(label = "HeroHover")
    val mascotOffsetY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MascotY"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Blue Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DuolingoBlue)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🦅",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Code in Go",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                // Site Language Picker Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { showLanguageSheet = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = selectedLanguage.flagEmoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = selectedLanguage.nativeName.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Login Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { onLoginClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LOG IN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = DuolingoBlue
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Floating Hero Mascot
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(190.dp)
                        .offset(y = mascotOffsetY.dp)
                ) {
                    // Soft glow background aura
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .clip(CircleShape)
                            .background(DuolingoGreen.copy(alpha = 0.15f))
                    )
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.simats.codeingo.R.drawable.phoenix),
                        contentDescription = "Codeingo Phoenix Mascot",
                        modifier = Modifier.size(150.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Hero Headline
                Text(
                    text = "The free, fun, and effective way to learn data structures!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Master arrays, trees, graphs, and dynamic programming with 18-stage Phoenix evolution!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = SubtextGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Primary Action: GET STARTED
                DuolingoButton(
                    text = "GET STARTED",
                    faceColor = DuolingoGreen,
                    shadowColor = DuolingoGreenDark,
                    onClick = onGetStarted,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Secondary Action: I ALREADY HAVE AN ACCOUNT
                DuolingoButton(
                    text = "I ALREADY HAVE AN ACCOUNT",
                    faceColor = CardBackground,
                    shadowColor = Color(0xFF141F28),
                    textColor = DuolingoBlue,
                    borderStroke = 2.dp to InputBorder,
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Features Highlight Cards
                FeatureHighlightsSection()

                Spacer(modifier = Modifier.height(28.dp))

                // App Store Badges
                DownloadBadgeView(
                    onSelectPlatform = { /* Store link handler */ },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Language Picker Bottom Sheet
        if (showLanguageSheet) {
            ModalBottomSheet(
                onDismissRequest = { showLanguageSheet = false },
                sheetState = sheetState,
                containerColor = DarkBackground
            ) {
                LanguagePickerSheet(
                    selectedLanguageCode = selectedLanguage.code,
                    onLanguageSelected = { selected ->
                        selectedLanguage = selected
                    },
                    onDismiss = { showLanguageSheet = false }
                )
            }
        }
    }
}

@Composable
private fun FeatureHighlightsSection() {
    val features = listOf(
        Triple("⚡", "Bite-Sized DSA Lessons", "Learn complex algorithms in 5-minute interactive gamified steps"),
        Triple("🔥", "18-Stage Phoenix Evolution", "Hatch and evolve your companion as you defeat unit bosses"),
        Triple("🔬", "Interactive Visualizers", "Step through heaps, trees, and linked lists with live memory models")
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        features.forEach { (emoji, title, desc) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.dp, InputBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = emoji, fontSize = 32.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = desc,
                        fontSize = 12.sp,
                        color = SubtextGray,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
