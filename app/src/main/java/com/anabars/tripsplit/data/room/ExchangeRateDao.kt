package com.anabars.tripsplit.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.model.ExchangeRate

@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM ${TripSplitConstants.EXCHANGE_RATE_TABLE}")
    suspend fun getAllRates(): List<ExchangeRate>

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