package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.entity.TripExpense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TripExpensesRepository @Inject constructor(private val tripExpensesDao: TripExpensesDao) {

    fun getExpensesByTrip(tripId: Long): Flow<List<TripExpense>> {
        return tripExpensesDao.getExpensesByTripId(tripId)
    }

}