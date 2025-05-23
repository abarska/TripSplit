package com.anabars.tripsplit.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "participant_table")
data class Participant(
    @PrimaryKey val userId: UUID = UUID.randomUUID(),
    val name: String = "",
    val tripId: UUID
)