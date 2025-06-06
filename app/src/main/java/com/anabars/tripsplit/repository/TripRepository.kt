package com.anabars.tripsplit.repository

import androidx.room.Transaction
import com.anabars.tripsplit.data.room.TripCurrencyDao
import com.anabars.tripsplit.data.room.TripParticipantDao
import com.anabars.tripsplit.data.room.TripDao
import com.anabars.tripsplit.model.TripParticipant
import com.anabars.tripsplit.model.Trip
import com.anabars.tripsplit.model.TripCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TripRepository @Inject constructor(
    private val tripDao: TripDao,
    private val participantDao: TripParticipantDao,
    private val currencyDao: TripCurrencyDao
) {

    fun getAllTrips() = tripDao.getAllTrips().flowOn(Dispatchers.IO).conflate()
    suspend fun deleteAllTrips() = tripDao.deleteAllTrips()

    suspend fun getTrip(id: String) = tripDao.getTripById(id)

    @Transaction
    suspend fun saveTrip(trip: Trip, participantNames: List<String>, currencyCodes: List<String>) {
        val tripId = tripDao.insertTrip(trip)
        val participants = participantNames.map { name ->
            TripParticipant(name = name, tripId = tripId)
        }
        val currencies = currencyCodes.map { code ->
            TripCurrency(code = code, tripId = tripId)
        }
        participantDao.insertParticipants(participants)
        currencyDao.insertCurrencies(currencies)
    }

    suspend fun updateTrip(trip: Trip) = tripDao.update(trip)

    suspend fun deleteTrip(trip: Trip) = tripDao.deleteTrip(trip)

    suspend fun saveParticipant(participant: TripParticipant) = tripDao.saveParticipant(participant)

    suspend fun deleteParticipant(participant: TripParticipant) =
        tripDao.deleteParticipant(participant)

    suspend fun getParticipantsByTripId(id: Long) = participantDao.getParticipantsByTripId(id)

    suspend fun deleteParticipantsByTripId(id: Long) = participantDao.deleteParticipantsByTripId(id)
}