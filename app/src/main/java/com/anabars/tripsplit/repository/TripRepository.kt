package com.anabars.tripsplit.repository

import androidx.room.withTransaction
import com.anabars.tripsplit.data.room.TripSplitDatabase
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
import com.anabars.tripsplit.data.room.dao.TripPaymentDao
import com.anabars.tripsplit.data.room.entity.CurrencyStatus
import com.anabars.tripsplit.data.room.entity.ExchangeRate
import com.anabars.tripsplit.data.room.entity.ParticipantStatus
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripStatus
import com.anabars.tripsplit.data.room.model.TripDetails
import com.anabars.tripsplit.utils.MoneyMath
import com.anabars.tripsplit.utils.ShareAllocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import java.math.BigDecimal
import java.math.MathContext
import javax.inject.Inject

class TripRepository @Inject constructor(
    private val db: TripSplitDatabase,
    private val tripDao: TripDao,
    private val tripExpensesDao: TripExpensesDao,
    private val tripPaymentDao: TripPaymentDao,
    private val participantDao: TripParticipantDao,
    private val exchangeRateDao: ExchangeRateDao,
    private val currencyDao: TripCurrencyDao
) {

    fun getTripsWithStatuses(statuses: List<TripStatus>) =
        tripDao.getTripsWithStatuses(statuses).flowOn(Dispatchers.IO).conflate()

    suspend fun saveTrip(
        tripId: Long?,
        trip: Trip,
        participants: List<TripParticipant>,
        currencyCodes: List<String>
    ): TripItemResult = db.withTransaction {
        val switch = if (tripId == null) null else {
            val oldCurrency = tripDao.getTripById(tripId).defaultCurrency
            planSwitch(tripId, oldCurrency, trip.defaultCurrency)
                ?: return@withTransaction TripItemResult.MissingExchangeRate
        }
        val finalTripId =
            if (tripId == null) {
                tripDao.insertTrip(trip)
            } else {
                tripDao.updateTrip(trip.copy(id = tripId))
                tripId
            }

        handleParticipants(tripId = finalTripId, incomingParticipants = participants)
        handleCurrencies(tripId = finalTripId, incomingCurrencyCodes = currencyCodes)
        if (switch != null && switch.rate != null) {
            applySwitch(finalTripId, switch.newCurrency, switch.rate)
        }
        TripItemResult.Success
    }

    private class CurrencySwitch(val newCurrency: String, val rate: BigDecimal?)

    /**
     * Returns the plan for the default currency of the trip, with a null rate when nothing needs converting,
     * or null when the trip has items and a rate needed for the switch is not stored.
     */
    private suspend fun planSwitch(tripId: Long, oldCurrency: String, newCurrency: String): CurrencySwitch? {
        val noConversion = CurrencySwitch(newCurrency, null)
        if (oldCurrency == newCurrency) return noConversion
        if (tripExpensesDao.getExpensesByTripIdAsList(tripId).isEmpty() &&
            tripPaymentDao.getPaymentsByTripIdAsList(tripId).isEmpty()
        ) return noConversion
        val fromUsdRate = exchangeRateDao.getRateForCurrency(oldCurrency) ?: return null
        val toUsdRate = exchangeRateDao.getRateForCurrency(newCurrency) ?: return null
        return CurrencySwitch(newCurrency, MoneyMath.crossRate(fromUsdRate, toUsdRate))
    }

    /** Recomputes every item from its original amount, so cents never compound across repeated switches. */
    private suspend fun applySwitch(tripId: Long, newCurrency: String, switchRate: BigDecimal) {
        fun newRate(currency: String, rateToTrip: BigDecimal) =
            if (currency == newCurrency) BigDecimal.ONE
            else rateToTrip.multiply(switchRate, MathContext.DECIMAL128)

        for (expense in tripExpensesDao.getExpensesByTripIdAsList(tripId)) {
            val rate = newRate(expense.currency, expense.rateToTrip)
            val total = MoneyMath.convert(expense.amountMinor, expense.currency, newCurrency, rate)
            val oldShares = tripExpensesDao.getSharesByExpenseId(expense.id)
            val newShares = ShareAllocator.allocate(
                total = total,
                weights = oldShares.associate { it.participantId to it.weight },
                payerId = expense.paidById
            )
            tripExpensesDao.updateExpense(expense.copy(rateToTrip = rate, tripAmountMinor = total))
            tripExpensesDao.saveShares(
                oldShares.map { it.copy(shareMinor = newShares.getValue(it.participantId)) }
            )
        }
        for (payment in tripPaymentDao.getPaymentsByTripIdAsList(tripId)) {
            val rate = newRate(payment.currency, payment.rateToTrip)
            val total = MoneyMath.convert(payment.amountMinor, payment.currency, newCurrency, rate)
            tripPaymentDao.updatePayment(payment.copy(rateToTrip = rate, tripAmountMinor = total))
        }
    }

    private suspend fun handleCurrencies(
        tripId: Long,
        incomingCurrencyCodes: List<String>
    ) {
        val existingCurrencies = currencyDao.getCurrenciesByTripIdAsList(tripId)
        val existingCurrenciesMap = existingCurrencies.associateBy { it.code }
        val incomingCurrencyCodesSet = incomingCurrencyCodes.toSet()
        val currenciesToUpsert = mutableListOf<TripCurrency>()
        for (incomingCode in incomingCurrencyCodesSet) {
            val existingCurrency = existingCurrenciesMap[incomingCode]
            if (existingCurrency == null) {
                currenciesToUpsert.add(TripCurrency(code = incomingCode, tripId = tripId))
            } else if (existingCurrency.status == CurrencyStatus.INACTIVE) {
                currenciesToUpsert.add(existingCurrency.copy(status = CurrencyStatus.ACTIVE))
            }
        }
        val currenciesToInactivate = existingCurrencies
            .filter { it.code !in incomingCurrencyCodesSet && it.status == CurrencyStatus.ACTIVE }
            .map { it.copy(status = CurrencyStatus.INACTIVE) }
        currencyDao.upsertCurrencies(currenciesToUpsert + currenciesToInactivate)
    }

    private suspend fun handleParticipants(
        tripId: Long,
        incomingParticipants: List<TripParticipant>
    ) {
        val existingParticipants = participantDao.getParticipantsByTripIdAsList(tripId)
        val existingParticipantsMap = existingParticipants.associateBy { it.name }
        val participantsToUpsert = mutableListOf<TripParticipant>()

        for (incomingP in incomingParticipants) {
            val existingP = existingParticipantsMap[incomingP.name]
            if (existingP == null) {
                participantsToUpsert.add(incomingP.copy(tripId = tripId))
            } else {
                var shouldUpdate = false
                var updatedParticipant = existingP
                if (existingP.status == ParticipantStatus.INACTIVE) {
                    updatedParticipant = updatedParticipant.copy(status = ParticipantStatus.ACTIVE)
                    shouldUpdate = true
                }
                if (existingP.multiplicator != incomingP.multiplicator) {
                    updatedParticipant =
                        updatedParticipant.copy(multiplicator = incomingP.multiplicator)
                    shouldUpdate = true
                }
                if (shouldUpdate) {
                    participantsToUpsert.add(
                        updatedParticipant.copy(id = existingP.id, tripId = tripId)
                    )
                }
            }
        }
        val incomingParticipantNames = incomingParticipants.map { it.name }.toSet()
        val participantsToInactivate = existingParticipants
            .filter { it.name !in incomingParticipantNames && it.status == ParticipantStatus.ACTIVE }
            .map { it.copy(status = ParticipantStatus.INACTIVE) }
        participantDao.upsertParticipants(participantsToUpsert + participantsToInactivate)
    }

    fun getTripDetailsFlow(tripId: Long): Flow<TripDetails?> {
        return tripDao.getTripDetailsFlow(tripId)
    }

    fun getTripDetailsData(tripId: Long): TripDetails? {
        return tripDao.getTripDetailsData(tripId)
    }

    fun getExpensesByTripId(id: Long): Flow<List<TripExpense>> {
        return tripExpensesDao.getExpensesByTripId(id)
    }

    fun getExchangeRatesFlow(): Flow<List<ExchangeRate>> {
        return exchangeRateDao.getExchangeRatesFlow()
    }

    suspend fun updateTripStatus(id: Long, status: TripStatus) {
        tripDao.updateTripStatus(tripId = id, status = status)
    }
}