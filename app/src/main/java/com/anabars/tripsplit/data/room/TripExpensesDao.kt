package com.anabars.tripsplit.data.room

import androidx.room.Dao
import androidx.room.Query
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.model.TripExpense
import kotlinx.coroutines.flow.Flow

@Dao
interface TripExpensesDao {

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_EXPENSES_TABLE} WHERE tripId = :tripId ORDER BY timestamp DESC")
    fun getExpensesByTripId(tripId: Long): Flow<List<TripExpense>>

}
