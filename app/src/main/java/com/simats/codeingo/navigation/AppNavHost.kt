package com.simats.codeingo.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.auth.LoginScreen
import com.simats.codeingo.ui.auth.ProfileCreationScreen
import com.simats.codeingo.ui.dashboard.MainDashboardScreen
import com.simats.codeingo.ui.exercise.AssessmentScreen
import com.simats.codeingo.ui.exercise.BossBattleScreen
import com.simats.codeingo.ui.onboarding.OnboardingScreen
import com.simats.codeingo.ui.phoenix.PhoenixEvolutionCelebrationScreen
import com.simats.codeingo.ui.phoenix.PhoenixSanctuaryScreen
import com.simats.codeingo.ui.settings.SettingsScreen
import com.simats.codeingo.ui.splash.SplashScreen

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
            startDestination = Screen.Splash.route
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onFinishedSplash = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onGetStarted = {
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
                    onStartLesson = { lessonId ->
                        navController.navigate(Screen.Assessment.createRoute(lessonId))
                    },
                    onStartBoss = { bossId ->
                        navController.navigate(Screen.BossBattle.createRoute(bossId))
                    },
                    onOpenPhoenixSanctuary = {
                        navController.navigate(Screen.PhoenixSanctuary.route)
                    },
                    onOpenSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onLogout = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.Assessment.route,
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: "u1_l1_what_is_ds"
                AssessmentScreen(
                    lessonId = lessonId,
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

            composable(Screen.PhoenixSanctuary.route) {
                PhoenixSanctuaryScreen(
                    onDismiss = {
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
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
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
