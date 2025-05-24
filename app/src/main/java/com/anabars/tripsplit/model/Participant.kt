package com.anabars.tripsplit.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "participant_table",
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
data class Participant(
    @PrimaryKey val userId: UUID = UUID.randomUUID(),
    @ColumnInfo val name: String = "",
    @ColumnInfo val status: ParticipantStatus = ParticipantStatus.ACTIVE,
    @ColumnInfo val tripId: UUID
)

enum class ParticipantStatus {
    ACTIVE,
    INACTIVE
}