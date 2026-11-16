package com.anabars.tripsplit.repository

import androidx.room.withTransaction
import com.anabars.tripsplit.BalanceCalculator
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
import kotlinx.coroutines.flow.Flow
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

    suspend fun savePayment(payment: TripPayment): TripItemResult =
        db.withTransaction {
            // calculate deltas for updating per participant balance
            val exchangeRate = exchangeRateDao.getRateForCurrency(payment.currency)
                ?: return@withTransaction TripItemResult.MissingExchangeRate
            val deltas = BalanceCalculator.calculateDeltasForPayment(
                payment,
                exchangeRate,
                payment.fromUserId,
                payment.toUserId
            )

            // save payment and deltas
            tripPaymentDao.savePayment(payment)
            saveBalanceDeltas(deltas)
            TripItemResult.Success
        }

    suspend fun saveExpenseWithParticipants(
        expense: TripExpense,
        participants: Set<TripParticipant>
    ): TripItemResult =
        db.withTransaction {
            // calculate deltas for updating per participant balance
            val exchangeRate = exchangeRateDao.getRateForCurrency(expense.currency)
                ?: return@withTransaction TripItemResult.MissingExchangeRate
            val deltas = BalanceCalculator.calculateDeltasForExpense(expense, exchangeRate, participants)

            // save expense, cross refs and deltas
            val expenseId = tripExpensesDao.saveExpense(expense)

            val crossRefs = participants.map {
                ExpenseParticipantCrossRef(expenseId = expenseId, participantId = it.id)
            }
            tripExpensesDao.saveCrossRefs(crossRefs)
            saveBalanceDeltas(deltas)
            TripItemResult.Success
        }

    suspend fun deleteExpenseById(expenseId: Long): TripItemResult =
        db.withTransaction {
            // calculate deltas for this expense
            val expenseWithParticipants = tripExpensesDao.getExpenseWithParticipantsById(expenseId)
                ?: return@withTransaction TripItemResult.Success
            val exchangeRate = exchangeRateDao.getRateForCurrency(expenseWithParticipants.expense.currency)
                ?: return@withTransaction TripItemResult.MissingExchangeRate
            val deltas = BalanceCalculator.calculateDeltasForExpense(
                expense = expenseWithParticipants.expense,
                exchangeRate = exchangeRate,
                participants = expenseWithParticipants.participants.toSet()
            )

            // invert deltas and update balances
            val inverted = deltas.map { it.copy(deltaUsd = it.deltaUsd.negate()) }
            saveBalanceDeltas(inverted)

            // delete expense and cross-refs
            tripExpensesDao.deleteExpenseWithCrossRefs(expenseId)
            TripItemResult.Success
        }

    suspend fun deletePaymentById(paymentId: Long): TripItemResult =
        db.withTransaction {
            // calculate deltas for this payment
            val payment = tripPaymentDao.getPaymentById(paymentId)
                ?: return@withTransaction TripItemResult.Success
            val exchangeRate = exchangeRateDao.getRateForCurrency(payment.currency)
                ?: return@withTransaction TripItemResult.MissingExchangeRate
            val deltas = BalanceCalculator.calculateDeltasForPayment(
                payment = payment,
                exchangeRate = exchangeRate,
                fromUserId = payment.fromUserId,
                toUserId = payment.toUserId
            )

            // invert deltas and update balances
            val inverted = deltas.map { it.copy(deltaUsd = it.deltaUsd.negate()) }
            saveBalanceDeltas(inverted)

            // delete payment
            tripPaymentDao.deletePaymentById(paymentId)
            TripItemResult.Success
        }

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