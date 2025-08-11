package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.dao.TripPaymentDao
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TripItemRepository @Inject constructor(
    private val tripExpensesDao: TripExpensesDao,
    private val tripPaymentDao: TripPaymentDao,
    private val tripDao: TripDao
) {

    fun getExpensesWithParticipantsByTrip(tripId: Long): Flow<List<ExpenseWithParticipants>> {
        return tripExpensesDao.getExpensesWithParticipantsByTrip(tripId)
    }

    fun getActiveCurrenciesByTripId(tripId: Long): Flow<List<TripCurrency>> {
        return tripDao.getActiveCurrenciesByTripId(tripId)
    }

    fun getParticipantsByTripId(tripId: Long): Flow<List<TripParticipant>> {
        return tripDao.getParticipantsByTripId(tripId)
    }

    fun getActiveParticipantsByTripId(tripId: Long): Flow<List<TripParticipant>> {
        return tripDao.getActiveParticipantsByTripId(tripId)
    }

    suspend fun savePayment(payment: TripPayment) {
        tripPaymentDao.savePayment(payment)
    }

    suspend fun saveExpenseWithParticipants(
        expense: TripExpense,
        participants: Set<TripParticipant>
    ) {
        return tripExpensesDao.insertExpenseWithParticipants(expense, participants)
    }

    suspend fun deleteExpenseById(expenseId: Long) {
        tripExpensesDao.deleteExpenseById(expenseId)
    }
}