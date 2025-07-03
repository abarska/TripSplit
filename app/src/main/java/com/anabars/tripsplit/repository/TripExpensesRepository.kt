package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TripExpensesRepository @Inject constructor(
    private val tripExpensesDao: TripExpensesDao,
    private val tripDao: TripDao
) {

    fun getExpensesByTrip(tripId: Long): Flow<List<TripExpense>> {
        return tripExpensesDao.getExpensesByTripId(tripId)
    }

    fun getCurrenciesByTrip(tripId: Long): Flow<List<TripCurrency>> {
        return tripDao.getCurrenciesByTripId(tripId)
    }

    fun getParticipantsByTrip(tripId: Long): Flow<List<TripParticipant>> {
        return tripDao.getParticipantsByTripId(tripId)
    }

    suspend fun saveExpense(expense: TripExpense) {
        return tripExpensesDao.saveExpense(expense)
    }

}