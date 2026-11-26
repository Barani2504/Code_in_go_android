package com.simats.codeingo.navigation

import com.simats.codeingo.data.network.repository.LessonIdAdapter

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object ProfileCreation : Screen("profile_creation")
    object Dashboard : Screen("dashboard")
    object Assessment : Screen("assessment/{unitId}/{levelNumber}/{totalLevelsInUnit}/{isBoss}") {
        fun createRoute(unitId: Int, levelNumber: Int, totalLevelsInUnit: Int = 6, isBoss: Boolean = false) =
            "assessment/$unitId/$levelNumber/$totalLevelsInUnit/$isBoss"

        fun createRoute(lessonId: String): String {
            val localIndex = LessonIdAdapter.toLocalLevelIndex(lessonId) ?: 1
            return if (localIndex in 101..105) {
                val stage = localIndex - 100
                createRoute(unitId = 0, levelNumber = stage, totalLevelsInUnit = 5, isBoss = stage == 5)
            } else {
                val unitId = ((localIndex - 1) / 5) + 1
                val stageInUnit = ((localIndex - 1) % 5) + 1
                createRoute(unitId = unitId, levelNumber = localIndex, totalLevelsInUnit = 5, isBoss = stageInUnit == 5)
            }
        }
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
