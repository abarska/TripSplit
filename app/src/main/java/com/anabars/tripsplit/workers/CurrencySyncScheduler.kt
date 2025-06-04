package com.anabars.tripsplit.workers

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.anabars.tripsplit.common.TripSplitConstants
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencySyncScheduler @Inject constructor(private val workManager: WorkManager) {

    fun scheduleCurrencySync() {
        scheduleImmediateSyncIfFirstInstall()
        schedulePeriodicSync()
    }

    private fun scheduleImmediateSyncIfFirstInstall() {
        val request = OneTimeWorkRequestBuilder<CurrencySyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniqueWork(
            TripSplitConstants.CURRENCY_SYNC_IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    private fun schedulePeriodicSync() {
        val delayMinutes = calculateInitialDelay()
        val request = PeriodicWorkRequestBuilder<CurrencySyncWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.HOURS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            TripSplitConstants.CURRENCY_SYNC_PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        val target = now.withHour(1).withMinute(0).withSecond(0).withNano(0)
        val nextRun = if (now.isAfter(target)) target.plusDays(1) else target
        return Duration.between(now, nextRun).toMinutes()
    }
}