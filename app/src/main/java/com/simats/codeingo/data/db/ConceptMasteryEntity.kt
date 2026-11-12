package com.simats.codeingo.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "concept_mastery",
    indices = [
        Index(value = ["user_id", "concept_id"], unique = true)
    ]
)
data class ConceptMasteryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "concept_id")
    val conceptId: String,

    @ColumnInfo(name = "mastery_score")
    val masteryScore: Float = 0.0f, // 0.0 to 1.0 (Ember -> Flame -> Blaze -> Inferno)

    @ColumnInfo(name = "repetition_number")
    val repetitionNumber: Int = 0,

    @ColumnInfo(name = "easiness_factor")
    val easinessFactor: Float = 2.5f, // SuperMemo-2 baseline

    @ColumnInfo(name = "interval_days")
    val intervalDays: Int = 1,

    @ColumnInfo(name = "last_reviewed_epoch_ms")
    val lastReviewedEpochMs: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "next_review_epoch_ms")
    val nextReviewEpochMs: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000L)
)

