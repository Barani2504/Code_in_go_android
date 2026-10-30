package com.simats.codeingo.di

import android.content.Context
import com.simats.codeingo.data.db.CodeingoDatabase
import com.simats.codeingo.data.db.MistakeVaultDao
import com.simats.codeingo.data.db.OfflineSyncDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CodeingoDatabase {
        return CodeingoDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideMistakeVaultDao(database: CodeingoDatabase): MistakeVaultDao {
        return database.mistakeVaultDao()
    }

    @Provides
    @Singleton
    fun provideOfflineSyncDao(database: CodeingoDatabase): OfflineSyncDao {
        return database.offlineSyncDao()
    }
}
