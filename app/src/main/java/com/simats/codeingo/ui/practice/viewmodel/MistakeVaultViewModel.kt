package com.simats.codeingo.ui.practice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.codeingo.data.db.MistakeVaultDao
import com.simats.codeingo.data.db.MistakeVaultEntity
import com.simats.codeingo.data.model.MistakeVaultItem
import com.simats.codeingo.data.network.model.ApiResult
import com.simats.codeingo.data.network.repository.SyncRepository
import com.simats.codeingo.domain.GameManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MistakeVaultViewModel @Inject constructor(
    private val mistakeVaultDao: MistakeVaultDao,
    private val syncRepository: SyncRepository? = null,
    private val gameManager: GameManager
) : ViewModel() {

    val mistakeVaultItems: StateFlow<List<MistakeVaultItem>> = mistakeVaultDao
        .getAllMistakesFlow()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        syncRemoteMistakes()
    }

    fun syncRemoteMistakes() {
        val repo = syncRepository ?: return
        viewModelScope.launch {
            when (val result = repo.fetchMistakeReviewQuestions(limit = 10)) {
                is ApiResult.Success -> {
                    val remoteItems = result.data
                    for (item in remoteItems) {
                        val id = "remote_q_${item.questionId}"
                        // Use getMistakeById (actual DAO method) instead of findById
                        val existing = mistakeVaultDao.getMistakeById(id)
                        if (existing == null) {
                            mistakeVaultDao.insertOrUpdate(
                                MistakeVaultEntity(
                                    id = id,
                                    // Map remote DTO fields to actual MistakeVaultEntity fields
                                    prompt = item.questionText.ifEmpty { "Cryptographic Vulnerability" },
                                    conceptId = item.topic,
                                    wrongAnswerGiven = "Misconfigured parameter",
                                    correctAnswer = "Correct standard padding / MAC verification",
                                    explanation = item.explanation.ifEmpty {
                                        "Verify cryptographic authenticity before decrypting."
                                    },
                                    timestamp = System.currentTimeMillis(),
                                    isRepaired = false,
                                    conceptTag = item.topic,
                                    misconceptionTag = "",
                                    chapterId = 1,
                                    attemptCount = 1
                                )
                            )
                        }
                    }
                }
                else -> {
                    // Gracefully rely on local Room database; no crash on network failure
                }
            }
        }
    }

    fun markMistakeRepaired(id: String) {
        viewModelScope.launch {
            mistakeVaultDao.markRepaired(id)
        }
    }

    fun deleteMistake(id: String) {
        viewModelScope.launch {
            mistakeVaultDao.deleteById(id)
        }
    }

    fun clearVault() {
        viewModelScope.launch {
            mistakeVaultDao.clearAll()
        }
    }
}
