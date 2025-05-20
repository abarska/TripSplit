package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.TripDao
import com.anabars.tripsplit.model.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TripRepository @Inject constructor(private val tripDao: TripDao) {

    fun getAllTrips() = tripDao.getAllTrips().flowOn(Dispatchers.IO).conflate()
    suspend fun deleteAllTrips() = tripDao.deleteAllTrips()

    suspend fun getTrip(id: String) =tripDao.getTripById(id)
    suspend fun addTrip(trip: Trip) = tripDao.insert(trip)
    suspend fun updateTrip(trip: Trip) = tripDao.update(trip)
    suspend fun deleteTrip(trip: Trip) = tripDao.deleteTrip(trip)
}