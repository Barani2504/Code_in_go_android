package com.simats.codeingo.ui.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import com.simats.codeingo.data.local.UserPreferences
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val gameManager: GameManager,
    private val emotionManager: PhoenixEmotionManager,
    private val userPreferences: UserPreferences? = null
) : ViewModel() {

    val isInitialized: StateFlow<Boolean> = gameManager.isInitialized
    val totalXP: StateFlow<Int> = gameManager.totalXP
    val streakDays: StateFlow<Int> = gameManager.streakDays
    val heartsCount: StateFlow<Int> = gameManager.heartsCount
    val gemsCount: StateFlow<Int> = gameManager.gemsCount
    val activePhoenixStage: StateFlow<Int> = gameManager.activePhoenixStage
    val highestCombo: StateFlow<Int> = gameManager.highestCombo
    val activeLevelIndex: StateFlow<Int> = gameManager.activeLevelIndex

    val currentEmotion = emotionManager.currentEmotion
    val isAutoEmotionEnabled = emotionManager.isAutoEmotionEnabled

    fun refillHearts() {
        gameManager.refillHearts()
    }

    fun buyHearts(count: Int, gemPrice: Int): Pair<Boolean, String> {
        return gameManager.buyHearts(count, gemPrice)
    }

    fun regenerateHeart() {
        gameManager.regenerateHeart()
    }

    fun triggerMascotTapReaction() {
        emotionManager.triggerMascotTapReaction()
    }
}
