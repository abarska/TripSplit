package com.anabars.tripsplit.repository

import androidx.room.Transaction
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.entity.ExchangeRate
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripStatus
import com.anabars.tripsplit.data.room.model.TripWithDetails
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
        trip: Trip,
        participants: List<TripParticipant>,
        currencyCodes: List<String>
    ) {
        val tripId = tripDao.insertTrip(trip)
        val participantsWithTripId = participants.map { it.withTripId(tripId) }
        val currencies = currencyCodes.map { code ->
            TripCurrency(code = code, tripId = tripId)
        }
        participantDao.insertParticipants(participantsWithTripId)
        currencyDao.insertCurrencies(currencies)
    }

    fun getTripDetailsWithFlow(tripId: Long): Flow<TripWithDetails?> {
        return tripDao.getTripWithDetails(tripId)
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