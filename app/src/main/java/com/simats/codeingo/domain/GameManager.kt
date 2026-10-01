package com.simats.codeingo.domain

import com.simats.codeingo.data.model.DSABossSpec
import com.simats.codeingo.data.model.DSAExerciseItem
import com.simats.codeingo.data.model.DailyQuestItem
import com.simats.codeingo.data.model.MistakeVaultItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class GameManager private constructor() {

    companion object {
        val instance: GameManager by lazy { GameManager() }
    }

    private val _totalXP = MutableStateFlow(120)
    val totalXP: StateFlow<Int> = _totalXP.asStateFlow()

    private val _gemsCount = MutableStateFlow(450)
    val gemsCount: StateFlow<Int> = _gemsCount.asStateFlow()

    private val _heartsCount = MutableStateFlow(10)
    val heartsCount: StateFlow<Int> = _heartsCount.asStateFlow()

    private val _streakDays = MutableStateFlow(3)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

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

    init {
        initializeDailyQuests()
        loadDefaultMistakes()
    }

    fun awardLessonXP(baseXP: Int, accuracyPercentage: Double, speedSeconds: Int): Int {
        val earned = XPCalculator.calculateLessonXP(
            baseXP = baseXP,
            accuracyPercentage = accuracyPercentage,
            speedSeconds = speedSeconds,
            currentCombo = _currentCombo.value
        )
        _totalXP.value += earned
        _gemsCount.value += (earned / 5)
        incrementDailyQuest("lessons")
        return earned
    }

    fun awardBossVictory(boss: DSABossSpec) {
        val bossXP = 60
        _totalXP.value += bossXP
        _gemsCount.value += 50
        _completedBossIds.value = _completedBossIds.value + boss.id
        triggerEvolution(boss.targetPhoenixStageAwarded)
    }

    fun recordCorrectAnswer() {
        _currentCombo.value += 1
        if (_currentCombo.value > _highestCombo.value) {
            _highestCombo.value = _currentCombo.value
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
    }

    fun triggerEvolution(toStage: Int) {
        if (toStage > _activePhoenixStage.value && toStage <= 18) {
            _evolutionFromStage.value = _activePhoenixStage.value
            _evolutionToStage.value = toStage
            _activePhoenixStage.value = toStage
            _showEvolutionModal.value = true
        }
    }

    fun dismissEvolutionModal() {
        _showEvolutionModal.value = false
    }

    fun refillHearts() {
        _heartsCount.value = 10
    }

    fun completeLesson(lessonId: String, nextLevelIndex: Int? = null) {
        _completedLessonIds.value = _completedLessonIds.value + lessonId
        if (nextLevelIndex != null) {
            _unlockedLevelIndices.value = _unlockedLevelIndices.value + nextLevelIndex
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
    }

    fun claimQuest(questId: String) {
        _dailyQuests.value = _dailyQuests.value.map { quest ->
            if (quest.id == questId && quest.isCompleted && !quest.isClaimed) {
                _totalXP.value += quest.xpReward
                _gemsCount.value += quest.gemReward
                quest.copy(isClaimed = true)
            } else quest
        }
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
