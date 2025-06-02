package com.anabars.tripsplit.workers

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.anabars.tripsplit.common.TripSplitConstants
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencySyncScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun scheduleDailySync() {
        val workRequest = PeriodicWorkRequestBuilder<CurrencySyncWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = TripSplitConstants.CURRENCY_SYNC_WORK_NAME,
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
            request = workRequest
        )
    }
}