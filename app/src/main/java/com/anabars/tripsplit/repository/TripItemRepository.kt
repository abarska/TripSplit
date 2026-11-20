package com.anabars.tripsplit.repository

import androidx.room.withTransaction
import com.anabars.tripsplit.data.room.TripSplitDatabase
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
import com.anabars.tripsplit.data.room.dao.TripPaymentDao
import com.anabars.tripsplit.data.room.entity.ExpenseShare
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
import com.anabars.tripsplit.utils.MoneyMath
import com.anabars.tripsplit.utils.ShareAllocator
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject

class TripItemRepository @Inject constructor(
    private val db: TripSplitDatabase,
    private val tripDao: TripDao,
    private val tripCurrencyDao: TripCurrencyDao,
    private val tripParticipantDao: TripParticipantDao,
    private val tripExpensesDao: TripExpensesDao,
    private val tripPaymentDao: TripPaymentDao,
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

    fun getTripDefaultCurrencyFlow(tripId: Long): Flow<String?> =
        tripDao.getDefaultCurrencyFlow(tripId)

    suspend fun getTripDefaultCurrency(tripId: Long): String =
        tripDao.getTripById(tripId).defaultCurrency

    /** Freezes the rate to the trip currency and saves the payment; [payment]'s rateToTrip and tripAmountMinor are ignored. */
    suspend fun savePayment(payment: TripPayment): TripItemResult = db.withTransaction {
        val frozen = freeze(payment.tripId, payment.currency, payment.amountMinor)
            ?: return@withTransaction TripItemResult.MissingExchangeRate
        tripPaymentDao.savePayment(
            payment.copy(rateToTrip = frozen.rate, tripAmountMinor = frozen.tripAmountMinor)
        )
        TripItemResult.Success
    }

    /**
     * Freezes the rate to the trip currency, snapshots the sharers' weights and saves the expense with its shares;
     * [expense]'s rateToTrip and tripAmountMinor are ignored.
     */
    suspend fun saveExpenseWithParticipants(
        expense: TripExpense,
        participants: Set<TripParticipant>
    ): TripItemResult = db.withTransaction {
        val frozen = freeze(expense.tripId, expense.currency, expense.amountMinor)
            ?: return@withTransaction TripItemResult.MissingExchangeRate
        val shares = ShareAllocator.allocate(
            total = frozen.tripAmountMinor,
            weights = participants.associate { it.id to it.multiplicator },
            payerId = expense.paidById
        )
        val expenseId = tripExpensesDao.saveExpense(
            expense.copy(rateToTrip = frozen.rate, tripAmountMinor = frozen.tripAmountMinor)
        )
        tripExpensesDao.saveShares(
            participants.map {
                ExpenseShare(expenseId, it.id, it.multiplicator, shares.getValue(it.id))
            }
        )
        TripItemResult.Success
    }

    private class FrozenAmount(val rate: BigDecimal, val tripAmountMinor: Long)

    /** Returns null when a rate needed for the conversion is not stored. */
    private suspend fun freeze(tripId: Long, currency: String, amountMinor: Long): FrozenAmount? {
        val tripCurrency = tripDao.getTripById(tripId).defaultCurrency
        if (currency == tripCurrency) return FrozenAmount(BigDecimal.ONE, amountMinor)
        val fromUsdRate = exchangeRateDao.getRateForCurrency(currency) ?: return null
        val toUsdRate = exchangeRateDao.getRateForCurrency(tripCurrency) ?: return null
        val rate = MoneyMath.crossRate(fromUsdRate, toUsdRate)
        return FrozenAmount(rate, MoneyMath.convert(amountMinor, currency, tripCurrency, rate))
    }

    /** Deletes the expense; its shares go with it by cascade. Needs no exchange rate. */
    suspend fun deleteExpenseById(expenseId: Long) = tripExpensesDao.deleteExpenseById(expenseId)

    /** Deletes the payment. Needs no exchange rate. */
    suspend fun deletePaymentById(paymentId: Long) = tripPaymentDao.deletePaymentById(paymentId)
}
