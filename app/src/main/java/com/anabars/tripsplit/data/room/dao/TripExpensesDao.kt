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
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.math.RoundingMode

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

    @Transaction
    suspend fun insertExpenseWithParticipants(
        expense: TripExpense,
        participants: Set<TripParticipant>
    ) {
        val expenseId = saveExpense(expense)
        val crossRefs = participants.map {
            ExpenseParticipantCrossRef(expenseId = expenseId, participantId = it.id)
        }
        saveCrossRefs(crossRefs)
        updateOrInsertBalance(
            tripId = expense.tripId,
            participantId = expense.paidById,
            deltaUsd = expense.amount
        )
        participants.forEach {
            updateOrInsertBalance(
                tripId = expense.tripId,
                participantId = it.id,
                deltaUsd = BigDecimal(expense.amount)
                    .divide(BigDecimal(participants.size), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(-1.0)).toDouble()
            )
        }
    }

    @Transaction
    suspend fun updateOrInsertBalance(tripId: Long, participantId: Long, deltaUsd: Double) {
        val updatedRows = updateBalance(tripId, participantId, deltaUsd)
        if (updatedRows == 0) {
            insertBalance(tripId, participantId, deltaUsd)
        }
    }

    @Query("UPDATE ${TripSplitConstants.PARTICIPANT_BALANCES_TABLE} SET amountUsd = amountUsd + :deltaUsd WHERE tripId = :tripId AND participantId = :participantId")
    suspend fun updateBalance(tripId: Long, participantId: Long, deltaUsd: Double): Int

    @Query("INSERT INTO ${TripSplitConstants.PARTICIPANT_BALANCES_TABLE} (tripId, participantId, amountUsd) VALUES (:tripId, :participantId, :deltaUsd)")
    suspend fun insertBalance(tripId: Long, participantId: Long, deltaUsd: Double)

    @Query("DELETE FROM $TRIP_EXPENSES_TABLE WHERE id = :expenseId")
    suspend fun deleteExpenseById(expenseId: Long)
}
