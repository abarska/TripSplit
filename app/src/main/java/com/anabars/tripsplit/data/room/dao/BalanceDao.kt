package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.entity.ParticipantBalance
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {
    @Query("SELECT * FROM ${TripSplitConstants.PARTICIPANT_BALANCES_TABLE} WHERE tripId = :tripId")
    fun getBalancesForTrip(tripId: Long): Flow<List<ParticipantBalance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBalance(balance: ParticipantBalance)
}