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
        OfflineSyncEntity::class,
        ConceptMasteryEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class CodeingoDatabase : RoomDatabase() {

    abstract fun mistakeVaultDao(): MistakeVaultDao
    abstract fun offlineSyncDao(): OfflineSyncDao
    abstract fun conceptMasteryDao(): ConceptMasteryDao

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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `concept_mastery` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `user_id` TEXT NOT NULL,
                        `concept_id` TEXT NOT NULL,
                        `mastery_score` REAL NOT NULL,
                        `repetition_number` INTEGER NOT NULL,
                        `easiness_factor` REAL NOT NULL,
                        `interval_days` INTEGER NOT NULL,
                        `last_reviewed_epoch_ms` INTEGER NOT NULL,
                        `next_review_epoch_ms` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS `index_concept_mastery_user_id_concept_id`
                    ON `concept_mastery` (`user_id`, `concept_id`)
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(context: Context): CodeingoDatabase = getDatabase(context)
    }
}
