package com.anabars.tripsplit.ui.model

import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.dialogs.ActiveDialog

data class AddTripDialogState(
    val activeDialog: ActiveDialog = ActiveDialog.NONE,
)

data class AddTripNameUiState(
    val tripName: String = "",
    val tripNameErrorMessage: Int = 0,
    val tripNameError: Boolean = false
)

data class AddTripParticipantsUiState(
    val tripParticipants: List<TripParticipant> = emptyList(),
    val newParticipantName: String = "",
    val newParticipantMultiplicator: Int = 1,
    val updatedParticipantIndex: Int = -1
) {
    val isEditParticipant: Boolean
        get() = updatedParticipantIndex >= 0
}

data class AddTripCurrenciesUiState(
    val availableCurrencies: List<String> = emptyList(),
    val tripCurrencyCodes: List<String> = emptyList()
)