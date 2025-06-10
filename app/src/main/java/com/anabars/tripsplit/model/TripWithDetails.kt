package com.anabars.tripsplit.model

import androidx.room.Embedded
import androidx.room.Relation

data class TripWithDetails(

    @Embedded
    val trip: Trip,

    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val participants: List<TripParticipant>,

    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val currencies: List<TripCurrency>
)
