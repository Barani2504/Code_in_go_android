package com.simats.codeingo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MistakeVaultDao {

    @Query("SELECT * FROM mistake_vault ORDER BY timestamp DESC")
    fun getAllMistakesFlow(): Flow<List<MistakeVaultEntity>>

    @Query("SELECT * FROM mistake_vault WHERE isRepaired = 0 ORDER BY timestamp DESC")
    fun getUnrepairedMistakesFlow(): Flow<List<MistakeVaultEntity>>

    @Query("SELECT * FROM mistake_vault WHERE id = :id LIMIT 1")
    suspend fun getMistakeById(id: String): MistakeVaultEntity?

    @Query("SELECT * FROM mistake_vault WHERE prompt = :prompt AND wrongAnswerGiven = :wrongAnswer LIMIT 1")
    suspend fun findByPromptAndWrongAnswer(prompt: String, wrongAnswer: String): MistakeVaultEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: MistakeVaultEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<MistakeVaultEntity>)

    @Query("UPDATE mistake_vault SET isRepaired = 1 WHERE id = :id")
    suspend fun markRepaired(id: String): Int

    @Query("DELETE FROM mistake_vault WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("DELETE FROM mistake_vault")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM mistake_vault")
    suspend fun getCount(): Int
}
