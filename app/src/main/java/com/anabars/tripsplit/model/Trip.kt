package com.anabars.tripsplit.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "trip_table")
data class Trip(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo val title: String = "",
    @ColumnInfo val description: String = "",
    @ColumnInfo val tag: String = "",
    @ColumnInfo val status: TripStatus = TripStatus.PLANNED,
    @ColumnInfo val ownerId: String = "",
//    @ColumnInfo val participantIds: List<String> = emptyList(),
    @ColumnInfo val createdAt: Long = System.currentTimeMillis()
)

enum class TripStatus {
    PLANNED,
    STARTED,
    FINISHED,
    COMPLETED
}