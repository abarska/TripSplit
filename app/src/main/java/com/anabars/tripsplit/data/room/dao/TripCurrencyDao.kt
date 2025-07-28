package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.entity.TripCurrency

@Dao
interface TripCurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<TripCurrency>)

    @Query("DELETE FROM ${TripSplitConstants.TRIP_CURRENCIES_TABLE} WHERE tripId = :tripId")
    suspend fun deleteCurrenciesByTripId(tripId: Long)

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_CURRENCIES_TABLE} WHERE tripId = :tripId")
    suspend fun getCurrenciesByTripId(tripId: Long): List<TripCurrency>

    @Upsert
    suspend fun upsertCurrencies(currencies: List<TripCurrency>)
}