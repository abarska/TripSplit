package com.anabars.tripsplit.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anabars.tripsplit.BuildConfig
import com.anabars.tripsplit.data.network.CurrencyApiService
import com.anabars.tripsplit.data.network.CurrencyResponse
import com.anabars.tripsplit.data.preferences.ExchangeRatePreference
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CurrencySyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: CurrencyApiService,
    private val exchangeRatePreference: ExchangeRatePreference
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val response = apiService.getLatestRates(BuildConfig.CURRENCY_FREAKS_API_KEY)
            saveRates(response)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun saveRates(response: CurrencyResponse) {
        exchangeRatePreference.saveRates(response.rates)
    }
}