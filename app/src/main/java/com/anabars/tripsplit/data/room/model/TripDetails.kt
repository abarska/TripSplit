package com.anabars.tripsplit.data.room.model

import androidx.room.Embedded
import androidx.room.Relation
import com.anabars.tripsplit.data.room.entity.CurrencyStatus
import com.anabars.tripsplit.data.room.entity.ParticipantStatus
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripParticipant

data class TripDetails(

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
) {
    val activeParticipants: List<TripParticipant>
        get() = participants.filter { it.status == ParticipantStatus.ACTIVE }

    val activeCurrencies: List<TripCurrency>
        get() = currencies.filter { it.status == CurrencyStatus.ACTIVE }
}
