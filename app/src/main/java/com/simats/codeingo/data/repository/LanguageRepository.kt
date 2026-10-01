package com.simats.codeingo.data.repository

import com.simats.codeingo.data.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class LanguageRepository {
    fun getLanguages(): Flow<List<Language>> = flowOf(Language.defaultLanguages)

    fun getLanguageByCode(code: String): Language? {
        return Language.defaultLanguages.firstOrNull { it.code.equals(code, ignoreCase = true) }
    }
}
