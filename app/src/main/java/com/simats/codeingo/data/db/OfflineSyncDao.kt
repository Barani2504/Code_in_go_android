package com.simats.codeingo.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflineSyncDao {

    @Query("SELECT * FROM offline_sync_queue WHERE user_id = :userId AND lesson_id = :lessonId LIMIT 1")
    suspend fun findByUserAndLesson(userId: String, lessonId: String): OfflineSyncEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: OfflineSyncEntity): Long

    @Query("SELECT * FROM offline_sync_queue WHERE sync_state != 'FAILED_PERMANENT' ORDER BY completed_at_epoch_ms ASC")
    suspend fun getPendingItems(): List<OfflineSyncEntity>

    @Query("SELECT * FROM offline_sync_queue WHERE user_id = :userId AND sync_state != 'FAILED_PERMANENT' ORDER BY completed_at_epoch_ms ASC")
    suspend fun getPendingItemsForUser(userId: String): List<OfflineSyncEntity>

    @Query("UPDATE offline_sync_queue SET sync_state = :state, attempt_count = attempt_count + 1, last_error = :error WHERE id = :id")
    suspend fun updateSyncState(id: Long, state: SyncState, error: String? = null)

    @Delete
    suspend fun delete(entity: OfflineSyncEntity)

    @Query("DELETE FROM offline_sync_queue WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("DELETE FROM offline_sync_queue WHERE user_id = :userId")
    suspend fun clearForUser(userId: String)

    @Query("DELETE FROM offline_sync_queue")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM offline_sync_queue WHERE sync_state != 'FAILED_PERMANENT'")
    fun getPendingCountFlow(): Flow<Int>
}
