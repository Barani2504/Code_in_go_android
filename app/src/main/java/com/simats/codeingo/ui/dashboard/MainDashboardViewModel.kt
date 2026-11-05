package com.simats.codeingo.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.codeingo.data.network.NetworkConfig
import com.simats.codeingo.data.network.NetworkEnvironment
import com.simats.codeingo.data.network.model.AchievementDTO
import com.simats.codeingo.data.network.model.ApiResult
import com.simats.codeingo.data.network.model.DailyQuestItem
import com.simats.codeingo.data.network.model.SocialEventDTO
import com.simats.codeingo.data.network.model.SystemAnnouncementDTO
import com.simats.codeingo.data.network.model.UpdateProfileRequest
import com.simats.codeingo.data.network.repository.ProgressOutboxRepository
import com.simats.codeingo.data.network.repository.SyncRepository
import com.simats.codeingo.domain.GameManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SyncUiStatus {
    object Idle : SyncUiStatus()
    object Syncing : SyncUiStatus()
    data class Synced(val message: String = "Synchronized with Common Backend") : SyncUiStatus()
    data class Error(val message: String) : SyncUiStatus()
}

@HiltViewModel
class MainDashboardViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val progressOutboxRepository: ProgressOutboxRepository,
    private val networkConfig: NetworkConfig,
    val gameManager: GameManager
) : ViewModel() {

    private val _syncStatus = MutableStateFlow<SyncUiStatus>(SyncUiStatus.Idle)
    val syncStatus: StateFlow<SyncUiStatus> = _syncStatus.asStateFlow()

    private val _dailyQuests = MutableStateFlow<List<DailyQuestItem>>(emptyList())
    val dailyQuests: StateFlow<List<DailyQuestItem>> = _dailyQuests.asStateFlow()

    private val _systemAnnouncement = MutableStateFlow<String?>(null)
    val systemAnnouncement: StateFlow<String?> = _systemAnnouncement.asStateFlow()

    private val _achievements = MutableStateFlow<List<AchievementDTO>>(emptyList())
    val achievements: StateFlow<List<AchievementDTO>> = _achievements.asStateFlow()

    private val _socialFeed = MutableStateFlow<List<SocialEventDTO>>(emptyList())
    val socialFeed: StateFlow<List<SocialEventDTO>> = _socialFeed.asStateFlow()

    val currentEnvironment: StateFlow<NetworkEnvironment> = networkConfig.currentEnvironment
    val currentBaseUrl: StateFlow<String> = networkConfig.currentBaseUrl

    val pendingSyncCount: StateFlow<Int> = progressOutboxRepository.getPendingCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // Initial sync on dashboard load: reconciles user stats & remote curriculum progress
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            _syncStatus.value = SyncUiStatus.Syncing

            // 1. Fetch live user profile (XP, gems, hearts, streak)
            val userResult = syncRepository.syncUserProfileToLocal()

            // 2. Fetch live curriculum progress (CNS course nodes)
            val progressResult = syncRepository.syncRemoteProgressToLocal()

            // 3. Fetch live daily quests
            loadDailyQuests()

            // 4. Fetch live system broadcast announcement
            loadSystemAnnouncement()

            // 5. Fetch achievements
            loadAchievements()

            // 6. Fetch community social feed
            loadSocialFeed()

            // 7. Drain any pending local completions to the common backend
            progressOutboxRepository.uploadPendingCompletions()

            if (userResult is ApiResult.Success || progressResult is ApiResult.Success) {
                _syncStatus.value = SyncUiStatus.Synced("Connected to Common Backend")
            } else if (userResult is ApiResult.NetworkError) {
                _syncStatus.value = SyncUiStatus.Error("Working in Offline Cache Mode")
            } else {
                _syncStatus.value = SyncUiStatus.Idle
            }
        }
    }

    fun loadSystemAnnouncement() {
        viewModelScope.launch {
            when (val result = syncRepository.fetchSystemAnnouncement()) {
                is ApiResult.Success -> {
                    val text = result.data.announcement?.trim()
                    _systemAnnouncement.value = if (!text.isNullOrEmpty()) text else null
                }
                else -> {
                    // Silently ignore if unconfigured or offline
                }
            }
        }
    }

    fun dismissAnnouncement() {
        _systemAnnouncement.value = null
    }

    fun loadAchievements() {
        viewModelScope.launch {
            when (val result = syncRepository.fetchAchievements()) {
                is ApiResult.Success -> {
                    _achievements.value = result.data
                }
                else -> {
                    // Fallback to local defaults if remote unreachable
                    if (_achievements.value.isEmpty()) {
                        _achievements.value = listOf(
                            AchievementDTO(
                                id = "first_defense",
                                title = "Cryptographic Sentinel",
                                description = "Completed your first Cryptography defense mission",
                                emoji = "🛡️",
                                xpReward = 50,
                                gemReward = 25,
                                category = "MILESTONE",
                                targetValue = 1,
                                currentValue = 1,
                                isUnlocked = true,
                                isClaimed = false
                            ),
                            AchievementDTO(
                                id = "streak_igniter",
                                title = "Streak Igniter",
                                description = "Maintain a 3-day cipher streak",
                                emoji = "🔥",
                                xpReward = 60,
                                gemReward = 30,
                                category = "STREAK",
                                targetValue = 3,
                                currentValue = gameManager.streakDays.value,
                                isUnlocked = gameManager.streakDays.value >= 3,
                                isClaimed = false
                            ),
                            AchievementDTO(
                                id = "rsa_master",
                                title = "RSA Keymaster",
                                description = "Master public-key primes and Euler totient",
                                emoji = "🔑",
                                xpReward = 100,
                                gemReward = 50,
                                category = "ASYMMETRIC",
                                targetValue = 1,
                                currentValue = 0,
                                isUnlocked = false,
                                isClaimed = false
                            )
                        )
                    }
                }
            }
        }
    }

    fun claimAchievement(id: String, onClaimed: (xp: Int, gems: Int) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val item = _achievements.value.firstOrNull { it.id == id } ?: return@launch
            if (item.isUnlocked && !item.isClaimed) {
                gameManager.addXP(item.xpReward)
                gameManager.addGems(item.gemReward)
                onClaimed(item.xpReward, item.gemReward)

                _achievements.value = _achievements.value.map {
                    if (it.id == id) it.copy(isClaimed = true) else it
                }

                syncRepository.claimAchievement(id)
            }
        }
    }

    fun loadSocialFeed() {
        viewModelScope.launch {
            when (val result = syncRepository.fetchSocialFeed()) {
                is ApiResult.Success -> {
                    _socialFeed.value = result.data
                }
                else -> {
                    // Keep existing feed or empty
                }
            }
        }
    }

    fun cheerSocialEvent(id: Int) {
        viewModelScope.launch {
            _socialFeed.value = _socialFeed.value.map { event ->
                if (event.id == id) {
                    event.copy(cheerCount = event.cheerCount + 1)
                } else event
            }
            syncRepository.cheerSocialEvent(id)
        }
    }

    fun updateDisplayName(name: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val trimmed = name.trim()
            if (trimmed.isEmpty()) {
                onError("Display name cannot be empty")
                return@launch
            }
            when (val result = syncRepository.updateProfile(UpdateProfileRequest(name = trimmed))) {
                is ApiResult.Success -> onSuccess()
                is ApiResult.HttpError -> onError(result.errorMessage())
                is ApiResult.NetworkError -> {
                    onSuccess() // optimistic local update
                }
                else -> onError("Failed to update display name")
            }
        }
    }

    fun updateUsername(username: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val trimmed = username.trim().lowercase()
            if (trimmed.isEmpty()) {
                onError("Username cannot be empty")
                return@launch
            }
            when (val result = syncRepository.updateProfile(UpdateProfileRequest(username = trimmed))) {
                is ApiResult.Success -> onSuccess()
                is ApiResult.HttpError -> onError(result.errorMessage())
                is ApiResult.NetworkError -> {
                    onSuccess() // optimistic local update
                }
                else -> onError("Failed to update username")
            }
        }
    }

    fun loadDailyQuests() {
        viewModelScope.launch {
            when (val result = syncRepository.fetchDailyQuests()) {
                is ApiResult.Success -> {
                    _dailyQuests.value = result.data
                }
                else -> {
                    // Fallback to local default quests if remote is unreachable
                    if (_dailyQuests.value.isEmpty()) {
                        _dailyQuests.value = listOf(
                            DailyQuestItem(
                                id = "quest_daily_lesson",
                                title = "Cryptographic Protocol Defense",
                                requirement = "Complete 1 security node",
                                targetCount = 1,
                                currentCount = 0,
                                xpReward = 15,
                                gemReward = 10
                            ),
                            DailyQuestItem(
                                id = "quest_daily_review",
                                title = "Mistake Vault Patching",
                                requirement = "Review 2 vulnerability cards",
                                targetCount = 2,
                                currentCount = 0,
                                xpReward = 20,
                                gemReward = 15
                            )
                        )
                    }
                }
            }
        }
    }

    fun claimDailyChestReward(xp: Int = 20, gems: Int = 15, onClaimed: (Int, Int) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            // Apply immediate local reward
            gameManager.addXP(xp)
            gameManager.addGems(gems)
            onClaimed(xp, gems)

            // Persist to common backend
            syncRepository.claimDailyReward(xp = xp, gems = gems)
        }
    }

    fun claimQuest(questId: String) {
        viewModelScope.launch {
            val quest = _dailyQuests.value.firstOrNull { it.id == questId } ?: return@launch
            if (!quest.isClaimed && quest.isCompleted) {
                gameManager.addXP(quest.xpReward)
                gameManager.addGems(quest.gemReward)

                _dailyQuests.value = _dailyQuests.value.map {
                    if (it.id == questId) it.copy(isClaimed = true) else it
                }

                syncRepository.claimQuest(questId)
            }
        }
    }

    fun buyStreakFreeze(price: Int = 50, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            if (gameManager.gemsCount.value < price) {
                onError("Not enough gems (${gameManager.gemsCount.value}/$price)")
                return@launch
            }
            gameManager.addGems(-price)
            onSuccess()
            syncRepository.buyStreakFreeze(price = price)
        }
    }

    fun refillHearts(price: Int = 35, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            if (gameManager.gemsCount.value < price) {
                onError("Not enough gems (${gameManager.gemsCount.value}/$price)")
                return@launch
            }
            gameManager.addGems(-price)
            gameManager.refillHearts()
            onSuccess()
            syncRepository.buyHeartRefill(price = price, hearts = 10)
        }
    }

    fun setNetworkEnvironment(environment: NetworkEnvironment) {
        networkConfig.setEnvironment(environment)
        refreshAll()
    }
}
