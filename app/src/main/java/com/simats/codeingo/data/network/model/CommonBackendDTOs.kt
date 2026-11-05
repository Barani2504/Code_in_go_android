package com.simats.codeingo.data.network.model

import kotlinx.serialization.Serializable

/**
 * COMMON SHARED BACKEND DTO CONTRACT
 *
 * Direct Kotlin serialization parity with iOS APIDTOs.swift.
 * Shared backend endpoint requests and responses across Android (CNS) and iOS (DSA).
 */

// -------------------------------------------------------------------------
// Game Sessions and Answers (/api/game/sessions/*)
// -------------------------------------------------------------------------

@Serializable
data class StartSessionResponse(
    val sessionId: Int = 0,
    val lessonId: String = "",
    val sessionType: String? = null,
    val difficultyBand: String? = null,
    val totalQuestions: Int = 5,
    val userHearts: Int = 5,
    val userXp: Int = 0,
    val userGems: Int = 0,
    val userId: Int = 0,
    val username: String = ""
)

@Serializable
data class AnswerRequest(
    val questionId: Int,
    val userAnswer: String,
    val baseXp: Int = 10,
    val timeTakenMs: Int = 1000,
    val explanation: String? = null
)

@Serializable
data class SubmitAnswerResponse(
    val isCorrect: Boolean = true,
    val xpEarned: Int = 10,
    val speedBonus: Int = 0,
    val explanation: String? = null,
    val heartsRemaining: Int = 5,
    val totalXp: Int = 0,
    val enterLearningPhase: Boolean = false,
    val gems: Int = 0,
    val isFireMode: Boolean = false,
    val currentCombo: Int = 1
)

@Serializable
data class CompleteSessionRequest(
    val accuracy: Double = 100.0,
    val totalTimeSeconds: Int = 60,
    val heartsRemaining: Int = 5
)

@Serializable
data class CompleteSessionResponse(
    val sessionId: Int? = null,
    val xpGained: Int? = null,
    val gemsAwarded: Int? = null,
    val accuracy: Double? = null,
    val perfectBonus: Boolean? = null,
    val updatedStreak: Int? = null,
    val heartsRemaining: Int? = null
)

// -------------------------------------------------------------------------
// User Economy and Heart Refill (/api/users/me/*)
// -------------------------------------------------------------------------

@Serializable
data class RefillHeartsResponse(
    val success: Boolean? = true,
    val hearts: Int? = 5,
    val gemsRemaining: Int? = null,
    val gems: Int? = null,
    val message: String? = null
)

@Serializable
data class CosmeticPurchaseRequest(
    val cosmeticId: String,
    val price: Int,
    val category: String = "hats"
)

// -------------------------------------------------------------------------
// Daily Quests and Missions (/api/users/me/quests)
// -------------------------------------------------------------------------

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

// -------------------------------------------------------------------------
// Dynamic Learning Goals (/api/courses/goals)
// -------------------------------------------------------------------------

@Serializable
data class CodingGoalDTO(
    val id: String,
    val title: String,
    val description: String = "",
    val icon: String = "",
    val tag: String = "SECURITY",
    val courseTrackId: String? = "cns_defense",
    val startingUnitId: String? = null,
    val curriculumHighlights: String? = null,
    val dailyMinutes: Int? = 15,
    val enabled: Boolean? = true
)

@Serializable
data class CodingGoalsResponseDTO(
    val pageTitle: String? = "Choose Your Defense Path",
    val pageSubtitle: String? = "Master Cryptography & Network Security",
    val defaultGoalId: String? = "cns_defense",
    val goals: List<CodingGoalDTO> = emptyList()
)

// -------------------------------------------------------------------------
// Bug and Issue Reporting (/api/reports)
// -------------------------------------------------------------------------

@Serializable
data class ReportIssueRequest(
    val target: String,
    val reason: String
)

// -------------------------------------------------------------------------
// Mistake Review Questions (/api/game/practice/mistakes)
// -------------------------------------------------------------------------

@Serializable
data class MistakeReviewDTO(
    val questionId: Int = 0,
    val questionText: String = "",
    val topic: String = "General Cryptography",
    val timesFailed: Int = 1,
    val explanation: String = ""
)

// -------------------------------------------------------------------------
// System Announcement & Broadcast (/api/courses/announcement)
// -------------------------------------------------------------------------

@Serializable
data class SystemAnnouncementDTO(
    val announcement: String? = null,
    val maintenanceMode: Boolean? = false,
    val xpMultiplier: Double? = 1.0,
    val serverTime: String? = null
)

// -------------------------------------------------------------------------
// Persistent Achievements System (/api/users/me/achievements)
// -------------------------------------------------------------------------

@Serializable
data class AchievementDTO(
    val id: String,
    val title: String,
    val description: String? = null,
    val emoji: String = "🏆",
    val xpReward: Int = 50,
    val gemReward: Int = 25,
    val category: String = "GENERAL",
    val targetValue: Int = 1,
    val currentValue: Int? = 0,
    val isUnlocked: Boolean = false,
    val isClaimed: Boolean = false
)

// -------------------------------------------------------------------------
// Community Social Activity Feed (/api/users/social/feed)
// -------------------------------------------------------------------------

@Serializable
data class SocialEventDTO(
    val id: Int,
    val userId: Int,
    val username: String,
    val userDisplayName: String? = null,
    val avatarEmoji: String? = "⚡",
    val eventType: String = "MILESTONE",
    val title: String,
    val description: String? = null,
    val timestamp: String? = null,
    val cheerCount: Int = 0,
    val cheeredUserIds: List<Int>? = emptyList()
)
