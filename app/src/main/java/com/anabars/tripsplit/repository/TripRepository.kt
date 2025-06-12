package com.anabars.tripsplit.repository

import androidx.room.Transaction
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.model.TripWithDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TripRepository @Inject constructor(
    private val tripDao: TripDao,
    private val participantDao: TripParticipantDao,
    private val currencyDao: TripCurrencyDao
) {

    fun getAllTrips() = tripDao.getAllTrips().flowOn(Dispatchers.IO).conflate()

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

    fun getTripDetailsWithFlow(tripId: Long): Flow<TripWithDetails?> {
        return tripDao.getTripWithDetails(tripId)
    }
}