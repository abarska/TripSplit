package com.anabars.tripsplit.repository

import androidx.room.withTransaction
import com.anabars.tripsplit.BalanceCalculator
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.TripSplitDatabase
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.dao.TripPaymentDao
import com.anabars.tripsplit.data.room.entity.ExpenseParticipantCrossRef
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TripItemRepository @Inject constructor(
    private val db: TripSplitDatabase,
    private val tripExpensesDao: TripExpensesDao,
    private val tripPaymentDao: TripPaymentDao,
    private val exchangeRateDao: ExchangeRateDao,
    private val tripDao: TripDao
) {

    fun getExpensesWithParticipantsByTrip(tripId: Long): Flow<List<ExpenseWithParticipants>> =
        tripExpensesDao.getExpensesWithParticipantsByTrip(tripId)

    fun getPaymentsByTripId(tripId: Long): Flow<List<TripPayment>> =
        tripPaymentDao.getPaymentsByTripId(tripId)

    fun getActiveCurrenciesByTripId(tripId: Long): Flow<List<TripCurrency>> =
        tripDao.getActiveCurrenciesByTripId(tripId)

    fun getParticipantsByTripId(tripId: Long): Flow<List<TripParticipant>> =
        tripDao.getParticipantsByTripId(tripId)

    fun getActiveParticipantsByTripId(tripId: Long): Flow<List<TripParticipant>> =
        tripDao.getActiveParticipantsByTripId(tripId)

    suspend fun savePayment(payment: TripPayment) {
        tripPaymentDao.savePayment(payment)
    }

    suspend fun saveExpenseWithParticipants(
        expense: TripExpense,
        participants: Set<TripParticipant>
    ) {
        val exchangeRate =
            if (expense.currency == TripSplitConstants.BASE_CURRENCY) 1.0
            else exchangeRateDao.getExchangeRateForCurrency(expense.currency).rate

        // calculate deltas for updating per participant balance
        val deltas = withContext(Dispatchers.Default) {
            BalanceCalculator.calculateDeltas(expense, exchangeRate, participants)
        }

        // save expense, cross refs and deltas in a single transaction
        db.withTransaction {
            val expenseId = tripExpensesDao.saveExpense(expense)

            val crossRefs = participants.map {
                ExpenseParticipantCrossRef(expenseId = expenseId, participantId = it.id)
            }
            tripExpensesDao.saveCrossRefs(crossRefs)

            deltas.forEach { delta ->
                val updated =
                    tripExpensesDao.updateBalance(delta.tripId, delta.participantId, delta.deltaUsd)
                if (updated == 0) {
                    tripExpensesDao.insertBalance(delta.tripId, delta.participantId, delta.deltaUsd)
                }
            }
        }
    }

    suspend fun deleteExpenseById(expenseId: Long) {
        tripExpensesDao.deleteExpenseById(expenseId)
    }

    suspend fun deletePaymentById(paymentId: Long) {
        tripPaymentDao.deletePaymentById(paymentId)
    }
}