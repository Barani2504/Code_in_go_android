# Navigation, Entry, Splash, Onboarding & Auth

> **Files in this bundle:** 14

## File Index

1. `app/src/main/java/com/simats/codeingo/MainActivity.kt` (67 lines)
2. `app/src/main/java/com/simats/codeingo/navigation/AppNavHost.kt` (375 lines)
3. `app/src/main/java/com/simats/codeingo/navigation/BottomNavBar.kt` (351 lines)
4. `app/src/main/java/com/simats/codeingo/navigation/Routes.kt` (24 lines)
5. `app/src/main/java/com/simats/codeingo/ui/auth/AppleAuthSheet.kt` (336 lines)
6. `app/src/main/java/com/simats/codeingo/ui/auth/ForgotPasswordSheet.kt` (475 lines)
7. `app/src/main/java/com/simats/codeingo/ui/auth/GoogleAuthSheet.kt` (333 lines)
8. `app/src/main/java/com/simats/codeingo/ui/auth/LoginScreen.kt` (1188 lines)
9. `app/src/main/java/com/simats/codeingo/ui/auth/ProfileCreationScreen.kt` (1247 lines)
10. `app/src/main/java/com/simats/codeingo/ui/onboarding/BeginnerOnboardingFlowView.kt` (845 lines)
11. `app/src/main/java/com/simats/codeingo/ui/onboarding/CreativeTemperatureGaugeView.kt` (437 lines)
12. `app/src/main/java/com/simats/codeingo/ui/onboarding/LanguagePickerSheet.kt` (173 lines)
13. `app/src/main/java/com/simats/codeingo/ui/onboarding/OnboardingScreen.kt` (583 lines)
14. `app/src/main/java/com/simats/codeingo/ui/splash/SplashScreen.kt` (390 lines)

---

## 1. `app/src/main/java/com/simats/codeingo/MainActivity.kt`

```kotlin

package com.simats.codeingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.simats.codeingo.navigation.AppNavHost
import com.simats.codeingo.ui.theme.CodeingoTheme
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.ProvideScreenMetrics

import android.view.View
import android.view.ViewTreeObserver
import com.simats.codeingo.domain.GameManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep splash screen up until the snapshot is ready (with a 1500ms safety timeout)
        val content: View = findViewById(android.R.id.content)
        val startTime = System.currentTimeMillis()
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    val isReady = GameManager.instance.isInitialized.value
                    val isTimedOut = (System.currentTimeMillis() - startTime) > 1500L
                    return if (isReady || isTimedOut) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )

        com.simats.codeingo.ui.theme.FeedbackManager.init(this)
        com.simats.codeingo.ui.motion.SoundEffectsManager.getInstance(this)

        setContent {
            CodeingoTheme {
                ProvideScreenMetrics {
                    val colors = com.simats.codeingo.ui.theme.LocalDynamicThemeColors.current
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = colors.background
                    ) {
                        AppNavHost()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        com.simats.codeingo.data.network.worker.ProgressSyncWorker.enqueue(this)
    }
}
```

## 2. `app/src/main/java/com/simats/codeingo/navigation/AppNavHost.kt`

```kotlin
package com.simats.codeingo.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.arenas.block_cipher_forge.BlockCipherForgeArenaScreen
import com.simats.codeingo.ui.arenas.cipher_sanctum.CipherSanctumArenaScreen
import com.simats.codeingo.ui.arenas.cyber_bastion.CyberBastionArenaScreen
import com.simats.codeingo.ui.arenas.integrity_foundry.IntegrityFoundryArenaScreen
import com.simats.codeingo.ui.arenas.key_exchange_citadel.KeyExchangeCitadelArenaScreen
import com.simats.codeingo.ui.arenas.routing.ArenaRoute
import com.simats.codeingo.ui.auth.LoginScreen
import com.simats.codeingo.ui.auth.ProfileCreationScreen
import com.simats.codeingo.ui.dashboard.MainDashboardScreen
import com.simats.codeingo.ui.exercise.AssessmentScreen
import com.simats.codeingo.ui.exercise.BossBattleScreen
import com.simats.codeingo.ui.onboarding.OnboardingScreen
import com.simats.codeingo.ui.phoenix.PhoenixEggHatch3DView
import com.simats.codeingo.ui.phoenix.PhoenixEvolutionCelebrationScreen
import com.simats.codeingo.ui.phoenix.PhoenixSanctuaryScreen
import com.simats.codeingo.ui.practice.PracticeHubScreen
import com.simats.codeingo.ui.settings.SettingsScreen
import com.simats.codeingo.ui.splash.SplashScreen
import com.simats.codeingo.ui.visualizer.VisualizerScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val showEvolutionModal by gameManager.showEvolutionModal.collectAsState()
    val evolutionFrom by gameManager.evolutionFromStage.collectAsState()
    val evolutionTo by gameManager.evolutionToStage.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = spring(
                        dampingRatio = 0.88f,
                        stiffness = 380f
                    )
                ) + fadeIn(animationSpec = tween(280))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = spring(
                        dampingRatio = 0.88f,
                        stiffness = 380f
                    ),
                    targetOffset = { fullWidth -> (fullWidth * 0.35f).toInt() }
                ) + fadeOut(animationSpec = tween(240))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = spring(
                        dampingRatio = 0.88f,
                        stiffness = 380f
                    ),
                    initialOffset = { fullWidth -> (fullWidth * 0.35f).toInt() }
                ) + fadeIn(animationSpec = tween(280))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = spring(
                        dampingRatio = 0.88f,
                        stiffness = 380f
                    )
                ) + fadeOut(animationSpec = tween(240))
            }
        ) {
            composable(
                route = Screen.Splash.route,
                exitTransition = {
                    fadeOut(animationSpec = tween(350))
                }
            ) {
                SplashScreen(
                    onFinishedSplash = {
                        val destination = runCatching {
                            if (com.simats.codeingo.domain.AuthService.instance.isAuthenticated.value) {
                                Screen.Dashboard.route
                            } else {
                                Screen.Onboarding.route
                            }
                        }.getOrDefault(Screen.Onboarding.route)
                        navController.navigate(destination) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onGetStarted = {
                        if (!com.simats.codeingo.domain.AuthService.instance.isAuthenticated.value) {
                            com.simats.codeingo.domain.AuthService.instance.signInAsGuest()
                        }
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { _, _ ->
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onDismiss = {
                        navController.popBackStack()
                    },
                    onCreateAccountClick = {
                        navController.navigate(Screen.ProfileCreation.route)
                    }
                )
            }

            composable(Screen.ProfileCreation.route) {
                ProfileCreationScreen(
                    onProfileCreated = { _, _ ->
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.ProfileCreation.route) { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navController.navigate(Screen.Login.route)
                    },
                    onDismiss = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                MainDashboardScreen(
                    onStartLesson = { unitId, levelNumber, totalLevelsInUnit, isBoss ->
                        navController.navigate(Screen.Assessment.createRoute(unitId, levelNumber, totalLevelsInUnit, isBoss))
                    },
                    onStartBoss = { bossId ->
                        navController.navigate(Screen.BossBattle.createRoute(bossId))
                    },
                    onOpenPhoenixSanctuary = {
                        navController.navigate(Screen.PhoenixSanctuary.route)
                    },
                    onOpenPracticeHub = {
                        navController.navigate(Screen.PracticeHub.route)
                    },
                    onOpenSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onLogout = {
                        com.simats.codeingo.domain.AuthService.shared.signOut()
                        com.simats.codeingo.domain.LocalizationManager.instance.resetProfileData()
                        com.simats.codeingo.domain.GameManager.instance.deleteAccountAndClearAllData()
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.Assessment.route,
                arguments = listOf(
                    navArgument("unitId") { type = NavType.IntType; defaultValue = 1 },
                    navArgument("levelNumber") { type = NavType.IntType; defaultValue = 1 },
                    navArgument("totalLevelsInUnit") { type = NavType.IntType; defaultValue = 6 },
                    navArgument("isBoss") { type = NavType.BoolType; defaultValue = false }
                )
            ) { backStackEntry ->
                val unitId = backStackEntry.arguments?.getInt("unitId") ?: 1
                val levelNumber = backStackEntry.arguments?.getInt("levelNumber") ?: 1
                val totalLevelsInUnit = backStackEntry.arguments?.getInt("totalLevelsInUnit") ?: 6
                val isBoss = backStackEntry.arguments?.getBoolean("isBoss") ?: false
                AssessmentScreen(
                    unitId = unitId,
                    levelNumber = levelNumber,
                    totalLevelsInUnit = totalLevelsInUnit,
                    isBoss = isBoss,
                    onComplete = { _ ->
                        navController.popBackStack()
                    },
                    onDismiss = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.BossBattle.route,
                arguments = listOf(navArgument("bossId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bossId = backStackEntry.arguments?.getString("bossId") ?: "boss_unit_01"
                BossBattleScreen(
                    bossId = bossId,
                    onVictory = {
                        navController.popBackStack()
                    },
                    onDismiss = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Visualizer.route) {
                var activeWorldArena by remember { mutableStateOf<String?>(null) }
                Box(modifier = Modifier.fillMaxSize()) {
                    VisualizerScreen(
                        onOpenArrayKingdom = { activeWorldArena = ArenaRoute.CIPHER_SANCTUM },
                        onOpenStackTower = { activeWorldArena = ArenaRoute.KEY_EXCHANGE_CITADEL },
                        onOpenQueueStation = { activeWorldArena = ArenaRoute.INTEGRITY_FOUNDRY },
                        onOpenLinkedListRoad = { activeWorldArena = ArenaRoute.BLOCK_CIPHER_FORGE },
                        onOpenBinaryTreeForest = { activeWorldArena = ArenaRoute.CYBER_BASTION },
                        modifier = Modifier.fillMaxSize()
                    )
                    ArenaOverlay(
                        activeWorldArena = activeWorldArena,
                        onDismiss = { activeWorldArena = null }
                    )
                }
            }

            composable(Screen.PracticeHub.route) {
                var activeWorldArena by remember { mutableStateOf<String?>(null) }
                Box(modifier = Modifier.fillMaxSize()) {
                    PracticeHubScreen(
                        onBack = { navController.popBackStack() },
                        onOpenArrayKingdom = { activeWorldArena = ArenaRoute.CIPHER_SANCTUM },
                        onOpenStackTower = { activeWorldArena = ArenaRoute.KEY_EXCHANGE_CITADEL },
                        onOpenQueueStation = { activeWorldArena = ArenaRoute.INTEGRITY_FOUNDRY },
                        onOpenLinkedListRoad = { activeWorldArena = ArenaRoute.BLOCK_CIPHER_FORGE },
                        onOpenBinaryTreeForest = { activeWorldArena = ArenaRoute.CYBER_BASTION },
                        modifier = Modifier.fillMaxSize()
                    )
                    ArenaOverlay(
                        activeWorldArena = activeWorldArena,
                        onDismiss = { activeWorldArena = null }
                    )
                }
            }

            composable(Screen.PhoenixSanctuary.route) {
                PhoenixSanctuaryScreen(
                    onDismiss = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.PhoenixEggHatch.route) {
                PhoenixEggHatch3DView(
                    onContinue = {
                        navController.popBackStack()
                    },
                    onFinish = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onDismiss = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        com.simats.codeingo.domain.AuthService.shared.signOut()
                        com.simats.codeingo.domain.LocalizationManager.instance.resetProfileData()
                        com.simats.codeingo.domain.GameManager.instance.deleteAccountAndClearAllData()
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onOpenAnimationLab = {
                        navController.navigate(Screen.AnimationLab.route)
                    },
                    onOpenMotionLab = {
                        navController.navigate(Screen.MotionLab.route)
                    }
                )
            }

            composable(Screen.AnimationLab.route) {
                com.simats.codeingo.ui.components.AnimationLabScreen(
                    onDismiss = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.MotionLab.route) {
                com.simats.codeingo.ui.debug.MotionLabScreen(
                    onDismiss = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Global Phoenix Evolution Celebration Overlay
        AnimatedVisibility(
            visible = showEvolutionModal,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            PhoenixEvolutionCelebrationScreen(
                fromStageId = evolutionFrom,
                toStageId = evolutionTo,
                onDismiss = {
                    gameManager.dismissEvolutionModal()
                }
            )
        }
    }
}

@Composable
private fun ArenaOverlay(
    activeWorldArena: String?,
    onDismiss: () -> Unit
) {
    activeWorldArena?.let { arenaKey ->
        BackHandler(onBack = onDismiss)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(500f)
        ) {
            when (arenaKey) {
                ArenaRoute.CIPHER_SANCTUM -> CipherSanctumArenaScreen(onDismiss = onDismiss)
                ArenaRoute.BLOCK_CIPHER_FORGE -> BlockCipherForgeArenaScreen(onDismiss = onDismiss)
                ArenaRoute.KEY_EXCHANGE_CITADEL -> KeyExchangeCitadelArenaScreen(onDismiss = onDismiss)
                ArenaRoute.INTEGRITY_FOUNDRY -> IntegrityFoundryArenaScreen(onDismiss = onDismiss)
                ArenaRoute.CYBER_BASTION -> CyberBastionArenaScreen(onDismiss = onDismiss)
                else -> CipherSanctumArenaScreen(onDismiss = onDismiss)
            }
        }
    }
}

```

## 3. `app/src/main/java/com/simats/codeingo/navigation/BottomNavBar.kt`

```kotlin
package com.simats.codeingo.navigation

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DashboardTab
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.CodeingoTheme
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixMotion
import com.simats.codeingo.ui.theme.pressScale
import kotlin.math.abs

// ══════════════════════════════════════════════════════════════════
// 📱 LiquidFloatBottomNavBar — High-Level Liquid Floating Tab Bar
// Features:
// 1. Floating Capsule Geometry (hovering above bottom navigation insets)
// 2. Translucent Liquid Glassmorphism with dual-tone specular perimeter rim
// 3. Morphing Liquid Indicator Pill with fluid viscoelastic horizontal stretch
// 4. Ambient Radial Glow drawn dynamically beneath active tab
// 5. Active Icon Liquid Spring Lift & Elevation Wobble
// 6. Instagram-Level Silky Responsive Micro-Interactions & Haptics
// ══════════════════════════════════════════════════════════════════

@Composable
fun BottomNavBar(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier,
    swipePosition: Float? = null
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val view = LocalView.current

    val coreTabs = listOf(
        DashboardTab.LEARN,
        DashboardTab.VISUALIZER,
        DashboardTab.LEADERBOARDS,
        DashboardTab.ARENAS,
        DashboardTab.PROFILE
    )

    val selectedIndex = coreTabs.indexOf(selectedTab).coerceAtLeast(0)
    val targetIndex = swipePosition?.coerceIn(0f, (coreTabs.size - 1).toFloat()) ?: selectedIndex.toFloat()

    // Animated float index for smooth liquid interpolation
    val animatedIndex by animateFloatAsState(
        targetValue = targetIndex,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = 340f
        ),
        label = "liquidTabIndex"
    )

    // Dynamic Viscoelastic Stretch: elongates horizontally during motion
    val delta = targetIndex - animatedIndex
    val stretchX = 1f + (abs(delta) * 0.40f).coerceIn(0f, 0.42f)
    val squashY = 1f - (abs(delta) * 0.20f).coerceIn(0f, 0.22f)

    val capsuleShape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. Outer Translucent Liquid Glass Floating Capsule
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(
                    elevation = 18.dp,
                    shape = capsuleShape,
                    spotColor = if (isDark) Color(0x77000000) else Color(0x33475569)
                )
                .clip(capsuleShape)
                .background(
                    if (isDark) {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF131D33).copy(alpha = 0.92f),
                                Color(0xFF090E1B).copy(alpha = 0.96f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.98f),
                                Color(0xFFF1F5F9).copy(alpha = 0.98f)
                            )
                        )
                    }
                )
                .border(
                    width = 1.2.dp,
                    brush = if (isDark) {
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.24f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFCBD5E1),
                                Color(0xFFE2E8F0)
                            )
                        )
                    },
                    shape = capsuleShape
                )
        ) {
            // Specular Top-Rim Sheen Arc
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            if (isDark) listOf(
                                Color.Transparent,
                                Color(0xFFFF9E44).copy(alpha = 0.45f),
                                Color.White.copy(alpha = 0.4f),
                                Color(0xFFFF9E44).copy(alpha = 0.45f),
                                Color.Transparent
                            ) else listOf(
                                Color.Transparent,
                                Color(0xFFFF7A00).copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.6f),
                                Color(0xFFFF7A00).copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // 2. Sliding Liquid Morphing Pill & Radial Glow
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 6.dp)
            ) {
                val tabCount = coreTabs.size
                val tabWidth = maxWidth / tabCount
                val pillWidth = tabWidth - 6.dp
                val pillLeft = tabWidth * animatedIndex + 3.dp

                // Liquid Morphing Pill Indicator (Crisp floating bubble without blurry background shadow)
                Box(
                    modifier = Modifier
                        .offset(x = pillLeft)
                        .width(pillWidth)
                        .fillMaxHeight()
                        .graphicsLayer {
                            scaleX = stretchX
                            scaleY = squashY
                        }
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(26.dp),
                            spotColor = if (isDark) Color(0x44FF3D00) else Color(0x28E65100),
                            ambientColor = if (isDark) Color(0x22FF5722) else Color(0x18000000)
                        )
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    AmberGold,
                                    Color(0xFFFF5722),
                                    Color(0xFFFF3D00)
                                )
                            )
                        )
                        .drawBehind {
                            // Glossy top-half shine on the liquid pill
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.34f),
                                topLeft = Offset(4.dp.toPx(), 2.dp.toPx()),
                                size = Size(size.width - 8.dp.toPx(), size.height * 0.40f),
                                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                            )
                        }
                )

                // 3. Tab Items Row with Dynamic Elevations & Micro-Animations
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    coreTabs.forEachIndexed { index, tab ->
                        val isSelected = if (swipePosition != null) {
                            abs(animatedIndex - index) < 0.5f
                        } else {
                            selectedTab == tab
                        }
                        val interactionSource = remember { MutableInteractionSource() }

                        // Animated Icon Scale & Spring Elevation Lift
                        val iconScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.20f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = 0.55f,
                                stiffness = 420f
                            ),
                            label = "tabIconScale_${tab.name}"
                        )

                        val iconOffsetY by animateDpAsState(
                            targetValue = if (isSelected) (-2.5).dp else 0.dp,
                            animationSpec = spring(
                                dampingRatio = 0.6f,
                                stiffness = 400f
                            ),
                            label = "tabIconOffset_${tab.name}"
                        )

                        val contentColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White
                            else if (isDark) Color(0xFF94A3B8)
                            else Color(0xFF64748B),
                            animationSpec = spring(),
                            label = "tabColor_${tab.name}"
                        )

                        val tabIcon: ImageVector = when (tab) {
                            DashboardTab.LEARN -> Icons.Default.Home
                            DashboardTab.VISUALIZER -> Icons.Default.Build
                            DashboardTab.LEADERBOARDS -> Icons.Default.Leaderboard
                            DashboardTab.ARENAS -> Icons.Default.Shield
                            DashboardTab.SHOP -> Icons.Default.ShoppingBag
                            DashboardTab.PROFILE -> Icons.Default.Person
                            else -> Icons.Default.Home
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .pressScale(targetScale = 0.92f)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    onTabSelected(tab)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.offset(y = iconOffsetY)
                            ) {
                                Icon(
                                    imageVector = tabIcon,
                                    contentDescription = tab.title,
                                    tint = contentColor,
                                    modifier = Modifier
                                        .size(23.dp)
                                        .scale(iconScale)
                                )
                                Text(
                                    text = tab.title,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontFamily = AppFontFamily,
                                    color = contentColor,
                                    letterSpacing = 0.2.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun BottomNavBarPreview() {
    CodeingoTheme {
        BottomNavBar(
            selectedTab = DashboardTab.LEARN,
            onTabSelected = {}
        )
    }
}

```

## 4. `app/src/main/java/com/simats/codeingo/navigation/Routes.kt`

```kotlin
package com.simats.codeingo.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object ProfileCreation : Screen("profile_creation")
    object Dashboard : Screen("dashboard")
    object Assessment : Screen("assessment/{unitId}/{levelNumber}/{totalLevelsInUnit}/{isBoss}") {
        fun createRoute(unitId: Int, levelNumber: Int, totalLevelsInUnit: Int = 6, isBoss: Boolean = false) =
            "assessment/$unitId/$levelNumber/$totalLevelsInUnit/$isBoss"
        fun createRoute(lessonId: String) = "assessment/1/1/6/false"
    }
    object BossBattle : Screen("boss_battle/{bossId}") {
        fun createRoute(bossId: String) = "boss_battle/$bossId"
    }
    object Visualizer : Screen("visualizer")
    object PracticeHub : Screen("practice_hub")
    object PhoenixSanctuary : Screen("phoenix_sanctuary")
    object PhoenixEggHatch : Screen("phoenix_egg_hatch")
    object Settings : Screen("settings")
    object AnimationLab : Screen("animation_lab")
    object MotionLab : Screen("motion_lab")
}

```

## 5. `app/src/main/java/com/simats/codeingo/ui/auth/AppleAuthSheet.kt`

```kotlin
package com.simats.codeingo.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.ui.components.AppleLogoView
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.pressScale
import kotlinx.coroutines.delay

/**
 * 🍎 Official-Style Apple ID Sign In Sheet
 * Faithfully mirrors the iOS native "Sign in with Apple" authorization modal.
 * Features:
 * - Apple ID badge & Hide My Email relay toggle
 * - Face ID / Touch ID authorization prompt
 * - Instant profile federation
 */
@Composable
fun AppleAuthSheet(
    onSelectAccount: (name: String, email: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    var isAuthorizing by remember { mutableStateOf(false) }
    var hideMyEmail by remember { mutableStateOf(true) }

    val appleName = "Apple Adventurer"
    val realEmail = "user@icloud.com"
    val relayEmail = "ash.k92fx@privaterelay.appleid.com"
    val activeEmail = if (hideMyEmail) relayEmail else realEmail

    LaunchedEffect(isAuthorizing) {
        if (isAuthorizing) {
            delay(800)
            val profile = AuthService.shared.signInWithApple(
                appleID = "apple_${System.currentTimeMillis()}",
                name = appleName,
                email = activeEmail
            )
            onSelectAccount(profile.name, profile.email)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(if (isDark) Color(0xFF0F172A) else Color.White)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Header Row: Apple Logo + Dismiss
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppleLogoView(size = 22.dp, tint = if (isDark) Color.White else Color.Black)
                    Text(
                        text = "Sign in with Apple",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = dynamicColors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // App Identity & Scope Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF1E293B).copy(alpha = 0.6f) else Color(0xFFF8FAFC))
                    .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // App Name & Requirement
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "APP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textSecondary
                        )
                        Text(
                            text = "AshCrypt CNS Arena",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                    )

                    // Name Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "NAME",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textSecondary
                        )
                        Text(
                            text = appleName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = dynamicColors.textPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                    )

                    // Email Options (Share vs Hide)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "EMAIL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textSecondary
                        )

                        // Hide My Email option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (hideMyEmail) (if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFEDF2F7)) else Color.Transparent)
                                .clickable { hideMyEmail = true }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Hide My Email",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = dynamicColors.textPrimary
                                )
                                Text(
                                    text = "Forward to $realEmail",
                                    fontSize = 11.sp,
                                    color = dynamicColors.textSecondary
                                )
                            }
                            if (hideMyEmail) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF007AFF),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Share My Email option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!hideMyEmail) (if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFEDF2F7)) else Color.Transparent)
                                .clickable { hideMyEmail = false }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Share My Email ($realEmail)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = dynamicColors.textPrimary
                            )
                            if (!hideMyEmail) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF007AFF),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Apple Authorize Button
            val buttonBg = if (isDark) Color.White else Color.Black
            val buttonText = if (isDark) Color.Black else Color.White

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .pressScale(0.97f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(buttonBg)
                    .clickable(enabled = !isAuthorizing) { isAuthorizing = true },
                contentAlignment = Alignment.Center
            ) {
                if (isAuthorizing) {
                    CircularProgressIndicator(
                        strokeWidth = 2.5.dp,
                        color = buttonText,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppleLogoView(size = 18.dp, tint = buttonText)
                        Text(
                            text = "Continue with Passkey",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = buttonText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Privacy Footnote
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color(0xFF34C759),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "Apple does not track or profile your activity in AshCrypt",
                    fontSize = 10.5.sp,
                    color = dynamicColors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

```

## 6. `app/src/main/java/com/simats/codeingo/ui/auth/ForgotPasswordSheet.kt`

```kotlin
package com.simats.codeingo.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.network.model.ApiResult
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.domain.AuthServiceEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.components.AppCard
import com.simats.codeingo.ui.components.CustomSecureField
import com.simats.codeingo.ui.components.CustomTextField
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.shake
import com.simats.codeingo.ui.theme.staggeredAppear
import kotlinx.coroutines.delay

// ══════════════════════════════════════════════════════════════════
// 🔑 ForgotPasswordSheet — Fluid Glassmorphic Password Recovery
// ══════════════════════════════════════════════════════════════════

enum class ForgotPasswordStep {
    ENTER_EMAIL,
    SENDING_EMAIL,
    VERIFY_CODE,
    RESET_PASSWORD,
    SUCCESS
}

@Composable
fun ForgotPasswordSheet(
    initialEmail: String = "",
    onPasswordResetSuccess: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(ForgotPasswordStep.ENTER_EMAIL) }
    var email by remember { mutableStateOf(initialEmail) }
    var verificationCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isDark = LocalDynamicThemeColors.current.isDark

    val context = LocalContext.current
    val activity = context as? android.app.Activity
    androidx.compose.runtime.DisposableEffect(activity) {
        activity?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember(context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            AuthServiceEntryPoint::class.java
        ).authRepository()
    }

    LaunchedEffect(step) {
        if (step == ForgotPasswordStep.SENDING_EMAIL) {
            val res = authRepository.forgotPassword(email.trim().lowercase())
            when (res) {
                is ApiResult.Success -> {
                    errorMessage = null
                    step = ForgotPasswordStep.VERIFY_CODE
                }
                is ApiResult.HttpError -> {
                    errorMessage = res.message.ifEmpty { "Unable to send recovery code" }
                    step = ForgotPasswordStep.ENTER_EMAIL
                }
                is ApiResult.NetworkError -> {
                    errorMessage = "Network error. Please check your connection."
                    step = ForgotPasswordStep.ENTER_EMAIL
                }
                else -> {
                    errorMessage = "Failed to send code. Please try again."
                    step = ForgotPasswordStep.ENTER_EMAIL
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalDynamicThemeColors.current.background)
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF12_18_26)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "RESET PASSWORD",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = AppFontFamily,
                    color = if (isDark) Color.White else Color(0xFF12_18_26),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (step) {
                ForgotPasswordStep.ENTER_EMAIL -> {
                    AppCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shake(errorMessage)
                            .staggeredAppear(0),
                        cornerRadius = 24.dp,
                        accentGlow = AmberGold.copy(alpha = 0.20f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Forgot your password?",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = AppFontFamily,
                                color = if (isDark) Color.White else Color(0xFF12_18_26),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Enter your registered email address to receive a 6-digit recovery code.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = AppFontFamily,
                                color = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF64_74_8B),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(22.dp))
                            CustomTextField(
                                value = email,
                                onValueChange = { email = it; errorMessage = null },
                                placeholder = "Email address",
                                modifier = Modifier.fillMaxWidth()
                            )
                            val currentErr = errorMessage
                            if (!currentErr.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currentErr,
                                    color = Color(0xFFE8_24_10),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            AppButton(
                                title = "SEND RECOVERY CODE",
                                style = AppButtonStyle.PRIMARY_AMBER,
                                onClick = {
                                    if (email.contains("@")) {
                                        step = ForgotPasswordStep.SENDING_EMAIL
                                    } else {
                                        errorMessage = "Please enter a valid email address."
                                    }
                                }
                            )
                        }
                    }
                }

                ForgotPasswordStep.SENDING_EMAIL -> {
                    Spacer(modifier = Modifier.height(60.dp))
                    CircularProgressIndicator(color = AmberGold, modifier = Modifier.size(52.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Sending recovery code...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFontFamily,
                        color = if (isDark) Color.White else Color(0xFF12_18_26)
                    )
                }

                ForgotPasswordStep.VERIFY_CODE -> {
                    AppCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shake(errorMessage)
                            .staggeredAppear(0),
                        cornerRadius = 24.dp,
                        accentGlow = AmberGold.copy(alpha = 0.20f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Enter Verification Code",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = AppFontFamily,
                                color = if (isDark) Color.White else Color(0xFF12_18_26),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "We sent a 6-digit code to $email",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = AppFontFamily,
                                color = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF64_74_8B),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(22.dp))
                            CustomTextField(
                                value = verificationCode,
                                onValueChange = { verificationCode = it; errorMessage = null },
                                placeholder = "6-digit code (e.g. 123456)",
                                modifier = Modifier.fillMaxWidth()
                            )
                            val currentErr = errorMessage
                            if (!currentErr.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currentErr,
                                    color = Color(0xFFE8_24_10),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            AppButton(
                                title = "VERIFY CODE",
                                style = AppButtonStyle.PRIMARY_AMBER,
                                onClick = {
                                    if (verificationCode.trim().length >= 4) {
                                        coroutineScope.launch {
                                            when (val res = authRepository.verifyOtp(email.trim().lowercase(), verificationCode.trim())) {
                                                is ApiResult.Success -> {
                                                    errorMessage = null
                                                    step = ForgotPasswordStep.RESET_PASSWORD
                                                }
                                                is ApiResult.HttpError -> {
                                                    errorMessage = res.message.ifEmpty { "Invalid or expired verification code" }
                                                }
                                                is ApiResult.NetworkError -> {
                                                    errorMessage = "Network error. Please check your connection."
                                                }
                                                else -> {
                                                    errorMessage = "Invalid verification code"
                                                }
                                            }
                                        }
                                    } else {
                                        errorMessage = "Code must be at least 4 digits."
                                    }
                                }
                            )
                        }
                    }
                }

                ForgotPasswordStep.RESET_PASSWORD -> {
                    AppCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shake(errorMessage)
                            .staggeredAppear(0),
                        cornerRadius = 24.dp,
                        accentGlow = AmberGold.copy(alpha = 0.20f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Create New Password",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = AppFontFamily,
                                color = if (isDark) Color.White else Color(0xFF12_18_26),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            CustomSecureField(
                                value = newPassword,
                                onValueChange = { newPassword = it; errorMessage = null },
                                placeholder = "New password",
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            CustomSecureField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it; errorMessage = null },
                                placeholder = "Confirm password",
                                modifier = Modifier.fillMaxWidth()
                            )
                            val currentErr = errorMessage
                            if (!currentErr.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currentErr,
                                    color = Color(0xFFE8_24_10),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            AppButton(
                                title = "UPDATE PASSWORD",
                                style = AppButtonStyle.SUCCESS_GREEN,
                                onClick = {
                                    if (newPassword.length < 6) {
                                        errorMessage = "Password must be at least 6 characters."
                                    } else if (newPassword != confirmPassword) {
                                        errorMessage = "Passwords do not match."
                                    } else {
                                        coroutineScope.launch {
                                            when (val res = authRepository.resetPassword(email.trim().lowercase(), verificationCode.trim(), newPassword)) {
                                                is ApiResult.Success -> {
                                                    errorMessage = null
                                                    AuthService.shared.updatePassword(email.trim().lowercase(), newPassword)
                                                    step = ForgotPasswordStep.SUCCESS
                                                }
                                                is ApiResult.HttpError -> {
                                                    errorMessage = res.message.ifEmpty { "Password reset failed" }
                                                }
                                                is ApiResult.NetworkError -> {
                                                    errorMessage = "Network error. Please check your connection."
                                                }
                                                else -> {
                                                    errorMessage = "Failed to reset password. Please try again."
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                ForgotPasswordStep.SUCCESS -> {
                    AppCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .staggeredAppear(0),
                        cornerRadius = 24.dp,
                        accentGlow = PhoenixGreen.copy(alpha = 0.25f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(PhoenixGreen.copy(alpha = 0.20f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = PhoenixGreen,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Password Updated!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = AppFontFamily,
                                color = if (isDark) Color.White else Color(0xFF12_18_26)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your password has been successfully updated. You can now sign in with your new credentials.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = AppFontFamily,
                                color = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF64_74_8B),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            AppButton(
                                title = "CONTINUE TO SIGN IN",
                                style = AppButtonStyle.PRIMARY_AMBER,
                                onClick = { onPasswordResetSuccess(email) }
                            )
                        }
                    }
                }
            }
        }
    }
}

```

## 7. `app/src/main/java/com/simats/codeingo/ui/auth/GoogleAuthSheet.kt`

```kotlin
package com.simats.codeingo.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.GoogleLogoView
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.pressScale
import kotlinx.coroutines.delay

data class GoogleAccountItem(
    val name: String,
    val email: String,
    val avatarInitial: String,
    val avatarBgColor: Color
)

/**
 * 🌐 Enhanced Google Account Selection Sheet
 * Clean Material 3 Google Identity styling:
 * - Official Google G-logo header
 * - Responsive Light & Dark Mode card surfaces
 * - Account cards with vibrant letter avatars & check indicators
 * - "Use another account" option
 */
@Composable
fun GoogleAuthSheet(
    onSelectAccount: (name: String, email: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAuthenticating by remember { mutableStateOf(false) }
    var selectedName by remember { mutableStateOf("") }
    var selectedEmail by remember { mutableStateOf("") }
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val savedAccounts by com.simats.codeingo.domain.AuthService.shared.savedGoogleAccounts.collectAsState()

    val displayAccounts = if (savedAccounts.isNotEmpty()) {
        savedAccounts.map { acc ->
            GoogleAccountItem(
                name = acc.name,
                email = acc.email,
                avatarInitial = acc.avatarInitial,
                avatarBgColor = when (acc.colorIndex % 3) {
                    0 -> Color(0xFF4285F4)
                    1 -> Color(0xFF34A853)
                    else -> Color(0xFFEA4335)
                }
            )
        }
    } else {
        listOf(
            GoogleAccountItem(
                name = "Lokesh Kumar",
                email = "lokeshkumar1232005@gmail.com",
                avatarInitial = "L",
                avatarBgColor = Color(0xFF4285F4)
            ),
            GoogleAccountItem(
                name = "Lokesh Developer",
                email = "lokesh.developer@gmail.com",
                avatarInitial = "L",
                avatarBgColor = Color(0xFF34A853)
            )
        )
    }

    LaunchedEffect(isAuthenticating) {
        if (isAuthenticating) {
            delay(750)
            com.simats.codeingo.domain.AuthService.shared.signInWithGoogle(selectedName, selectedEmail)
            onSelectAccount(selectedName, selectedEmail)
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(if (isDark) Color(0xFF0F172A) else Color.White)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Header Row: Security pill + Dismiss
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                        .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("🔒", fontSize = 11.sp)
                    Text(
                        text = "accounts.google.com",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF34A853)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = dynamicColors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Logo Header
            GoogleLogoView(size = 36.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Choose an account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = dynamicColors.textPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "to continue to AshCrypt",
                fontSize = 13.sp,
                color = dynamicColors.textSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isAuthenticating) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        color = Color(0xFF4285F4),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "Signing in as $selectedName...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = dynamicColors.textPrimary
                    )
                }
            } else {
                // Account list
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    displayAccounts.forEach { acc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .pressScale(0.97f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isDark) Color(0xFF1E293B).copy(alpha = 0.7f) else Color(0xFFF8FAFC))
                                .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                .clickable {
                                    selectedName = acc.name
                                    selectedEmail = acc.email
                                    isAuthenticating = true
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(acc.avatarBgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = acc.avatarInitial,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = acc.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = dynamicColors.textPrimary
                                )
                                Text(
                                    text = acc.email,
                                    fontSize = 12.sp,
                                    color = dynamicColors.textSecondary
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = dynamicColors.textSecondary.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Use another account
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressScale(0.97f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0xFF1E293B).copy(alpha = 0.4f) else Color.Transparent)
                            .border(1.dp, if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .clickable {
                                selectedName = "Developer Agent"
                                selectedEmail = "agent@ashcrypt.io"
                                isAuthenticating = true
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = dynamicColors.textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = "Use another account",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = dynamicColors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "To continue, Google will share your name, email address, and profile picture with AshCrypt. Review AshCrypt's Privacy Policy and Terms of Service.",
                fontSize = 10.5.sp,
                color = dynamicColors.textSecondary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )
        }
    }
}

```

## 8. `app/src/main/java/com/simats/codeingo/ui/auth/LoginScreen.kt`

```kotlin
package com.simats.codeingo.ui.auth

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.password
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.network.model.ApiResult
import com.simats.codeingo.data.network.model.GoogleLoginRequest
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.domain.AuthServiceEntryPoint
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import dagger.hilt.android.EntryPointAccessors
import com.simats.codeingo.ui.components.AppleLogoView
import com.simats.codeingo.ui.components.GoogleLogoView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.liquidGlassCard
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.pulse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * ⚡ Modernized Cyber Authentication Interface
 * Completely restructured from the ground up:
 * - Ambient cyber-atmospheric backdrop with dynamic embers
 * - Minimalist, modern top navigation bar
 * - Hero brand emblem with interactive Phoenix companion pill
 * - Unified glowing glassmorphic auth card with integrated primary actions
 * - Streamlined biometric quick-unlock
 * - Polished Google OAuth and Guest passes
 * - Clean mobile & tablet responsive layout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (name: String, email: String) -> Unit,
    onDismiss: () -> Unit,
    onCreateAccountClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val localizationManager = LocalizationManager.instance
    val emotionManager = PhoenixEmotionManager.instance
    val currentEmotion by emotionManager.currentEmotion.collectAsState()
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val prefs = remember { context.getSharedPreferences("ashcrypt_preferences", Context.MODE_PRIVATE) }

    val activity = context as? android.app.Activity
    DisposableEffect(activity) {
        activity?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
    val authRepository = remember(context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            AuthServiceEntryPoint::class.java
        ).authRepository()
    }
    val syncRepository = remember(context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            AuthServiceEntryPoint::class.java
        ).syncRepository()
    }

    // Form inputs
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    // Field focus tracking
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    // Validation
    val isEmailValid = emailOrUsername.contains("@") && emailOrUsername.contains(".")
    val isPasswordStrong = password.length >= 6
    val isFormValid = emailOrUsername.trim().isNotEmpty() && password.length >= 4

    // UI Feedback & Animations
    var isSubmitting by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessToast by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }

    // Sheets
    var showGoogleAuthSheet by remember { mutableStateOf(false) }
    var showAppleAuthSheet by remember { mutableStateOf(false) }
    var showForgotPasswordSheet by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Pre-fill email if remembered
    LaunchedEffect(Unit) {
        val saved = prefs.getString("dsaUserEmail", "")
        if (!saved.isNullOrEmpty()) {
            emailOrUsername = saved
        }
    }

    // Auto dismiss toast after 3.2s
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(3200)
            toastMessage = null
        }
    }

    fun triggerShake() {
        coroutineScope.launch {
            for (i in 0..4) {
                shakeOffset.animateTo(if (i % 2 == 0) -12f else 12f, tween(35))
            }
            shakeOffset.animateTo(0f, tween(35))
        }
    }

    fun handleLogin() {
        if (!isFormValid || isSubmitting) return
        focusManager.clearFocus()
        isSubmitting = true

        coroutineScope.launch {
            val trimmed = emailOrUsername.trim().lowercase()
            val result = authRepository.authenticate(trimmed, password)

            when (result) {
                is ApiResult.Success -> {
                    val user = result.data
                    val displayName = user.name ?: user.username
                    if (rememberMe) {
                        prefs.edit().putString("dsaUserEmail", trimmed).apply()
                    } else {
                        prefs.edit().remove("dsaUserEmail").apply()
                    }
                    AuthService.shared.updatePassword(trimmed, password)
                    localizationManager.updateUserProfile(displayName, user.username)
                    localizationManager.setLoggedIn(true, displayName, user.username)

                    // Refresh remote progress in background
                    launch {
                        syncRepository.syncRemoteProgressToLocal()
                    }

                    isSubmitting = false
                    isSuccessToast = true
                    toastMessage = "Welcome back, $displayName! 🔥"

                    delay(600)
                    onLoginSuccess(displayName, user.email)
                }
                is ApiResult.Unauthorized -> {
                    isSubmitting = false
                    isSuccessToast = false
                    toastMessage = "Incorrect email or password. Tap 'Forgot password?' to reset."
                    triggerShake()
                }
                is ApiResult.HttpError -> {
                    isSubmitting = false
                    isSuccessToast = false
                    toastMessage = result.message.ifEmpty { "Sign in failed (${result.code})" }
                    triggerShake()
                }
                is ApiResult.NetworkError -> {
                    // Graceful offline fallback: verify salted local credentials
                    val isPasswordCorrect = AuthService.shared.verifyPassword(trimmed, password)
                    if (isPasswordCorrect) {
                        val profile = AuthService.shared.signInWithEmail(trimmed)
                        localizationManager.updateUserProfile(profile.name, profile.handle)
                        localizationManager.setLoggedIn(true, profile.name, profile.handle)
                        isSubmitting = false
                        isSuccessToast = true
                        toastMessage = "Welcome back, ${profile.name}! (Offline) 🔥"
                        delay(600)
                        onLoginSuccess(profile.name, profile.email)
                    } else {
                        isSubmitting = false
                        isSuccessToast = false
                        toastMessage = "Server unreachable. Please check your network connection."
                        triggerShake()
                    }
                }
            }
        }
    }

    // Ambient background pulse
    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val heroAuraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF070B14) else Color(0xFFF6F8FC))
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // ── 1. Top Modern Navigation Bar ─────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Circular Frosted Close Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .pressScale(0.92f)
                        .clip(CircleShape)
                        .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                        .border(1.dp, if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.10f), CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = dynamicColors.textPrimary,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Cyber Access Insignia Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDark) Color(0xFF0F172A).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f))
                        .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(AmberGold)
                    )
                    Text(
                        text = "ASHCRYPT ✦ ACCESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textPrimary,
                        letterSpacing = 1.sp
                    )
                }

                // Sign Up Action Chip
                Box(
                    modifier = Modifier
                        .pressScale(0.94f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AmberGold.copy(alpha = 0.12f))
                        .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { onCreateAccountClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SIGN UP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold,
                        letterSpacing = 0.6.sp
                    )
                }
            }

            // ── 2. Scrollable Body ───────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // ── Hero Section: Cyber Insignia & Dynamic Title ─────────────
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(72.dp)
                ) {
                    // Radiant aura glow
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .scale(heroAuraScale)
                            .clip(CircleShape)
                            .background(AmberGold.copy(alpha = 0.28f))
                            .blur(14.dp)
                    )
                    // Modern 3D emblem
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        if (isDark) Color(0xFF1E293B) else Color.White,
                                        if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9)
                                    )
                                )
                            )
                            .border(1.5.dp, AmberGold.copy(alpha = 0.65f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Security Shield",
                            tint = AmberGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Welcome Back",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = dynamicColors.textPrimary,
                    letterSpacing = (-0.5).sp
                )

                Text(
                    text = "Authenticate to unlock the Cryptographic Cyber Arenas",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = dynamicColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Dynamic Island Mascot Companion Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF131D2E).copy(alpha = 0.75f) else Color.White.copy(alpha = 0.85f))
                        .border(1.dp, AmberGold.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = currentEmotion.emoji, fontSize = 16.sp, modifier = Modifier.pulse(0.95f..1.05f))
                    val companionTip = when {
                        isPasswordFocused -> "Validating cryptographic passkey..."
                        isEmailFocused -> "Looking up agent credentials..."
                        isFormValid -> "Passkey ready. Tap Authenticate!"
                        else -> "Phoenix Security Protocol Active"
                    }
                    Text(
                        text = companionTip,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AmberGold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ── 3. Unified Glassmorphic Authentication Card ───────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                        .liquidGlassCard(accentGlow = AmberGold.copy(alpha = 0.22f), cornerRadius = 24.dp)
                        .padding(20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Email / Username Field
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AGENT IDENTIFIER",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isEmailFocused) AmberGold else dynamicColors.textSecondary,
                                    letterSpacing = 0.8.sp
                                )
                                if (isEmailValid) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = DuolingoGreen,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "VERIFIED",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = DuolingoGreen
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0xFF0F172A).copy(alpha = 0.90f) else Color(0xFFF1F5F9))
                                    .border(
                                        width = if (isEmailFocused) 1.8.dp else 1.dp,
                                        color = if (isEmailFocused) AmberGold else if (isDark) dynamicColors.cardBorder.copy(alpha = 0.4f) else Color(0xFFD7DEEB),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = if (isEmailFocused) AmberGold else dynamicColors.textSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )

                                BasicTextField(
                                    value = emailOrUsername,
                                    onValueChange = { emailOrUsername = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .onFocusChanged { isEmailFocused = it.isFocused },
                                    textStyle = TextStyle(
                                        fontFamily = AppFontFamily,
                                        color = dynamicColors.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(AmberGold),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    decorationBox = { innerTextField ->
                                        if (emailOrUsername.isEmpty()) {
                                            Text(
                                                text = "agent@ashcrypt.io or username",
                                                color = dynamicColors.textSecondary.copy(alpha = 0.5f),
                                                fontSize = 14.sp,
                                                fontFamily = AppFontFamily
                                            )
                                        }
                                        innerTextField()
                                    }
                                )

                                if (emailOrUsername.isNotEmpty()) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = dynamicColors.textSecondary.copy(alpha = 0.5f),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { emailOrUsername = "" }
                                    )
                                }
                            }
                        }

                        // Password Field
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SECURITY PASSKEY",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isPasswordFocused) AmberGold else dynamicColors.textSecondary,
                                    letterSpacing = 0.8.sp
                                )
                                if (password.isNotEmpty()) {
                                    Text(
                                        text = if (isPasswordStrong) "STRONG PASSKEY ✓" else "MIN 4 CHARS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = AppFontFamily,
                                        color = if (isPasswordStrong) DuolingoGreen else AmberGold
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0xFF0F172A).copy(alpha = 0.90f) else Color(0xFFF1F5F9))
                                    .border(
                                        width = if (isPasswordFocused) 1.8.dp else 1.dp,
                                        color = if (isPasswordFocused) AmberGold else if (isDark) dynamicColors.cardBorder.copy(alpha = 0.4f) else Color(0xFFD7DEEB),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isPasswordFocused) AmberGold else dynamicColors.textSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )

                                BasicTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .onFocusChanged { isPasswordFocused = it.isFocused }
                                        .semantics { password() },
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    textStyle = TextStyle(
                                        fontFamily = AppFontFamily,
                                        color = dynamicColors.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(AmberGold),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = { handleLogin() }),
                                    decorationBox = { innerTextField ->
                                        if (password.isEmpty()) {
                                            Text(
                                                text = "Enter passkey",
                                                color = dynamicColors.textSecondary.copy(alpha = 0.5f),
                                                fontSize = 14.sp,
                                                fontFamily = AppFontFamily
                                            )
                                        }
                                        innerTextField()
                                    }
                                )

                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = AmberGold.copy(alpha = 0.85f),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { isPasswordVisible = !isPasswordVisible }
                                )
                            }
                        }

                        // Utility Row: Remember Me & Forgot Password
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { rememberMe = !rememberMe }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(if (rememberMe) AmberGold else Color.Transparent)
                                        .border(
                                            1.2.dp,
                                            if (rememberMe) AmberGold else dynamicColors.cardBorder,
                                            RoundedCornerShape(5.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (rememberMe) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Remember passkey",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = dynamicColors.textSecondary
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showForgotPasswordSheet = true }
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Forgot passkey?",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold
                                )
                            }
                        }

                        // Primary Action Row: Biometric + Sign In Action Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Biometrics Quick Button
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .pressScale(0.92f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f))
                                    .border(1.2.dp, AmberGold.copy(alpha = 0.40f), RoundedCornerShape(14.dp))
                                    .clickable {
                                        val savedUser = AuthService.shared.currentUser.value
                                            ?: AuthService.shared.savedGoogleAccounts.value.firstOrNull()?.let {
                                                AuthService.shared.signInWithGoogle(it.name, it.email)
                                            }
                                        if (savedUser != null) {
                                            localizationManager.updateUserProfile(savedUser.name, savedUser.handle)
                                            localizationManager.setLoggedIn(true, savedUser.name, savedUser.handle)
                                            isSuccessToast = true
                                            toastMessage = "Biometric credentials authorized! ⚡"
                                            coroutineScope.launch {
                                                delay(450)
                                                onLoginSuccess(savedUser.name, savedUser.email)
                                            }
                                        } else {
                                            toastMessage = "No biometric profile registered. Sign in once with passkey."
                                            isSuccessToast = false
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = "Biometric Login",
                                    tint = AmberGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // 3D Duolingo Action Button
                            val buttonColor = if (isFormValid) AmberGold else (if (isDark) Color.White.copy(alpha = 0.10f) else Color.Black.copy(alpha = 0.08f))

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .then(if (isFormValid && !isSubmitting) Modifier.pressScale(0.96f) else Modifier)
                                    .shadow(
                                        elevation = if (isFormValid) 6.dp else 0.dp,
                                        shape = RoundedCornerShape(14.dp),
                                        ambientColor = Color(0xFFCC8800),
                                        spotColor = Color(0xFFCC8800)
                                    )
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(buttonColor)
                                    .border(
                                        width = 1.dp,
                                        color = if (isFormValid) Color(0xFFFFA000) else Color.Transparent,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable(enabled = isFormValid && !isSubmitting) { handleLogin() },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.5.dp,
                                        color = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "AUTHENTICATE & ENTER",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 0.8.sp,
                                            color = if (isFormValid) Color.Black else dynamicColors.textSecondary.copy(alpha = 0.5f)
                                        )
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = if (isFormValid) Color.Black else dynamicColors.textSecondary.copy(alpha = 0.5f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ── 4. Modern Alternative Auth Options ───────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(dynamicColors.cardBorder.copy(alpha = 0.4f))
                    )
                    Text(
                        text = "OR CONTINUE WITH",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textSecondary.copy(alpha = 0.6f),
                        letterSpacing = 0.8.sp
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(dynamicColors.cardBorder.copy(alpha = 0.4f))
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Branded OAuth Actions: Google & Apple Identity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Enhanced Google OAuth Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .pressScale(0.96f)
                            .shadow(
                                elevation = if (isDark) 3.dp else 4.dp,
                                shape = RoundedCornerShape(14.dp),
                                ambientColor = if (isDark) Color.Black else Color(0xFF64748B).copy(alpha = 0.25f),
                                spotColor = if (isDark) Color.Black else Color(0xFF64748B).copy(alpha = 0.25f)
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0xFF131D2E) else Color.White)
                            .border(
                                1.2.dp,
                                if (isDark) Color(0xFF2A3C53) else Color(0xFFE2E8F0),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { showGoogleAuthSheet = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GoogleLogoView(size = 19.dp)
                            Text(
                                text = "Google",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = dynamicColors.textPrimary
                            )
                        }
                    }

                    // Official Apple Sign-In Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .pressScale(0.96f)
                            .shadow(
                                elevation = if (isDark) 3.dp else 4.dp,
                                shape = RoundedCornerShape(14.dp),
                                ambientColor = Color.Black.copy(alpha = 0.3f),
                                spotColor = Color.Black.copy(alpha = 0.3f)
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color.White else Color(0xFF0F172A))
                            .border(
                                1.2.dp,
                                if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { showAppleAuthSheet = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppleLogoView(
                                size = 19.dp,
                                tint = if (isDark) Color.Black else Color.White
                            )
                            Text(
                                text = "Apple",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.Black else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Guest Instant Pass Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .pressScale(0.97f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f))
                        .border(
                            1.dp,
                            if (isDark) AmberGold.copy(alpha = 0.35f) else Color(0xFFCBD5E1),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            if (isSubmitting) return@clickable
                            isSubmitting = true
                            coroutineScope.launch {
                                when (val res = authRepository.guestStart()) {
                                    is ApiResult.Success -> {
                                        val user = res.data
                                        val guestName = user.name ?: user.username
                                        localizationManager.updateUserProfile(guestName, user.username)
                                        localizationManager.setLoggedIn(true, guestName, user.username)
                                        isSubmitting = false
                                        onLoginSuccess(guestName, user.email)
                                    }
                                    else -> {
                                        val guest = AuthService.shared.signInAsGuest()
                                        localizationManager.updateUserProfile(guest.name, guest.handle)
                                        localizationManager.setLoggedIn(true, guest.name, guest.handle)
                                        isSubmitting = false
                                        onLoginSuccess(guest.name, guest.email)
                                    }
                                }
                            }
                        }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Continue with Guest Pass",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = dynamicColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Security Note
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = DuolingoGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "256-bit AES Cryptographic Channel • TLS 1.3 Verified",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = dynamicColors.textSecondary.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // ── 5. Feedback Toast Banner ─────────────────────────────────────────
        AnimatedVisibility(
            visible = toastMessage != null,
            enter = slideInVertically(initialOffsetY = { -50 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -50 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 64.dp, start = 20.dp, end = 20.dp)
        ) {
            toastMessage?.let { msg ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF131D2E) else Color.White)
                        .border(1.5.dp, if (isSuccessToast) DuolingoGreen else DuolingoRed, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (isSuccessToast) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isSuccessToast) DuolingoGreen else DuolingoRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = msg,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                }
            }
        }

        // ── 6. Sheets: Google Identity & Forgot Password ─────────────────────
        if (showGoogleAuthSheet) {
            ModalBottomSheet(
                onDismissRequest = { showGoogleAuthSheet = false },
                sheetState = bottomSheetState,
                containerColor = if (isDark) Color(0xFF0F1522) else Color.White
            ) {
                GoogleAuthSheet(
                    onSelectAccount = { accName, accEmail ->
                        showGoogleAuthSheet = false
                        isSubmitting = true
                        coroutineScope.launch {
                            val googleReq = GoogleLoginRequest(
                                idToken = "google_${System.currentTimeMillis()}",
                                email = accEmail,
                                name = accName,
                                picture = null,
                                googleId = accEmail.substringBefore("@")
                            )
                            when (val res = authRepository.googleAuthenticate(googleReq)) {
                                is ApiResult.Success -> {
                                    val user = res.data
                                    val displayName = user.name ?: user.username
                                    localizationManager.updateUserProfile(displayName, user.username)
                                    localizationManager.setLoggedIn(true, displayName, user.username)
                                    isSubmitting = false
                                    isSuccessToast = true
                                    toastMessage = "Welcome back, $displayName!"
                                    delay(450)
                                    onLoginSuccess(displayName, user.email)
                                }
                                else -> {
                                    val user = AuthService.shared.signInWithGoogle(accName, accEmail)
                                    localizationManager.updateUserProfile(user.name, user.handle)
                                    localizationManager.setLoggedIn(true, user.name, user.handle)
                                    isSubmitting = false
                                    isSuccessToast = true
                                    toastMessage = "Welcome, ${user.name}!"
                                    delay(450)
                                    onLoginSuccess(user.name, user.email)
                                }
                            }
                        }
                    },
                    onDismiss = { showGoogleAuthSheet = false }
                )
            }
        }

        if (showAppleAuthSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAppleAuthSheet = false },
                sheetState = bottomSheetState,
                containerColor = if (isDark) Color(0xFF0F1522) else Color.White
            ) {
                AppleAuthSheet(
                    onSelectAccount = { accName, accEmail ->
                        showAppleAuthSheet = false
                        val user = AuthService.shared.currentUser.value
                            ?: AuthService.shared.signInWithApple(
                                appleID = "apple_${System.currentTimeMillis()}",
                                name = accName,
                                email = accEmail
                            )
                        localizationManager.updateUserProfile(user.name, user.handle)
                        localizationManager.setLoggedIn(true, user.name, user.handle)
                        isSuccessToast = true
                        toastMessage = "Authorized with Apple ID, ${user.name}!"
                        coroutineScope.launch {
                            delay(450)
                            onLoginSuccess(user.name, user.email)
                        }
                    },
                    onDismiss = { showAppleAuthSheet = false }
                )
            }
        }

        if (showForgotPasswordSheet) {
            ModalBottomSheet(
                onDismissRequest = { showForgotPasswordSheet = false },
                sheetState = bottomSheetState,
                containerColor = if (isDark) Color(0xFF0F1522) else Color.White
            ) {
                ForgotPasswordSheet(
                    initialEmail = emailOrUsername,
                    onPasswordResetSuccess = { resetEmail ->
                        showForgotPasswordSheet = false
                        emailOrUsername = resetEmail
                        password = ""
                        toastMessage = "Password reset! You can now sign in with your new password."
                        isSuccessToast = true
                    },
                    onDismiss = { showForgotPasswordSheet = false }
                )
            }
        }
    }
}

```

## 9. `app/src/main/java/com/simats/codeingo/ui/auth/ProfileCreationScreen.kt`

```kotlin
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
import androidx.compose.foundation.layout.imePadding
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
import com.simats.codeingo.ui.components.AshButton
import com.simats.codeingo.ui.components.AshButtonStyle
import com.simats.codeingo.ui.components.GoogleLogoView
import com.simats.codeingo.ui.phoenix.AnimatedGIFView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.simats.codeingo.data.network.model.ApiResult
import com.simats.codeingo.domain.AuthServiceEntryPoint
import dagger.hilt.android.EntryPointAccessors
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

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

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember(context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            AuthServiceEntryPoint::class.java
        ).authRepository()
    }
    var isSubmitting by remember { mutableStateOf(false) }
    var submissionError by remember { mutableStateOf<String?>(null) }

    val isCredentialsValid = emailText.trim().isNotEmpty() && passwordText.length >= 6
    val displayName = nameText.trim().ifEmpty {
        emailText.substringBefore("@").ifEmpty { "Vishal Rao" }.replaceFirstChar { it.uppercase() }
    }
    val generatedHandle = displayName.replace(" ", "").lowercase() + "3454"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalDynamicThemeColors.current.background)
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
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
                            tint = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "CREATE ACCOUNT",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = LocalDynamicThemeColors.current.textPrimary,
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
                                        SolidColor(LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f))
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
                    .background(LocalDynamicThemeColors.current.cardBackground.copy(alpha = 0.95f))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (currentStep) {
                    1 -> {
                        com.simats.codeingo.ui.components.AppButton(
                            title = "NEXT",
                            style = if (ageText.isNotEmpty()) com.simats.codeingo.ui.components.AppButtonStyle.PRIMARY_AMBER else com.simats.codeingo.ui.components.AppButtonStyle.DISABLED,
                            isEnabled = ageText.isNotEmpty(),
                            onClick = { currentStep = 2 },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    2 -> {
                        com.simats.codeingo.ui.components.AppButton(
                            title = "NEXT",
                            style = com.simats.codeingo.ui.components.AppButtonStyle.PRIMARY_AMBER,
                            onClick = { currentStep = 3 },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    3 -> {
                        val subErr = submissionError
                        if (!subErr.isNullOrBlank()) {
                            Text(
                                text = subErr,
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                            )
                        }
                        com.simats.codeingo.ui.components.AppButton(
                            title = if (isSubmitting) "CREATING ACCOUNT..." else "CREATE ACCOUNT",
                            style = if (isCredentialsValid && !isSubmitting) com.simats.codeingo.ui.components.AppButtonStyle.PRIMARY_AMBER else com.simats.codeingo.ui.components.AppButtonStyle.DISABLED,
                            isEnabled = isCredentialsValid && !isSubmitting,
                            onClick = {
                                val resolvedEmail = emailText.trim().lowercase()
                                val parsedAge = ageText.toIntOrNull() ?: 21
                                isSubmitting = true
                                submissionError = null

                                coroutineScope.launch {
                                    val regResult = authRepository.register(
                                        email = resolvedEmail,
                                        password = passwordText,
                                        username = displayName.replace(" ", "").lowercase(),
                                        name = displayName,
                                        age = parsedAge
                                    )
                                    when (regResult) {
                                        is ApiResult.Success -> {
                                            isSubmitting = false
                                            currentStep = 4
                                        }
                                        is ApiResult.HttpError -> {
                                            if (regResult.code == 409 || regResult.message.contains("already exists", ignoreCase = true)) {
                                                // Try logging in with the entered credentials
                                                val authResult = authRepository.authenticate(resolvedEmail, passwordText)
                                                if (authResult is ApiResult.Success) {
                                                    isSubmitting = false
                                                    currentStep = 4
                                                } else {
                                                    isSubmitting = false
                                                    submissionError = "An account with this email already exists. Please sign in or reset your password."
                                                }
                                            } else {
                                                isSubmitting = false
                                                submissionError = regResult.message.ifEmpty { "Registration failed (${regResult.code})" }
                                            }
                                        }
                                        is ApiResult.NetworkError -> {
                                            // Fallback locally
                                            com.simats.codeingo.domain.AuthService.instance.signInWithEmail(resolvedEmail, displayName)
                                            if (passwordText.isNotEmpty()) {
                                                com.simats.codeingo.domain.AuthService.instance.updatePassword(resolvedEmail, passwordText)
                                            }
                                            isSubmitting = false
                                            currentStep = 4
                                        }
                                        else -> {
                                            isSubmitting = false
                                            submissionError = "Unable to create account. Please try again."
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "By creating an account, you agree to our Terms of Service & Privacy Policy.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.40f),
                            textAlign = TextAlign.Center
                        )
                    }
                    4 -> {
                        com.simats.codeingo.ui.components.AppButton(
                            title = "CONTINUE",
                            style = com.simats.codeingo.ui.components.AppButtonStyle.PRIMARY_AMBER,
                            onClick = {
                                val resolvedEmail = emailText.ifEmpty { "${generatedHandle.removePrefix("@")}@ashcrypt.app" }
                                com.simats.codeingo.domain.AuthService.instance.signInWithEmail(resolvedEmail, displayName)
                                if (passwordText.isNotEmpty()) {
                                    com.simats.codeingo.domain.AuthService.instance.updatePassword(resolvedEmail, passwordText)
                                }
                                localizationManager.updateUserProfile(displayName, generatedHandle)
                                localizationManager.setLoggedIn(true, displayName, generatedHandle)
                                onProfileCreated(displayName, resolvedEmail)
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
                containerColor = LocalDynamicThemeColors.current.cardBackground
            ) {
                GoogleAuthSheet(
                    onSelectAccount = { accName, accEmail ->
                        showGoogleAuthSheet = false
                        nameText = accName
                        emailText = accEmail
                        passwordText = "GoogleAuth2026!"
                        coroutineScope.launch {
                            val googleReq = com.simats.codeingo.data.network.model.GoogleLoginRequest(
                                idToken = "google_${System.currentTimeMillis()}",
                                email = accEmail,
                                name = accName,
                                picture = null,
                                googleId = accEmail.substringBefore("@")
                            )
                            authRepository.googleAuthenticate(googleReq)
                            currentStep = 4
                        }
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
        in 1..19 -> "Starting young! Master Cryptography & Network Security early to become a legend."
        in 20..29 -> "The prime defense years! Let's conquer Cryptography & Cyber Security together."
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
                        .background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF141F36) else LocalDynamicThemeColors.current.cardBackground)
                        .border(1.5.dp, AmberGold.copy(alpha = 0.40f), CircleShape)
                ) {
                    Text(text = emotionEmoji, fontSize = 22.sp)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF141F36) else LocalDynamicThemeColors.current.cardBackground.copy(alpha = 0.90f))
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
                    color = LocalDynamicThemeColors.current.textPrimary
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
                color = LocalDynamicThemeColors.current.textPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Providing your age ensures you get the right learning experience.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.60f),
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
                    .background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF141F36) else LocalDynamicThemeColors.current.cardBackground.copy(alpha = 0.85f))
                    .border(
                        if (isAgeFocused) 2.dp else 1.2.dp,
                        if (isAgeFocused) AmberGold else if (ageText.isNotEmpty()) AmberGold.copy(alpha = 0.6f) else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.14f),
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
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    cursorBrush = SolidColor(AmberGold),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    decorationBox = { innerTextField ->
                        if (ageText.isEmpty()) {
                            Text(
                                text = "e.g. 21",
                                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.35f),
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
                        tint = LocalDynamicThemeColors.current.placeholder,
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
                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.45f)
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
                            .background(if (isSelected) AmberGold.copy(alpha = 0.25f) else if (LocalDynamicThemeColors.current.isDark) Color(0xFF141F36) else LocalDynamicThemeColors.current.cardBackground.copy(alpha = 0.70f))
                            .border(1.dp, if (isSelected) AmberGold else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                            .clickable { onAgeChange(preset) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (preset == "32") "32+" else preset,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.70f)
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
        CodingGoal("cns", "🛡️", "Master Cryptography & Network Security", "Ciphers, AES, RSA, Hashes, ECC & Cyber Defense", "POPULAR"),
        CodingGoal("security", "🎯", "Ace Cyber Security & Cryptanalysis", "Security protocols, cryptanalysis & defense fundamentals", "RECOMMENDED"),
        CodingGoal("protocols", "🚀", "Learn Cryptographic Protocols", "TLS, Diffie-Hellman, digital signatures & PKI infrastructure", "ESSENTIAL"),
        CodingGoal("habit", "🔥", "Build a 15-Minute Daily Habit", "Keep your Phoenix defense flame alive with bite-sized security challenges", "DAILY")
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
                color = LocalDynamicThemeColors.current.textPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Choose your focus path to customize your learning tree:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.65f),
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
                        .background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF141F36) else LocalDynamicThemeColors.current.cardBackground.copy(alpha = if (isSelected) 0.95f else 0.75f))
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
                                color = LocalDynamicThemeColors.current.textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = goal.tag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) AmberGold else LocalDynamicThemeColors.current.placeholder,
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
                            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.55f),
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
                            .border(1.5.dp, if (isSelected) AmberGold else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.25f), CircleShape)
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
                .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
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
                    color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f)
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
                color = LocalDynamicThemeColors.current.textPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Choose your name and secure password to save your progress.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.65f),
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
                    .background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF141F36) else LocalDynamicThemeColors.current.cardBackground.copy(alpha = 0.85f))
                    .border(
                        if (isNameFocused) 2.dp else 1.2.dp,
                        if (isNameFocused) AmberGold else if (nameText.isNotEmpty()) AmberGold.copy(alpha = 0.6f) else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f),
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
                    textStyle = TextStyle(color = LocalDynamicThemeColors.current.textPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    cursorBrush = SolidColor(AmberGold),
                    decorationBox = { inner ->
                        if (nameText.isEmpty()) {
                            Text(text = "e.g. Vishal Rao", color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.35f), fontSize = 15.sp)
                        }
                        inner()
                    }
                )

                if (nameText.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = LocalDynamicThemeColors.current.placeholder,
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
                    .background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF141F36) else LocalDynamicThemeColors.current.cardBackground.copy(alpha = 0.85f))
                    .border(
                        if (isEmailFocused) 2.dp else 1.2.dp,
                        if (isEmailFocused) AmberGold else if (emailText.isNotEmpty()) AmberGold.copy(alpha = 0.6f) else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f),
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
                    textStyle = TextStyle(color = LocalDynamicThemeColors.current.textPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    cursorBrush = SolidColor(AmberGold),
                    decorationBox = { inner ->
                        if (emailText.isEmpty()) {
                            Text(text = "name@example.com", color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.35f), fontSize = 15.sp)
                        }
                        inner()
                    }
                )

                if (emailText.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = LocalDynamicThemeColors.current.placeholder,
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
                    .background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF141F36) else LocalDynamicThemeColors.current.cardBackground.copy(alpha = 0.85f))
                    .border(
                        if (isPasswordFocused) 2.dp else 1.2.dp,
                        if (isPasswordFocused) AmberGold else if (passwordText.isNotEmpty()) AmberGold.copy(alpha = 0.6f) else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f),
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
                    textStyle = TextStyle(color = LocalDynamicThemeColors.current.textPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    cursorBrush = SolidColor(AmberGold),
                    decorationBox = { inner ->
                        if (passwordText.isEmpty()) {
                            Text(text = "At least 6 characters", color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.35f), fontSize = 15.sp)
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
                AnimatedGIFView(
                    resourceName = "phoenix_flying",
                    size = 80.dp
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
                color = LocalDynamicThemeColors.current.textPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Welcome to AshCrypt, $displayName! Your Phoenix is ready to conquer Cryptography & Network Security.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.70f),
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
                .background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF141F36) else LocalDynamicThemeColors.current.cardBackground.copy(alpha = 0.90f))
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
                    color = LocalDynamicThemeColors.current.textPrimary
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
                    color = LocalDynamicThemeColors.current.textPrimary
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

```

## 10. `app/src/main/java/com/simats/codeingo/ui/onboarding/BeginnerOnboardingFlowView.kt`

```kotlin
package com.simats.codeingo.ui.onboarding

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.R
import com.simats.codeingo.data.model.phoenixEmotion
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.guide.PhoenixPose
import com.simats.codeingo.ui.components.AshButton
import com.simats.codeingo.ui.components.AshButtonStyle
import com.simats.codeingo.ui.components.AshMono
import com.simats.codeingo.ui.components.guide.SpeechBubble
import com.simats.codeingo.ui.components.guide.SpeechBubbleTail
import com.simats.codeingo.ui.phoenix.Phoenix3DLiveWavingMascotView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AshTheme
import com.simats.codeingo.ui.theme.AshType
import com.simats.codeingo.ui.theme.PhoenixMotion
import kotlinx.coroutines.delay

enum class OnboardingStep(val stepIndex: Int) {
    WELCOME(0),
    GOAL_SELECTION(1),
    DAILY_TIME(2),
    PRIOR_LEVEL(3),
    CAESAR_INTRO(4),
    CAESAR_LESSON(5),
    FIRST_SUCCESS(6),
    INITIALIZING(7)
}

/**
 * BeginnerOnboardingFlowView — 8-step guided journey with Ash the Phoenix mascot:
 * 1. Welcome (Ash says hello)
 * 2. Goal Selection (exam / career / curiosity)
 * 3. Daily Time (5 / 10 / 15 min)
 * 4. Prior Level (beginner / a little / comfortable)
 * 5. First Lesson Intro: Caesar's Secret Note
 * 6. Interactive Caesar Shift Dial (solve KHOOR -> HELLO)
 * 7. First Success Celebration (+20 XP & Day 1 streak)
 * 8. Initializing & Transition to Home
 */
@Composable
fun BeginnerOnboardingFlowView(
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(OnboardingStep.WELCOME) }
    var selectedGoal by remember { mutableStateOf<String?>("🎓 Ace University Exams (CS8792 / CNS)") }
    var selectedTimeMinutes by remember { mutableIntStateOf(10) }
    var selectedPriorLevel by remember { mutableStateOf<String?>("🌱 Complete Beginner (Never heard of it)") }

    val totalSteps = OnboardingStep.entries.size
    val currentProgress = (currentStep.stepIndex + 1).toFloat() / totalSteps.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = currentProgress,
        animationSpec = PhoenixMotion.SnappySpring,
        label = "onboardingProgress"
    )

    fun advanceStep() {
        val nextIdx = currentStep.stepIndex + 1
        if (nextIdx < totalSteps) {
            currentStep = OnboardingStep.entries[nextIdx]
        } else {
            // Persist learner preferences per user
            val userId = AuthService.shared.currentUserId ?: "guest"
            val prefs = context.getSharedPreferences("ashcrypt_onboarding_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("user_goal_$userId", selectedGoal)
                .putInt("daily_minutes_$userId", selectedTimeMinutes)
                .putString("prior_level_$userId", selectedPriorLevel)
                .putBoolean("onboarding_completed_$userId", true)
                .apply()

            // Award starting XP and ignite streak
            try {
                GameManager.instance.addXP(20)
            } catch (_: Throwable) {}

            onCompleteOnboarding()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AshTheme.colors.bg)
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Navigation & Progress Bar
            if (currentStep != OnboardingStep.INITIALIZING) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep.stepIndex > 0) {
                        IconButton(
                            onClick = {
                                val prevIdx = currentStep.stepIndex - 1
                                if (prevIdx >= 0) {
                                    currentStep = OnboardingStep.entries[prevIdx]
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Back",
                                tint = AshTheme.colors.ink
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    // Rounded Progress Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(AshTheme.colors.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(AshTheme.colors.brand)
                        )
                    }

                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboardingRouter",
                modifier = Modifier.weight(1f)
            ) { step ->
                when (step) {
                    OnboardingStep.WELCOME -> {
                        WelcomeStep(onContinue = { advanceStep() })
                    }
                    OnboardingStep.GOAL_SELECTION -> {
                        GoalSelectionStep(
                            selected = selectedGoal,
                            onSelect = { selectedGoal = it },
                            onContinue = { advanceStep() }
                        )
                    }
                    OnboardingStep.DAILY_TIME -> {
                        DailyTimeStep(
                            selectedMinutes = selectedTimeMinutes,
                            onSelectMinutes = { selectedTimeMinutes = it },
                            onContinue = { advanceStep() }
                        )
                    }
                    OnboardingStep.PRIOR_LEVEL -> {
                        PriorLevelStep(
                            selected = selectedPriorLevel,
                            onSelect = { selectedPriorLevel = it },
                            onContinue = { advanceStep() }
                        )
                    }
                    OnboardingStep.CAESAR_INTRO -> {
                        CaesarIntroStep(onContinue = { advanceStep() })
                    }
                    OnboardingStep.CAESAR_LESSON -> {
                        CaesarDialLessonStep(onDecodedSuccessfully = { advanceStep() })
                    }
                    OnboardingStep.FIRST_SUCCESS -> {
                        FirstSuccessCelebrationStep(onContinue = { advanceStep() })
                    }
                    OnboardingStep.INITIALIZING -> {
                        InitializingStep(onReady = { advanceStep() })
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 1: WELCOME
// ─────────────────────────────────────────────────────────────────
@Composable
private fun WelcomeStep(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        SpeechBubble(
            text = "Hi, I'm Ash! Your fiery guide to cryptography and network security. Together, we'll turn secret codes into superpowers!",
            actionLabel = "Hi Ash!",
            onActionClick = onContinue,
            tail = SpeechBubbleTail.BOTTOM_LEFT
        )

        Spacer(modifier = Modifier.height(20.dp))

        Phoenix3DLiveWavingMascotView(size = 240.dp)

        Spacer(modifier = Modifier.weight(1f))
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 2: GOAL SELECTION
// ─────────────────────────────────────────────────────────────────
@Composable
private fun GoalSelectionStep(
    selected: String?,
    onSelect: (String) -> Unit,
    onContinue: () -> Unit
) {
    val goals = listOf(
        "🎓 Ace University Exams (CS8792 / CNS)",
        "💼 Tech Career & Security Analyst Jobs",
        "🕵️‍♂️ Curiosity & Ethical Hacking",
        "🧠 Master Modern Number Theory & Ciphers"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        SpeechBubble(
            text = "What brings you to cryptography? I'll tune your study path to match your target.",
            actionLabel = "Continue",
            onActionClick = onContinue,
            tail = SpeechBubbleTail.BOTTOM_LEFT
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            goals.forEach { goal ->
                val isSelected = selected == goal
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) AshTheme.colors.brand.copy(alpha = 0.15f) else AshTheme.colors.surface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) AshTheme.colors.brand else AshTheme.colors.accent.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelect(goal) }
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = goal,
                            style = AshType.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = AshTheme.colors.ink,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AshTheme.colors.brand,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        AshButton(
            text = "Continue",
            onClick = onContinue,
            style = if (selected != null) AshButtonStyle.PRIMARY else AshButtonStyle.OUTLINE,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 3: DAILY TIME
// ─────────────────────────────────────────────────────────────────
@Composable
private fun DailyTimeStep(
    selectedMinutes: Int,
    onSelectMinutes: (Int) -> Unit,
    onContinue: () -> Unit
) {
    val times = listOf(
        Pair(5, "⚡ 5 min / day (Casual Ember)"),
        Pair(10, "🔥 10 min / day (Steady Flame)"),
        Pair(15, "🌟 15 min / day (Blazing Phoenix)")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        SpeechBubble(
            text = "Consistency beats cramming every single time. How much daily time works best for you?",
            actionLabel = "Lock It In",
            onActionClick = onContinue,
            tail = SpeechBubbleTail.BOTTOM_LEFT
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            times.forEach { (mins, label) ->
                val isSelected = selectedMinutes == mins
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) AshTheme.colors.brand.copy(alpha = 0.15f) else AshTheme.colors.surface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) AshTheme.colors.brand else AshTheme.colors.accent.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectMinutes(mins) }
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = AshType.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = AshTheme.colors.ink
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AshTheme.colors.brand,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        AshButton(
            text = "Set Daily Goal",
            onClick = onContinue,
            style = AshButtonStyle.PRIMARY,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 4: PRIOR LEVEL
// ─────────────────────────────────────────────────────────────────
@Composable
private fun PriorLevelStep(
    selected: String?,
    onSelect: (String) -> Unit,
    onContinue: () -> Unit
) {
    val levels = listOf(
        Pair("beginner", "🌱 Complete Beginner\nNever heard of cryptography before"),
        Pair("little", "🔑 A Little Experience\nKnow basic passwords and HTTPS padlocks"),
        Pair("comfortable", "🛡️ Comfortable\nKnow modular math or basic classic ciphers")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        SpeechBubble(
            text = "Where are you starting from? No matter your background, we build from ground zero.",
            actionLabel = "Continue",
            onActionClick = onContinue,
            tail = SpeechBubbleTail.BOTTOM_LEFT
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            levels.forEach { (id, label) ->
                val isSelected = selected?.contains(id, ignoreCase = true) == true
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) AshTheme.colors.brand.copy(alpha = 0.15f) else AshTheme.colors.surface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) AshTheme.colors.brand else AshTheme.colors.accent.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelect(label) }
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = label,
                        style = AshType.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        ),
                        color = AshTheme.colors.ink
                    )
                }
            }
        }

        AshButton(
            text = "Let's Begin!",
            onClick = onContinue,
            style = AshButtonStyle.PRIMARY,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 5: CAESAR INTRO
// ─────────────────────────────────────────────────────────────────
@Composable
private fun CaesarIntroStep(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        SpeechBubble(
            text = "Over 2,000 years ago, Julius Caesar sent secret battle orders by shifting every letter by 3. Let's crack an intercepted message right now!",
            actionLabel = "Decode The Note",
            onActionClick = onContinue,
            tail = SpeechBubbleTail.BOTTOM_LEFT
        )

        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.phoenix_emotion_4), // Curious Ash
            contentDescription = "Ash Curious",
            modifier = Modifier.size(190.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 6: INTERACTIVE CAESAR SHIFT DIAL
// ─────────────────────────────────────────────────────────────────
@Composable
private fun CaesarDialLessonStep(onDecodedSuccessfully: () -> Unit) {
    // Original ciphertext is "KHOOR" (shifted +3 from "HELLO")
    val ciphertext = "KHOOR"
    var shiftDial by remember { mutableIntStateOf(0) } // shift offset applied by user

    // Function shifting character in alphabet
    fun shiftChar(c: Char, shift: Int): Char {
        if (c !in 'A'..'Z') return c
        val originalPos = c - 'A'
        val newPos = (originalPos + shift + 26) % 26
        return 'A' + newPos
    }

    val liveDecoded = ciphertext.map { shiftChar(it, shiftDial) }.joinToString("")
    val isCorrect = liveDecoded == "HELLO"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CAESAR'S SECRET SCROLL",
            style = AshType.labelSmall.copy(
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            ),
            color = AshTheme.colors.brand
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Rotate the dial until the secret message makes sense!",
            style = AshType.bodyMedium.copy(fontSize = 14.sp),
            color = AshTheme.colors.inkMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Intercepted Cipher Display Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(AshTheme.colors.surface)
                .border(2.dp, if (isCorrect) AshTheme.colors.brand else AshTheme.colors.accent.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ORIGINAL CIPHERTEXT: K H O O R",
                    style = AshType.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = AshTheme.colors.inkMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    liveDecoded.forEach { ch ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCorrect) AshTheme.colors.brand else AshTheme.colors.accent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            AshMono(
                                text = ch.toString(),
                                fontSize = 24.sp,
                                color = if (isCorrect) AshTheme.colors.onBrand else AshTheme.colors.ink
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isCorrect) {
                    Text(
                        text = "🎉 MATCH! Message decrypted: HELLO",
                        style = AshType.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        ),
                        color = AshTheme.colors.brand
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Dial Controls
        Text(
            text = "Alphabet Shift: ${if (shiftDial > 0) "+$shiftDial" else "$shiftDial"}",
            style = AshType.titleMedium.copy(
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            ),
            color = AshTheme.colors.ink
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (shiftDial > -6) shiftDial -= 1 },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AshTheme.colors.surface)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Shift Left",
                    tint = AshTheme.colors.brand
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Slider(
                value = shiftDial.toFloat(),
                onValueChange = { shiftDial = it.toInt() },
                valueRange = -6f..6f,
                steps = 11,
                colors = SliderDefaults.colors(
                    thumbColor = AshTheme.colors.brand,
                    activeTrackColor = AshTheme.colors.brand,
                    inactiveTrackColor = AshTheme.colors.surface
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = { if (shiftDial < 6) shiftDial += 1 },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AshTheme.colors.surface)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Shift Right",
                    tint = AshTheme.colors.brand
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AshButton(
            text = if (isCorrect) "DECODE & VERIFY ✓" else "Try Shift -3 (Left 3)",
            onClick = {
                if (isCorrect) {
                    onDecodedSuccessfully()
                } else {
                    shiftDial = -3
                }
            },
            style = if (isCorrect) AshButtonStyle.PRIMARY else AshButtonStyle.OUTLINE,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 7: FIRST SUCCESS CELEBRATION
// ─────────────────────────────────────────────────────────────────
@Composable
private fun FirstSuccessCelebrationStep(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = "Fire",
            tint = AshTheme.colors.brand,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "FIRST CIPHER CRACKED!",
            style = AshType.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 24.sp
            ),
            color = AshTheme.colors.ink
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "+20 XP Earned • 1-Day Streak Ignited",
            style = AshType.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            color = AshTheme.colors.brand
        )

        Spacer(modifier = Modifier.height(20.dp))

        SpeechBubble(
            text = "You cracked your first cryptographic message! Julius Caesar's secret note was indeed 'HELLO'. You're officially a codebreaker!",
            actionLabel = "Enter Sanctuary",
            onActionClick = onContinue,
            tail = SpeechBubbleTail.BOTTOM_LEFT
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.phoenix_emotion_2), // Celebrating Ash
            contentDescription = "Ash Celebrating",
            modifier = Modifier.size(170.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 8: INITIALIZING
// ─────────────────────────────────────────────────────────────────
@Composable
private fun InitializingStep(onReady: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1800)
        onReady()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        CircularProgressIndicator(
            color = AshTheme.colors.brand,
            strokeWidth = 4.dp,
            modifier = Modifier.size(54.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "IGNITING ASHCRYPT...",
            style = AshType.titleMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = AshTheme.colors.ink
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Preparing Realm 1: Secrets 101",
            style = AshType.bodyMedium.copy(fontSize = 13.sp),
            color = AshTheme.colors.inkMuted
        )
    }
}

```

## 11. `app/src/main/java/com/simats/codeingo/ui/onboarding/CreativeTemperatureGaugeView.kt`

```kotlin
package com.simats.codeingo.ui.onboarding

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.components.AppCard
import com.simats.codeingo.ui.components.AppChip
import com.simats.codeingo.ui.phoenix.PhoenixAnimatedMascotView
import com.simats.codeingo.ui.phoenix.PhoenixMascotPose
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.PhoenixMotion
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.shake
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// ══════════════════════════════════════════════════════════════════
// 🌡️ CreativeTemperatureGaugeView — Interactive Memory Pointer Game
// Exact Parity with iOS CreativeTemperatureGaugeView.swift
// Array: nums = [14, 32, 58, 77, 91] -> Find nums[3] == 77 in O(1)
// ══════════════════════════════════════════════════════════════════

@Composable
fun CreativeTemperatureGaugeView(
    modifier: Modifier = Modifier,
    onCorrectAnswer: () -> Unit
) {
    val arrayElements = remember { listOf(14, 32, 58, 77, 91) }
    val targetIndex = 3

    var selectedIndex by remember { mutableIntStateOf(1) }
    var isChecked by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var shakeTrigger by remember { mutableStateOf<Any?>(null) }

    val scope = rememberCoroutineScope()
    val view = LocalView.current
    val isDark = LocalDynamicThemeColors.current.isDark

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val laserPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserPulse"
    )

    val isCurrentCorrect = selectedIndex == targetIndex

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Header: Crypto Challenge & Block Key Setup
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 18.dp,
            accentGlow = AmberGold.copy(alpha = 0.20f),
            contentPadding = 14.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = if (isDark) AmberGold else Color(0xFFB45309),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "CRYPTO ARENA CHALLENGE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = AppFontFamily,
                            color = if (isDark) AmberGold else Color(0xFFB45309),
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00_CD_9C).copy(alpha = 0.18f))
                            .border(1.dp, Color(0xFF00_CD_9C).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "O(1) ACCESS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00_CD_9C)
                        )
                    }
                }

                Text(
                    text = "nums = [14, 32, 58, 77, 91]",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isDark) Color.White else Color(0xFF12_18_26)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Align Memory Pointer to: ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFontFamily,
                        color = if (isDark) Color.White.copy(alpha = 0.70f) else Color(0xFF64_74_8B)
                    )
                    Text(
                        text = "nums[ ? ] == 77",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isDark) AmberGold else Color(0xFFB45309)
                    )
                }
            }
        }

        // 2. Memory Rack Stage with Animated Phoenix Mascot
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contiguous Memory Rack Gauge
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .shake(shakeTrigger),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Active Memory Readout Pill
                val activeColor = if (isChecked) {
                    if (isCorrect) PhoenixGreen else Color(0xFFE8_24_10)
                } else if (isCurrentCorrect) (if (isDark) AmberGold else Color(0xFFB45309)) else Color(0xFF1C_B0_F6)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(activeColor.copy(alpha = 0.18f))
                        .border(1.2.dp, activeColor.copy(alpha = 0.50f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "nums[$selectedIndex] = ${arrayElements[selectedIndex]}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = activeColor
                    )
                }

                // 5 Stacked Array Slots
                arrayElements.forEachIndexed { idx, value ->
                    val isSlotSelected = idx == selectedIndex
                    val isTarget = idx == targetIndex
                    val slotBorderColor = if (isSlotSelected) {
                        if (isChecked) (if (isCorrect) PhoenixGreen else Color(0xFFE8_24_10)) else (if (isDark) AmberGold else Color(0xFFB45309))
                    } else if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFD7_DE_EB)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressScale(targetScale = 0.96f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSlotSelected) activeColor.copy(alpha = if (isDark) 0.25f else 0.18f)
                                else (if (isDark) Color(0xFF14_1F_36).copy(alpha = 0.75f) else Color(0xFFF1_F4_FA))
                            )
                            .border(if (isSlotSelected) 2.dp else 1.dp, slotBorderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isChecked) {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                selectedIndex = idx
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "[$idx]",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSlotSelected) activeColor else (if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF71_80_96))
                        )
                        Text(
                            text = "$value",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSlotSelected) activeColor else (if (isDark) Color.White else Color(0xFF12_18_26))
                        )
                        if (isSlotSelected) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = activeColor,
                                modifier = Modifier
                                    .size(16.dp)
                                    .scale(laserPulse)
                            )
                        } else {
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Mascot Feedback Stage with Pose Reaction
            Column(
                modifier = Modifier.weight(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val currentPose = when {
                    isChecked && isCorrect -> PhoenixMascotPose.Noting(isWriting = false, showTick = true)
                    isChecked && !isCorrect -> PhoenixMascotPose.TemperatureReacting(temp = 0.0, isSuccess = false, isError = true)
                    selectedIndex == targetIndex -> PhoenixMascotPose.Noting(isWriting = false, showTick = false)
                    else -> PhoenixMascotPose.Welcoming
                }

                PhoenixAnimatedMascotView(
                    pose = currentPose,
                    size = 135.dp
                )

                // Dynamic Speech Reaction Bubble
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF14_1E_37).copy(alpha = 0.90f) else Color.White)
                        .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when {
                            isChecked && isCorrect -> "🎯 O(1) Instant Match!"
                            isChecked && !isCorrect -> "⚠️ Look at Index [3]!"
                            selectedIndex == targetIndex -> "🔥 Target 77 locked!"
                            else -> "Pointer at Index [$selectedIndex]"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFontFamily,
                        color = if (isDark) Color.White else Color(0xFF12_18_26),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 3. Stepper & Quick Select Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .pressScale(targetScale = 0.90f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF14_1F_36) else Color(0xFFF1_F5_F9))
                    .border(1.dp, if (isDark) Color.Transparent else Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                    .clickable(enabled = !isChecked && selectedIndex > 0) {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        selectedIndex--
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Prev",
                    tint = if (selectedIndex > 0) (if (isDark) AmberGold else Color(0xFFB45309)) else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Quick Select Index Pills
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (0..4).forEach { i ->
                    AppChip(
                        text = "[$i]",
                        isSelected = selectedIndex == i,
                        onClick = {
                            if (!isChecked) {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                selectedIndex = i
                            }
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .pressScale(targetScale = 0.90f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF14_1F_36) else Color(0xFFF1_F5_F9))
                    .border(1.dp, if (isDark) Color.Transparent else Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                    .clickable(enabled = !isChecked && selectedIndex < arrayElements.size - 1) {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        selectedIndex++
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "Next",
                    tint = if (selectedIndex < arrayElements.size - 1) (if (isDark) AmberGold else Color(0xFFB45309)) else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4. Check / Continue Button & Result Banner
        if (!isChecked) {
            AppButton(
                title = "CHECK ALIGNMENT",
                style = AppButtonStyle.PRIMARY_AMBER,
                onClick = {
                    isChecked = true
                    if (selectedIndex == targetIndex) {
                        isCorrect = true
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    } else {
                        isCorrect = false
                        shakeTrigger = Any()
                    }
                }
            )
        } else {
            if (isCorrect) {
                AppButton(
                    title = "CONTINUE JOURNEY",
                    style = AppButtonStyle.SUCCESS_GREEN,
                    icon = Icons.Default.CheckCircle,
                    onClick = onCorrectAnswer
                )
            } else {
                AppButton(
                    title = "TRY AGAIN",
                    style = AppButtonStyle.DANGER_CRIMSON,
                    onClick = {
                        isChecked = false
                        isCorrect = false
                    }
                )
            }
        }
    }
}

```

## 12. `app/src/main/java/com/simats/codeingo/ui/onboarding/LanguagePickerSheet.kt`

```kotlin
package com.simats.codeingo.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.Language
import com.simats.codeingo.ui.components.AppCard
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.staggeredAppear

// ══════════════════════════════════════════════════════════════════
// 🌐 LanguagePickerSheet — Glassmorphic Language Selector
// ══════════════════════════════════════════════════════════════════

@Composable
fun LanguagePickerSheet(
    selectedLanguageCode: String,
    onLanguageSelected: (Language) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val languages = Language.defaultLanguages
    val isDark = LocalDynamicThemeColors.current.isDark

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
            .background(if (isDark) Color(0xFF0D_14_26) else Color.White)
            .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 32.dp)
    ) {
        // Drag Indicator Pill
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 44.dp, height = 4.dp)
                .clip(RoundedCornerShape(50))
                .background(if (isDark) Color.White.copy(alpha = 0.25f) else Color(0xFFCBD5E1))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "What language do you code in?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = AppFontFamily,
                color = if (isDark) Color.White else Color(0xFF12_18_26),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF12_18_26)
                )
            }
        }

        Text(
            text = "Select your primary language for examples, syntax, and exercises:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = AppFontFamily,
            color = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF64_74_8B),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(languages) { language ->
                val isSelected = language.code.equals(selectedLanguageCode, ignoreCase = true)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pressScale(targetScale = 0.97f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) AmberGold.copy(alpha = if (isDark) 0.22f else 0.15f)
                            else (if (isDark) Color(0xFF14_1F_36).copy(alpha = 0.70f) else Color(0xFFF1_F4_FA))
                        )
                        .border(
                            if (isSelected) 1.8.dp else 1.dp,
                            if (isSelected) AmberGold else (if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFD7_DE_EB)),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onLanguageSelected(language)
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = language.flagEmoji,
                        fontSize = 26.sp,
                        modifier = Modifier.padding(end = 14.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = language.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppFontFamily,
                            color = if (isDark) Color.White else Color(0xFF12_18_26)
                        )
                        Text(
                            text = language.nativeName,
                            fontSize = 12.sp,
                            fontFamily = AppFontFamily,
                            color = if (isDark) Color.White.copy(alpha = 0.60f) else Color(0xFF64_74_8B)
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = AmberGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

```

## 13. `app/src/main/java/com/simats/codeingo/ui/onboarding/OnboardingScreen.kt`

```kotlin
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
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
import com.simats.codeingo.ui.components.AshButton
import com.simats.codeingo.ui.components.AshButtonStyle
import com.simats.codeingo.ui.phoenix.AnimatedGIFView
import com.simats.codeingo.ui.components.HeaderView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AmberGoldDark
import com.simats.codeingo.ui.theme.AmberGoldText
import com.simats.codeingo.ui.theme.AppFontFamily
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
            .background(LocalDynamicThemeColors.current.background)
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
                    .background(LocalDynamicThemeColors.current.cardBackground.copy(alpha = 0.95f))
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
                            icon = Icons.Default.Lock,
                            badgeText = "CIPHERS & CODES",
                            badgeColor = AmberGoldText,
                            title = "Crack the Secrets",
                            subtitle = "Watch secrets turn into scrambled text, then crack them yourself."
                        )
                        2 -> FeatureSlide(
                            icon = Icons.Default.Shield,
                            badgeText = "CYBER BATTLES",
                            badgeColor = Color(0xFFFF731A),
                            title = "5 Threat Arenas",
                            subtitle = "Battle hackers in 5 arenas and protect the network."
                        )
                        3 -> FeatureSlide(
                            icon = Icons.Default.Bolt,
                            badgeText = "DAILY HABIT",
                            badgeColor = AmberGoldText,
                            title = "Daily Power-Up",
                            subtitle = "Just 5 minutes a day. Ready?"
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
                com.simats.codeingo.ui.components.AppButton(
                    title = localizationManager.string("get_started_caps"),
                    style = com.simats.codeingo.ui.components.AppButtonStyle.PRIMARY_AMBER,
                    onClick = {
                        showBeginnerOnboarding = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // I ALREADY HAVE AN ACCOUNT — 3D Secondary Glass Button
                com.simats.codeingo.ui.components.AppButton(
                    title = localizationManager.string("already_have_account_caps"),
                    style = com.simats.codeingo.ui.components.AppButtonStyle.SECONDARY_GLASS,
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
                containerColor = LocalDynamicThemeColors.current.cardBackground
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
            text = "Meet Ash",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = LocalDynamicThemeColors.current.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "I'll guide you through the secret world of codes.",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.80f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
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
            val isDark = LocalDynamicThemeColors.current.isDark
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.White)
                    .border(
                        2.dp,
                        Brush.linearGradient(
                            if (isDark) listOf(LocalDynamicThemeColors.current.textSecondary, badgeColor.copy(alpha = 0.5f))
                            else listOf(Color(0xFFCBD5E1), badgeColor.copy(alpha = 0.6f))
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
                .background(badgeColor.copy(alpha = if (LocalDynamicThemeColors.current.isDark) 0.18f else 0.12f))
                .border(1.dp, badgeColor.copy(alpha = 0.40f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text = badgeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = AppFontFamily,
                color = badgeColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            fontFamily = AppFontFamily,
            color = LocalDynamicThemeColors.current.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = AppFontFamily,
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
    val isDark = LocalDynamicThemeColors.current.isDark
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pageCount) {
            val isSelected = i == currentPage
            val width = if (isSelected) 22.dp else 7.dp
            val color = if (isSelected) (if (isDark) AmberGold else Color(0xFFB45309))
                else (if (isDark) LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.25f) else Color(0xFFCBD5E1))

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

```

## 14. `app/src/main/java/com/simats/codeingo/ui/splash/SplashScreen.kt`

```kotlin
package com.simats.codeingo.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.phoenix.SmoothFlyingPhoenixView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════
// 🌌 SplashScreen — Exact Parity with iOS SplashScreenView.swift
// 3D Glossy Ember Particles, Physics Timeline, Spiral Vortex,
// Kinetic Energy Squeeze, Dual Shockwaves & Elastic Landing
// ══════════════════════════════════════════════════════════════════

private data class RealisticSplashBubble(
    val baseAngle: Double,
    val maxDistance: Float,
    val size: Float,
    val color: Color,
    val swirlDirection: Double
)

@Composable
fun SplashScreen(
    onFinishedSplash: () -> Unit,
    modifier: Modifier = Modifier
) {
    var elapsedTimeSec by remember { mutableFloatStateOf(0f) }

    // Pre-generate 32 realistic 3D glossy fiery ember particles (Phoenix Palette)
    val bubbles = remember {
        val colors = listOf(
            AmberGold,
            Color(0xFFFF7A1A),
            Color(0xFFFFC733),
            Color(0xFFF2401F),
            Color(0xFFFF9E0D),
            Color.White,
            Color(0xFFFF590D)
        )
        (0 until 32).map { i ->
            val angle = (i.toDouble() / 32.0) * 2.0 * PI + (Math.random() - 0.5) * 0.24
            val distance = (110f + Math.random().toFloat() * 110f)
            val size = (14f + Math.random().toFloat() * 22f)
            val color = colors[i % colors.size]
            val swirlDir = if (i % 2 == 0) 1.0 else -1.0
            RealisticSplashBubble(
                baseAngle = angle,
                maxDistance = distance,
                size = size,
                color = color,
                swirlDirection = swirlDir
            )
        }
    }

    LaunchedEffect(Unit) {
        val startNanos = System.nanoTime()
        while (true) {
            withFrameNanos { frameTimeNanos ->
                elapsedTimeSec = (frameTimeNanos - startNanos) / 1_000_000_000f
            }
            if (elapsedTimeSec >= 3.5f) {
                onFinishedSplash()
                break
            }
        }
    }

    val t = elapsedTimeSec

    // Phase 0: Distance Fly-In [0.0 ... 0.45s]
    val p0 = min(1.0f, max(0.0f, t / 0.45f))
    val easeFlyIn = 1.0f - (1.0f - p0).pow(3)

    // Phase 0.5: Kinetic Energy Squeeze [0.45s ... 0.62s]
    val pSqueeze = min(1.0f, max(0.0f, (t - 0.45f) / 0.17f))
    val squeezeAmount = sin(pSqueeze * PI.toFloat())

    // Phase 1: Explosion Blast into 3D Embers [0.62s ... 1.25s]
    val pBlast = min(1.0f, max(0.0f, (t - 0.62f) / 0.58f))
    val easeBlast = 1.0f - (1.0f - pBlast).pow(2.8f)

    // Phase 2: Spiral Vortex Magnetic Fusion [1.25s ... 1.95s]
    val pVortex = min(1.0f, max(0.0f, (t - 1.25f) / 0.70f))
    val easeVortex = pVortex.pow(2.2f)

    // Phase 3: Fluid Mascot Re-Formation & Elastic Landing [1.95s ... 3.5s]
    val pLanding = min(1.0f, max(0.0f, (t - 1.95f) / 0.55f))
    val springBounce = (exp(-5.5f * pLanding) * cos(14.0f * pLanding))

    // Continuous Floating Hover Loop
    val hoverTime = max(0f, t - 2.45f)
    val hoverY = sin(hoverTime * 3.5f) * 7.0f
    val hoverRot = sin(hoverTime * 2.8f) * 3.5f

    // Mascot Character Transformations
    val mascotYOffset: Float = when {
        t < 0.45f -> -380.0f * (1.0f - easeFlyIn)
        t < 0.62f -> 14.0f * squeezeAmount
        t < 0.82f -> -10.0f * (t - 0.62f) / 0.20f
        t < 1.95f -> -5.0f * sin(((t - 0.82f) / 1.13f) * PI.toFloat())
        else -> -14.0f * (1.0f - springBounce) + hoverY
    }

    val mascotScaleX: Float = when {
        t < 0.45f -> 0.20f + 0.85f * easeFlyIn
        t < 0.62f -> 1.05f + 0.28f * squeezeAmount
        t < 0.78f -> 1.25f - (t - 0.62f) * 2.0f
        t < 1.95f -> 0.88f + 0.12f * sin(((t - 0.78f) / 1.17f) * PI.toFloat())
        else -> 1.0f + 0.20f * springBounce
    }

    val mascotScaleY: Float = when {
        t < 0.45f -> 0.20f + 0.85f * easeFlyIn
        t < 0.62f -> 1.05f - 0.32f * squeezeAmount
        t < 0.78f -> 0.85f + (t - 0.62f) * 2.0f
        t < 1.95f -> 0.88f + 0.12f * sin(((t - 0.78f) / 1.17f) * PI.toFloat())
        else -> 1.0f - 0.20f * springBounce
    }

    val mascotOpacity: Float = when {
        t < 0.15f -> t / 0.15f
        t < 0.62f -> 1.0f
        t < 1.25f -> max(0.65f, 1.0f - pBlast * 0.35f)
        t < 1.95f -> min(1.0f, 0.65f + easeVortex * 0.35f)
        else -> 1.0f
    }

    val mascotRotation: Float = when {
        t < 0.45f -> -25.0f * (1.0f - easeFlyIn)
        t > 2.45f -> hoverRot
        else -> 0.0f
    }

    // Shockwaves
    val sw1Scale = if (t in 0.62f..1.15f) 0.2f + 2.2f * ((t - 0.62f) / 0.53f) else 0.1f
    val sw1Opacity = if (t in 0.62f..1.15f) sin(((t - 0.62f) / 0.53f) * PI.toFloat()) * 0.65f else 0.0f

    val sw2Scale = if (t in 0.65f..1.25f) 0.1f + 2.6f * ((t - 0.65f) / 0.60f) else 0.1f
    val sw2Opacity = if (t in 0.65f..1.25f) sin(((t - 0.65f) / 0.60f) * PI.toFloat()) * 0.45f else 0.0f

    // Central Energy Orb
    val orbProgress = min(1.0f, max(0.0f, (t - 1.65f) / 0.35f))
    val orbScale = sin(orbProgress * PI.toFloat()) * 1.8f
    val orbOpacity = sin(orbProgress * PI.toFloat())

    // Logo & Progress
    val logoOpacity = min(1.0f, max(0.0f, t / 0.40f))
    val logoScale = 0.90f + 0.10f * min(1.0f, max(0.0f, (t - 1.95f) / 0.45f))
    val logoYOffset = if (t < 1.95f) 0.0f else 4.0f * sin(min(1.0f, (t - 1.95f) / 0.35f) * PI.toFloat())
    val progressRatio = min(1.0f, t / 3.3f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F_0A_1A),
                        Color(0xFF3D_14_08),
                        Color(0xFF12_0D_21)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Stage: Mascot + Embers + Shockwaves
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(260.dp)
            ) {
                // Ground Soft Shadow
                Box(
                    modifier = Modifier
                        .size(width = 140.dp, height = 26.dp)
                        .offset(y = 85.dp)
                        .scale(if (t < 0.45f) 0.05f + 0.95f * easeFlyIn else 1.0f)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .blur(8.dp)
                )

                // Canvas for Shockwaves & 3D Glossy Ember Particles
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerOffset = Offset(size.width / 2f, size.height / 2f)

                    // Shockwave 1
                    if (sw1Opacity > 0f) {
                        drawCircle(
                            color = AmberGold.copy(alpha = sw1Opacity),
                            radius = (95.dp.toPx()) * sw1Scale,
                            center = centerOffset,
                            style = Stroke(width = 6.dp.toPx())
                        )
                    }

                    // Shockwave 2
                    if (sw2Opacity > 0f) {
                        drawCircle(
                            color = Color(0xFFFF7A1A).copy(alpha = sw2Opacity),
                            radius = (95.dp.toPx()) * sw2Scale,
                            center = centerOffset,
                            style = Stroke(width = 4.dp.toPx())
                        )
                    }

                    // 32 3D Glossy Ember Particles
                    for (b in bubbles) {
                        val (dist, angle, scale, opacity) = when {
                            t < 0.62f -> listOf(0f, b.baseAngle.toFloat(), 0f, 0f)
                            t < 1.25f -> {
                                val d = b.maxDistance * easeBlast
                                val s = sin(pBlast * (PI.toFloat() / 2f)) * 1.15f
                                val op = min(1.0f, pBlast * 4f)
                                listOf(d, b.baseAngle.toFloat(), s, op)
                            }
                            t < 1.95f -> {
                                val d = b.maxDistance * (1.0f - easeVortex)
                                val swirlAngle = (b.baseAngle + b.swirlDirection * easeVortex * PI * 2.2).toFloat()
                                val s = 1.15f * (1.0f - easeVortex * 0.75f)
                                val op = max(0.0f, 1.0f - pVortex.pow(2.5f))
                                listOf(d, swirlAngle, s, op)
                            }
                            else -> listOf(0f, b.baseAngle.toFloat(), 0f, 0f)
                        }

                        if (opacity > 0f && scale > 0f) {
                            val px = centerOffset.x + cos(angle) * dist * density
                            val py = centerOffset.y + sin(angle) * dist * density
                            val radius = (b.size / 2f) * scale * density

                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        b.color.copy(alpha = opacity * 0.98f),
                                        b.color.copy(alpha = opacity * 0.65f)
                                    ),
                                    center = Offset(px - radius * 0.3f, py - radius * 0.3f),
                                    radius = radius * 1.2f
                                ),
                                radius = radius,
                                center = Offset(px, py)
                            )
                            // Specular Top-Left Highlight
                            drawCircle(
                                color = Color.White.copy(alpha = opacity * 0.85f),
                                radius = radius * 0.35f,
                                center = Offset(px - radius * 0.35f, py - radius * 0.35f)
                            )
                        }
                    }

                    // Central Energy Orb
                    if (orbOpacity > 0f) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White, AmberGold, Color(0xFFFF590D)),
                                center = centerOffset,
                                radius = 40.dp.toPx() * orbScale
                            ),
                            radius = 35.dp.toPx() * orbScale,
                            center = centerOffset,
                            alpha = orbOpacity
                        )
                    }
                }

                // Smooth Flying Phoenix Mascot Character
                Box(
                    modifier = Modifier
                        .size(205.dp)
                        .graphicsLayer {
                            scaleX = mascotScaleX
                            scaleY = mascotScaleY
                            translationY = mascotYOffset * density
                            rotationZ = mascotRotation
                            this.alpha = mascotOpacity
                        }
                ) {
                    SmoothFlyingPhoenixView(size = 205.dp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Brand Title "AshCrypt"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    scaleX = logoScale
                    scaleY = logoScale
                    translationY = logoYOffset * density
                    this.alpha = logoOpacity
                }
            ) {
                Text(
                    text = "AshCrypt",
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = AppFontFamily,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Master Cryptography & Network Security",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFontFamily,
                    color = AmberGold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Glowing Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.12f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressRatio)
                        .height(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(AmberGold, Color(0xFFFF8C1A))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

```

