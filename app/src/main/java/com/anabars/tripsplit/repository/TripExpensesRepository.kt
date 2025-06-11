package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.TripExpensesDao
import com.anabars.tripsplit.model.TripExpense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TripExpensesRepository @Inject constructor(private val tripExpensesDao: TripExpensesDao) {

    fun getExpensesByTrip(tripId: Long): Flow<List<TripExpense>> {
        return tripExpensesDao.getExpensesByTripId(tripId)
    }

}