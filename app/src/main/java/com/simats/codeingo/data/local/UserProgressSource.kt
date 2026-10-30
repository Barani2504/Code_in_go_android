package com.simats.codeingo.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Interface abstracting local user progress persistence for unit testing and outbox seeding.
 */
interface UserProgressSource {
    val completedLevelIndices: Flow<Set<String>>
    val basecampCompletedLevels: Flow<Set<String>>
}
