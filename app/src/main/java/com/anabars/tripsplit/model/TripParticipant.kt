package com.anabars.tripsplit.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anabars.tripsplit.common.TripSplitConstants

@Entity(
    tableName = TripSplitConstants.TRIP_PARTICIPANTS_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class TripParticipant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo val name: String = "",
    @ColumnInfo val status: ParticipantStatus = ParticipantStatus.ACTIVE,
    @ColumnInfo val tripId: Long
)

enum class ParticipantStatus {
    ACTIVE,
    INACTIVE
}