package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.TripSplitDatabase
import com.anabars.tripsplit.data.room.dao.BalanceDao
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
import com.anabars.tripsplit.data.room.dao.TripPaymentDao
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
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
        TODO("drift-free-balances 3.1: freeze the rate and save the payment")

    suspend fun saveExpenseWithParticipants(
        expense: TripExpense,
        participants: Set<TripParticipant>
    ): TripItemResult =
        TODO("drift-free-balances 3.1: freeze the rate, allocate shares and save the expense")

    suspend fun deleteExpenseById(expenseId: Long): TripItemResult =
        TODO("drift-free-balances 3.2: delete the expense rows only")

    suspend fun deletePaymentById(paymentId: Long): TripItemResult =
        TODO("drift-free-balances 3.2: delete the payment row only")
}
