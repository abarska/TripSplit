package com.anabars.tripsplit.repository

import androidx.room.withTransaction
import com.anabars.tripsplit.BalanceCalculator
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.TripSplitDatabase
import com.anabars.tripsplit.data.room.dao.BalanceDao
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
import com.anabars.tripsplit.data.room.dao.TripPaymentDao
import com.anabars.tripsplit.data.room.entity.ExpenseParticipantCrossRef
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
import com.anabars.tripsplit.ui.model.BalanceDelta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TripItemRepository @Inject constructor(
    private val db: TripSplitDatabase,
    private val tripCurrencyDao: TripCurrencyDao,
    private val tripParticipantDao: TripParticipantDao,
    private val tripExpensesDao: TripExpensesDao,
    private val tripPaymentDao: TripPaymentDao,
    private val balanceDao: BalanceDao,
    private val exchangeRateDao: ExchangeRateDao
) {

    fun getExpensesWithParticipantsByTrip(tripId: Long): Flow<List<ExpenseWithParticipants>> =
        tripExpensesDao.getExpensesWithParticipantsByTrip(tripId)

    fun getPaymentsByTripId(tripId: Long): Flow<List<TripPayment>> =
        tripPaymentDao.getPaymentsByTripId(tripId)

    fun getActiveCurrenciesByTripId(tripId: Long): Flow<List<TripCurrency>> =
        tripCurrencyDao.getActiveCurrenciesByTripId(tripId)

    fun getParticipantsByTripId(tripId: Long): Flow<List<TripParticipant>> =
        tripParticipantDao.getParticipantsByTripIdAsFlow(tripId)

    fun getActiveParticipantsByTripId(tripId: Long): Flow<List<TripParticipant>> =
        tripParticipantDao.getActiveParticipantsByTripId(tripId)

    suspend fun savePayment(payment: TripPayment) {
        // calculate deltas for updating per participant balance
        val exchangeRate = getExchangeRateForCurrency(payment.currency)
        val deltas = withContext(Dispatchers.Default) {
            BalanceCalculator.calculateDeltasForPayment(payment, exchangeRate, payment.fromUserId, payment.toUserId)
        }

        // save payment and deltas in a single transaction
        db.withTransaction {
            tripPaymentDao.savePayment(payment)
            saveBalanceDeltas(deltas)
        }
    }

    suspend fun saveExpenseWithParticipants(
        expense: TripExpense,
        participants: Set<TripParticipant>
    ) {
        // calculate deltas for updating per participant balance
        val exchangeRate = getExchangeRateForCurrency(expense.currency)
        val deltas = withContext(Dispatchers.Default) {
            BalanceCalculator.calculateDeltasForExpense(expense, exchangeRate, participants)
        }

        // save expense, cross refs and deltas in a single transaction
        db.withTransaction {
            val expenseId = tripExpensesDao.saveExpense(expense)

            val crossRefs = participants.map {
                ExpenseParticipantCrossRef(expenseId = expenseId, participantId = it.id)
            }
            tripExpensesDao.saveCrossRefs(crossRefs)
            saveBalanceDeltas(deltas)
        }
    }

    suspend fun deleteExpenseById(expenseId: Long) {
        db.withTransaction {
            // load expense with participants as it was saved
            val expenseWithParticipants = tripExpensesDao.getExpenseWithParticipantsById(expenseId)
                ?: return@withTransaction
            val expense = expenseWithParticipants.expense
            val participants = expenseWithParticipants.participants.toSet()

            // calculate deltas for this expense
            val exchangeRate = getExchangeRateForCurrency(expense.currency)
            val deltas = withContext(Dispatchers.Default) {
                BalanceCalculator.calculateDeltasForExpense(expense, exchangeRate, participants)
            }

            // invert deltas and update balances
            val inverted = deltas.map { it.copy(deltaUsd = it.deltaUsd.negate()) }
            saveBalanceDeltas(inverted)

            // delete expense and cross-refs
            tripExpensesDao.deleteExpenseWithCrossRefs(expenseId)
        }
    }

    suspend fun deletePaymentById(paymentId: Long) {
        tripPaymentDao.deletePaymentById(paymentId)
    }

    private suspend fun getExchangeRateForCurrency(currency: String): Double =
        if (currency == TripSplitConstants.BASE_CURRENCY) 1.0
        else exchangeRateDao.getExchangeRateForCurrency(currency).rate

    private suspend fun saveBalanceDeltas(deltas: List<BalanceDelta>) {
        deltas.forEach { delta ->
            val updated =
                balanceDao.updateBalance(delta.tripId, delta.participantId, delta.deltaUsd)
            if (updated == 0) {
                balanceDao.insertBalance(delta.tripId, delta.participantId, delta.deltaUsd)
            }
        }
    }
}