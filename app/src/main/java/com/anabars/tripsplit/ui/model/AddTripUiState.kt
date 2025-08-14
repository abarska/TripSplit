package com.anabars.tripsplit.ui.model

import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripStatus
import com.anabars.tripsplit.ui.dialogs.ActiveDialog

data class AddTripUiState(
    val activeDialog: ActiveDialog = ActiveDialog.NONE,
    val tripName: String = "",
    val tripNameErrorMessage: Int = 0,
    val tripNameError: Boolean = false,
    val tripStatus: TripStatus = TripStatus.PLANNED,
    val tripParticipants: List<TripParticipant> = emptyList(),
    val newParticipantName: String = "",
    val newParticipantMultiplicator: Int = 1,
    val updatedParticipantIndex: Int = -1,
    val availableCurrencies: List<String> = emptyList(),
    val tripCurrencyCodes: List<String> = emptyList()
) {
    val isEditParticipant: Boolean
        get() = updatedParticipantIndex >= 0
}