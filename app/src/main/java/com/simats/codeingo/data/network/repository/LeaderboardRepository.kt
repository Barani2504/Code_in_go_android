package com.simats.codeingo.data.network.repository

import android.util.Log
import com.simats.codeingo.data.network.api.CodeingoApiService
import com.simats.codeingo.data.network.model.ApiResult
import com.simats.codeingo.data.network.model.LeaderboardEntryDTO
import com.simats.codeingo.data.network.model.RankEntry
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.domain.GameManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepository @Inject constructor(
    private val apiService: CodeingoApiService,
    private val gameManager: GameManager
) {
    companion object {
        private const val TAG = "LeaderboardRepository"
        const val DEFAULT_COURSE_ID = "cns_defense"

        private val BENCHMARK_AVATARS = listOf("🦊", "🦉", "🦁", "🦄", "🐼", "🐯", "🐻", "🦅", "🐺", "🐲")

        private val DEFAULT_BENCHMARK_ROSTER = listOf(
            LeaderboardEntryDTO(username = "Vikram S.", totalXp = 540, currentStreak = 7, activeMascotStage = 3, courseId = DEFAULT_COURSE_ID),
            LeaderboardEntryDTO(username = "Sarah M.", totalXp = 480, currentStreak = 5, activeMascotStage = 2, courseId = DEFAULT_COURSE_ID),
            LeaderboardEntryDTO(username = "Alex K.", totalXp = 360, currentStreak = 4, activeMascotStage = 2, courseId = DEFAULT_COURSE_ID),
            LeaderboardEntryDTO(username = "Elena R.", totalXp = 310, currentStreak = 3, activeMascotStage = 1, courseId = DEFAULT_COURSE_ID),
            LeaderboardEntryDTO(username = "Chen W.", totalXp = 280, currentStreak = 3, activeMascotStage = 1, courseId = DEFAULT_COURSE_ID),
            LeaderboardEntryDTO(username = "Priya N.", totalXp = 220, currentStreak = 2, activeMascotStage = 1, courseId = DEFAULT_COURSE_ID),
            LeaderboardEntryDTO(username = "David L.", totalXp = 180, currentStreak = 1, activeMascotStage = 1, courseId = DEFAULT_COURSE_ID)
        )
    }

    private val _cachedRankings = MutableStateFlow<List<RankEntry>>(emptyList())
    val cachedRankings: StateFlow<List<RankEntry>> = _cachedRankings.asStateFlow()

    /**
     * Fetches leaderboard from remote backend with course scoping (`courseId = "cns_defense"`).
     * If network fails or returns empty, falls back gracefully to cached or benchmark roster.
     * Guarantees that the UI never crashes or displays an empty list.
     */
    suspend fun getRankings(
        league: String = "BRONZE",
        courseId: String = DEFAULT_COURSE_ID,
        forceRefresh: Boolean = false
    ): List<RankEntry> = withContext(Dispatchers.IO) {
        if (!forceRefresh && _cachedRankings.value.isNotEmpty()) {
            return@withContext reconcileWithLiveUserState(_cachedRankings.value)
        }

        val remoteList = fetchRemoteLeaderboard(league, courseId)
        val candidateEntries = if (remoteList.isNotEmpty()) {
            // Filter strictly for CNS curriculum track, discarding foreign DSA entries if backend mixes tracks
            remoteList.filter { it.courseId == null || it.courseId.isEmpty() || it.courseId.equals(courseId, ignoreCase = true) }
        } else {
            DEFAULT_BENCHMARK_ROSTER
        }

        val processed = processAndRankEntries(candidateEntries)
        _cachedRankings.value = processed
        processed
    }

    private suspend fun fetchRemoteLeaderboard(league: String, courseId: String): List<LeaderboardEntryDTO> {
        return try {
            val response = apiService.getLeaderboard(league = league, courseId = courseId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.w(TAG, "Backend returned HTTP ${response.code()} for leaderboard query")
                emptyList()
            }
        } catch (e: IOException) {
            Log.w(TAG, "Network unavailable for leaderboard: ${e.message}")
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error fetching leaderboard", e)
            emptyList()
        }
    }

    /**
     * Resolves avatars, live-injects current user if absent, sorts numerically descending by XP,
     * and assigns dynamic 1-based ranks.
     */
    fun processAndRankEntries(entries: List<LeaderboardEntryDTO>): List<RankEntry> {
        val currentUser = AuthService.shared.currentUser.value
        val currentEmail = currentUser?.email?.trim()?.lowercase().orEmpty()
        val currentName = currentUser?.name?.trim().orEmpty()
        val currentHandle = currentUser?.handle?.trim()?.lowercase().orEmpty()
        val liveXP = gameManager.totalXP.value
        val liveStreak = gameManager.streakDays.value

        var userFound = false
        val rankEntries = mutableListOf<RankEntry>()

        for (entry in entries) {
            val entryEmail = entry.email?.trim()?.lowercase().orEmpty()
            val entryUsername = entry.username.trim().lowercase()

            val isUser = (currentEmail.isNotEmpty() && entryEmail == currentEmail) ||
                    (currentHandle.isNotEmpty() && entryUsername == currentHandle) ||
                    (currentName.isNotEmpty() && entryUsername == currentName.lowercase())

            if (isUser) {
                userFound = true
                rankEntries.add(
                    RankEntry(
                        rank = 0,
                        name = if (currentName.isNotEmpty()) "$currentName (You)" else "You (Operative)",
                        xp = maxOf(entry.totalXp ?: 0, liveXP),
                        avatar = "👤",
                        isUser = true,
                        streak = maxOf(entry.currentStreak ?: 0, liveStreak)
                    )
                )
            } else {
                val avatar = resolveAvatar(entry)
                rankEntries.add(
                    RankEntry(
                        rank = 0,
                        name = entry.username.ifEmpty { "Cadet" },
                        xp = entry.totalXp ?: 0,
                        avatar = avatar,
                        isUser = false,
                        streak = entry.currentStreak ?: 1
                    )
                )
            }
        }

        // Live injection: Ensure current user is always represented on the leaderboard
        if (!userFound) {
            val displayName = if (currentName.isNotEmpty()) "$currentName (You)" else "You (Operative)"
            rankEntries.add(
                RankEntry(
                    rank = 0,
                    name = displayName,
                    xp = liveXP,
                    avatar = "👤",
                    isUser = true,
                    streak = liveStreak
                )
            )
        }

        // Sort descending by XP, with streak as tie-breaker
        val sorted = rankEntries.sortedWith(
            compareByDescending<RankEntry> { it.xp }
                .thenByDescending { it.streak }
        )

        // Assign 1-based sequential ranks
        return sorted.mapIndexed { index, item ->
            item.copy(rank = index + 1)
        }
    }

    private fun reconcileWithLiveUserState(list: List<RankEntry>): List<RankEntry> {
        val liveXP = gameManager.totalXP.value
        val liveStreak = gameManager.streakDays.value

        val updated = list.map { entry ->
            if (entry.isUser) {
                entry.copy(
                    xp = maxOf(entry.xp, liveXP),
                    streak = maxOf(entry.streak, liveStreak)
                )
            } else {
                entry
            }
        }

        return updated.sortedWith(
            compareByDescending<RankEntry> { it.xp }
                .thenByDescending { it.streak }
        ).mapIndexed { index, item ->
            item.copy(rank = index + 1)
        }
    }

    private fun resolveAvatar(entry: LeaderboardEntryDTO): String {
        val stage = entry.activeMascotStage ?: 1
        val index = Math.abs(entry.username.hashCode() + stage) % BENCHMARK_AVATARS.size
        return BENCHMARK_AVATARS[index]
    }
}
