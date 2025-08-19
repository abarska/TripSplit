package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.entity.TripCurrency
import kotlinx.coroutines.flow.Flow

@Dao
interface TripCurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<TripCurrency>)

    @Upsert
    suspend fun upsertCurrencies(currencies: List<TripCurrency>)

    @Query("DELETE FROM ${TripSplitConstants.TRIP_CURRENCIES_TABLE} WHERE tripId = :tripId")
    suspend fun deleteCurrenciesByTripId(tripId: Long)

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_CURRENCIES_TABLE} WHERE tripId = :tripId")
    suspend fun getCurrenciesByTripIdAsList(tripId: Long): List<TripCurrency>

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_CURRENCIES_TABLE} WHERE tripId = :tripId")
    fun getCurrenciesByTripIdAsFlow(tripId: Long): Flow<List<TripCurrency>>

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_CURRENCIES_TABLE} WHERE tripId = :tripId AND status = 'ACTIVE'")
    fun getActiveCurrenciesByTripId(tripId: Long): Flow<List<TripCurrency>>
}