package com.simats.codeingo.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.simats.codeingo.data.model.MistakeVaultItem
import java.util.UUID

/**
 * Room Entity representing a mistake logged during quizzes, lessons, or arenas.
 *
 * Indexed for fast lookups by concept, chapter, and timestamp.
 * Includes a unique composite index on [prompt, wrongAnswerGiven] so repeat mistakes
 * increment attempt counts instead of duplicating records.
 */
@Entity(
    tableName = "mistake_vault",
    indices = [
        Index(value = ["conceptId"]),
        Index(value = ["chapterId"]),
        Index(value = ["timestamp"]),
        Index(value = ["prompt", "wrongAnswerGiven"], unique = true)
    ]
)
data class MistakeVaultEntity(
    @PrimaryKey
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
    val chapterId: Int = 1,
    val attemptCount: Int = 1
) {
    fun toDomain(): MistakeVaultItem = MistakeVaultItem(
        id = id,
        prompt = prompt,
        conceptId = conceptId,
        wrongAnswerGiven = wrongAnswerGiven,
        correctAnswer = correctAnswer,
        explanation = explanation,
        timestamp = timestamp,
        isRepaired = isRepaired,
        conceptTag = conceptTag,
        misconceptionTag = misconceptionTag,
        chapterId = chapterId
    )

    companion object {
        fun fromDomain(item: MistakeVaultItem, attemptCount: Int = 1): MistakeVaultEntity =
            MistakeVaultEntity(
                id = item.id,
                prompt = item.prompt,
                conceptId = item.conceptId,
                wrongAnswerGiven = item.wrongAnswerGiven,
                correctAnswer = item.correctAnswer,
                explanation = item.explanation,
                timestamp = item.timestamp,
                isRepaired = item.isRepaired,
                conceptTag = item.conceptTag,
                misconceptionTag = item.misconceptionTag,
                chapterId = item.chapterId,
                attemptCount = attemptCount
            )
    }
}
