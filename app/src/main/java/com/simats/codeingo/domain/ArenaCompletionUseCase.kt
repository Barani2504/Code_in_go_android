package com.simats.codeingo.domain

import android.content.Context
import com.simats.codeingo.data.network.model.ApiResult
import com.simats.codeingo.data.network.repository.SyncRepository
import com.simats.codeingo.ui.arenas.common.ArenaProgressionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArenaCompletionUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gameManager: GameManager,
    private val syncRepository: SyncRepository
) {

    suspend fun execute(
        arenaKey: String,
        completedLevel: Int,
        totalLevelsInArena: Int,
        baseXpReward: Int
    ): Int = withContext(Dispatchers.IO) {
        val prefs = ArenaProgressionManager.getPrefs(context)
        val currentUnlocked = ArenaProgressionManager.getUnlockedLevels(prefs, arenaKey)
        val nextLevel = completedLevel + 1

        if (nextLevel <= totalLevelsInArena) {
            val updated = currentUnlocked + nextLevel
            ArenaProgressionManager.saveUnlockedLevels(prefs, arenaKey, updated)
        }

        // Optimistic local XP update
        withContext(Dispatchers.Main) {
            gameManager.addXP(baseXpReward)
        }

        // Authoritative server sync via lesson completion endpoint
        val lessonId = "${arenaKey}_lvl_${completedLevel}"
        try {
            when (val res = syncRepository.completeLesson(lessonId, 100)) {
                is ApiResult.Success -> {
                    res.data.xpEarned ?: baseXpReward
                }
                else -> baseXpReward
            }
        } catch (_: Exception) {
            baseXpReward
        }
    }
}

