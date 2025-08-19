package com.anabars.tripsplit.repository

import androidx.room.Transaction
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
import com.anabars.tripsplit.data.room.entity.CurrencyStatus
import com.anabars.tripsplit.data.room.entity.ExchangeRate
import com.anabars.tripsplit.data.room.entity.ParticipantStatus
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripStatus
import com.anabars.tripsplit.data.room.model.TripDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TripRepository @Inject constructor(
    private val tripDao: TripDao,
    private val tripExpensesDao: TripExpensesDao,
    private val participantDao: TripParticipantDao,
    private val exchangeRateDao: ExchangeRateDao,
    private val currencyDao: TripCurrencyDao
) {

    fun getTripsWithStatuses(statuses: List<TripStatus>) =
        tripDao.getTripsWithStatuses(statuses).flowOn(Dispatchers.IO).conflate()

    @Transaction
    suspend fun saveTrip(
        tripId: Long?,
        trip: Trip,
        participants: List<TripParticipant>,
        currencyCodes: List<String>
    ) {
        val finalTripId =
            if (tripId == null) {
                tripDao.insertTrip(trip)
            } else {
                tripDao.updateTrip(trip.copy(id = tripId))
                tripId
            }

        handleParticipants(tripId = finalTripId, incomingParticipants = participants)
        handleCurrencies(tripId = finalTripId, incomingCurrencyCodes = currencyCodes)
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