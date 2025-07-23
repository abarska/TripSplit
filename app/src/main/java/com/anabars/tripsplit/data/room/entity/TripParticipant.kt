package com.anabars.tripsplit.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.ui.model.AddTripParticipantsUiState

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
    @ColumnInfo val multiplicator: Int = 1,
    @ColumnInfo val tripId: Long = 0L
) {
    fun withTripId(id: Long): TripParticipant = copy(tripId = id)
    fun chipDisplayLabelName() = name
    fun chipDisplayLabelNameWithMultiplicator() = "$name ($multiplicator)"

    companion object {
        fun fromUiState(uiState: AddTripParticipantsUiState, tripId: Long) =
            TripParticipant(
                name = uiState.newParticipantName,
                multiplicator = uiState.newParticipantMultiplicator,
                tripId = tripId
            )
    }
}

enum class ParticipantStatus {
    ACTIVE,
    INACTIVE
}