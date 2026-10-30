package com.simats.codeingo.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class SyncState {
    PENDING,
    IN_FLIGHT,
    FAILED_PERMANENT
}

@Entity(
    tableName = "offline_sync_queue",
    indices = [
        Index(value = ["user_id", "lesson_id"], unique = true)
    ]
)
data class OfflineSyncEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "idempotency_key")
    val idempotencyKey: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "lesson_id")
    val lessonId: String,

    @ColumnInfo(name = "accuracy_percentage")
    val accuracyPercentage: Int,

    @ColumnInfo(name = "completed_at_epoch_ms")
    val completedAtEpochMs: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "timezone_offset_minutes")
    val timezoneOffsetMinutes: Int = java.util.TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (60 * 1000),

    @ColumnInfo(name = "attempt_count")
    val attemptCount: Int = 0,

    @ColumnInfo(name = "sync_state")
    val syncState: SyncState = SyncState.PENDING,

    @ColumnInfo(name = "last_error")
    val lastError: String? = null
)
