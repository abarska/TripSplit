package com.anabars.tripsplit.ui.screens.addtrip

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.dialogs.ActiveDialog
import com.anabars.tripsplit.ui.dialogs.UserInputDialog
import com.anabars.tripsplit.ui.dialogs.ConfirmationDialog
import com.anabars.tripsplit.ui.screens.AppScreens
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
    var activeDialog by rememberSaveable { mutableStateOf(ActiveDialog.NONE) }

    val onTripNameChanged = { input: String ->
        tripName = input.trimStart().replaceFirstChar { it.titlecase() }
        tripNameErrorMessage = 0
        tripNameError = false
    }

    val onTripDescriptionChanged = { input: String ->
        tripDescription = input
    }

    val onSaveTrip = {
        val tripNameTrimmed = tripName.trim()
        if (tripViewModel.fieldNotEmpty(value = tripNameTrimmed)) {
            tripViewModel.saveTrip(
                tripName = tripNameTrimmed,
                tripDescription = tripDescription.trim()
            )
            tripViewModel.clearParticipants()
            navigateHome(navController)
        } else {
            activeDialog = ActiveDialog.NONE
            tripNameError = true
            tripNameErrorMessage = R.string.error_mandatory_field
        }
    }

    val onAddParticipantButtonClick = {
        activeDialog = ActiveDialog.USER_INPUT
    }

    val onNewParticipant = {
        val nameTrimmed = newParticipantName.trim()
        if (tripViewModel.fieldNotEmpty(value = nameTrimmed)) {
            val nameCapitalized = nameTrimmed.replaceFirstChar { it.titlecase() }
            if (tripViewModel.hasParticipant(nameCapitalized)) {
                activeDialog = ActiveDialog.WARNING
            } else {
                tripViewModel.addParticipant(nameCapitalized)
                activeDialog = ActiveDialog.NONE
                newParticipantName = ""
            }
        }
    }
    val onDeletedParticipant = { name: String ->
        tripViewModel.removeParticipant(name)
    }

    val onDismissAddParticipantDialog = {
        activeDialog = ActiveDialog.NONE
        newParticipantName = ""
    }

    val onDismissSaveChanges = {
        tripViewModel.clearParticipants()
        navigateHome(navController)
    }

    val participants by tripViewModel.participants.collectAsState()

    val hasUnsavedInput by remember(tripName, tripDescription, participants) {
        derivedStateOf {
            tripName.isNotBlank() || tripDescription.isNotBlank() || participants.size > 1
        }
    }

    BackHandler(enabled = hasUnsavedInput) {
        activeDialog = ActiveDialog.CONFIRMATION
    }

    val you = stringResource(R.string.you)
    LaunchedEffect(Unit) {
        if (!tripViewModel.hasParticipant(you)) tripViewModel.addParticipant(you)
        tripViewModel.setBackHandler {
            if (hasUnsavedInput) {
                activeDialog = ActiveDialog.CONFIRMATION
                true
            } else false
        }
    }

    when (activeDialog) {
        ActiveDialog.USER_INPUT -> {
            UserInputDialog(
                input = newParticipantName,
                onInputChange = { newInput -> newParticipantName = newInput },
                onConfirm = { onNewParticipant() },
                onDismiss = { onDismissAddParticipantDialog() },
                titleRes = R.string.add_a_participant,
                labelRes = R.string.participant_name_hint,
                positiveTextRes = R.string.add,
                negativeTextRes = R.string.cancel
            )
        }

        ActiveDialog.CONFIRMATION -> {
            ConfirmationDialog(
                onDismiss = onDismissSaveChanges,
                onConfirm = onSaveTrip,
                titleRes = R.string.save_changes_dialog_title,
                questionRes = R.string.save_changes_dialog_question,
                positiveTextRes = R.string.save,
                negativeTextRes = R.string.discard
            )
        }

        ActiveDialog.WARNING -> {
            ConfirmationDialog(
                onConfirm = {
                    newParticipantName = ""
                    activeDialog = ActiveDialog.USER_INPUT
                },
                titleRes = R.string.duplicate_name_dialog_title,
                questionRes = R.string.duplicate_name_dialog_warning,
                positiveTextRes = R.string.ok,
            )
        }

        ActiveDialog.NONE -> {
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
}

private fun navigateHome(navController: NavController) {
    navController.navigate(AppScreens.ROUTE_TRIPS) {
        popUpTo(navController.graph.startDestinationId) {
            inclusive = true
        }
        launchSingleTop = true
    }
}