package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.entity.TripParticipant

@Dao
interface TripParticipantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<TripParticipant>)

    @Query("DELETE FROM ${TripSplitConstants.TRIP_PARTICIPANTS_TABLE} WHERE tripId = :tripId")
    suspend fun deleteParticipantsByTripId(tripId: Long)

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_PARTICIPANTS_TABLE} WHERE tripId = :tripId")
    suspend fun getParticipantsByTripId(tripId: Long): List<TripParticipant>

    @Upsert
    suspend fun upsertParticipants(participants: List<TripParticipant>)
}