package com.simats.codeingo.ui.practice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.codeingo.data.db.MistakeVaultDao
import com.simats.codeingo.data.model.MistakeVaultItem
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
