package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TripExpensesRepository @Inject constructor(
    private val tripExpensesDao: TripExpensesDao,
    private val tripDao: TripDao
) {

    fun getExpensesWithParticipantsByTrip(tripId: Long): Flow<List<ExpenseWithParticipants>> {
        return tripExpensesDao.getExpensesWithParticipantsByTrip(tripId)
    }

    fun getCurrenciesByTripId(tripId: Long): Flow<List<TripCurrency>> {
        return tripDao.getCurrenciesByTripId(tripId)
    }

    fun getParticipantsByTripId(tripId: Long): Flow<List<TripParticipant>> {
        return tripDao.getParticipantsByTripId(tripId)
    }

    suspend fun saveExpenseWithParticipants(
        expense: TripExpense,
        participants: Set<TripParticipant>
    ) {
        return tripExpensesDao.insertExpenseWithParticipants(expense, participants)
    }
}