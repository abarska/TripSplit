package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.anabars.tripsplit.common.TripSplitConstants.TRIP_EXPENSES_TABLE
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
import kotlinx.coroutines.flow.Flow

@Dao
interface TripExpensesDao {

    @Query("SELECT * FROM $TRIP_EXPENSES_TABLE WHERE tripId = :tripId ORDER BY timestamp DESC")
    fun getExpensesByTripId(tripId: Long): Flow<List<TripExpense>>

    @Transaction
    @Query("SELECT * FROM $TRIP_EXPENSES_TABLE WHERE tripId = :tripId")
    fun getExpensesWithParticipantsByTrip(tripId: Long): Flow<List<ExpenseWithParticipants>>
}
