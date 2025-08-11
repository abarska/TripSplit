package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.anabars.tripsplit.data.room.entity.TripPayment

@Dao
interface TripPaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePayment(payment: TripPayment)
}