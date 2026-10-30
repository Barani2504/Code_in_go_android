package com.simats.codeingo.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ProgressStatusDTO(
    val completedLessons: List<String>? = emptyList(),
    val completedUnits: List<String>? = emptyList(),
    val currentUnitIndex: Int? = 1,
    val currentLessonId: String? = null,
    val totalXp: Int? = null,
    val hearts: Int? = null,
    val gems: Int? = null,
    val streakDays: Int? = null
)

@Serializable
data class BackendLessonCompleteRequest(
    val lessonId: String,
    val accuracyPercentage: Int = 100,
    val completedAtEpochMs: Long? = null,
    val idempotencyKey: String? = null
)

@Serializable
data class BackendLessonCompleteResponse(
    val xpEarned: Int? = 0,
    val totalXp: Int? = null,
    val accuracyPercentage: Int? = null,
    val currentStreak: Int? = null,
    val streakExtendedToday: Boolean? = false,
    val gemsEarned: Int? = 0,
    val totalGems: Int? = null
)

@Serializable
data class UserProgressItemDTO(
    val id: Int? = null,
    val userId: Int? = null,
    val lessonId: String,
    val crowns: Int? = null,
    val isCompleted: Boolean? = null,
    val lastPlayedAt: String? = null
)

@Serializable
data class GenericApiResponse(
    val message: String? = null,
    val error: String? = null,
    val success: Boolean? = null,
    @Serializable(with = LenientStringSerializer::class)
    val status: String? = null,
    val debugCode: String? = null
)
