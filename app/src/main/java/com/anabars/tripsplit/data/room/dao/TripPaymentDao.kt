package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anabars.tripsplit.common.TripSplitConstants.TRIP_PAYMENTS_TABLE
import com.anabars.tripsplit.data.room.entity.TripPayment
import kotlinx.coroutines.flow.Flow

@Dao
interface TripPaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePayment(payment: TripPayment)

    @Query("SELECT * FROM $TRIP_PAYMENTS_TABLE WHERE tripId = :tripId ORDER BY timestamp DESC")
    fun getPaymentsByTripId(tripId: Long): Flow<List<TripPayment>>

    @Query("DELETE FROM $TRIP_PAYMENTS_TABLE WHERE id = :paymentId")
    suspend fun deletePaymentById(paymentId: Long)

    @Query("SELECT * FROM $TRIP_PAYMENTS_TABLE WHERE id = :paymentId")
    suspend fun getPaymentById(paymentId: Long): TripPayment?
}