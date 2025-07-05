package com.anabars.tripsplit.ui.screens.addtrip

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.anabars.tripsplit.ui.model.AddTripEvent.AddCurrencyClicked
import com.anabars.tripsplit.ui.model.AddTripEvent.AddParticipantClicked
import com.anabars.tripsplit.ui.model.AddTripEvent.CurrencyAdded
import com.anabars.tripsplit.ui.model.AddTripEvent.CurrencyDeleted
import com.anabars.tripsplit.ui.model.AddTripEvent.DismissAddParticipantDialog
import com.anabars.tripsplit.ui.model.AddTripEvent.DismissCurrencyDialog
import com.anabars.tripsplit.ui.model.AddTripEvent.DuplicateNameDialogConfirmed
import com.anabars.tripsplit.ui.model.AddTripEvent.ExistingParticipantEdited
import com.anabars.tripsplit.ui.model.AddTripEvent.NewParticipantMultiplicatorChanged
import com.anabars.tripsplit.ui.model.AddTripEvent.NewParticipantNameChanged
import com.anabars.tripsplit.ui.model.AddTripEvent.NewParticipantSaveClicked
import com.anabars.tripsplit.ui.model.AddTripEvent.ParticipantDeleted
import com.anabars.tripsplit.ui.model.AddTripEvent.ParticipantEditRequested
import com.anabars.tripsplit.ui.model.AddTripEvent.SaveTripClicked
import com.anabars.tripsplit.ui.model.AddTripEvent.TripNameChanged
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
    val shouldNavigateHome by viewModel.shouldNavigateHome.collectAsState()

    val handleBackNavigation: () -> Boolean = {
        if (viewModel.hasUnsavedInput()) {
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

    LaunchedEffect(localCurrency, shouldNavigateHome) {
        val mainUser = TripParticipant(name = you, multiplicator = 1)
        if (!viewModel.nameAlreadyInUse(mainUser)) viewModel.addParticipant(mainUser)
        if (localCurrency.isNotBlank() && !viewModel.hasCurrency(localCurrency)) {
            viewModel.addCurrency(localCurrency)
        }
        sharedViewModel.setBackHandler { handleBackNavigation() }
        if (shouldNavigateHome) {
            navigateHome(addTripViewModel = viewModel, navController = navController)
        }
    }

    DisposableEffect(Unit) {
        onDispose { sharedViewModel.setBackHandler(null) }
    }

    when (uiState.activeDialog) {
        ActiveDialog.CHOOSER -> {
            val expanded = remember { mutableStateOf(true) }
            TsCurrencyPicker(
                currencies = uiState.availableCurrencies,
                expanded = expanded,
                onCurrencySelected = { viewModel.onEvent(CurrencyAdded(it)) },
                onDismissAddCurrencyDialog = { viewModel.onEvent(DismissCurrencyDialog) }
            )
        }

        ActiveDialog.USER_INPUT -> {
            TsUserInputDialog(
                uiState = uiState,
                onInputChange = { viewModel.onEvent(NewParticipantNameChanged(it)) },
                onMultiplicatorChange = { viewModel.onEvent(NewParticipantMultiplicatorChanged(it)) },
                onConfirm = {
                    if (uiState.updatedParticipantIndex >= 0) { viewModel.onEvent(ExistingParticipantEdited) }
                    else { viewModel.onEvent(NewParticipantSaveClicked) }
                },
                onDismiss = { viewModel.onEvent(DismissAddParticipantDialog) },
                titleRes = if (uiState.updatedParticipantIndex >= 0) R.string.edit_participant else R.string.add_participant,
                labelRes = R.string.participant_name_hint,
                positiveTextRes = if (uiState.updatedParticipantIndex >= 0) R.string.save else R.string.add,
                negativeTextRes = R.string.cancel
            )
        }

        ActiveDialog.CONFIRMATION -> {
            TsConfirmationDialog(
                onDismiss = {
                    navigateHome(
                        addTripViewModel = viewModel,
                        navController = navController
                    )
                },
                onConfirm = { viewModel.onEvent(SaveTripClicked) },
                titleRes = R.string.save_changes_dialog_title,
                questionRes = R.string.save_changes_dialog_question,
                positiveTextRes = R.string.save,
                negativeTextRes = R.string.discard
            )
        }

        ActiveDialog.WARNING -> {
            TsConfirmationDialog(
                onConfirm = { viewModel.onEvent(DuplicateNameDialogConfirmed) },
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
                    onAddParticipantButtonClick = { viewModel.onEvent(AddParticipantClicked) },
                    onEditParticipantButtonClick = { viewModel.onEvent(ParticipantEditRequested(it)) },
                    onDeleteParticipant = { viewModel.onEvent(ParticipantDeleted(it)) },
                    onAddCurrencyButtonClick = { viewModel.onEvent(AddCurrencyClicked) },
                    onDeleteCurrency = { viewModel.onEvent(CurrencyDeleted(it)) },
                    onSaveTrip = { viewModel.onEvent(SaveTripClicked) }
                )
            else
                AddTripLandscapeContent(
                    uiState = uiState,
                    onTripNameChanged = { viewModel.onEvent(TripNameChanged(it)) },
                    onAddParticipantButtonClick = { viewModel.onEvent(AddParticipantClicked) },
                    onEditParticipantButtonClick = { viewModel.onEvent(ParticipantEditRequested(it)) },
                    onDeleteParticipant = { viewModel.onEvent(ParticipantDeleted(it)) },
                    onAddCurrencyButtonClick = { viewModel.onEvent(AddCurrencyClicked) },
                    onDeleteCurrency = { viewModel.onEvent(CurrencyDeleted(it)) },
                    onSaveTrip = { viewModel.onEvent(SaveTripClicked) }
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