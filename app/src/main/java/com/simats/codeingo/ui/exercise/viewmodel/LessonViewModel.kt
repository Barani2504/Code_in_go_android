package com.simats.codeingo.ui.exercise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.codeingo.data.model.ExerciseType
import com.simats.codeingo.data.repository.BasecampQuestionsData
import com.simats.codeingo.data.repository.QuizQuestion
import com.simats.codeingo.data.repository.QuizQuestionsData
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.LessonCompletionUseCase
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.data.network.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LessonUiState {
    object Loading : LessonUiState

    data class ActiveQuestion(
        val questionIndex: Int,
        val totalQuestions: Int,
        val question: QuizQuestion,
        val selectedOptionIndex: Int? = null,
        val matchedPairs: Map<String, String> = emptyMap(),
        val orderedSteps: List<String> = emptyList(),
        val selectedFillToken: String? = null,
        val selectedKeySizeOrComplexity: String? = null,
        val selectedTrueFalse: Boolean? = null,
        val isChecked: Boolean = false,
        val isCorrect: Boolean = false,
        val showFeedback: Boolean = false,
        val timeRemaining: Int = 45,
        val isTimerEnabled: Boolean = false,
        val heartsCount: Int = 5,
        val comboStreak: Int = 0
    ) : LessonUiState

    data class OutOfHearts(val heartsCount: Int = 0) : LessonUiState

    data class Completed(
        val xpEarned: Int,
        val gemsEarned: Int,
        val starsEarned: Int,
        val accuracyPercentage: Int,
        val isBoss: Boolean
    ) : LessonUiState
}

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val gameManager: GameManager,
    private val syncRepository: SyncRepository,
    private val lessonCompletionUseCase: LessonCompletionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LessonUiState>(LessonUiState.Loading)
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    private var questions: List<QuizQuestion> = emptyList()
    private var currentIndex = 0
    private var correctCount = 0
    private var unitId = 1
    private var levelNumber = 1
    private var totalLevelsInUnit = 6
    private var isBoss = false
    private var comboStreak = 0
    private var timerJob: Job? = null

    fun initialize(
        unitId: Int,
        levelNumber: Int,
        totalLevelsInUnit: Int = 6,
        isBoss: Boolean = false
    ) {
        this.unitId = unitId
        this.levelNumber = levelNumber
        this.totalLevelsInUnit = totalLevelsInUnit
        this.isBoss = isBoss
        this.currentIndex = 0
        this.correctCount = 0
        this.comboStreak = 0

        val loaded: List<QuizQuestion> = QuizQuestionsData.getQuestionsForUnit(unitId, levelNumber)

        this.questions = if (loaded.isNotEmpty()) loaded else listOf(
            QuizQuestion(
                gameType = ExerciseType.MULTIPLE_CHOICE,
                typeTitle = "CNS Challenge",
                speakerName = "Phoenix",
                speakerImage = "PhoenixCurious",
                promptSentence = "Analyze the cryptographic defense protocol:",
                targetPrompt = "Select the secure option"
            )
        )

        loadQuestion(0)
    }

    private fun loadQuestion(index: Int) {
        if (index >= questions.size) {
            completeLesson()
            return
        }

        val q = questions[index]
        val currentHearts = gameManager.heartsCount.value
        if (currentHearts <= 0) {
            _uiState.value = LessonUiState.OutOfHearts(currentHearts)
            return
        }

        currentIndex = index
        _uiState.value = LessonUiState.ActiveQuestion(
            questionIndex = index,
            totalQuestions = questions.size,
            question = q,
            orderedSteps = q.orderStepsInitial,
            isTimerEnabled = isBoss,
            timeRemaining = 45,
            heartsCount = currentHearts,
            comboStreak = comboStreak
        )

        startTimerIfNeeded()
    }

    private fun startTimerIfNeeded() {
        timerJob?.cancel()
        if (!isBoss) return

        timerJob = viewModelScope.launch {
            var remaining = 45
            while (isActive && remaining > 0) {
                delay(1000)
                remaining--
                val curr = _uiState.value
                if (curr is LessonUiState.ActiveQuestion && !curr.isChecked) {
                    _uiState.value = curr.copy(timeRemaining = remaining)
                }
            }
        }
    }

    fun selectOption(index: Int) {
        val curr = _uiState.value as? LessonUiState.ActiveQuestion ?: return
        if (curr.isChecked) return
        _uiState.value = curr.copy(selectedOptionIndex = index)
    }

    fun matchPair(left: String, right: String) {
        val curr = _uiState.value as? LessonUiState.ActiveQuestion ?: return
        if (curr.isChecked) return
        val updated = curr.matchedPairs.toMutableMap()
        updated[left] = right
        _uiState.value = curr.copy(matchedPairs = updated)
    }

    fun reorderSteps(steps: List<String>) {
        val curr = _uiState.value as? LessonUiState.ActiveQuestion ?: return
        if (curr.isChecked) return
        _uiState.value = curr.copy(orderedSteps = steps)
    }

    fun selectFillToken(token: String) {
        val curr = _uiState.value as? LessonUiState.ActiveQuestion ?: return
        if (curr.isChecked) return
        _uiState.value = curr.copy(selectedFillToken = token)
    }

    fun selectKeySizeOrComplexity(value: String) {
        val curr = _uiState.value as? LessonUiState.ActiveQuestion ?: return
        if (curr.isChecked) return
        _uiState.value = curr.copy(selectedKeySizeOrComplexity = value)
    }

    fun selectTrueFalse(value: Boolean) {
        val curr = _uiState.value as? LessonUiState.ActiveQuestion ?: return
        if (curr.isChecked) return
        _uiState.value = curr.copy(selectedTrueFalse = value)
    }

    fun checkAnswer(): Boolean {
        val curr = _uiState.value as? LessonUiState.ActiveQuestion ?: return false
        if (curr.isChecked) return false

        timerJob?.cancel()
        val q = curr.question
        val isCorrect = when (q.gameType) {
            ExerciseType.MULTIPLE_CHOICE -> curr.selectedOptionIndex == q.correctOptionIndex
            ExerciseType.MATCH_PAIRS -> curr.matchedPairs.size == q.matchSolution.size &&
                    q.matchSolution.all { curr.matchedPairs[it.key] == it.value }
            ExerciseType.ORDER_STEPS -> curr.orderedSteps == q.orderStepsSolution
            ExerciseType.FILL_CODE -> curr.selectedFillToken == q.fillCodeCorrectToken
            ExerciseType.KEY_SIZE_DIAL -> curr.selectedKeySizeOrComplexity == q.complexityDialCorrect
            ExerciseType.TRUE_FALSE_SWIPE -> curr.selectedTrueFalse == q.trueFalseIsCorrectTrue
            else -> curr.selectedOptionIndex == q.correctOptionIndex
        }

        if (isCorrect) {
            correctCount++
            comboStreak++
            PhoenixEmotionManager.instance.handleAnswerSubmitted(isCorrect = true, remainingHearts = gameManager.heartsCount.value, currentStreak = comboStreak)
        } else {
            comboStreak = 0
            gameManager.loseHeart()
            PhoenixEmotionManager.instance.handleAnswerSubmitted(isCorrect = false, remainingHearts = gameManager.heartsCount.value, currentStreak = 0)
        }

        _uiState.value = curr.copy(
            isChecked = true,
            isCorrect = isCorrect,
            showFeedback = true,
            heartsCount = gameManager.heartsCount.value,
            comboStreak = comboStreak
        )

        return isCorrect
    }

    fun continueToNextQuestion() {
        val currentHearts = gameManager.heartsCount.value
        if (currentHearts <= 0) {
            _uiState.value = LessonUiState.OutOfHearts(currentHearts)
            return
        }
        loadQuestion(currentIndex + 1)
    }

    fun recoverHeartWithGems() {
        viewModelScope.launch {
            syncRepository.buyHeartRefill(price = 35, hearts = 10)
            loadQuestion(currentIndex)
        }
    }

    fun recoverHeartWithPractice() {
        viewModelScope.launch {
            syncRepository.earnPracticeHeart()
            loadQuestion(currentIndex)
        }
    }

    fun continueInReviewMode() {
        loadQuestion(currentIndex)
    }

    private fun completeLesson() {
        timerJob?.cancel()
        viewModelScope.launch {
            val totalQ = questions.size
            val result = lessonCompletionUseCase.execute(
                unitId = unitId,
                levelNumber = levelNumber,
                totalLevelsInUnit = totalLevelsInUnit,
                isBoss = isBoss,
                correctCount = correctCount,
                totalQuestions = totalQ
            )

            val accuracy = if (totalQ > 0) {
                ((correctCount.toFloat() / totalQ) * 100).toInt()
            } else {
                100
            }

            _uiState.value = LessonUiState.Completed(
                xpEarned = result.xpEarned,
                gemsEarned = result.gemsEarned,
                starsEarned = result.starsEarned,
                accuracyPercentage = accuracy,
                isBoss = isBoss
            )
        }
    }
}
