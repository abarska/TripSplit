package com.anabars.tripsplit.ui.screens.addtrip

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.model.Trip
import com.anabars.tripsplit.ui.dialogs.AddParticipantDialog
import com.anabars.tripsplit.viewmodels.TripViewModel

@Composable
fun AddTripScreen(
    navController: NavController,
    tripViewModel: TripViewModel,
    modifier: Modifier = Modifier,
) {

    var tripName by rememberSaveable { mutableStateOf("") }
    var tripNameErrorMessage by rememberSaveable { mutableIntStateOf(0) }
    var tripNameError by rememberSaveable { mutableStateOf(false) }
    var tripDescription by rememberSaveable { mutableStateOf("") }
    var newParticipantName by rememberSaveable { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    val onTripNameChanged = { input: String ->
        tripName = input
        tripNameErrorMessage = 0
        tripNameError = false
    }

    val onTripDescriptionChanged = { input: String ->
        tripDescription = input
    }

    val onSaveTrip = {
        if (tripViewModel.fieldNotEmpty(value = tripName)) {
            tripViewModel.saveTrip(
                Trip(title = tripName, description = tripDescription)
            )
            tripName = ""
            tripDescription = ""
        } else {
            tripNameError = true
            tripNameErrorMessage = R.string.error_mandatory_field
        }
    }

    val onAddParticipantButtonClick = {
        showDialog = true
    }

    val onNewParticipant = {
        if (tripViewModel.fieldNotEmpty(value = newParticipantName)) {
            tripViewModel.addParticipant(newParticipantName)
        }
        showDialog = false
        newParticipantName = ""
    }
    val onDeletedParticipant = { name: String ->
        tripViewModel.removeParticipant(name)
    }

    val onDismissAddParticipantDialog = {
        showDialog = false
        newParticipantName = ""
    }

    val participants by tripViewModel.participants.collectAsState()

    if (showDialog) {
        AddParticipantDialog(
            name = newParticipantName,
            onNameChange = { newName -> newParticipantName = newName },
            onSave = { onNewParticipant() },
            onDismiss = { onDismissAddParticipantDialog() },
        )
    } else {
        val isPortrait =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
        if (isPortrait)
            AddTripPortraitContent(
                tripName = tripName,
                tripNameError = tripNameError,
                tripNameErrorMessage = tripNameErrorMessage,
                onTripNameChanged = onTripNameChanged,
                tripDescription = tripDescription,
                onTripDescriptionChanged = onTripDescriptionChanged,
                participants = participants,
                onAddParticipantButtonClick = onAddParticipantButtonClick,
                onDeletedParticipant = onDeletedParticipant,
                onSaveTrip = onSaveTrip,
                modifier = modifier
            )
        else
            AddTripLandscapeContent(
                tripName = tripName,
                tripNameError = tripNameError,
                tripNameErrorMessage = tripNameErrorMessage,
                onTripNameChanged = onTripNameChanged,
                tripDescription = tripDescription,
                onTripDescriptionChanged = onTripDescriptionChanged,
                participants = participants,
                onAddParticipantButtonClick = onAddParticipantButtonClick,
                onDeletedParticipant = onDeletedParticipant,
                onSaveTrip = onSaveTrip,
                modifier = modifier
            )
    }
}