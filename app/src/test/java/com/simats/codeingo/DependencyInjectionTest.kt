package com.simats.codeingo

import com.simats.codeingo.data.db.MistakeVaultDao
import com.simats.codeingo.data.db.MistakeVaultEntity
import com.simats.codeingo.data.local.UserPreferences
import com.simats.codeingo.di.DomainModule
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.domain.ThemeManager
import com.simats.codeingo.ui.dashboard.viewmodel.DashboardViewModel
import com.simats.codeingo.ui.practice.viewmodel.MistakeVaultViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Test
import java.lang.reflect.Proxy

class DependencyInjectionTest {

    @Test
    fun testDomainModule_thinFacadeParity() {
        val gmFromHilt = DomainModule.provideGameManager()
        val pemFromHilt = DomainModule.providePhoenixEmotionManager()
        val tmFromHilt = DomainModule.provideThemeManager()
        val lmFromHilt = DomainModule.provideLocalizationManager()
        val authFromHilt = DomainModule.provideAuthService()

        // Assert Hilt provides the exact same singleton instance as the legacy static facades
        assertSame("GameManager singleton instance matches", GameManager.instance, gmFromHilt)
        assertSame("PhoenixEmotionManager singleton instance matches", PhoenixEmotionManager.instance, pemFromHilt)
        assertSame("ThemeManager singleton instance matches", ThemeManager.instance, tmFromHilt)
        assertSame("LocalizationManager singleton instance matches", LocalizationManager.instance, lmFromHilt)
        assertSame("AuthService singleton instance matches", AuthService.instance, authFromHilt)
    }

    @Test
    fun testDashboardViewModel_delegatesToInjectedGameManager() {
        val gm = GameManager.instance
        val pem = PhoenixEmotionManager.instance

        val vm = DashboardViewModel(
            gameManager = gm,
            emotionManager = pem,
            userPreferences = null
        )

        assertNotNull(vm.isInitialized)
        assertEquals(gm.heartsCount.value, vm.heartsCount.value)
        assertEquals(gm.gemsCount.value, vm.gemsCount.value)
        assertEquals(gm.totalXP.value, vm.totalXP.value)
        assertEquals(gm.streakDays.value, vm.streakDays.value)
    }

    @Test
    fun testMistakeVaultViewModel_flowIntegration() {
        val fakeFlow = MutableStateFlow<List<MistakeVaultEntity>>(emptyList())
        val fakeDao = object : MistakeVaultDao {
            override fun getAllMistakesFlow(): Flow<List<MistakeVaultEntity>> = fakeFlow
            override fun getUnrepairedMistakesFlow(): Flow<List<MistakeVaultEntity>> = fakeFlow
            override suspend fun getMistakeById(id: String): MistakeVaultEntity? = fakeFlow.value.firstOrNull { it.id == id }
            override suspend fun findByPromptAndWrongAnswer(prompt: String, wrongAnswer: String): MistakeVaultEntity? =
                fakeFlow.value.firstOrNull { it.prompt == prompt && it.wrongAnswerGiven == wrongAnswer }
            override suspend fun insertOrUpdate(entity: MistakeVaultEntity) { fakeFlow.value = fakeFlow.value + entity }
            override suspend fun insertAll(entities: List<MistakeVaultEntity>) { fakeFlow.value = entities }
            override suspend fun markRepaired(id: String): Int = 1
            override suspend fun deleteById(id: String): Int = 1
            override suspend fun clearAll() { fakeFlow.value = emptyList() }
            override suspend fun getCount(): Int = fakeFlow.value.size
        }

        val vm = MistakeVaultViewModel(
            mistakeVaultDao = fakeDao,
            gameManager = GameManager.instance
        )

        assertNotNull(vm.mistakeVaultItems)
        assertEquals(0, vm.mistakeVaultItems.value.size)
    }
}
