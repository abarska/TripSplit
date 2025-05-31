package com.anabars.tripsplit.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anabars.tripsplit.model.Participant

@Dao
interface ParticipantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<Participant>)

    @Query("DELETE FROM participant_table WHERE tripId = :tripId")
    suspend fun deleteParticipantsByTripId(tripId: Long)

    @Query("SELECT * FROM participant_table WHERE tripId = :tripId")
    suspend fun getParticipantsByTripId(tripId: Long): List<Participant>
}