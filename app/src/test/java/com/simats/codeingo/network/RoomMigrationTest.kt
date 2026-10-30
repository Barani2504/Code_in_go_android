package com.simats.codeingo.network

import androidx.sqlite.db.SupportSQLiteDatabase
import com.simats.codeingo.data.db.CodeingoDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Proxy

/**
 * Validates Room Database Migration from version 1 to 2.
 * Ensures the offline_sync_queue table and composite unique index are created correctly.
 */
class RoomMigrationTest {

    @Test
    fun testMigration1To2_createsOfflineSyncTableAndIndex() {
        val executedSqls = mutableListOf<String>()
        val dbProxy = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedSqls.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        CodeingoDatabase.MIGRATION_1_2.migrate(dbProxy)

        assertEquals("Expected exactly 2 SQL statements to be executed during migration", 2, executedSqls.size)

        val createTableSql = executedSqls[0]
        assertTrue("Must create offline_sync_queue table", createTableSql.contains("CREATE TABLE IF NOT EXISTS `offline_sync_queue`"))
        assertTrue("Must contain id PK", createTableSql.contains("`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL"))
        assertTrue("Must contain idempotency_key", createTableSql.contains("`idempotency_key` TEXT NOT NULL"))
        assertTrue("Must contain user_id", createTableSql.contains("`user_id` TEXT NOT NULL"))
        assertTrue("Must contain lesson_id", createTableSql.contains("`lesson_id` TEXT NOT NULL"))
        assertTrue("Must contain accuracy_percentage", createTableSql.contains("`accuracy_percentage` INTEGER NOT NULL"))
        assertTrue("Must contain completed_at_epoch_ms", createTableSql.contains("`completed_at_epoch_ms` INTEGER NOT NULL"))
        assertTrue("Must contain sync_state", createTableSql.contains("`sync_state` TEXT NOT NULL"))

        val createIndexSql = executedSqls[1]
        assertTrue("Must create unique index on user_id and lesson_id", createIndexSql.contains("CREATE UNIQUE INDEX IF NOT EXISTS `index_offline_sync_queue_user_id_lesson_id`"))
        assertTrue("Index must target (user_id, lesson_id)", createIndexSql.contains("(`user_id`, `lesson_id`)"))
    }
}
