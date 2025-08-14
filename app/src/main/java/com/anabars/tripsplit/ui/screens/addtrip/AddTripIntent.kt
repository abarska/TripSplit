package com.anabars.tripsplit.ui.screens.addtrip

import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripStatus

sealed class AddTripIntent {
    data class TripNameChanged(val name: String) : AddTripIntent()
    data class TripStatusChanged(val status: TripStatus) : AddTripIntent()
    data class NewParticipantNameChanged(val name: String) : AddTripIntent()
    data class NewParticipantMultiplicatorChanged(val multiplicator: Int) : AddTripIntent()
    data class ParticipantEditRequested(val participant: TripParticipant) : AddTripIntent()
    data class ParticipantDeleted(val participant: TripParticipant) : AddTripIntent()
    data class AddDefaultParticipant(val name: String) : AddTripIntent()
    data class CurrencyAdded(val currency: String) : AddTripIntent()
    data class CurrencyDeleted(val code: String) : AddTripIntent()
    data object AddCurrencyClicked : AddTripIntent()
    data object DismissCurrencyDialog : AddTripIntent()
    data object DuplicateNameDialogConfirmed : AddTripIntent()
    data object AddParticipantClicked : AddTripIntent()
    data object DismissAddParticipantDialog : AddTripIntent()
    data object SaveTripClicked : AddTripIntent()
    data object ParticipantInputSaved : AddTripIntent()
}