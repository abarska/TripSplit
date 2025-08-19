package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.anabars.tripsplit.common.TripSplitConstants.CROSS_TABLE_EXPENSE_PARTICIPANT
import com.anabars.tripsplit.common.TripSplitConstants.TRIP_EXPENSES_TABLE
import com.anabars.tripsplit.data.room.entity.ExpenseParticipantCrossRef
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
import kotlinx.coroutines.flow.Flow

@Dao
interface TripExpensesDao {

    @Query("SELECT * FROM $TRIP_EXPENSES_TABLE WHERE tripId = :tripId ORDER BY timestamp DESC")
    fun getExpensesByTripId(tripId: Long): Flow<List<TripExpense>>

    @Transaction
    @Query("SELECT * FROM $TRIP_EXPENSES_TABLE WHERE tripId = :tripId ORDER BY timestamp DESC")
    fun getExpensesWithParticipantsByTrip(tripId: Long): Flow<List<ExpenseWithParticipants>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveExpense(expense: TripExpense): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCrossRefs(crossRefs: List<ExpenseParticipantCrossRef>)

    @Query("DELETE FROM $TRIP_EXPENSES_TABLE WHERE id = :expenseId")
    suspend fun deleteExpenseById(expenseId: Long)

    @Query("DELETE FROM $CROSS_TABLE_EXPENSE_PARTICIPANT WHERE expenseId = :expenseId")
    suspend fun deleteCrossRefsForExpense(expenseId: Long)

    @Transaction
    suspend fun deleteExpenseWithCrossRefs(expenseId: Long) {
        deleteCrossRefsForExpense(expenseId)
        deleteExpenseById(expenseId)
    }
}
