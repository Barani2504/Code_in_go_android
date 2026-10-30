package com.simats.codeingo.data.network.worker

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface abstracting background sync scheduling, allowing pure JVM testing without Android Context.
 */
fun interface SyncScheduler {
    fun scheduleSync()
}

@Singleton
class WorkManagerSyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : SyncScheduler {
    override fun scheduleSync() {
        ProgressSyncWorker.enqueue(context)
    }
}
