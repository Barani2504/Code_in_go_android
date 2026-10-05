package com.simats.codeingo.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.R
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonStyle
import com.simats.codeingo.ui.components.GoogleLogoView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import kotlin.math.cos
import kotlin.math.sin

/**
 * ProfileCreationScreen faithfully synchronized with iOS ProfileCreationFlowView.swift.
 * 4-Step Questionnaire & Celebration Flow:
 * 1. Age Selection with speech bubble & quick-select pills
 * 2. Coding Goal with 4 rich cards & Google quick option
 * 3. Profile Credentials (name, email, password)
 * 4. Flame Ignition Celebration with rotating aura, ember badge, and profile summary card
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCreationScreen(
    onProfileCreated: (name: String, email: String) -> Unit,
    onLoginClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val localizationManager = LocalizationManager.instance
    val emotionManager = PhoenixEmotionManager.instance
    val currentEmotion by emotionManager.currentEmotion.collectAsState()

    var currentStep by remember { mutableIntStateOf(1) } // 1: Age, 2: Goal, 3: Credentials, 4: Celebration
    var ageText by remember { mutableStateOf("") }
    var selectedGoalId by remember { mutableStateOf("habit") }
    var nameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var isAgeFocused by remember { mutableStateOf(false) }
    var isNameFocused by remember { mutableStateOf(false) }
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    var showGoogleAuthSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val isCredentialsValid = emailText.trim().isNotEmpty() && passwordText.length >= 6
    val displayName = nameText.trim().ifEmpty {
        emailText.substringBefore("@").ifEmpty { "Vishal Rao" }.replaceFirstChar { it.uppercase() }
    }
    val generatedHandle = displayName.replace(" ", "").lowercase() + "3454"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0914))
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Bar (Hidden during celebration)
            if (currentStep < 4) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable {
                                if (currentStep > 1) currentStep-- else onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (currentStep == 1) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "CREATE ACCOUNT",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "SIGN IN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AmberGold.copy(alpha = 0.12f))
                            .border(1.2.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .clickable { onLoginClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // 3-Segment Step Progress Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (step in 1..3) {
                        val isFilled = step <= currentStep
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (isFilled) {
                                        Brush.horizontalGradient(listOf(AmberGold, Color(0xFFFF8C1A)))
                                    } else {
                                        SolidColor(Color.White.copy(alpha = 0.12f))
                                    }
                                )
                        )
                    }
                }
            }

            // Scrollable Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                when (currentStep) {
                    1 -> AgeStepView(
                        ageText = ageText,
                        onAgeChange = { ageText = it },
                        isAgeFocused = isAgeFocused,
                        onFocusChange = { isAgeFocused = it },
                        emotionEmoji = currentEmotion.emoji
                    )
                    2 -> GoalStepView(
                        selectedGoalId = selectedGoalId,
                        onSelectGoal = { selectedGoalId = it },
                        onGoogleSignUp = { showGoogleAuthSheet = true }
                    )
                    3 -> CredentialsStepView(
                        nameText = nameText,
                        onNameChange = { nameText = it },
                        emailText = emailText,
                        onEmailChange = { emailText = it },
                        passwordText = passwordText,
                        onPasswordChange = { passwordText = it },
                        isPasswordVisible = isPasswordVisible,
                        onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                        isNameFocused = isNameFocused,
                        onNameFocus = { isNameFocused = it },
                        isEmailFocused = isEmailFocused,
                        onEmailFocus = { isEmailFocused = it },
                        isPasswordFocused = isPasswordFocused,
                        onPasswordFocus = { isPasswordFocused = it }
                    )
                    4 -> CelebrationStepView(
                        displayName = displayName,
                        handle = generatedHandle,
                        emotionEmoji = currentEmotion.emoji
                    )
                }
            }

            // Docked Bottom Action Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0E17).copy(alpha = 0.95f))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (currentStep) {
                    1 -> {
                        Duolingo3DButton(
                            title = "NEXT",
                            style = if (ageText.isNotEmpty()) Duolingo3DButtonStyle.Amber else Duolingo3DButtonStyle.Disabled,
                            isEnabled = ageText.isNotEmpty(),
                            onClick = { currentStep = 2 },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    2 -> {
                        Duolingo3DButton(
                            title = "NEXT",
                            style = Duolingo3DButtonStyle.Amber,
                            onClick = { currentStep = 3 },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    3 -> {
                        Duolingo3DButton(
                            title = "CREATE ACCOUNT",
                            style = if (isCredentialsValid) Duolingo3DButtonStyle.Amber else Duolingo3DButtonStyle.Disabled,
                            isEnabled = isCredentialsValid,
                            onClick = { currentStep = 4 },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "By creating an account, you agree to our Terms of Service & Privacy Policy.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.40f),
                            textAlign = TextAlign.Center
                        )
                    }
                    4 -> {
                        Duolingo3DButton(
                            title = "CONTINUE",
                            style = Duolingo3DButtonStyle.Amber,
                            onClick = {
                                localizationManager.updateUserProfile(displayName, generatedHandle)
                                localizationManager.setLoggedIn(true)
                                onProfileCreated(displayName, emailText.ifEmpty { "user@codeingo.dev" })
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Google Auth Sheet
        if (showGoogleAuthSheet) {
            ModalBottomSheet(
                onDismissRequest = { showGoogleAuthSheet = false },
                sheetState = bottomSheetState,
                containerColor = Color(0xFF0F0E17)
            ) {
                GoogleAuthSheet(
                    onSelectAccount = { accName, accEmail ->
                        showGoogleAuthSheet = false
                        nameText = accName
                        emailText = accEmail
                        passwordText = "GoogleAuth2026!"
                        currentStep = 4
                    },
                    onDismiss = { showGoogleAuthSheet = false }
                )
            }
        }
    }
}

// MARK: - Step 1: Age Step View
@Composable
private fun AgeStepView(
    ageText: String,
    onAgeChange: (String) -> Unit,
    isAgeFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    emotionEmoji: String
) {
    val motivationalText = when (val age = ageText.toIntOrNull() ?: 0) {
        0 -> "Whether 16 or 60, anyone can ignite their inner coding Phoenix!"
        in 1..19 -> "Starting young! Master Data Structures early to become a legend."
        in 20..29 -> "The prime coding years! Let's conquer FAANG interviews together."
        else -> "A seasoned mind! Deep analytical thinking makes the best engineers."
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Phoenix Guide Speech Bubble
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(54.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(AmberGold.copy(alpha = 0.20f))
                        .blur(6.dp)
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF141F36))
                        .border(1.5.dp, AmberGold.copy(alpha = 0.40f), CircleShape)
                ) {
                    Text(text = emotionEmoji, fontSize = 22.sp)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF141F36).copy(alpha = 0.90f))
                    .border(1.dp, AmberGold.copy(alpha = 0.30f), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Phoenix Guide",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = motivationalText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "How old are you?",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Providing your age ensures you get the right learning experience.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.60f),
                textAlign = TextAlign.Center
            )
        }

        // Age Input Field
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "YOUR AGE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold.copy(alpha = 0.90f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF141F36).copy(alpha = 0.85f))
                    .border(
                        if (isAgeFocused) 2.dp else 1.2.dp,
                        if (isAgeFocused) AmberGold else if (ageText.isNotEmpty()) AmberGold.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.14f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = null,
                    tint = AmberGold.copy(alpha = 0.80f),
                    modifier = Modifier.size(18.dp)
                )

                BasicTextField(
                    value = ageText,
                    onValueChange = { onAgeChange(it.filter { ch -> ch.isDigit() }) },
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { onFocusChange(it.isFocused) },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    cursorBrush = SolidColor(AmberGold),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    decorationBox = { innerTextField ->
                        if (ageText.isEmpty()) {
                            Text(
                                text = "e.g. 21",
                                color = Color.White.copy(alpha = 0.35f),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        innerTextField()
                    }
                )

                if (ageText.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color.White.copy(alpha = 0.40f),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onAgeChange("") }
                    )
                }
            }
        }

        // Quick Select Pills
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "QUICK SELECT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.45f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("18", "21", "25", "28", "32").forEach { preset ->
                    val isSelected = ageText == preset
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) AmberGold.copy(alpha = 0.25f) else Color(0xFF141F36).copy(alpha = 0.70f))
                            .border(1.dp, if (isSelected) AmberGold else Color.White.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                            .clickable { onAgeChange(preset) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (preset == "32") "32+" else preset,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.70f)
                        )
                    }
                }
            }
        }
    }
}

// MARK: - Step 2: Goal Step View
private data class CodingGoal(
    val id: String,
    val icon: String,
    val title: String,
    val desc: String,
    val tag: String
)

@Composable
private fun GoalStepView(
    selectedGoalId: String,
    onSelectGoal: (String) -> Unit,
    onGoogleSignUp: () -> Unit
) {
    val goals = listOf(
        CodingGoal("dsa", "⚡", "Master Data Structures & Algorithms", "Arrays, Trees, Graphs, Dynamic Programming & recursion", "POPULAR"),
        CodingGoal("interview", "🎯", "Ace Tech & FAANG Interviews", "Top 150 LeetCode patterns & system design fundamentals", "RECOMMENDED"),
        CodingGoal("languages", "🚀", "Learn Go & Swift from Scratch", "Modern syntax, memory management & concurrent programming", "ESSENTIAL"),
        CodingGoal("habit", "🔥", "Build a 15-Minute Daily Habit", "Keep your Phoenix flame alive with bite-sized daily challenges", "DAILY")
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "What is your main coding goal?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Choose your focus path to customize your learning tree:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )
        }

        // 4 Selectable Goal Cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            goals.forEach { goal ->
                val isSelected = selectedGoalId == goal.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF141F36).copy(alpha = if (isSelected) 0.95f else 0.75f))
                        .border(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) AmberGold else Color.White.copy(alpha = 0.08f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectGoal(goal.id) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Icon Circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AmberGold.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.06f))
                    ) {
                        Text(text = goal.icon, fontSize = 24.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = goal.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = goal.tag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) AmberGold else Color.White.copy(alpha = 0.40f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) AmberGold.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.06f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = goal.desc,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.55f),
                            lineHeight = 15.sp
                        )
                    }

                    // Radio Checkmark Circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AmberGold else Color.Transparent)
                            .border(1.5.dp, if (isSelected) AmberGold else Color.White.copy(alpha = 0.25f), CircleShape)
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color(0xFF140F03),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Google Fast Signup Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.06f))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                .clickable { onGoogleSignUp() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GoogleLogoView(size = 18.dp)
                Text(
                    text = "Or sign up instantly with Google",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

// MARK: - Step 3: Credentials Step View
@Composable
private fun CredentialsStepView(
    nameText: String,
    onNameChange: (String) -> Unit,
    emailText: String,
    onEmailChange: (String) -> Unit,
    passwordText: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePassword: () -> Unit,
    isNameFocused: Boolean,
    onNameFocus: (Boolean) -> Unit,
    isEmailFocused: Boolean,
    onEmailFocus: (Boolean) -> Unit,
    isPasswordFocused: Boolean,
    onPasswordFocus: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Create Your Profile",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Choose your name and secure password to save your progress.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )
        }

        // Input 1: Full Name (Optional)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "FULL NAME (OPTIONAL)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold.copy(alpha = 0.90f),
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF141F36).copy(alpha = 0.85f))
                    .border(
                        if (isNameFocused) 2.dp else 1.2.dp,
                        if (isNameFocused) AmberGold else if (nameText.isNotEmpty()) AmberGold.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = AmberGold.copy(alpha = 0.80f),
                    modifier = Modifier.size(18.dp)
                )

                BasicTextField(
                    value = nameText,
                    onValueChange = onNameChange,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { onNameFocus(it.isFocused) },
                    textStyle = TextStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    cursorBrush = SolidColor(AmberGold),
                    decorationBox = { inner ->
                        if (nameText.isEmpty()) {
                            Text(text = "e.g. Vishal Rao", color = Color.White.copy(alpha = 0.35f), fontSize = 15.sp)
                        }
                        inner()
                    }
                )

                if (nameText.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onNameChange("") }
                    )
                }
            }
        }

        // Input 2: Email Address
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "EMAIL ADDRESS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold.copy(alpha = 0.90f),
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF141F36).copy(alpha = 0.85f))
                    .border(
                        if (isEmailFocused) 2.dp else 1.2.dp,
                        if (isEmailFocused) AmberGold else if (emailText.isNotEmpty()) AmberGold.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = AmberGold.copy(alpha = 0.80f),
                    modifier = Modifier.size(18.dp)
                )

                BasicTextField(
                    value = emailText,
                    onValueChange = onEmailChange,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { onEmailFocus(it.isFocused) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    textStyle = TextStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    cursorBrush = SolidColor(AmberGold),
                    decorationBox = { inner ->
                        if (emailText.isEmpty()) {
                            Text(text = "name@example.com", color = Color.White.copy(alpha = 0.35f), fontSize = 15.sp)
                        }
                        inner()
                    }
                )

                if (emailText.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onEmailChange("") }
                    )
                }
            }
        }

        // Input 3: Password
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "PASSWORD",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold.copy(alpha = 0.90f),
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF141F36).copy(alpha = 0.85f))
                    .border(
                        if (isPasswordFocused) 2.dp else 1.2.dp,
                        if (isPasswordFocused) AmberGold else if (passwordText.isNotEmpty()) AmberGold.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = AmberGold.copy(alpha = 0.80f),
                    modifier = Modifier.size(18.dp)
                )

                BasicTextField(
                    value = passwordText,
                    onValueChange = onPasswordChange,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { onPasswordFocus(it.isFocused) },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    textStyle = TextStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    cursorBrush = SolidColor(AmberGold),
                    decorationBox = { inner ->
                        if (passwordText.isEmpty()) {
                            Text(text = "At least 6 characters", color = Color.White.copy(alpha = 0.35f), fontSize = 15.sp)
                        }
                        inner()
                    }
                )

                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle password",
                    tint = AmberGold.copy(alpha = 0.80f),
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onTogglePassword() }
                )
            }
        }
    }
}

// MARK: - Step 4: Celebration Animation Screen (Flame Ignition!)
@Composable
private fun CelebrationStepView(
    displayName: String,
    handle: String,
    emotionEmoji: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Celebration")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Rotating Fire Rings & Mascot
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(180.dp)
        ) {
            // Pulsing Aura
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .scale(auraScale)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                AmberGold.copy(alpha = 0.45f),
                                Color(0xFFFF4D0D).copy(alpha = 0.20f),
                                Color.Transparent
                            )
                        )
                    )
                    .blur(14.dp)
            )

            // Dashed rotating ring
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .rotate(rotationAngle)
                    .border(2.dp, AmberGold, CircleShape)
            )

            // Central mascot avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF45190A), Color(0xFF14122E))
                        )
                    )
                    .border(2.dp, AmberGold, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.phoenix),
                    contentDescription = "Phoenix",
                    modifier = Modifier.size(80.dp)
                )
            }

            // Sparkles around
            for (i in 0 until 6) {
                val rad = Math.toRadians((i * 60).toDouble())
                val x = (cos(rad) * 75).toFloat()
                val y = (sin(rad) * 75).toFloat()
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AmberGold,
                    modifier = Modifier
                        .offset(x = x.dp, y = y.dp)
                        .size(16.dp)
                )
            }
        }

        // Ember Flame Badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(AmberGold.copy(alpha = 0.14f))
                .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = AmberGold,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "EMBER LEVEL 1 • FLAME IGNITED",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold
            )
        }

        // Headlines
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Account Successfully Created! 🎉",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Welcome to Code in Go, $displayName! Your Phoenix is ready to conquer Data Structures.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.70f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        // Profile Summary Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF141F36).copy(alpha = 0.90f))
                .border(1.5.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF19B259).copy(alpha = 0.20f))
            ) {
                Text(
                    text = displayName.firstOrNull()?.uppercase() ?: "P",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "@$handle",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "1-Day Streak",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "🔥 Daily Fire",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9500)
                )
            }
        }
    }
}
