package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.entity.ExchangeRate
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM ${TripSplitConstants.EXCHANGE_RATE_TABLE}")
    fun getExchangeRatesFlow(): Flow<List<ExchangeRate>>

    @Query("SELECT * FROM ${TripSplitConstants.EXCHANGE_RATE_TABLE} WHERE currencyCode = :currencyCode")
    suspend fun getExchangeRateForCurrency(currencyCode: String): ExchangeRate

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