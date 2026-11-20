package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.anabars.tripsplit.common.TripSplitConstants.EXPENSE_SHARES_TABLE
import com.anabars.tripsplit.common.TripSplitConstants.TRIP_EXPENSES_TABLE
import com.anabars.tripsplit.data.room.entity.ExpenseShare
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

    @Transaction
    @Query("SELECT * FROM $TRIP_EXPENSES_TABLE WHERE id = :expenseId")
    fun getExpenseWithParticipantsById(expenseId: Long): ExpenseWithParticipants?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveExpense(expense: TripExpense): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveShares(shares: List<ExpenseShare>)

    @Query("SELECT * FROM $TRIP_EXPENSES_TABLE WHERE tripId = :tripId")
    suspend fun getExpensesByTripIdAsList(tripId: Long): List<TripExpense>

    @Query("SELECT * FROM $EXPENSE_SHARES_TABLE WHERE expenseId = :expenseId")
    suspend fun getSharesByExpenseId(expenseId: Long): List<ExpenseShare>

    // an in-place update, because replacing the row would cascade-delete its shares
    @Update
    suspend fun updateExpense(expense: TripExpense)

    @Query("DELETE FROM $TRIP_EXPENSES_TABLE WHERE id = :expenseId")
    suspend fun deleteExpenseById(expenseId: Long)
}
