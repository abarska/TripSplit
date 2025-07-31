package com.anabars.tripsplit.ui.model

import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripStatus
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

sealed class AddTripEvent {
    data class TripNameChanged(val name: String) : AddTripEvent()
    data class TripStatusChanged(val status: TripStatus) : AddTripEvent()
    data class NewParticipantNameChanged(val name: String) : AddTripEvent()
    data class NewParticipantMultiplicatorChanged(val multiplicator: Int) : AddTripEvent()
    data class ParticipantEditRequested(val participant: TripParticipant) : AddTripEvent()
    data class ParticipantDeleted(val participant: TripParticipant) : AddTripEvent()
    data class AddDefaultParticipant(val name: String) : AddTripEvent()
    data class CurrencyAdded(val currency: String) : AddTripEvent()
    data class CurrencyDeleted(val code: String) : AddTripEvent()
    object AddCurrencyClicked : AddTripEvent()
    object DismissCurrencyDialog : AddTripEvent()
    object DuplicateNameDialogConfirmed : AddTripEvent()
    object AddParticipantClicked : AddTripEvent()
    object DismissAddParticipantDialog : AddTripEvent()
    object SaveTripClicked : AddTripEvent()
    object ParticipantInputSaved : AddTripEvent()
    object OnBackPressed : AddTripEvent()
}