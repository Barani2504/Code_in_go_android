package com.simats.codeingo.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntryDTO(
    val id: Int? = null,
    val username: String = "",
    val email: String? = null,
    val totalXp: Int? = 0,
    val league: String? = "BRONZE",
    val currentStreak: Int? = 1,
    val hearts: Int? = 5,
    val gems: Int? = 0,
    val activeMascotStage: Int? = 1,
    val rank: Int? = null,
    val courseId: String? = null
)

@Serializable
data class RankEntry(
    val rank: Int,
    val name: String,
    val xp: Int,
    val avatar: String,
    val isUser: Boolean = false,
    val streak: Int = 0
)

enum class LeagueTier(
    val code: String,
    val displayName: String,
    val badgeEmoji: String,
    val promotionSubtitle: String,
    val promotionDividerLabel: String
) {
    BRONZE(
        code = "BRONZE",
        displayName = "Junior League",
        badgeEmoji = "🥉",
        promotionSubtitle = "Top 3 advance to Silver League!",
        promotionDividerLabel = "PROMOTION ZONE (TOP 3 ADVANCE)"
    ),
    SILVER(
        code = "SILVER",
        displayName = "Silver League",
        badgeEmoji = "🥈",
        promotionSubtitle = "Top 3 advance to Gold League!",
        promotionDividerLabel = "PROMOTION ZONE (TOP 3 ADVANCE)"
    ),
    GOLD(
        code = "GOLD",
        displayName = "Gold League",
        badgeEmoji = "🥇",
        promotionSubtitle = "Top 3 advance to Obsidian League!",
        promotionDividerLabel = "PROMOTION ZONE (TOP 3 ADVANCE)"
    ),
    DIAMOND(
        code = "DIAMOND",
        displayName = "Obsidian League",
        badgeEmoji = "💎",
        promotionSubtitle = "Top 3 claim the Obsidian Crown! 👑",
        promotionDividerLabel = "CHAMPIONS PODIUM (TOP 3 WIN REWARDS)"
    );

    companion object {
        fun fromCode(code: String?): LeagueTier {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: BRONZE
        }
    }
}
