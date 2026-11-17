package com.simats.codeingo.domain.guide

import android.content.Context
import com.simats.codeingo.data.model.phoenixEmotion

/**
 * Poses for Ash the Phoenix Guide.
 * Maps directly to emotional states in [PhoenixEmotionManager].
 */
enum class PhoenixPose(val emotionId: Int, val displayName: String) {
    IDLE(0, "Calm Ash"),
    CURIOUS(4, "Curious Ash"),
    HAPPY(7, "Happy Ash"),
    PROUD(3, "Proud Ash"),
    THINKING(11, "Thinking Ash"),
    SAD(16, "Encouraging Ash"),
    SLEEPY(14, "Resting Ash"),
    CELEBRATING(2, "Celebrating Ash")
}

/**
 * Single step in an Ash guide script.
 * Plain English reading level ~ Grade 8, <= 18 words per line.
 */
data class GuideStep(
    val id: String,
    val pose: PhoenixPose,
    val line: String,
    val targetTag: String? = null,
    val actionLabel: String = "Next",
    val canSkip: Boolean = true
)

/**
 * Multi-step scripted tour guided by Ash.
 */
data class GuideScript(
    val id: String,
    val title: String,
    val steps: List<GuideStep>
)

/**
 * Director managing guide scripts, tours, and per-user progression persistence.
 * All persistence keys are isolated per user ID.
 */
object PhoenixGuideDirector {

    private const val PREFS_NAME = "ashcrypt_guide_prefs"
    private const val KEY_PREFIX = "guide_seen"

    // ─────────────────────────────────────────────────────────────────
    // TOUR SCRIPTS
    // ─────────────────────────────────────────────────────────────────

    val HOME_TOUR = GuideScript(
        id = "HOME_TOUR",
        title = "Home & Learning Path Tour",
        steps = listOf(
            GuideStep(
                id = "home_step_path",
                pose = PhoenixPose.CURIOUS,
                line = "This is your learning path! Each realm takes you deeper into cryptography and network security.",
                targetTag = "tour_path",
                actionLabel = "Next"
            ),
            GuideStep(
                id = "home_step_continue",
                pose = PhoenixPose.HAPPY,
                line = "Tap this button anytime to jump right into your next interactive lesson or lab.",
                targetTag = "tour_continue_btn",
                actionLabel = "Got it"
            ),
            GuideStep(
                id = "home_step_streak",
                pose = PhoenixPose.PROUD,
                line = "Your daily streak flame! Practice every day to keep your cybersecurity fire burning bright.",
                targetTag = "tour_streak",
                actionLabel = "Next"
            ),
            GuideStep(
                id = "home_step_hearts",
                pose = PhoenixPose.HAPPY,
                line = "Hearts protect your trial runs. Making mistakes is how we learn, so never fear trying!",
                targetTag = "tour_hearts",
                actionLabel = "Next"
            ),
            GuideStep(
                id = "home_step_bottom_nav",
                pose = PhoenixPose.CELEBRATING,
                line = "Use these tabs below to visit Visualizer Labs, Threat Arenas, Reforge practice, and your Profile!",
                targetTag = "tour_bottom_nav",
                actionLabel = "Let's Go!"
            )
        )
    )

    val VISUALIZER_TOUR = GuideScript(
        id = "VISUALIZER_TOUR",
        title = "Visualizer Labs Tour",
        steps = listOf(
            GuideStep(
                id = "vis_step_scaffold",
                pose = PhoenixPose.CURIOUS,
                line = "Welcome to the Labs! Here you can watch encryption algorithms transform bytes in real time.",
                targetTag = "tour_vis_scaffold",
                actionLabel = "Show me"
            ),
            GuideStep(
                id = "vis_step_controls",
                pose = PhoenixPose.THINKING,
                line = "Step forward or backward through each cipher operation at your own comfortable pace.",
                targetTag = "tour_vis_controls",
                actionLabel = "Got it"
            ),
            GuideStep(
                id = "vis_step_narrator",
                pose = PhoenixPose.PROUD,
                line = "I'll explain every key schedule and permutation step right here in plain English!",
                targetTag = "tour_vis_explanation",
                actionLabel = "Start Exploring"
            )
        )
    )

    val ARENA_TOUR = GuideScript(
        id = "ARENA_TOUR",
        title = "Threat Arenas Tour",
        steps = listOf(
            GuideStep(
                id = "arena_step_brief",
                pose = PhoenixPose.CURIOUS,
                line = "Welcome to the Threat Arenas! Test your instincts under simulated cybersecurity attacks.",
                targetTag = "tour_arena_brief",
                actionLabel = "Next"
            ),
            GuideStep(
                id = "arena_step_levels",
                pose = PhoenixPose.PROUD,
                line = "Select a scenario to intercept ciphers, patch protocol flaws, and defend the network!",
                targetTag = "tour_arena_levels",
                actionLabel = "Enter Arena"
            )
        )
    )

    val PRACTICE_TOUR = GuideScript(
        id = "PRACTICE_TOUR",
        title = "Reforge Practice Tour",
        steps = listOf(
            GuideStep(
                id = "practice_step_cards",
                pose = PhoenixPose.HAPPY,
                line = "Reforge fragile knowledge here. Regular retrieval practice turns concepts into permanent reflexes!",
                targetTag = "tour_practice_cards",
                actionLabel = "Let's Practice"
            )
        )
    )

    val LEADERBOARD_TOUR = GuideScript(
        id = "LEADERBOARD_TOUR",
        title = "Leaderboards Tour",
        steps = listOf(
            GuideStep(
                id = "leaderboard_step_list",
                pose = PhoenixPose.PROUD,
                line = "Rise through cryptographic leagues as you earn XP and build an unstoppable daily streak!",
                targetTag = "tour_leaderboard_list",
                actionLabel = "Understood"
            )
        )
    )

    val SHOP_TOUR = GuideScript(
        id = "SHOP_TOUR",
        title = "Gem Shop Tour",
        steps = listOf(
            GuideStep(
                id = "shop_step_items",
                pose = PhoenixPose.HAPPY,
                line = "Spend earned gems on heart refills, streak freezes, and mythical Phoenix cosmetics!",
                targetTag = "tour_shop_items",
                actionLabel = "Browse Shop"
            )
        )
    )

    val PROFILE_TOUR = GuideScript(
        id = "PROFILE_TOUR",
        title = "Security Profile Tour",
        steps = listOf(
            GuideStep(
                id = "profile_step_stats",
                pose = PhoenixPose.PROUD,
                line = "Track your cryptographic mastery, badges, accuracy rate, and security certificates here.",
                targetTag = "tour_profile_stats",
                actionLabel = "View Profile"
            )
        )
    )

    val ALL_TOURS = listOf(
        HOME_TOUR,
        VISUALIZER_TOUR,
        ARENA_TOUR,
        PRACTICE_TOUR,
        LEADERBOARD_TOUR,
        SHOP_TOUR,
        PROFILE_TOUR
    )

    // ─────────────────────────────────────────────────────────────────
    // PER-USER PERSISTENCE
    // ─────────────────────────────────────────────────────────────────

    private fun userKey(scriptId: String, userId: String?): String {
        val safeId = if (userId.isNullOrBlank()) "guest" else userId
        return "${KEY_PREFIX}_${scriptId}_${safeId}"
    }

    fun isTourSeen(prefs: android.content.SharedPreferences, scriptId: String, userId: String?): Boolean {
        return prefs.getBoolean(userKey(scriptId, userId), false)
    }

    fun isTourSeen(context: Context, scriptId: String, userId: String?): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return isTourSeen(prefs, scriptId, userId)
    }

    fun markTourSeen(prefs: android.content.SharedPreferences, scriptId: String, userId: String?, seen: Boolean = true) {
        prefs.edit().putBoolean(userKey(scriptId, userId), seen).apply()
    }

    fun markTourSeen(context: Context, scriptId: String, userId: String?, seen: Boolean = true) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        markTourSeen(prefs, scriptId, userId, seen)
    }

    fun resetTour(prefs: android.content.SharedPreferences, scriptId: String, userId: String?) {
        markTourSeen(prefs, scriptId, userId, false)
    }

    fun resetTour(context: Context, scriptId: String, userId: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        resetTour(prefs, scriptId, userId)
    }

    fun resetAllTours(prefs: android.content.SharedPreferences, userId: String?) {
        val editor = prefs.edit()
        ALL_TOURS.forEach { tour ->
            editor.remove(userKey(tour.id, userId))
        }
        editor.apply()
    }

    fun resetAllTours(context: Context, userId: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        resetAllTours(prefs, userId)
    }

    fun getTourById(scriptId: String): GuideScript? {
        return ALL_TOURS.firstOrNull { it.id == scriptId }
    }
}
