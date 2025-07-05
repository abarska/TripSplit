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
import com.anabars.tripsplit.ui.model.AddTripEvent.*
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.viewmodels.AddTripViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel

@Composable
fun AddTripScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {

    val viewModel: AddTripViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val currentTripParticipants by viewModel.currentTripParticipants.collectAsState()
    val currentTripCurrencies by viewModel.currentTripCurrencies.collectAsState()
    val availableCurrencies by viewModel.currencies.collectAsState()

    val resetParticipant = {
        viewModel.updateNewParticipantName("")
        viewModel.updateNewParticipantMultiplicator(1)
        viewModel.updateParticipantIndex(-1)
    }

    val onSaveTrip = {
        val tripNameTrimmed = uiState.tripName.trim()
        if (viewModel.fieldNotEmpty(value = tripNameTrimmed)) {
            viewModel.saveTrip(tripName = tripNameTrimmed)
            navigateHome(addTripViewModel = viewModel, navController = navController)
        } else {
            viewModel.updateActiveDialog(ActiveDialog.NONE)
            viewModel.updateTripNameError(true)
            viewModel.updateTripNameErrorMessage(R.string.error_mandatory_field)
        }
    }

    val onAddParticipantButtonClick = {
        viewModel.updateActiveDialog(ActiveDialog.USER_INPUT)
    }

    val onNewParticipant = {
        val nameTrimmed = uiState.newParticipantName.trim()
        if (viewModel.fieldNotEmpty(value = nameTrimmed)) {
            val newParticipant =
                TripParticipant(
                    name = nameTrimmed,
                    multiplicator = uiState.newParticipantMultiplicator
                )
            if (viewModel.nameAlreadyInUse(newParticipant)) {
                viewModel.updateActiveDialog(ActiveDialog.WARNING)
            } else {
                viewModel.addParticipant(newParticipant)
                viewModel.updateActiveDialog(ActiveDialog.NONE)
                resetParticipant()
            }
        }
    }

    val onEditParticipant = {
        val nameTrimmed = uiState.newParticipantName.trim()
        if (viewModel.fieldNotEmpty(nameTrimmed) && uiState.updatedParticipantIndex >= 0) {
            val updatedParticipant =
                TripParticipant(
                    name = nameTrimmed,
                    multiplicator = uiState.newParticipantMultiplicator
                )
            viewModel.updateParticipant(
                uiState.updatedParticipantIndex,
                updatedParticipant
            )
            viewModel.updateActiveDialog(ActiveDialog.NONE)
            resetParticipant()
        }
    }

    val onDeletedParticipant = { participant: TripParticipant ->
        viewModel.removeParticipant(participant)
    }

    val onDismissAddParticipantDialog = {
        viewModel.updateActiveDialog(ActiveDialog.NONE)
        resetParticipant()
    }

    val onAddCurrencyButtonClick = {
        viewModel.updateActiveDialog(ActiveDialog.CHOOSER)
    }

    val onNewCurrency = { currency: String ->
        val code = currency.take(3)
        if (!viewModel.hasCurrency(code)) {
            viewModel.addCurrency(code)
        }
        viewModel.updateActiveDialog(ActiveDialog.NONE)
    }

    val onDeleteCurrency = { code: String ->
        viewModel.removeCurrency(code)
    }

    val onDismissAddCurrencyDialog = {
        viewModel.updateActiveDialog(ActiveDialog.NONE)
    }

    val onDismissSaveChanges = {
        navigateHome(addTripViewModel = viewModel, navController = navController)
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
            viewModel.updateActiveDialog(ActiveDialog.CONFIRMATION)
            true
        } else {
            navigateHome(addTripViewModel = viewModel, navController = navController)
            false
        }
    }

    BackHandler(enabled = true) {
        handleBackNavigation()
    }

    val you = stringResource(R.string.you)
    val localCurrency by viewModel.localCurrency.collectAsState()

    LaunchedEffect(localCurrency) {
        val mainUser = TripParticipant(name = you, multiplicator = 1)
        if (!viewModel.nameAlreadyInUse(mainUser)) viewModel.addParticipant(mainUser)
        if (localCurrency.isNotBlank() && !viewModel.hasCurrency(localCurrency)) {
            viewModel.addCurrency(localCurrency)
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
                onInputChange = {
                    viewModel.onEvent(NewParticipantNameChanged(it))
                },
                onMultiplicatorChange = {
                    viewModel.onEvent(NewParticipantMultiplicatorChanged(it))
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
                    viewModel.updateActiveDialog(ActiveDialog.USER_INPUT)
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
                    onTripNameChanged = { viewModel.onEvent(TripNameChanged(it)) },
                    participants = currentTripParticipants,
                    onAddParticipantButtonClick = onAddParticipantButtonClick,
                    onEditParticipantButtonClick = { viewModel.onEvent(ParticipantEditRequested(it)) },
                    onDeleteParticipant = onDeletedParticipant,
                    currencies = currentTripCurrencies,
                    onAddCurrencyButtonClick = onAddCurrencyButtonClick,
                    onDeleteCurrency = onDeleteCurrency,
                    onSaveTrip = onSaveTrip
                )
            else
                AddTripLandscapeContent(
                    uiState = uiState,
                    onTripNameChanged = { viewModel.onEvent(TripNameChanged(it)) },
                    participants = currentTripParticipants,
                    onAddParticipantButtonClick = onAddParticipantButtonClick,
                    onEditParticipantButtonClick = { viewModel.onEvent(ParticipantEditRequested(it)) },
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