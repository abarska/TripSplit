package com.anabars.tripsplit.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.model.TripParticipant
import com.anabars.tripsplit.model.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_TABLE}")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("DELETE from ${TripSplitConstants.TRIP_TABLE}")
    suspend fun deleteAllTrips()

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_TABLE} WHERE id = :id")
    suspend fun getTripById(id: String): Trip

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveParticipant(participant: TripParticipant)

    @Delete
    suspend fun deleteParticipant(participant: TripParticipant)

}
