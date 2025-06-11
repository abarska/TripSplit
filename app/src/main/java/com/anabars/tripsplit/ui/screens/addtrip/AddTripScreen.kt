package com.anabars.tripsplit.ui.screens.addtrip

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.dialogs.ActiveDialog
import com.anabars.tripsplit.ui.dialogs.ConfirmationDialog
import com.anabars.tripsplit.ui.dialogs.UserInputDialog
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.ui.widgets.CurrencyPicker
import com.anabars.tripsplit.viewmodels.SharedViewModel
import com.anabars.tripsplit.viewmodels.TripViewModel

@Composable
fun AddTripScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    modifier: Modifier = Modifier,
) {

    val tripViewModel: TripViewModel = hiltViewModel()

    var tripName by rememberSaveable { mutableStateOf("") }
    var tripNameErrorMessage by rememberSaveable { mutableIntStateOf(0) }
    var tripNameError by rememberSaveable { mutableStateOf(false) }
    var newParticipantName by rememberSaveable { mutableStateOf("") }
    var activeDialog by rememberSaveable { mutableStateOf(ActiveDialog.NONE) }

    val onTripNameChanged = { input: String ->
        tripName = input.trimStart().replaceFirstChar { it.titlecase() }
        tripNameErrorMessage = 0
        tripNameError = false
    }

    val onSaveTrip = {
        val tripNameTrimmed = tripName.trim()
        if (tripViewModel.fieldNotEmpty(value = tripNameTrimmed)) {
            tripViewModel.saveTrip(
                tripName = tripNameTrimmed
            )
            navigateHome(tripViewModel = tripViewModel, navController = navController)
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
            if (tripViewModel.hasParticipant(nameTrimmed)) {
                activeDialog = ActiveDialog.WARNING
            } else {
                tripViewModel.addParticipant(nameTrimmed)
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

    val onAddCurrencyButtonClick = {
        activeDialog = ActiveDialog.CHOOSER
    }

    val onNewCurrency = { currency: String ->
        val code = currency.take(3)
        if (!tripViewModel.hasCurrency(code)) {
            tripViewModel.addCurrency(code)
        }
        activeDialog = ActiveDialog.NONE
    }

    val onDeleteCurrency = { code: String ->
        tripViewModel.removeCurrency(code)
    }

    val onDismissAddCurrencyDialog = {
        activeDialog = ActiveDialog.NONE
    }

    val onDismissSaveChanges = {
        navigateHome(tripViewModel = tripViewModel, navController = navController)
    }

    val currentTripParticipants by tripViewModel.currentTripParticipants.collectAsState()
    val currentTripCurrencies by tripViewModel.currentTripCurrencies.collectAsState()
    val availableCurrencies by tripViewModel.currencies.collectAsState()

    val hasUnsavedInput by remember(
        tripName,
        currentTripParticipants,
        currentTripCurrencies
    ) {
        derivedStateOf {
            tripName.isNotBlank() || currentTripParticipants.size > 1 || currentTripCurrencies.size > 1
        }
    }

    val handleBackNavigation: () -> Boolean = {
        if (hasUnsavedInput) {
            activeDialog = ActiveDialog.CONFIRMATION
            true
        } else {
            navigateHome(tripViewModel = tripViewModel, navController = navController)
            false
        }
    }

    BackHandler(enabled = true) {
        handleBackNavigation()
    }

    val you = stringResource(R.string.you)
    val localCurrency = tripViewModel.getLocalCurrency()

    LaunchedEffect(Unit) {
        if (!tripViewModel.hasParticipant(you)) tripViewModel.addParticipant(you)
        if (!tripViewModel.hasCurrency(localCurrency)) tripViewModel.addCurrency(localCurrency)
        sharedViewModel.setBackHandler { handleBackNavigation() }
    }

    DisposableEffect(Unit) {
        onDispose {
            sharedViewModel.setBackHandler(null)
        }
    }

    when (activeDialog) {
        ActiveDialog.CHOOSER -> {
            val expanded = remember { mutableStateOf(true) }
            CurrencyPicker(
                currencies = availableCurrencies,
                expanded = expanded,
                onCurrencySelected = onNewCurrency,
                onDismissAddCurrencyDialog = onDismissAddCurrencyDialog
            )
        }

        ActiveDialog.USER_INPUT -> {
            UserInputDialog(
                input = newParticipantName,
                onInputChange = { newInput ->
                    newParticipantName = newInput.trimStart().replaceFirstChar { it.titlecase() }
                },
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
                positiveTextRes = android.R.string.ok,
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
                    participants = currentTripParticipants,
                    onAddParticipantButtonClick = onAddParticipantButtonClick,
                    onDeletedParticipant = onDeletedParticipant,
                    currencies = currentTripCurrencies,
                    onAddCurrencyButtonClick = onAddCurrencyButtonClick,
                    onDeleteCurrency = onDeleteCurrency,
                    onSaveTrip = onSaveTrip,
                    modifier = modifier.padding(dimensionResource(R.dimen.full_screen_padding))
                )
            else
                AddTripLandscapeContent(
                    tripName = tripName,
                    tripNameError = tripNameError,
                    tripNameErrorMessage = tripNameErrorMessage,
                    onTripNameChanged = onTripNameChanged,
                    participants = currentTripParticipants,
                    onAddParticipantButtonClick = onAddParticipantButtonClick,
                    onDeletedParticipant = onDeletedParticipant,
                    currencies = currentTripCurrencies,
                    onAddCurrencyButtonClick = onAddCurrencyButtonClick,
                    onDeleteCurrency = onDeleteCurrency,
                    onSaveTrip = onSaveTrip,
                    modifier = modifier.padding(dimensionResource(R.dimen.full_screen_padding))
                )
        }
    }
}

private fun navigateHome(navController: NavController, tripViewModel: TripViewModel) {
    tripViewModel.clearTempData()
    navController.navigate(AppScreens.ROUTE_TRIPS) {
        popUpTo(navController.graph.startDestinationId) {
            inclusive = true
        }
        launchSingleTop = true
    }
}