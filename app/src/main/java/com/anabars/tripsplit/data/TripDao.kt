package com.anabars.tripsplit.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anabars.tripsplit.model.Participant
import com.anabars.tripsplit.model.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Query("SELECT * FROM trip_table")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("DELETE from trip_table")
    suspend fun deleteAllTrips()

    @Query("SELECT * FROM trip_table WHERE id = :id")
    suspend fun getTripById(id: String): Trip

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTrip(trip: Trip)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveParticipant(participant: Participant)

    @Delete
    suspend fun deleteParticipant(participant: Participant)

}
