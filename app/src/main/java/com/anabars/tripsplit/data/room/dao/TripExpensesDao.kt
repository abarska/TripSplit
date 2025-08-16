package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.common.TripSplitConstants.TRIP_EXPENSES_TABLE
import com.anabars.tripsplit.data.room.entity.ExpenseParticipantCrossRef
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

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

    @Query(
        "UPDATE ${TripSplitConstants.PARTICIPANT_BALANCES_TABLE} " +
                "SET amountUsd = amountUsd + :deltaUsd " +
                "WHERE tripId = :tripId AND participantId = :participantId"
    )
    suspend fun updateBalance(tripId: Long, participantId: Long, deltaUsd: BigDecimal): Int

    @Query(
        "INSERT INTO ${TripSplitConstants.PARTICIPANT_BALANCES_TABLE} " +
                "(tripId, participantId, amountUsd) VALUES (:tripId, :participantId, :deltaUsd)"
    )
    suspend fun insertBalance(tripId: Long, participantId: Long, deltaUsd: BigDecimal)

    @Query("DELETE FROM $TRIP_EXPENSES_TABLE WHERE id = :expenseId")
    suspend fun deleteExpenseById(expenseId: Long)
}
