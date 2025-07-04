package com.anabars.tripsplit.ui.model

import com.anabars.tripsplit.ui.dialogs.ActiveDialog

data class AddTripUiState(
    val tripName: String = "",
    val tripNameErrorMessage: Int = 0,
    val tripNameError: Boolean = false,
    val newParticipantName: String = "",
    val newParticipantMultiplicator: Int = 1,
    val updatedParticipantIndex: Int = -1,
    val activeDialog: ActiveDialog = ActiveDialog.NONE,
)