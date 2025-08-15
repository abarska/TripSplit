package com.anabars.tripsplit.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anabars.tripsplit.common.TripSplitConstants

@Entity(
    tableName = TripSplitConstants.PARTICIPANT_BALANCES_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TripParticipant::class,
            parentColumns = ["id"],
            childColumns = ["participantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId", "participantId")]
)
data class ParticipantBalance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo val tripId: Long,
    @ColumnInfo val participantId: Long,
    @ColumnInfo val amount: Double = 0.0,
    @ColumnInfo val currency: String = "USD"
)