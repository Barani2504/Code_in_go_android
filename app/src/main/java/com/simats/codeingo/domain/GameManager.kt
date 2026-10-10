package com.simats.codeingo.domain

import android.content.Context
import com.simats.codeingo.data.local.UserPreferences
import com.simats.codeingo.data.model.DSABossSpec
import com.simats.codeingo.data.model.DSAExerciseItem
import com.simats.codeingo.data.model.DailyQuestItem
import com.simats.codeingo.data.model.MistakeVaultItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class GameManager private constructor() {

    companion object {
        val instance: GameManager by lazy { GameManager() }
        val shared: GameManager get() = instance
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var prefs: UserPreferences? = null

    // ── Core stats ────────────────────────────────────────────────────────────

    private val _totalXP = MutableStateFlow(860)
    val totalXP: StateFlow<Int> = _totalXP.asStateFlow()

    private val _gemsCount = MutableStateFlow(450)
    val gemsCount: StateFlow<Int> = _gemsCount.asStateFlow()

    private val _heartsCount = MutableStateFlow(10)
    val heartsCount: StateFlow<Int> = _heartsCount.asStateFlow()

    private val _streakDays = MutableStateFlow(3)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    private val _lastPlayedDateUnix = MutableStateFlow(0.0)
    val lastPlayedDateUnix: StateFlow<Double> = _lastPlayedDateUnix.asStateFlow()

    private val _isStreakLostPendingRestore = MutableStateFlow(false)
    val isStreakLostPendingRestore: StateFlow<Boolean> = _isStreakLostPendingRestore.asStateFlow()

    private val _savedStreakDays = MutableStateFlow(0)
    val savedStreakDays: StateFlow<Int> = _savedStreakDays.asStateFlow()

    private val _lastPracticedTimestamp = MutableStateFlow(System.currentTimeMillis() / 1000.0)
    val lastPracticedTimestamp: StateFlow<Double> = _lastPracticedTimestamp.asStateFlow()

    private val _streakLostTimestamp = MutableStateFlow(0.0)
    val streakLostTimestamp: StateFlow<Double> = _streakLostTimestamp.asStateFlow()

    val hasPracticedToday: Boolean
        get() {
            if (_lastPracticedTimestamp.value <= 0) return false
            val cal = java.util.Calendar.getInstance()
            val nowYear = cal.get(java.util.Calendar.YEAR)
            val nowDay = cal.get(java.util.Calendar.DAY_OF_YEAR)
            cal.timeInMillis = (_lastPracticedTimestamp.value * 1000).toLong()
            return cal.get(java.util.Calendar.YEAR) == nowYear &&
                    cal.get(java.util.Calendar.DAY_OF_YEAR) == nowDay
        }

    val isStreakAtRisk: Boolean
        get() {
            val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
            return !hasPracticedToday && hour >= 20
        }

    // ── Phoenix / progression ─────────────────────────────────────────────────

    private val _activePhoenixStage = MutableStateFlow(1)
    val activePhoenixStage: StateFlow<Int> = _activePhoenixStage.asStateFlow()

    private val _highestCombo = MutableStateFlow(5)
    val highestCombo: StateFlow<Int> = _highestCombo.asStateFlow()

    private val _eggCrackLevel = MutableStateFlow(0)
    val eggCrackLevel: StateFlow<Int> = _eggCrackLevel.asStateFlow()

    private val _currentCombo = MutableStateFlow(0)
    val currentCombo: StateFlow<Int> = _currentCombo.asStateFlow()

    private val _showEvolutionModal = MutableStateFlow(false)
    val showEvolutionModal: StateFlow<Boolean> = _showEvolutionModal.asStateFlow()

    private val _evolutionFromStage = MutableStateFlow(1)
    val evolutionFromStage: StateFlow<Int> = _evolutionFromStage.asStateFlow()

    private val _evolutionToStage = MutableStateFlow(2)
    val evolutionToStage: StateFlow<Int> = _evolutionToStage.asStateFlow()

    // ── Vault / quests ────────────────────────────────────────────────────────

    private val _mistakeVault = MutableStateFlow<List<MistakeVaultItem>>(emptyList())
    val mistakeVault: StateFlow<List<MistakeVaultItem>> = _mistakeVault.asStateFlow()

    private val _dailyQuests = MutableStateFlow<List<DailyQuestItem>>(emptyList())
    val dailyQuests: StateFlow<List<DailyQuestItem>> = _dailyQuests.asStateFlow()

    private val _completedLessonIds = MutableStateFlow<Set<String>>(setOf("u1_l1_what_is_ds"))
    val completedLessonIds: StateFlow<Set<String>> = _completedLessonIds.asStateFlow()

    private val _completedBossIds = MutableStateFlow<Set<String>>(emptySet())
    val completedBossIds: StateFlow<Set<String>> = _completedBossIds.asStateFlow()

    private val _unlockedLevelIndices = MutableStateFlow<Set<Int>>(setOf(1))
    val unlockedLevelIndices: StateFlow<Set<Int>> = _unlockedLevelIndices.asStateFlow()

    private val _maxUnlockedChapter = MutableStateFlow(1)
    val maxUnlockedChapter: StateFlow<Int> = _maxUnlockedChapter.asStateFlow()

    private val _completedChapters = MutableStateFlow<Set<Int>>(emptySet())
    val completedChapters: StateFlow<Set<Int>> = _completedChapters.asStateFlow()

    private val _completedLevelIndices = MutableStateFlow<Set<Int>>(emptySet())
    val completedLevelIndices: StateFlow<Set<Int>> = _completedLevelIndices.asStateFlow()

    private val _activeLevelIndex = MutableStateFlow(1)
    val activeLevelIndex: StateFlow<Int> = _activeLevelIndex.asStateFlow()

    private val _totalStars = MutableStateFlow(15)
    val totalStars: StateFlow<Int> = _totalStars.asStateFlow()

    init {
        initializeDailyQuests()
        loadDefaultMistakes()
        evaluateStreakOnLaunch()
    }

    // ── Initialization (called once from CodeingoApp) ─────────────────────────

    fun initialize(context: Context) {
        val p = UserPreferences(context)
        prefs = p
        scope.launch {
            // Seed StateFlows from persisted DataStore values (one-shot first())
            _totalXP.value = p.totalXP.first()
            _gemsCount.value = p.gemsCount.first()
            _heartsCount.value = p.heartsCount.first()
            _streakDays.value = p.streakDays.first()
            _activePhoenixStage.value = p.activePhoenixStage.first()
            _highestCombo.value = p.highestCombo.first()
            _eggCrackLevel.value = p.eggCrackLevel.first()
            _completedLessonIds.value = p.completedLessons.first()
            _completedBossIds.value = p.completedBosses.first()
            _unlockedLevelIndices.value = p.unlockedLevels.first().mapNotNull { it.toIntOrNull() }.toSet()
                .ifEmpty { setOf(1) }
            _maxUnlockedChapter.value = p.maxUnlockedChapter.first()
            _completedChapters.value = p.completedChapters.first().mapNotNull { it.toIntOrNull() }.toSet()
            _completedLevelIndices.value = p.completedLevelIndices.first().mapNotNull { it.toIntOrNull() }.toSet()
            _activeLevelIndex.value = p.activeLevelIndex.first()

            // Re-evaluate streak with restored persisted timestamp
            evaluateStreakOnLaunch()
        }
    }

    // ── Persistence helpers ───────────────────────────────────────────────────

    private fun persistStats() {
        val p = prefs ?: return
        scope.launch {
            p.updateGameState(
                xp = _totalXP.value,
                gems = _gemsCount.value,
                hearts = _heartsCount.value,
                streak = _streakDays.value,
                phoenixStage = _activePhoenixStage.value,
                highestCombo = _highestCombo.value,
                eggCrack = _eggCrackLevel.value
            )
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    fun addStars(count: Int) {
        _totalStars.value += count
    }

    fun addXP(amount: Int) {
        _totalXP.value += amount
        persistStats()
    }

    fun addGems(count: Int) {
        _gemsCount.value += count
        persistStats()
    }

    fun evaluateStreakOnLaunch() {
        val now = System.currentTimeMillis() / 1000.0
        if (_lastPlayedDateUnix.value <= 0) {
            _lastPlayedDateUnix.value = now
            return
        }
        val cal = java.util.Calendar.getInstance()
        val nowDay = cal.get(java.util.Calendar.DAY_OF_YEAR)
        val nowYear = cal.get(java.util.Calendar.YEAR)
        cal.timeInMillis = (_lastPlayedDateUnix.value * 1000).toLong()
        val lastDay = cal.get(java.util.Calendar.DAY_OF_YEAR)
        val lastYear = cal.get(java.util.Calendar.YEAR)

        val isSameDay = nowYear == lastYear && nowDay == lastDay
        val isYesterday = (nowYear == lastYear && nowDay == lastDay + 1) ||
                (nowYear == lastYear + 1 && lastDay >= 365 && nowDay == 1)

        if (!isSameDay && !isYesterday && _streakDays.value > 0) {
            _isStreakLostPendingRestore.value = true
            _savedStreakDays.value = _streakDays.value
            _streakDays.value = 0
            _streakLostTimestamp.value = now
            persistStats()
        }
    }

    fun restoreStreak() {
        _isStreakLostPendingRestore.value = false
        _streakDays.value = _savedStreakDays.value
        val yesterday = (System.currentTimeMillis() - 86400000L) / 1000.0
        _lastPlayedDateUnix.value = yesterday
        persistStats()
    }

    /** Debug helper — resets streak to 0, used by Settings streak controls. */
    fun debugResetStreak() {
        _streakDays.value = 0
        _savedStreakDays.value = 0
        _isStreakLostPendingRestore.value = false
        _lastPlayedDateUnix.value = 0.0
        persistStats()
    }

    /** Resets all unlocked levels and XP — used by Settings → Courses → RESET. */
    fun resetAllProgress() {
        _totalXP.value = 0
        _unlockedLevelIndices.value = setOf(1)
        _maxUnlockedChapter.value = 1
        _completedChapters.value = emptySet()
        _completedLevelIndices.value = emptySet()
        _activeLevelIndex.value = 1
        _streakDays.value = 0
        _savedStreakDays.value = 0
        _isStreakLostPendingRestore.value = false
        _lastPlayedDateUnix.value = 0.0
        persistStats()
        val p = prefs
        if (p != null) {
            scope.launch { p.resetChapterProgression() }
        }
    }

    // ── Chapter & Arena Progression API ───────────────────────────────────────

    fun isArenaUnlocked(chapterId: Int): Boolean = chapterId <= _maxUnlockedChapter.value

    fun isChapterCompleted(chapterId: Int): Boolean = _completedChapters.value.contains(chapterId)

    fun markChapterCompleted(chapterId: Int) {
        _completedChapters.value = _completedChapters.value + chapterId
        val p = prefs ?: return
        scope.launch { p.markChapterCompleted(chapterId) }
    }

    fun unlockNextChapter(nextId: Int) {
        _maxUnlockedChapter.value = maxOf(_maxUnlockedChapter.value, nextId)
        val p = prefs ?: return
        scope.launch { p.setMaxUnlockedChapter(nextId) }
    }

    fun setActiveLevel(level: Int) {
        _activeLevelIndex.value = level
        val p = prefs ?: return
        scope.launch { p.setActiveLevelIndex(level) }
    }

    fun markLevelCompleted(level: Int) {
        _completedLevelIndices.value = _completedLevelIndices.value + level
        val p = prefs ?: return
        scope.launch { p.markLevelCompleted(level) }
    }

    fun resetChapterProgression() {
        _maxUnlockedChapter.value = 1
        _completedChapters.value = emptySet()
        _completedLevelIndices.value = emptySet()
        _activeLevelIndex.value = 1
        _unlockedLevelIndices.value = setOf(1)
        val p = prefs ?: return
        scope.launch { p.resetChapterProgression() }
    }

    fun completeLessonAndExtendStreak() {
        val now = System.currentTimeMillis() / 1000.0
        val cal = java.util.Calendar.getInstance()
        val nowDay = cal.get(java.util.Calendar.DAY_OF_YEAR)
        val nowYear = cal.get(java.util.Calendar.YEAR)

        if (_lastPlayedDateUnix.value == 0.0) {
            _streakDays.value = 1
        } else {
            cal.timeInMillis = (_lastPlayedDateUnix.value * 1000).toLong()
            val lastDay = cal.get(java.util.Calendar.DAY_OF_YEAR)
            val lastYear = cal.get(java.util.Calendar.YEAR)

            val isYesterday = (nowYear == lastYear && nowDay == lastDay + 1) ||
                    (nowYear == lastYear + 1 && lastDay >= 365 && nowDay == 1)
            val isToday = nowYear == lastYear && nowDay == lastDay

            if (isYesterday) {
                _streakDays.value += 1
            } else if (!isToday) {
                if (_isStreakLostPendingRestore.value) {
                    _isStreakLostPendingRestore.value = false
                }
                _streakDays.value = 1
            }
        }
        _lastPlayedDateUnix.value = now
        _lastPracticedTimestamp.value = now
        incrementDailyQuest("lessons")
        persistStats()
    }

    fun awardStageCompletion(xp: Int = 12, diamonds: Int = 5) {
        _totalXP.value += xp
        _gemsCount.value += diamonds
        _lastPracticedTimestamp.value = System.currentTimeMillis() / 1000.0
        persistStats()
    }

    fun awardLessonXP(
        unitId: Int = 1,
        accuracyPercentage: Double = 1.0,
        speedSeconds: Int = 30,
        baseXP: Int? = null
    ): Int {
        var earned = baseXP ?: 10
        if (accuracyPercentage >= 1.0) {
            earned += 3
        } else if (accuracyPercentage >= 0.8) {
            earned += 2
        }
        if (speedSeconds < 45) {
            earned += 2
        } else if (speedSeconds < 90) {
            earned += 1
        }
        if (_currentCombo.value >= 5) {
            earned += 2
        }
        earned = minOf(earned, 18)
        _totalXP.value += earned
        _gemsCount.value += 5
        _lastPracticedTimestamp.value = System.currentTimeMillis() / 1000.0
        incrementDailyQuest("lessons")
        persistStats()

        // Duolingo-style automatic emotion evaluation
        PhoenixEmotionManager.instance.handleQuizCompleted(
            accuracy = accuracyPercentage,
            heartsRemaining = _heartsCount.value
        )
        return earned
    }

    fun loseHeart() {
        _currentCombo.value = 0
        if (_heartsCount.value > 0) {
            _heartsCount.value -= 1
        }
        persistStats()
    }

    fun refillHearts() {
        _heartsCount.value = 10
        persistStats()
        PhoenixEmotionManager.instance.handleHeartsRefilled()
    }

    fun buyHearts(amount: Int, price: Int): Pair<Boolean, String> {
        if (_heartsCount.value >= 10) {
            return Pair(false, "Your life hearts are already full! (10/10 ❤️)")
        }
        if (_gemsCount.value < price) {
            val needed = price - _gemsCount.value
            return Pair(false, "Not enough diamonds! You need $needed more 💎.")
        }
        _gemsCount.value -= price
        _heartsCount.value = minOf(10, _heartsCount.value + amount)
        persistStats()
        PhoenixEmotionManager.instance.handleHeartsRefilled()
        return Pair(true, "Restored +$amount life hearts! ❤️ (${_heartsCount.value}/10)")
    }

    fun awardBossVictory(boss: DSABossSpec, unitId: Int = 1) {
        val bossXP = 25
        _totalXP.value += bossXP
        _gemsCount.value += 15
        _completedBossIds.value = _completedBossIds.value + boss.id
        triggerEvolution(boss.targetPhoenixStageAwarded)
        persistStats()
        val p = prefs ?: return
        scope.launch { p.completeBoss(boss.id) }
        PhoenixEmotionManager.instance.handleBossBattle("victory")
    }

    fun recordCorrectAnswer() {
        _currentCombo.value += 1
        if (_currentCombo.value > _highestCombo.value) {
            _highestCombo.value = _currentCombo.value
            persistStats()
        }
        incrementDailyQuest("combo")
    }

    fun recordWrongAnswer(exercise: DSAExerciseItem, userAnswer: String) {
        _currentCombo.value = 0
        if (_heartsCount.value > 0) {
            _heartsCount.value -= 1
        }

        val correctAnswer = exercise.correctAnswers.firstOrNull()
            ?: exercise.options.getOrNull(exercise.correctIndex ?: -1)
            ?: "Correct answer"

        val mistake = MistakeVaultItem(
            id = UUID.randomUUID().toString(),
            prompt = exercise.prompt,
            conceptId = exercise.conceptId,
            wrongAnswerGiven = userAnswer,
            correctAnswer = correctAnswer,
            explanation = exercise.explanation
        )
        _mistakeVault.value = _mistakeVault.value + mistake
        persistStats()
    }

    fun triggerEvolution(toStage: Int) {
        if (toStage > _activePhoenixStage.value && toStage <= 18) {
            _evolutionFromStage.value = _activePhoenixStage.value
            _evolutionToStage.value = toStage
            _activePhoenixStage.value = toStage
            _showEvolutionModal.value = true
            persistStats()
        }
    }

    fun dismissEvolutionModal() {
        _showEvolutionModal.value = false
    }

    fun setActivePhoenixStage(stageId: Int) {
        _activePhoenixStage.value = stageId.coerceIn(1, 18)
        persistStats()
    }

    fun completeLesson(lessonId: String, nextLevelIndex: Int? = null) {
        _completedLessonIds.value = _completedLessonIds.value + lessonId
        if (nextLevelIndex != null) {
            _unlockedLevelIndices.value = _unlockedLevelIndices.value + nextLevelIndex
        }
        val p = prefs ?: return
        scope.launch { p.completeLesson(lessonId, nextLevelIndex) }
    }

    /**
     * Called when a lesson/stage is completed. Mirrors iOS onComplete / onUpgradePhoenixNextUnit:
     *  - Inserts [levelNumber + 1] into unlockedLevelIndices  (or nextUnitFirstLevel for boss)
     *  - Updates eggCrackLevel so PhoenixEggHatch3DView reflects the new crack stage
     *  - For boss completion, triggers Phoenix Evolution
     */
    fun unlockNextLevel(
        currentLevelIndex: Int,
        isBoss: Boolean = false,
        nextUnitFirstLevelIndex: Int? = null
    ) {
        markLevelCompleted(currentLevelIndex)
        val nextLevel = if (isBoss) {
            val currentUnitId = ((currentLevelIndex - 1) / 5) + 1
            markChapterCompleted(currentUnitId)
            val nextUnitId = currentUnitId + 1
            unlockNextChapter(nextUnitId)
            nextUnitFirstLevelIndex ?: (currentLevelIndex + 1)
        } else {
            currentLevelIndex + 1
        }
        _unlockedLevelIndices.value = _unlockedLevelIndices.value + nextLevel
        setActiveLevel(nextLevel)

        if (isBoss) {
            _eggCrackLevel.value = 0
            val targetStage = (_activePhoenixStage.value + 1).coerceAtMost(18)
            triggerEvolution(targetStage)
        } else {
            _eggCrackLevel.value = (_unlockedLevelIndices.value.size).coerceAtMost(5)
        }
        persistStats()
        val p = prefs ?: return
        scope.launch {
            p.completeLesson("level_$currentLevelIndex", nextLevel)
        }
    }

    fun regenerateHeart() {
        if (_heartsCount.value < 10) {
            _heartsCount.value++
            if (_heartsCount.value == 10) {
                PhoenixEmotionManager.instance.handleHeartsRefilled()
            }
            persistStats()
        }
    }

    fun repairMistake(id: String) {
        _mistakeVault.value = _mistakeVault.value.map {
            if (it.id == id) {
                _totalXP.value += 10
                _gemsCount.value += 5
                it.copy(isRepaired = true)
            } else it
        }
        persistStats()
    }

    fun claimQuest(questId: String) {
        _dailyQuests.value = _dailyQuests.value.map { quest ->
            if (quest.id == questId && quest.isCompleted && !quest.isClaimed) {
                _totalXP.value += quest.xpReward
                _gemsCount.value += quest.gemReward
                quest.copy(isClaimed = true)
            } else quest
        }
        persistStats()
    }

    fun incrementDailyQuest(type: String) {
        _dailyQuests.value = _dailyQuests.value.map { quest ->
            when {
                type == "lessons" && quest.id == "quest_1" ->
                    quest.copy(currentCount = minOf(quest.targetCount, quest.currentCount + 1))
                type == "combo" && quest.id == "quest_2" -> {
                    if (_currentCombo.value >= quest.targetCount) {
                        quest.copy(currentCount = quest.targetCount)
                    } else quest
                }
                type == "visualizer" && quest.id == "quest_3" ->
                    quest.copy(currentCount = minOf(quest.targetCount, quest.currentCount + 1))
                else -> quest
            }
        }
    }

    private fun initializeDailyQuests() {
        _dailyQuests.value = listOf(
            DailyQuestItem(
                id = "quest_1",
                title = "Daily Workout",
                requirement = "Complete 2 DSA lessons",
                targetCount = 2,
                currentCount = 1,
                xpReward = 20,
                gemReward = 10,
                isClaimed = false
            ),
            DailyQuestItem(
                id = "quest_2",
                title = "Combo Flame",
                requirement = "Achieve a 5x correct answer combo",
                targetCount = 5,
                currentCount = 3,
                xpReward = 25,
                gemReward = 15,
                isClaimed = false
            ),
            DailyQuestItem(
                id = "quest_3",
                title = "Visualizer Mastery",
                requirement = "Operate 1 Data Structure Visualizer",
                targetCount = 1,
                currentCount = 0,
                xpReward = 30,
                gemReward = 20,
                isClaimed = false
            )
        )
    }

    private fun loadDefaultMistakes() {
        _mistakeVault.value = listOf(
            MistakeVaultItem(
                id = "seed_1",
                prompt = "What is the worst-case search time in an unsorted array of size n?",
                conceptId = "array_search",
                wrongAnswerGiven = "O(log n)",
                correctAnswer = "O(n)",
                explanation = "Unsorted arrays must check every element from beginning to end in the worst case."
            ),
            MistakeVaultItem(
                id = "seed_2",
                prompt = "In a linked list insertion at head, which pointer assignment must happen FIRST?",
                conceptId = "pointer_order",
                wrongAnswerGiven = "head = newNode",
                correctAnswer = "newNode.next = head",
                explanation = "If head is updated first, you lose reference to the rest of the list!"
            )
        )
    }
}

typealias DSAGameManager = GameManager
