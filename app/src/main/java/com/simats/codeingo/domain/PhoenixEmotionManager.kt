package com.simats.codeingo.domain

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.simats.codeingo.data.model.PhoenixEmotion
import com.simats.codeingo.data.model.allPhoenixEmotions
import com.simats.codeingo.data.model.phoenixEmotion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import android.widget.Toast
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

// ══════════════════════════════════════════════════════════════════
// 📜 Emotion-Based App Icon & Mascot Rules (Exact Parity with iOS)
// ══════════════════════════════════════════════════════════════════
enum class EmotionRule(val rawValue: String) {
    STREAK_MILESTONE("Streak Mastery"),
    DAILY_URGENCY_NIGHT("Streak In Danger (Night)"),
    DAILY_URGENCY_EVE("Streak At Risk (Evening)"),
    DAILY_NOT_PRACTICED("Not Practiced Today"),
    DAILY_GOAL_ACHIEVED("Daily Goal Completed"),
    ZERO_HEARTS("0 Hearts (Out of Health)"),
    LOW_HEARTS("1 Heart (Critical Danger)"),
    HEARTS_REFILLED("Hearts Fully Refilled"),
    PERFECT_SCORE("100% Perfect Quiz Score"),
    HIGH_COMBO("5x Eureka Combo"),
    LEADERBOARD_TOP("Crown Champion (#1)"),
    BOSS_BATTLE("Boss Battle Encounter"),
    MANUAL_CUSTOM("Manual User Selection");

    val id: String get() = rawValue

    val targetEmotionId: Int
        get() = when (this) {
            STREAK_MILESTONE -> 6    // Starry Prodigy 🤩
            DAILY_URGENCY_NIGHT -> 23 // Furious Warning 💢
            DAILY_URGENCY_EVE -> 15   // Worried Ember 🥺
            DAILY_NOT_PRACTICED -> 18 // Pouting Grudge 😒
            DAILY_GOAL_ACHIEVED -> 6  // Starry Prodigy 🤩
            ZERO_HEARTS -> 17         // Heartbroken Cry 😭
            LOW_HEARTS -> 19          // Relieved Sigh 😮‍💨
            HEARTS_REFILLED -> 21     // Grateful Tears 🥹
            PERFECT_SCORE -> 6        // Starry Prodigy 🤩
            HIGH_COMBO -> 13          // Eureka Spark 💡
            LEADERBOARD_TOP -> 26     // Crown Champion 👑
            BOSS_BATTLE -> 28         // Battle Standard 🚩
            MANUAL_CUSTOM -> 0        // Default
        }

    val badgeEmoji: String
        get() = when (this) {
            STREAK_MILESTONE -> "🤩"
            DAILY_URGENCY_NIGHT -> "💢"
            DAILY_URGENCY_EVE -> "🥺"
            DAILY_NOT_PRACTICED -> "😒"
            DAILY_GOAL_ACHIEVED -> "🎉"
            ZERO_HEARTS -> "😭"
            LOW_HEARTS -> "😮‍💨"
            HEARTS_REFILLED -> "🥹"
            PERFECT_SCORE -> "🏆"
            HIGH_COMBO -> "💡"
            LEADERBOARD_TOP -> "👑"
            BOSS_BATTLE -> "🚩"
            MANUAL_CUSTOM -> "🎨"
        }

    val summary: String
        get() = when (this) {
            STREAK_MILESTONE -> "7+ Day Streak: Starry Prodigy with diamond aura"
            DAILY_URGENCY_NIGHT -> "9 PM+ & Not Practiced: Furious Blaze urges you to save streak"
            DAILY_URGENCY_EVE -> "5 PM - 9 PM & Not Practiced: Worried Ember misses you"
            DAILY_NOT_PRACTICED -> "Daytime & Not Practiced: Pouting Phoenix awaits your code"
            DAILY_GOAL_ACHIEVED -> "Daily Lesson Completed: Shining Starry Prodigy celebration"
            ZERO_HEARTS -> "0 Hearts Remaining: Heartbroken Cry begging for energy"
            LOW_HEARTS -> "1 Heart Left: Relieved Sigh high-stakes danger"
            HEARTS_REFILLED -> "Hearts Fully Refilled: Radiant tears of gratitude"
            PERFECT_SCORE -> "100% Quiz Accuracy: Legendary ecstatic celebration"
            HIGH_COMBO -> "5x Correct Answers: Eureka Lightbulb brilliant spark"
            LEADERBOARD_TOP -> "Rank #1 Leaderboard: Crown Champion royal cape"
            BOSS_BATTLE -> "Active Boss Battle: Battle Standard war flame"
            MANUAL_CUSTOM -> "Custom icon selected from the 28-emotion catalog"
        }
}

// ══════════════════════════════════════════════════════════════════
// 🕊️ PhoenixEmotionManager — Centralized Reactive Mascot Manager
// ══════════════════════════════════════════════════════════════════
class PhoenixEmotionManager private constructor() {

    companion object {
        val instance: PhoenixEmotionManager by lazy { PhoenixEmotionManager() }
        val shared: PhoenixEmotionManager get() = instance
        var isSuppressingIconAlert: Boolean = false
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Optional application context for icon alias switching
    private var appContext: Context? = null

    fun initialize(context: Context) {
        this.appContext = context.applicationContext
        try {
            val prefs = this.appContext?.getSharedPreferences("phoenix_icon_prefs", Context.MODE_PRIVATE)
            val savedEmotionId = prefs?.getInt("active_emotion_id", -1) ?: -1
            if (savedEmotionId != -1) {
                val savedEmotion = phoenixEmotion(savedEmotionId)
                _currentEmotion.value = savedEmotion
                _lastAppliedIconName.value = "MainActivityAlias_$savedEmotionId"
            }
        } catch (e: Exception) {
            Log.w("PhoenixEmotion", "Error restoring saved icon state: ${e.message}")
        }
    }

    // Reactive State
    private val _currentEmotion = MutableStateFlow(phoenixEmotion(0))
    val currentEmotion: StateFlow<PhoenixEmotion> = _currentEmotion.asStateFlow()

    private val _activeRule = MutableStateFlow(EmotionRule.STREAK_MILESTONE)
    val activeRule: StateFlow<EmotionRule> = _activeRule.asStateFlow()

    private val _isAutoEmotionEnabled = MutableStateFlow(true)
    val isAutoEmotionEnabled: StateFlow<Boolean> = _isAutoEmotionEnabled.asStateFlow()

    private val _lastTriggerReason = MutableStateFlow("App Launch")
    val lastTriggerReason: StateFlow<String> = _lastTriggerReason.asStateFlow()

    private val _lastSwitchedAt = MutableStateFlow(System.currentTimeMillis())
    val lastSwitchedAt: StateFlow<Long> = _lastSwitchedAt.asStateFlow()

    private val _showReactionToast = MutableStateFlow(false)
    val showReactionToast: StateFlow<Boolean> = _showReactionToast.asStateFlow()

    private val _isSheetPresented = MutableStateFlow(false)
    val isSheetPresented: StateFlow<Boolean> = _isSheetPresented.asStateFlow()

    private val _lastAppliedIconName = MutableStateFlow("AppIcon_0")
    val lastAppliedIconName: StateFlow<String> = _lastAppliedIconName.asStateFlow()

    private val _isInQuiz = MutableStateFlow(false)
    val isInQuiz: StateFlow<Boolean> = _isInQuiz.asStateFlow()

    // Internal timing & debounce
    private var lastLifecycleCallTimestamp: Long = 0
    private var toastDismissJob: Job? = null
    private var idleTimerJob: Job? = null
    private var livingCompanionJob: Job? = null

    private val companionEmotionIds = listOf(
        0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28
    )
    private var companionCycleIndex = 0

    init {
        startInAppIdleMonitor()
        startLivingCompanionEngine()
        evaluateEmotionRules(forceIconUpdate = true)
    }

    // MARK: - 👑 CORE EMOTION-BASED RULE ENGINE
    fun evaluateEmotionRules(forceIconUpdate: Boolean = true): Pair<PhoenixEmotion, EmotionRule> {
        if (!_isAutoEmotionEnabled.value) {
            return Pair(_currentEmotion.value, EmotionRule.MANUAL_CUSTOM)
        }

        val gameManager = GameManager.instance
        val hasPracticed = gameManager.hasPracticedToday
        val currentStreak = gameManager.streakDays.value
        val currentHearts = gameManager.heartsCount.value
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val targetRule: EmotionRule
        val targetEmotionId: Int

        // Rule Priority 1: Critical Health (0 Hearts Remaining)
        if (currentHearts <= 0) {
            targetRule = EmotionRule.ZERO_HEARTS
            targetEmotionId = 17 // Heartbroken Cry 😭
        }
        // Rule Priority 2: Critical Danger (1 Heart Left)
        else if (currentHearts == 1) {
            targetRule = EmotionRule.LOW_HEARTS
            targetEmotionId = 19 // Relieved Sigh 😮‍💨
        }
        // Rule Priority 3: Daily Practice & Time-of-Day Urgency
        else if (!hasPracticed) {
            if (currentHour >= 21) {
                targetRule = EmotionRule.DAILY_URGENCY_NIGHT
                targetEmotionId = 23 // Furious Warning 💢
            } else if (currentHour >= 17) {
                targetRule = EmotionRule.DAILY_URGENCY_EVE
                targetEmotionId = 15 // Worried Ember 🥺
            } else {
                targetRule = EmotionRule.DAILY_NOT_PRACTICED
                targetEmotionId = 18 // Pouting Grudge 😒
            }
        }
        // Rule Priority 4: Goal Secured! Streak Milestones & Celebrations
        else {
            if (currentStreak >= 7) {
                targetRule = EmotionRule.STREAK_MILESTONE
                targetEmotionId = 6 // Starry Prodigy 🤩
            } else if (currentStreak >= 3) {
                targetRule = EmotionRule.STREAK_MILESTONE
                targetEmotionId = 2 // Playful Wink 😉
            } else {
                targetRule = EmotionRule.DAILY_GOAL_ACHIEVED
                targetEmotionId = if (currentHour >= 21) 14 else 6
            }
        }

        val targetEmotion = phoenixEmotion(targetEmotionId)
        _activeRule.value = targetRule

        setEmotion(
            emotion = targetEmotion,
            reason = targetRule.summary,
            updateAppIcon = forceIconUpdate,
            showToast = false
        )

        return Pair(targetEmotion, targetRule)
    }

    // MARK: - 🚪 App Lifecycle Observers
    fun handleAppWentOut(forceReason: String? = null) {
        val now = System.currentTimeMillis()
        if (now - lastLifecycleCallTimestamp < 800) return
        lastLifecycleCallTimestamp = now
        evaluateEmotionRules(forceIconUpdate = true)
    }

    fun handleAppCameIn(forceReason: String? = null) {
        val now = System.currentTimeMillis()
        if (now - lastLifecycleCallTimestamp < 800) return
        lastLifecycleCallTimestamp = now
        evaluateEmotionRules(forceIconUpdate = true)
    }

    // MARK: - 🧭 In-App Tab Switch Reactions
    fun handleTabChanged(tabName: String) {
        _isInQuiz.value = false
        if (!_isAutoEmotionEnabled.value) return

        when (tabName.lowercase()) {
            "learn" -> evaluateEmotionRules(forceIconUpdate = false)
            "visualizer" -> setEmotion(phoenixEmotion(13), "Eureka Idea: Visualizing Algorithms", false, false)
            "practice" -> setEmotion(phoenixEmotion(11), "Focused Architect: Code Practice Hub", false, false)
            "leaderboards" -> setEmotion(phoenixEmotion(26), "Crown Champion: Competing for Top Ranks", false, false)
            "profile" -> setEmotion(phoenixEmotion(6), "Starry Prodigy: Celebrating Milestones", false, false)
        }
    }

    // MARK: - 🎮 Live Assessment & Question Reactions
    fun handleQuizStarted() {
        _isInQuiz.value = true
        if (!_isAutoEmotionEnabled.value) return
        setEmotion(phoenixEmotion(8), "Curious Explorer: Tackling DSA Problems", false, false)
    }

    fun handleQuestionLoaded() {
        if (!_isAutoEmotionEnabled.value) return
        val thinkingEmotion = phoenixEmotion(if (Math.random() < 0.5) 8 else 10)
        setEmotion(thinkingEmotion, "Analyzing Algorithm...", false, false)
    }

    fun handleAnswerSubmitted(isCorrect: Boolean, remainingHearts: Int, currentStreak: Int = 0) {
        if (!_isAutoEmotionEnabled.value) return

        if (isCorrect) {
            if (currentStreak >= 5) {
                _activeRule.value = EmotionRule.HIGH_COMBO
                setEmotion(phoenixEmotion(13), "5x Eureka Combo! Brilliant spark! 💡", true, true)
            } else {
                val winEmotionId = if (currentStreak >= 3) 6 else (if (Math.random() < 0.5) 3 else 2)
                val winEmotion = phoenixEmotion(winEmotionId)
                val reason = if (winEmotionId == 6) "Streak On Fire! 🤩" else "Correct! Algorithm Solved! 😆"
                setEmotion(winEmotion, reason, false, false)
            }
        } else {
            if (remainingHearts <= 0) {
                _activeRule.value = EmotionRule.ZERO_HEARTS
                setEmotion(phoenixEmotion(17), "Out of Hearts! 0 HP Remaining! 😭", true, true)
            } else if (remainingHearts == 1) {
                _activeRule.value = EmotionRule.LOW_HEARTS
                setEmotion(phoenixEmotion(19), "Danger! 1 Heart remaining! 😮‍💨", true, true)
            } else {
                setEmotion(phoenixEmotion(15), "Oops! Learn from mistakes! 🥺", false, false)
            }
        }
    }

    fun handleQuizCompleted(accuracy: Double, heartsRemaining: Int) {
        _isInQuiz.value = false
        if (!_isAutoEmotionEnabled.value) return

        if (accuracy >= 1.0) {
            _activeRule.value = EmotionRule.PERFECT_SCORE
            setEmotion(phoenixEmotion(6), "100% Perfect Quiz Mastery! 🤩", true, true)
        } else if (accuracy >= 0.8) {
            setEmotion(phoenixEmotion(3), "Lesson Crushed with ${(accuracy * 100).toInt()}% Accuracy! 😆", true, true)
        } else if (heartsRemaining == 1) {
            _activeRule.value = EmotionRule.LOW_HEARTS
            setEmotion(phoenixEmotion(19), "Narrowly beaten with 1 heart to spare! 😮‍💨", true, true)
        } else {
            evaluateEmotionRules(forceIconUpdate = true)
        }
    }

    fun handleVisualizerEngaged(concept: String) {
        if (!_isAutoEmotionEnabled.value) return
        setEmotion(phoenixEmotion(11), "Analyzing $concept in Visualizer", false, false)
    }

    fun handleLeaderboardViewed(rank: Int) {
        if (!_isAutoEmotionEnabled.value) return
        if (rank <= 3) {
            _activeRule.value = EmotionRule.LEADERBOARD_TOP
            setEmotion(phoenixEmotion(26), "Rank #$rank on Leaderboard! 👑", true, true)
        } else {
            setEmotion(phoenixEmotion(9), "Climbing Leaderboard Ranks", false, false)
        }
    }

    fun handleBossBattle(state: String) {
        if (!_isAutoEmotionEnabled.value) return
        if (state == "victory") {
            _activeRule.value = EmotionRule.STREAK_MILESTONE
            setEmotion(phoenixEmotion(6), "Boss Defeated! Evolution Complete! 🤩", true, true)
        } else {
            _activeRule.value = EmotionRule.BOSS_BATTLE
            setEmotion(phoenixEmotion(28), "Epic Boss Battle Underway! 🚩", true, true)
        }
    }

    fun handleHeartsRefilled() {
        _activeRule.value = EmotionRule.HEARTS_REFILLED
        setEmotion(phoenixEmotion(21), "Hearts Completely Refilled! Ready to Code 🥹", true, true)
    }

    fun handleGearEquipped(itemName: String) {
        if (!_isAutoEmotionEnabled.value) return
        setEmotion(phoenixEmotion(27), "Equipped $itemName! Loving the look", false, false)
    }

    fun handleQuestClaimed(questTitle: String) {
        if (!_isAutoEmotionEnabled.value) return
        setEmotion(phoenixEmotion(7), "Completed Quest: $questTitle!", false, false)
    }

    fun syncAutoEmotion(reason: String = "Auto-Dynamic Rules Enabled") {
        _isAutoEmotionEnabled.value = true
        evaluateEmotionRules(forceIconUpdate = true)
    }

    // MARK: - Manual & Custom Emotion Selection
    fun setEmotion(
        emotion: PhoenixEmotion,
        reason: String = "User Selected",
        updateAppIcon: Boolean = false,
        showToast: Boolean = false
    ) {
        val isDifferent = emotion.id != _currentEmotion.value.id

        if (isDifferent) {
            _currentEmotion.value = emotion
            _lastTriggerReason.value = reason
            _lastSwitchedAt.value = System.currentTimeMillis()
            if (showToast) {
                _showReactionToast.value = true
            }

            // Auto dismiss toast after 3.2s
            toastDismissJob?.cancel()
            if (showToast) {
                toastDismissJob = scope.launch {
                    delay(3200)
                    _showReactionToast.value = false
                }
            }
        } else if (showToast) {
            _lastTriggerReason.value = reason
            _showReactionToast.value = true
        }

        if (updateAppIcon) {
            applySystemAppIcon(emotion.drawableResName)
        }
    }

    fun dismissToast() {
        _showReactionToast.value = false
    }

    fun setSheetPresented(presented: Boolean) {
        _isSheetPresented.value = presented
    }

    // MARK: - 📲 System Alternate App Icon Updater
    fun applySystemAppIcon(named: String?) {
        val iconName = named ?: "phoenix_emotion_0"
        _lastAppliedIconName.value = iconName
        Log.d("PhoenixEmotion", "Requested launcher app icon sync: $iconName")

        val id = when {
            iconName.startsWith("phoenix_emotion_") -> iconName.removePrefix("phoenix_emotion_").toIntOrNull() ?: 0
            iconName.startsWith("AppIcon_") -> iconName.removePrefix("AppIcon_").toIntOrNull() ?: 0
            iconName.toIntOrNull() != null -> iconName.toInt()
            else -> 0
        }

        val targetAlias = "MainActivityAlias_$id"
        val allEmotionIds = listOf(0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28)
        val allAliases = listOf("MainActivityAliasDefault") + allEmotionIds.map { "MainActivityAlias_$it" }

        scope.launch(Dispatchers.IO) {
            appContext?.let { ctx ->
                try {
                    val pm = ctx.packageManager
                    val pkgName = ctx.packageName
                    val targetComponent = ComponentName(pkgName, "$pkgName.$targetAlias")
                    val currentSetting = pm.getComponentEnabledSetting(targetComponent)

                    Log.d("PhoenixEmotion", "Switching launcher alias to $targetAlias (current: $currentSetting)")

                    // 1. Enable target component FIRST
                    pm.setComponentEnabledSetting(
                        targetComponent,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP
                    )

                    // 2. Disable all other aliases
                    for (alias in allAliases) {
                        if (alias != targetAlias) {
                            val comp = ComponentName(pkgName, "$pkgName.$alias")
                            try {
                                val state = pm.getComponentEnabledSetting(comp)
                                if (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                                    pm.setComponentEnabledSetting(
                                        comp,
                                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                        PackageManager.DONT_KILL_APP
                                    )
                                }
                            } catch (e: Exception) {
                                Log.w("PhoenixEmotion", "Failed to disable alias $alias: ${e.message}")
                            }
                        }
                    }

                    // 3. Persist selection in SharedPreferences
                    ctx.getSharedPreferences("phoenix_icon_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putInt("active_emotion_id", id)
                        .putString("active_icon_name", targetAlias)
                        .apply()

                    Log.d("PhoenixEmotion", "Successfully switched and persisted launcher app icon: $targetAlias")

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            ctx,
                            "App icon updated to ${phoenixEmotion(id).title}! 🔥",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e("PhoenixEmotion", "Failed to update launcher app icon: ${e.message}", e)
                }
            }
        }
    }

    // MARK: - ⏱️ In-App Ambient Idle Monitor
    fun startInAppIdleMonitor() {
        idleTimerJob?.cancel()
        idleTimerJob = scope.launch {
            while (isActive) {
                delay(50_000) // 50 seconds
                if (!_isAutoEmotionEnabled.value) continue

                val currentId = _currentEmotion.value.id
                if (currentId == 0 || currentId == 4) {
                    setEmotion(
                        phoenixEmotion(10),
                        reason = "Idle & Thoughtful: Ready to continue DSA!",
                        updateAppIcon = false,
                        showToast = false
                    )
                }
            }
        }
    }

    fun recordUserInteraction() {
        if (_currentEmotion.value.id == 10) {
            evaluateEmotionRules(forceIconUpdate = false)
        }
        startInAppIdleMonitor()
    }

    // MARK: - 🕊️ Living Companion Engine
    fun startLivingCompanionEngine() {
        livingCompanionJob?.cancel()
        livingCompanionJob = scope.launch {
            while (isActive) {
                delay(25_000) // 25 seconds
                if (!_isAutoEmotionEnabled.value || _isInQuiz.value) continue

                val rule = _activeRule.value
                if (rule == EmotionRule.ZERO_HEARTS || rule == EmotionRule.LOW_HEARTS) {
                    continue
                }

                companionCycleIndex = (companionCycleIndex + 1) % companionEmotionIds.size
                val nextEmotionId = companionEmotionIds[companionCycleIndex]
                val nextEmotion = phoenixEmotion(nextEmotionId)

                setEmotion(
                    nextEmotion,
                    reason = "${nextEmotion.title} • Feeling: ${nextEmotion.feeling}",
                    updateAppIcon = false,
                    showToast = false
                )
            }
        }
    }

    // Mascot Tap Reaction
    fun triggerMascotTapReaction() {
        val celebrationEmotion = phoenixEmotion(if (Math.random() < 0.5) 3 else 2)
        setEmotion(celebrationEmotion, reason = "High Five with Phoenix!", updateAppIcon = false, showToast = true)

        scope.launch {
            delay(2500)
            if (_isAutoEmotionEnabled.value) {
                evaluateEmotionRules(forceIconUpdate = false)
            }
        }
    }

    // Quick Simulation Triggers
    fun simulateTrigger(testCase: String) {
        when (testCase) {
            "user_went_out_unpracticed" -> {
                _activeRule.value = EmotionRule.DAILY_NOT_PRACTICED
                setEmotion(phoenixEmotion(18), "Simulated: Left Without Practice (😒 Pouting)", true, true)
            }
            "user_went_out_goal_met" -> {
                _activeRule.value = EmotionRule.DAILY_GOAL_ACHIEVED
                setEmotion(phoenixEmotion(14), "Simulated: App Closed After Practicing (😌 Peaceful)", true, true)
            }
            "user_went_out_zero_hearts" -> {
                _activeRule.value = EmotionRule.ZERO_HEARTS
                setEmotion(phoenixEmotion(17), "Simulated: Closed With 0 Hearts (😭 Wailing)", true, true)
            }
            "user_came_in_welcome" -> {
                _activeRule.value = EmotionRule.STREAK_MILESTONE
                setEmotion(phoenixEmotion(4), "Simulated: Opened App (😊 Welcome Back)", true, true)
            }
            "user_came_in_grateful" -> {
                _activeRule.value = EmotionRule.HEARTS_REFILLED
                setEmotion(phoenixEmotion(21), "Simulated: Opened App (🥹 Grateful Tears)", true, true)
            }
            "perfect_quiz" -> {
                _activeRule.value = EmotionRule.PERFECT_SCORE
                setEmotion(phoenixEmotion(6), "Simulated: 100% Quiz Score (🤩 Starry)", true, true)
            }
            "one_heart_left" -> {
                _activeRule.value = EmotionRule.LOW_HEARTS
                setEmotion(phoenixEmotion(19), "Simulated: 1 Heart Left (😮‍💨 Relieved Sigh)", true, true)
            }
            "zero_hearts" -> {
                _activeRule.value = EmotionRule.ZERO_HEARTS
                setEmotion(phoenixEmotion(17), "Simulated: 0 Hearts Left (😭 Game Over)", true, true)
            }
            "streak_risk" -> {
                _activeRule.value = EmotionRule.DAILY_URGENCY_NIGHT
                setEmotion(phoenixEmotion(23), "Simulated: Streak in Danger (💢 Furious 11 PM)", true, true)
            }
            "streak_lost" -> {
                _activeRule.value = EmotionRule.DAILY_NOT_PRACTICED
                setEmotion(phoenixEmotion(24), "Simulated: Streak Broken! (😤 Steaming Rage)", true, true)
            }
            "boss_battle" -> {
                _activeRule.value = EmotionRule.BOSS_BATTLE
                setEmotion(phoenixEmotion(28), "Simulated: Boss Fight (🚩 Battle Standard)", true, true)
            }
            "eureka_idea" -> {
                _activeRule.value = EmotionRule.HIGH_COMBO
                setEmotion(phoenixEmotion(13), "Simulated: 5x Combo (💡 Eureka Lightbulb)", true, true)
            }
            "tricky_question" -> {
                setEmotion(phoenixEmotion(10), "Simulated: Tricky Review (❓ Puzzled)", true, true)
            }
            "hearts_refilled" -> {
                _activeRule.value = EmotionRule.HEARTS_REFILLED
                setEmotion(phoenixEmotion(21), "Simulated: Hearts Refilled (🥹 Grateful)", true, true)
            }
            "reset_happy" -> {
                _activeRule.value = EmotionRule.STREAK_MILESTONE
                setEmotion(phoenixEmotion(0), "Simulated: Reset to Cheerful Flame (🔥)", true, true)
            }
            else -> evaluateEmotionRules(forceIconUpdate = true)
        }
    }
}
