package com.simats.codeingo.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ConceptMasteryDao {

    @Query("SELECT * FROM concept_mastery WHERE user_id = :userId AND concept_id = :conceptId LIMIT 1")
    suspend fun getConceptMastery(userId: String, conceptId: String): ConceptMasteryEntity?

    @Query("SELECT * FROM concept_mastery WHERE user_id = :userId")
    fun getAllMasteryFlow(userId: String): Flow<List<ConceptMasteryEntity>>

    @Query("SELECT * FROM concept_mastery WHERE user_id = :userId AND next_review_epoch_ms <= :currentEpochMs ORDER BY next_review_epoch_ms ASC")
    suspend fun getDueForReview(userId: String, currentEpochMs: Long): List<ConceptMasteryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: ConceptMasteryEntity): Long

    @Query("DELETE FROM concept_mastery WHERE user_id = :userId")
    suspend fun clearForUser(userId: String)
}

