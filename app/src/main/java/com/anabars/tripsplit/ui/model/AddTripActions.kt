package com.anabars.tripsplit.ui.model

import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripStatus

data class AddTripActions(
    val onTripNameChanged: (String) -> Unit = {},
    val onTripStatusChanged: (TripStatus) -> Unit = {},
    val onAddParticipantButtonClick: () -> Unit = {},
    val onEditParticipantButtonClick: (TripParticipant) -> Unit = {},
    val onDeleteParticipant: (TripParticipant) -> Unit = {},
    val onAddCurrencyButtonClick: () -> Unit = {},
    val onDeleteCurrency: (String) -> Unit = {},
    val onSaveTrip: () -> Unit = {}
)