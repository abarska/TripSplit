package com.anabars.tripsplit.ui.screens.addtrip

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsCurrencyPicker
import com.anabars.tripsplit.ui.dialogs.ActiveDialog
import com.anabars.tripsplit.ui.dialogs.TsConfirmationDialog
import com.anabars.tripsplit.ui.dialogs.TsUserInputDialog
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.viewmodels.AddTripViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel

@Composable
fun AddTripScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {

    val addTripViewModel: AddTripViewModel = hiltViewModel()
    val uiState by addTripViewModel.uiState.collectAsState()

    val currentTripParticipants by addTripViewModel.currentTripParticipants.collectAsState()
    val currentTripCurrencies by addTripViewModel.currentTripCurrencies.collectAsState()
    val availableCurrencies by addTripViewModel.currencies.collectAsState()

    val resetParticipant = {
        addTripViewModel.updateNewParticipantName("")
        addTripViewModel.updateNewParticipantMultiplicator(1)
        addTripViewModel.updateParticipantIndex(-1)
    }

    val onTripNameChanged = { input: String ->
        addTripViewModel.updateTripName(input)
        addTripViewModel.updateTripNameErrorMessage(0)
        addTripViewModel.updateTripNameError(false)
    }

    val onSaveTrip = {
        val tripNameTrimmed = uiState.tripName.trim()
        if (addTripViewModel.fieldNotEmpty(value = tripNameTrimmed)) {
            addTripViewModel.saveTrip(tripName = tripNameTrimmed)
            navigateHome(addTripViewModel = addTripViewModel, navController = navController)
        } else {
            addTripViewModel.updateActiveDialog(ActiveDialog.NONE)
            addTripViewModel.updateTripNameError(true)
            addTripViewModel.updateTripNameErrorMessage(R.string.error_mandatory_field)
        }
    }

    val onAddParticipantButtonClick = {
        addTripViewModel.updateActiveDialog(ActiveDialog.USER_INPUT)
    }

    val onEditParticipantButtonClick: (TripParticipant) -> Unit = {
        addTripViewModel.updateNewParticipantName(it.name)
        addTripViewModel.updateNewParticipantMultiplicator(it.multiplicator)
        addTripViewModel.updateParticipantIndex(currentTripParticipants.indexOf(it))
        addTripViewModel.updateActiveDialog(ActiveDialog.USER_INPUT)
    }

    val onNewParticipant = {
        val nameTrimmed = uiState.newParticipantName.trim()
        if (addTripViewModel.fieldNotEmpty(value = nameTrimmed)) {
            val newParticipant =
                TripParticipant(
                    name = nameTrimmed,
                    multiplicator = uiState.newParticipantMultiplicator
                )
            if (addTripViewModel.nameAlreadyInUse(newParticipant)) {
                addTripViewModel.updateActiveDialog(ActiveDialog.WARNING)
            } else {
                addTripViewModel.addParticipant(newParticipant)
                addTripViewModel.updateActiveDialog(ActiveDialog.NONE)
                resetParticipant()
            }
        }
    }

    val onEditParticipant = {
        val nameTrimmed = uiState.newParticipantName.trim()
        if (addTripViewModel.fieldNotEmpty(nameTrimmed) && uiState.updatedParticipantIndex >= 0) {
            val updatedParticipant =
                TripParticipant(
                    name = nameTrimmed,
                    multiplicator = uiState.newParticipantMultiplicator
                )
            addTripViewModel.updateParticipant(
                uiState.updatedParticipantIndex,
                updatedParticipant
            )
            addTripViewModel.updateActiveDialog(ActiveDialog.NONE)
            resetParticipant()
        }
    }

    val onDeletedParticipant = { participant: TripParticipant ->
        addTripViewModel.removeParticipant(participant)
    }

    val onDismissAddParticipantDialog = {
        addTripViewModel.updateActiveDialog(ActiveDialog.NONE)
        resetParticipant()
    }

    val onAddCurrencyButtonClick = {
        addTripViewModel.updateActiveDialog(ActiveDialog.CHOOSER)
    }

    val onNewCurrency = { currency: String ->
        val code = currency.take(3)
        if (!addTripViewModel.hasCurrency(code)) {
            addTripViewModel.addCurrency(code)
        }
        addTripViewModel.updateActiveDialog(ActiveDialog.NONE)
    }

    val onDeleteCurrency = { code: String ->
        addTripViewModel.removeCurrency(code)
    }

    val onDismissAddCurrencyDialog = {
        addTripViewModel.updateActiveDialog(ActiveDialog.NONE)
    }

    val onDismissSaveChanges = {
        navigateHome(addTripViewModel = addTripViewModel, navController = navController)
    }

    val hasUnsavedInput by remember(
        uiState.tripName,
        currentTripParticipants,
        currentTripCurrencies
    ) {
        derivedStateOf {
            uiState.tripName.isNotBlank() || currentTripParticipants.size > 1 || currentTripCurrencies.size > 1
        }
    }

    val handleBackNavigation: () -> Boolean = {
        if (hasUnsavedInput) {
            addTripViewModel.updateActiveDialog(ActiveDialog.CONFIRMATION)
            true
        } else {
            navigateHome(addTripViewModel = addTripViewModel, navController = navController)
            false
        }
    }

    BackHandler(enabled = true) {
        handleBackNavigation()
    }

    val you = stringResource(R.string.you)
    val localCurrency by addTripViewModel.localCurrency.collectAsState()

    LaunchedEffect(localCurrency) {
        val mainUser = TripParticipant(name = you, multiplicator = 1)
        if (!addTripViewModel.nameAlreadyInUse(mainUser)) addTripViewModel.addParticipant(mainUser)
        if (localCurrency.isNotBlank() && !addTripViewModel.hasCurrency(localCurrency)) {
            addTripViewModel.addCurrency(localCurrency)
        }
        sharedViewModel.setBackHandler { handleBackNavigation() }
    }

    DisposableEffect(Unit) {
        onDispose { sharedViewModel.setBackHandler(null) }
    }

    when (uiState.activeDialog) {
        ActiveDialog.CHOOSER -> {
            val expanded = remember { mutableStateOf(true) }
            TsCurrencyPicker(
                currencies = availableCurrencies,
                expanded = expanded,
                onCurrencySelected = onNewCurrency,
                onDismissAddCurrencyDialog = onDismissAddCurrencyDialog
            )
        }

        ActiveDialog.USER_INPUT -> {
            TsUserInputDialog(
                uiState = uiState,
                onInputChange = { newInput -> addTripViewModel.updateNewParticipantName(newInput) },
                onMultiplicatorChange = { newMultiplicator ->
                    addTripViewModel.updateNewParticipantMultiplicator(newMultiplicator)
                },
                onConfirm = {
                    if (uiState.updatedParticipantIndex >= 0) onEditParticipant()
                    else onNewParticipant()
                },
                onDismiss = { onDismissAddParticipantDialog() },
                titleRes = if (uiState.updatedParticipantIndex >= 0) R.string.edit_participant else R.string.add_participant,
                labelRes = R.string.participant_name_hint,
                positiveTextRes = if (uiState.updatedParticipantIndex >= 0) R.string.save else R.string.add,
                negativeTextRes = R.string.cancel
            )
        }

        ActiveDialog.CONFIRMATION -> {
            TsConfirmationDialog(
                onDismiss = onDismissSaveChanges,
                onConfirm = onSaveTrip,
                titleRes = R.string.save_changes_dialog_title,
                questionRes = R.string.save_changes_dialog_question,
                positiveTextRes = R.string.save,
                negativeTextRes = R.string.discard
            )
        }

        ActiveDialog.WARNING -> {
            TsConfirmationDialog(
                onConfirm = {
                    resetParticipant()
                    addTripViewModel.updateActiveDialog(ActiveDialog.USER_INPUT)
                },
                titleRes = R.string.duplicate_name_dialog_title,
                questionRes = R.string.duplicate_name_dialog_warning,
                positiveTextRes = android.R.string.ok,
            )
        }

        ActiveDialog.NONE -> {
            val isPortrait =
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
            if (isPortrait)
                AddTripPortraitContent(
                    uiState = uiState,
                    onTripNameChanged = onTripNameChanged,
                    participants = currentTripParticipants,
                    onAddParticipantButtonClick = onAddParticipantButtonClick,
                    onEditParticipantButtonClick = onEditParticipantButtonClick,
                    onDeleteParticipant = onDeletedParticipant,
                    currencies = currentTripCurrencies,
                    onAddCurrencyButtonClick = onAddCurrencyButtonClick,
                    onDeleteCurrency = onDeleteCurrency,
                    onSaveTrip = onSaveTrip
                )
            else
                AddTripLandscapeContent(
                    uiState = uiState,
                    onTripNameChanged = onTripNameChanged,
                    participants = currentTripParticipants,
                    onAddParticipantButtonClick = onAddParticipantButtonClick,
                    onEditParticipantButtonClick = onEditParticipantButtonClick,
                    onDeleteParticipant = onDeletedParticipant,
                    currencies = currentTripCurrencies,
                    onAddCurrencyButtonClick = onAddCurrencyButtonClick,
                    onDeleteCurrency = onDeleteCurrency,
                    onSaveTrip = onSaveTrip
                )
        }
    }
}

private fun navigateHome(addTripViewModel: AddTripViewModel, navController: NavController) {
    navController.navigate(AppScreens.ROUTE_TRIPS) {
        popUpTo(navController.graph.startDestinationId) {
            inclusive = true
        }
        launchSingleTop = true
    }
    addTripViewModel.clearTempData()
}