package com.anabars.tripsplit.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anabars.tripsplit.BuildConfig
import com.anabars.tripsplit.data.network.CurrencyApiService
import com.anabars.tripsplit.data.network.CurrencyResponse
import com.anabars.tripsplit.data.room.ExchangeRateDao
import com.anabars.tripsplit.model.ExchangeRate
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Currency
import java.util.Locale

@HiltWorker
class CurrencySyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: CurrencyApiService,
    private val exchangeRateDao: ExchangeRateDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val symbols = currencyCodes().joinToString(separator = ",")
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

    private fun currencyCodes() = Locale.getAvailableLocales()
        .asSequence()
        .filter { it.country.isNotEmpty() }
        .mapNotNull { locale ->
            try {
                Currency.getInstance(locale).currencyCode
            } catch (e: IllegalArgumentException) {
                null
            }
        }
        .toSet()

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