package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.entity.TripParticipant
import kotlinx.coroutines.flow.Flow

@Dao
interface TripParticipantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<TripParticipant>)

    @Upsert
    suspend fun upsertParticipants(participants: List<TripParticipant>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveParticipant(participant: TripParticipant)

    @Delete
    suspend fun deleteParticipant(participant: TripParticipant)

    @Query("DELETE FROM ${TripSplitConstants.TRIP_PARTICIPANTS_TABLE} WHERE tripId = :tripId")
    suspend fun deleteParticipantsByTripId(tripId: Long)

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_PARTICIPANTS_TABLE} WHERE tripId = :tripId")
    suspend fun getParticipantsByTripIdAsList(tripId: Long): List<TripParticipant>

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_PARTICIPANTS_TABLE} WHERE tripId = :tripId")
    fun getParticipantsByTripIdAsFlow(tripId: Long): Flow<List<TripParticipant>>

    @Query("SELECT * FROM ${TripSplitConstants.TRIP_PARTICIPANTS_TABLE} WHERE tripId = :tripId AND status = 'ACTIVE'")
    fun getActiveParticipantsByTripId(tripId: Long): Flow<List<TripParticipant>>
}