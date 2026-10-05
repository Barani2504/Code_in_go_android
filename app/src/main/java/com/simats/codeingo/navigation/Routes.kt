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
    object Letters : Screen("letters")
    object Achievements : Screen("achievements")
    object Settings : Screen("settings")
}
