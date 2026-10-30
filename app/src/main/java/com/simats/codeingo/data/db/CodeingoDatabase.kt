package com.simats.codeingo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        MistakeVaultEntity::class,
        OfflineSyncEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class CodeingoDatabase : RoomDatabase() {

    abstract fun mistakeVaultDao(): MistakeVaultDao
    abstract fun offlineSyncDao(): OfflineSyncDao

    companion object {
        @Volatile
        private var INSTANCE: CodeingoDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `offline_sync_queue` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `idempotency_key` TEXT NOT NULL,
                        `user_id` TEXT NOT NULL,
                        `lesson_id` TEXT NOT NULL,
                        `accuracy_percentage` INTEGER NOT NULL,
                        `completed_at_epoch_ms` INTEGER NOT NULL,
                        `timezone_offset_minutes` INTEGER NOT NULL,
                        `attempt_count` INTEGER NOT NULL,
                        `sync_state` TEXT NOT NULL,
                        `last_error` TEXT
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS `index_offline_sync_queue_user_id_lesson_id` 
                    ON `offline_sync_queue` (`user_id`, `lesson_id`)
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): CodeingoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CodeingoDatabase::class.java,
                    "codeingo_vault.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(context: Context): CodeingoDatabase = getDatabase(context)
    }
}
