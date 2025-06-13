package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
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

}