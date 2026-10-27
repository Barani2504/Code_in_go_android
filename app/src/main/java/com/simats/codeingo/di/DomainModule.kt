package com.simats.codeingo.di

import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.domain.ThemeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideGameManager(): GameManager = GameManager.instance

    @Provides
    @Singleton
    fun providePhoenixEmotionManager(): PhoenixEmotionManager = PhoenixEmotionManager.instance

    @Provides
    @Singleton
    fun provideThemeManager(): ThemeManager = ThemeManager.instance

    @Provides
    @Singleton
    fun provideLocalizationManager(): LocalizationManager = LocalizationManager.instance

    @Provides
    @Singleton
    fun provideAuthService(): AuthService = AuthService.instance
}
