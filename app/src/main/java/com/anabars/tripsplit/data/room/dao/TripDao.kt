package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripStatus
import com.anabars.tripsplit.data.room.model.TripDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_TABLE} WHERE status IN (:statuses)")
    fun getTripsWithStatuses(statuses: List<TripStatus>): Flow<List<Trip>>

    @Query("DELETE from ${TripSplitConstants.TRIP_TABLE}")
    suspend fun deleteAllTrips()

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_TABLE} WHERE id = :id")
    suspend fun getTripById(id: Long): Trip

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveParticipant(participant: TripParticipant)

    @Delete
    suspend fun deleteParticipant(participant: TripParticipant)

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_CURRENCIES_TABLE} WHERE tripId = :tripId")
    fun getCurrenciesByTripId(tripId: Long): Flow<List<TripCurrency>>

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_PARTICIPANTS_TABLE} WHERE tripId = :tripId")
    fun getParticipantsByTripId(tripId: Long): Flow<List<TripParticipant>>

    @Transaction
    @Query("SELECT * FROM ${TripSplitConstants.TRIP_TABLE} WHERE id = :tripId")
    fun getTripDetailsFlow(tripId: Long): Flow<TripDetails?>

    @Transaction
    @Query("SELECT * FROM ${TripSplitConstants.TRIP_TABLE} WHERE id = :tripId")
    fun getTripDetailsData(tripId: Long): TripDetails?

    @Query("UPDATE ${TripSplitConstants.TRIP_TABLE}  SET status = :status WHERE id = :tripId")
    suspend fun updateTripStatus(tripId: Long, status: TripStatus): Int
}
