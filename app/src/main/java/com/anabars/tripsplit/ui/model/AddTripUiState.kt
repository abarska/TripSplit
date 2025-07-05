package com.anabars.tripsplit.ui.model

import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.dialogs.ActiveDialog

data class AddTripUiState(
    val tripCurrencies: List<String> = emptyList(),
    val tripName: String = "",
    val tripNameErrorMessage: Int = 0,
    val tripNameError: Boolean = false,
    val newParticipantName: String = "",
    val newParticipantMultiplicator: Int = 1,
    val updatedParticipantIndex: Int = -1,
    val activeDialog: ActiveDialog = ActiveDialog.NONE,
)

sealed class AddTripEvent {
    data class TripNameChanged(val name: String) : AddTripEvent()
    data class NewParticipantNameChanged(val name: String) : AddTripEvent()
    data class NewParticipantMultiplicatorChanged(val multiplicator: Int) : AddTripEvent()
    data class ParticipantEditRequested(val participant: TripParticipant) : AddTripEvent()
    data class ParticipantDeleted(val participant: TripParticipant) : AddTripEvent()
    data class CurrencyAdded(val currency: String) : AddTripEvent()
    data class CurrencyDeleted(val code: String) : AddTripEvent()
    object AddCurrencyClicked : AddTripEvent()
    object DismissCurrencyDialog : AddTripEvent()
    object DuplicateNameDialogConfirmed : AddTripEvent()
    object AddParticipantClicked : AddTripEvent()
    object DismissAddParticipantDialog : AddTripEvent()
    object SaveTripClicked : AddTripEvent()
    object NewParticipantSaveClicked : AddTripEvent()
    object ExistingParticipantEdited : AddTripEvent()
}