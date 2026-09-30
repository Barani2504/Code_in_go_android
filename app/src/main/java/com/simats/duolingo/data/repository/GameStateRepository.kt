package com.simats.duolingo.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.simats.duolingo.data.AppState
import com.simats.duolingo.domain.model.DsaLesson
import com.simats.duolingo.domain.model.DsaUnit
import com.simats.duolingo.domain.model.NodeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global game state management for the DSA Course:
 * Tracks XP, gems, combo, completed lessons, crowns, and unlocks.
 */
object GameStateRepository {

    // Reactive StateFlows for UI observing
    private val _totalXp = MutableStateFlow(120)
    val totalXp: StateFlow<Int> = _totalXp.asStateFlow()

    private val _gems = MutableStateFlow(450)
    val gems: StateFlow<Int> = _gems.asStateFlow()

    private val _combo = MutableStateFlow(0)
    val combo: StateFlow<Int> = _combo.asStateFlow()

    // Completed lesson IDs
    var completedLessonIds by mutableStateOf(setOf<String>("u1_l1"))
        private set

    // Crowns earned per lesson (0 to 5)
    var lessonCrowns by mutableStateOf(mapOf<String, Int>("u1_l1" to 1))
        private set

    // Active course selection
    var currentUnitIndex by mutableIntStateOf(1)
    var activeLesson: DsaLesson? by mutableStateOf(null)
    var activeUnit: DsaUnit? by mutableStateOf(null)

    // Pending evolution trigger to show celebration screen
    var pendingEvolutionStage by mutableStateOf<Int?>(null)

    fun isLessonUnlocked(unitIndex: Int, lessonIndex: Int): Boolean {
        if (unitIndex == 1 && lessonIndex == 1) return true
        val unit = CourseRepository.dsaCourse.units.firstOrNull { it.index == unitIndex } ?: return false
        if (lessonIndex == 1) {
            // First lesson of unit is unlocked if previous unit's boss is beaten
            val prevUnit = CourseRepository.dsaCourse.units.firstOrNull { it.index == unitIndex - 1 } ?: return true
            val prevBossLesson = prevUnit.lessons.lastOrNull() ?: return true
            return completedLessonIds.contains(prevBossLesson.id)
        }
        // Lesson is unlocked if previous lesson in same unit is completed
        val prevLesson = unit.lessons.getOrNull(lessonIndex - 2) ?: return true
        return completedLessonIds.contains(prevLesson.id)
    }

    fun getNodeState(unitIndex: Int, lesson: DsaLesson): NodeState {
        val isCompleted = completedLessonIds.contains(lesson.id)
        val crowns = lessonCrowns[lesson.id] ?: 0
        return when {
            crowns >= 5 -> NodeState.LEGENDARY
            isCompleted -> NodeState.COMPLETED
            isLessonUnlocked(unitIndex, lesson.index) -> NodeState.AVAILABLE
            else -> NodeState.LOCKED
        }
    }

    fun registerCorrectAnswer() {
        val nextCombo = _combo.value + 1
        _combo.value = nextCombo
    }

    fun registerWrongAnswer() {
        _combo.value = 0
        AppState.loseHeart()
    }

    fun completeLesson(lesson: DsaLesson, scoreAccuracy: Float): Pair<Int, Int> {
        val base = lesson.xpReward
        val bonus = (scoreAccuracy * 10).toInt()
        val earnedXp = base + bonus
        val earnedGems = 5

        _totalXp.value += earnedXp
        _gems.value += earnedGems
        _combo.value = 0

        completedLessonIds = completedLessonIds + lesson.id
        val prevCrowns = lessonCrowns[lesson.id] ?: 0
        lessonCrowns = lessonCrowns + (lesson.id to (prevCrowns + 1).coerceAtMost(5))

        // Check for Phoenix evolution trigger
        checkEvolutionTriggers(lesson)

        return earnedXp to earnedGems
    }

    private fun checkEvolutionTriggers(lesson: DsaLesson) {
        val unit = CourseRepository.dsaCourse.units.firstOrNull { it.id == lesson.unitId } ?: return
        val triggers = unit.phoenixEvolutionsAwarded
        for (trigger in triggers) {
            val isMidTrigger = trigger.triggerId.contains("mid")
            val isBossTrigger = trigger.triggerId.contains("boss")
            val shouldTrigger = when {
                isBossTrigger && lesson.type == com.simats.duolingo.domain.model.LessonType.BOSS -> true
                isMidTrigger && lesson.index == (unit.lessons.size / 2) -> true
                else -> false
            }
            if (shouldTrigger && trigger.targetStage > AppState.activePhoenixStage) {
                pendingEvolutionStage = trigger.targetStage
                AppState.activePhoenixStage = trigger.targetStage
                break
            }
        }
    }

    fun clearPendingEvolution() {
        pendingEvolutionStage = null
    }

    fun refillHeartsWithGems(): Boolean {
        if (_gems.value >= 100) {
            _gems.value -= 100
            AppState.heartsCount = 5
            return true
        }
        return false
    }
}
