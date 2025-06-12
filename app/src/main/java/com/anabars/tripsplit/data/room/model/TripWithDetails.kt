package com.anabars.tripsplit.data.room.model

import androidx.room.Embedded
import androidx.room.Relation
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripParticipant

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
