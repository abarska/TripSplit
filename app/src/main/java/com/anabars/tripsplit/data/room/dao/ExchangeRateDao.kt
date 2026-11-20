package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.entity.ExchangeRate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal

@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM ${TripSplitConstants.EXCHANGE_RATE_TABLE}")
    fun getExchangeRatesFlow(): Flow<List<ExchangeRate>>

    @Query("SELECT * FROM ${TripSplitConstants.EXCHANGE_RATE_TABLE} WHERE currencyCode = :currencyCode")
    suspend fun getExchangeRateForCurrency(currencyCode: String): ExchangeRate?

    @Query("SELECT rate FROM ${TripSplitConstants.EXCHANGE_RATE_TABLE} WHERE currencyCode = :currencyCode")
    fun getRateFlow(currencyCode: String): Flow<BigDecimal?>

    // the base currency has no row in the table; its rate is always 1
    suspend fun getRateForCurrency(currencyCode: String): BigDecimal? =
        if (currencyCode == TripSplitConstants.BASE_CURRENCY) BigDecimal.ONE
        else getExchangeRateForCurrency(currencyCode)?.rate

    fun getRateFlowForCurrency(currencyCode: String): Flow<BigDecimal?> =
        if (currencyCode == TripSplitConstants.BASE_CURRENCY) flowOf(BigDecimal.ONE)
        else getRateFlow(currencyCode)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<ExchangeRate>)

    @Query("DELETE FROM ${TripSplitConstants.EXCHANGE_RATE_TABLE}")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceAll(rates: List<ExchangeRate>) {
        clearAll()
        insertAll(rates)
    }
}