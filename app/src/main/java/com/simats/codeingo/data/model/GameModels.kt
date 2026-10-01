package com.simats.codeingo.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class MistakeVaultItem(
    val id: String = UUID.randomUUID().toString(),
    val prompt: String,
    val conceptId: String,
    val wrongAnswerGiven: String,
    val correctAnswer: String,
    val explanation: String,
    val timestamp: Long = System.currentTimeMillis(),
    var isRepaired: Boolean = false
)

@Serializable
data class DailyQuestItem(
    val id: String,
    val title: String,
    val requirement: String,
    val targetCount: Int,
    var currentCount: Int,
    val xpReward: Int,
    val gemReward: Int,
    var isClaimed: Boolean = false
) {
    val isCompleted: Boolean
        get() = currentCount >= targetCount
}
