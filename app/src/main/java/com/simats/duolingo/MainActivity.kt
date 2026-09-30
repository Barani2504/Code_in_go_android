package com.simats.duolingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.simats.duolingo.ui.screens.*
import com.simats.duolingo.ui.theme.DuolingoDarkBg
import com.simats.duolingo.ui.theme.DuolingoTheme

// ─── App stages (mirrors iOS ContentView OnboardingStage) ─────────────────────
private enum class AppStage {
    SPLASH,      // Animated splash
    HOME,        // Onboarding screen (before language selection)
    DASHBOARD,   // Main learning dashboard
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle  = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
        )
        setContent {
            DuolingoTheme {
                CodeInGoApp()
            }
        }
    }
}

@Composable
private fun CodeInGoApp() {
    var stage by remember { mutableStateOf(AppStage.SPLASH) }
    var showLoginSheet    by remember { mutableStateOf(false) }
    var showLangSheet     by remember { mutableStateOf(false) }
    var showLoadingScreen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
    ) {
        // ── Stage routing ──────────────────────────────────────────────────────
        AnimatedContent(
            targetState = stage,
            transitionSpec = {
                fadeIn(tween(400)) togetherWith fadeOut(tween(400))
            },
            label = "stageTransition"
        ) { current ->
            when (current) {
                AppStage.SPLASH -> SplashScreen(
                    onFinished = { stage = AppStage.HOME }
                )

                AppStage.HOME -> HomeScreen(
                    onGetStarted = {
                        if (com.simats.duolingo.data.AppState.selectedLanguage == null) {
                            showLangSheet = true
                        } else {
                            showLoadingScreen = true
                        }
                    },
                    onLogin = { showLoginSheet = true },
                    onPickLanguage = { showLangSheet = true },
                )

                AppStage.DASHBOARD -> DashboardScreen()
            }
        }

        // ── Modal overlays ─────────────────────────────────────────────────────
        if (showLoginSheet) {
            AnimatedVisibility(
                visible = showLoginSheet,
                enter = slideInVertically(initialOffsetY = { it }),
                exit  = slideOutVertically(targetOffsetY = { it }),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LoginSheet(onDismiss = { showLoginSheet = false })
                }
            }
        }

        if (showLangSheet) {
            AnimatedVisibility(
                visible = showLangSheet,
                enter = slideInVertically(initialOffsetY = { it }),
                exit  = slideOutVertically(targetOffsetY = { it }),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(0.6f))
                        .clickable(onClick = { showLangSheet = false }),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    LanguagePickerSheet(
                        onDismiss = { showLangSheet = false },
                        onLanguageSelected = {
                            showLangSheet = false
                            showLoadingScreen = true
                        },
                    )
                }
            }
        }

        if (showLoadingScreen) {
            AnimatedVisibility(
                visible = showLoadingScreen,
                enter = fadeIn(tween(300)),
                exit  = fadeOut(tween(300)),
            ) {
                LoadingScreen(
                    onFinished = {
                        showLoadingScreen = false
                        stage = AppStage.DASHBOARD
                    }
                )
            }
        }
    }
}