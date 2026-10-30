package com.simats.codeingo.data.network.worker

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.simats.codeingo.data.network.repository.ProgressOutboxRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Background WorkManager worker responsible for draining the offline lesson completion outbox.
 * Triggered automatically when lessons are completed locally, or when network connectivity is restored.
 */
class ProgressSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ProgressSyncWorkerEntryPoint {
        fun progressOutboxRepository(): ProgressOutboxRepository
    }

    override suspend fun doWork(): Result {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                ProgressSyncWorkerEntryPoint::class.java
            )
            val repository = entryPoint.progressOutboxRepository()
            val success = repository.uploadPendingCompletions()
            if (success) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: IOException) {
            Log.w(TAG, "Transient network error during sync, scheduling retry: ${e.message}")
            Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "Unrecoverable error during progress sync worker: ${e.message}", e)
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "codeingo_progress_sync_work"
        private const val TAG = "ProgressSyncWorker"

        fun enqueue(context: Context) {
            runCatching {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val workRequest = OneTimeWorkRequestBuilder<ProgressSyncWorker>()
                    .setConstraints(constraints)
                    .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        10,
                        TimeUnit.SECONDS
                    )
                    .build()

                WorkManager.getInstance(context).enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
            }.onFailure {
                Log.d(TAG, "Could not enqueue WorkManager job: ${it.message}")
            }
        }
    }
}
