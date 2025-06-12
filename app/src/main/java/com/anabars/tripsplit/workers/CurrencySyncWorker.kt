package com.anabars.tripsplit.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anabars.tripsplit.BuildConfig
import com.anabars.tripsplit.data.network.CurrencyApiService
import com.anabars.tripsplit.data.network.CurrencyResponse
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.entity.ExchangeRate
import com.anabars.tripsplit.utils.validCurrencyCodes
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CurrencySyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: CurrencyApiService,
    private val exchangeRateDao: ExchangeRateDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val symbols = validCurrencyCodes().joinToString(separator = ",")
            val response = apiService.getLatestRates(
                apiKey = BuildConfig.CURRENCY_FREAKS_API_KEY,
                symbols = symbols
            )
            saveRatesToDatabase(response)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun saveRatesToDatabase(response: CurrencyResponse) {
        val entities = response.rates.map { (currency, rate) ->
            ExchangeRate(
                currencyCode = currency,
                baseCurrency = response.base,
                rate = rate.toDoubleOrNull() ?: 0.0,
                date = response.date
            )
        }
        exchangeRateDao.replaceAll(entities)
    }
}