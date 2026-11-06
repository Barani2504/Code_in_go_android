
package com.simats.codeingo.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String? = null,
    val name: String? = null,
    val age: Int? = null,
    val codingGoal: String? = "cns",
    val activeCourseId: String? = "cns_defense"
)

@Serializable
data class GuestStartRequest(
    val activeCourseId: String = "cns_defense"
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)

@Serializable
data class UserDTO(
    val id: Int? = null,
    val username: String,
    val email: String,
    val name: String? = null,
    val role: String? = "USER",
    val totalXp: Int? = 0,
    val hearts: Int? = 5,
    val gems: Int? = 0,
    val bones: Int? = 0,
    val currentStreak: Int? = 1,
    val highestStreak: Int? = 1,
    val league: String? = "BRONZE",
    val activeMascotStage: Int? = 1,
    val activePhoenixStage: Int? = 1,
    val completedLessonIds: List<String>? = emptyList(),
    val selectedLanguage: String? = "python",
    val codingGoal: String? = "cns",
    val activeCourseId: String? = "cns_defense",
    val streakGoal: Int? = null,
    val soundEnabled: Boolean? = true,
    val hapticsEnabled: Boolean? = true,
    val equippedHat: String? = null,
    val equippedOutfit: String? = null,
    val equippedAccessory: String? = null,
    val equippedEffect: String? = null,
    val ownedCosmeticIds: List<String>? = emptyList(),
    val isPremium: Boolean? = false,
    val isGuest: Boolean? = false,
    val unlockedAchievements: List<String>? = emptyList(),
    val claimedQuestIds: List<String>? = emptyList(),
    val claimedAchievementIds: List<String>? = emptyList()
)

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String? = null,
    val user: UserDTO? = null,
    val message: String? = null,
    val id: Int? = null,
    val username: String? = null,
    val email: String? = null,
    val name: String? = null,
    val totalXp: Int? = 0,
    val hearts: Int? = 5,
    val gems: Int? = 0,
    val currentStreak: Int? = 1,
    val activePhoenixStage: Int? = 1,
    val activeCourseId: String? = "cns_defense"
) {
    val resolvedUser: UserDTO
        get() = user ?: UserDTO(
            id = id,
            username = username ?: "operative",
            email = email ?: "",
            name = name,
            totalXp = totalXp,
            hearts = hearts,
            gems = gems,
            currentStreak = currentStreak,
            activePhoenixStage = activePhoenixStage,
            activeCourseId = activeCourseId ?: "cns_defense"
        )
}

@Serializable
data class UpdateProfileRequest(
    val name: String? = null,
    val username: String? = null,
    val age: Int? = null,
    val activeCourseId: String? = null,
    val codingGoal: String? = null,
    val selectedLanguage: String? = null,
    val avatarUrl: String? = null,
    val fcmDeviceToken: String? = null,
    val activePhoenixStage: Int? = null,
    val currentStreak: Int? = null,
    val highestStreak: Int? = null,
    val soundEnabled: Boolean? = null,
    val hapticsEnabled: Boolean? = null,
    val themeMode: String? = null
)

@Serializable
data class LinkAccountRequest(
    val email: String,
    val password: String,
    val name: String? = null
)
