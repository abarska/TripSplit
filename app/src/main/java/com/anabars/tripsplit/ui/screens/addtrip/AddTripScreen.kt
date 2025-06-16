package com.anabars.tripsplit.ui.screens.addtrip

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anabars.tripsplit.R
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
    modifier: Modifier = Modifier,
) {

    val addTripViewModel: AddTripViewModel = hiltViewModel()

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
        if (addTripViewModel.fieldNotEmpty(value = tripNameTrimmed)) {
            addTripViewModel.saveTrip(tripName = tripNameTrimmed)
            navigateHome(addTripViewModel = addTripViewModel, navController = navController)
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
        if (addTripViewModel.fieldNotEmpty(value = nameTrimmed)) {
            if (addTripViewModel.hasParticipant(nameTrimmed)) {
                activeDialog = ActiveDialog.WARNING
            } else {
                addTripViewModel.addParticipant(nameTrimmed)
                activeDialog = ActiveDialog.NONE
                newParticipantName = ""
            }
        }
    }
    val onDeletedParticipant = { name: String ->
        addTripViewModel.removeParticipant(name)
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
        if (!addTripViewModel.hasCurrency(code)) {
            addTripViewModel.addCurrency(code)
        }
        activeDialog = ActiveDialog.NONE
    }

    val onDeleteCurrency = { code: String ->
        addTripViewModel.removeCurrency(code)
    }

    val onDismissAddCurrencyDialog = {
        activeDialog = ActiveDialog.NONE
    }

    val onDismissSaveChanges = {
        navigateHome(addTripViewModel = addTripViewModel, navController = navController)
    }

    val currentTripParticipants by addTripViewModel.currentTripParticipants.collectAsState()
    val currentTripCurrencies by addTripViewModel.currentTripCurrencies.collectAsState()
    val availableCurrencies by addTripViewModel.currencies.collectAsState()

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
            navigateHome(addTripViewModel = addTripViewModel, navController = navController)
            false
        }
    }

    BackHandler(enabled = true) {
        handleBackNavigation()
    }

    val you = stringResource(R.string.you)
    val localCurrency = addTripViewModel.getLocalCurrency()

    LaunchedEffect(Unit) {
        if (!addTripViewModel.hasParticipant(you)) addTripViewModel.addParticipant(you)
        if (!addTripViewModel.hasCurrency(localCurrency)) addTripViewModel.addCurrency(localCurrency)
        sharedViewModel.setBackHandler { handleBackNavigation() }
    }

    DisposableEffect(Unit) {
        onDispose { sharedViewModel.setBackHandler(null) }
    }

    when (activeDialog) {
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
                    onSaveTrip = onSaveTrip
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
                    onSaveTrip = onSaveTrip
                )
        }
    }
}

private fun navigateHome(addTripViewModel: AddTripViewModel, navController: NavController) {
    addTripViewModel.clearTempData()
    navController.navigate(AppScreens.ROUTE_TRIPS) {
        popUpTo(navController.graph.startDestinationId) {
            inclusive = true
        }
        launchSingleTop = true
    }
}