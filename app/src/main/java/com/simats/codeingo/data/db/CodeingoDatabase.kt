package com.simats.codeingo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MistakeVaultEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CodeingoDatabase : RoomDatabase() {

    abstract fun mistakeVaultDao(): MistakeVaultDao

    companion object {
        @Volatile
        private var INSTANCE: CodeingoDatabase? = null

        fun getDatabase(context: Context): CodeingoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CodeingoDatabase::class.java,
                    "codeingo_vault.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
