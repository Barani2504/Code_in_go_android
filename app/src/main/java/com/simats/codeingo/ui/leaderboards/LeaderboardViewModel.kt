package com.simats.codeingo.ui.leaderboards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.codeingo.data.network.model.LeagueTier
import com.simats.codeingo.data.network.model.RankEntry
import com.simats.codeingo.data.network.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repository: LeaderboardRepository
) : ViewModel() {

    private val _currentLeague = MutableStateFlow(LeagueTier.BRONZE)
    val currentLeague: StateFlow<LeagueTier> = _currentLeague.asStateFlow()

    private val _rankings = MutableStateFlow<List<RankEntry>>(emptyList())
    val rankings: StateFlow<List<RankEntry>> = _rankings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val currentUserRank: StateFlow<RankEntry?> = _rankings
        .map { list -> list.firstOrNull { it.isUser } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        loadLeaderboard(forceRefresh = false)
    }

    fun loadLeaderboard(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getRankings(
                    league = _currentLeague.value.code,
                    forceRefresh = forceRefresh
                )
                _rankings.value = result
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectLeague(league: LeagueTier) {
        if (_currentLeague.value == league) return
        _currentLeague.value = league
        loadLeaderboard(forceRefresh = true)
    }

    fun refresh() {
        loadLeaderboard(forceRefresh = true)
    }
}
