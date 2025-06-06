package com.anabars.tripsplit.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.model.TripParticipant

@Dao
interface TripParticipantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<TripParticipant>)

    @Query("DELETE FROM ${TripSplitConstants.TRIP_PARTICIPANTS_TABLE} WHERE tripId = :tripId")
    suspend fun deleteParticipantsByTripId(tripId: Long)

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_PARTICIPANTS_TABLE} WHERE tripId = :tripId")
    suspend fun getParticipantsByTripId(tripId: Long): List<TripParticipant>
}