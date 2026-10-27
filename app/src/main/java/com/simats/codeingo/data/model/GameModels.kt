package com.simats.codeingo.data.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import java.util.UUID

@Immutable
@Serializable
data class MistakeVaultItem(
    val id: String = UUID.randomUUID().toString(),
    val prompt: String,
    val conceptId: String,
    val wrongAnswerGiven: String,
    val correctAnswer: String,
    val explanation: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRepaired: Boolean = false,
    val conceptTag: String = "",
    val misconceptionTag: String = "",
    val chapterId: Int = 1
)

@Immutable
@Serializable
data class DailyQuestItem(
    val id: String,
    val title: String,
    val requirement: String,
    val targetCount: Int,
    val currentCount: Int,
    val xpReward: Int,
    val gemReward: Int,
    val isClaimed: Boolean = false
) {
    val isCompleted: Boolean
        get() = currentCount >= targetCount
}
