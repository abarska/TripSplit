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
import com.anabars.tripsplit.ui.components.TsCurrencyPicker
import com.anabars.tripsplit.ui.dialogs.ActiveDialog
import com.anabars.tripsplit.ui.dialogs.TsConfirmationDialog
import com.anabars.tripsplit.ui.dialogs.TsUserInputDialog
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.*
import com.anabars.tripsplit.viewmodels.AddTripViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

@Composable
fun AddTripScreen(
    navController: NavController,
    onTabTitleChange: (String) -> Unit,
    setBackHandler: ((() -> Boolean)?) -> Unit
) {

    val viewModel: AddTripViewModel = hiltViewModel()
    val dialogUiState by viewModel.dialogUiState.collectAsState()
    val nameUiState by viewModel.nameUiState.collectAsState()
    val tripStatusUiState by viewModel.statusUiState.collectAsState()
    val participantsUiState by viewModel.participantsUiState.collectAsState()
    val currenciesUiState by viewModel.currenciesUiState.collectAsState()

    BackHandler(enabled = true) {
        viewModel.onIntent(OnBackPressed)
    }

    val defaultParticipantName = stringResource(R.string.you)
    val addScreenTitle = stringResource(R.string.title_new_trip)
    val editScreenTitle = stringResource(R.string.title_edit_trip)

    LaunchedEffect(Unit) {
        onTabTitleChange(if (viewModel.isEditModeFlow.first()) editScreenTitle else addScreenTitle)
        viewModel.onIntent(AddDefaultParticipant(defaultParticipantName))
        viewModel.shouldNavigateBack.collectLatest { navigateBack ->
            if (navigateBack) navController.popBackStack()
        }
        setBackHandler {
            viewModel.onIntent(OnBackPressed)
            true
        }
    }

    DisposableEffect(Unit) {
        onDispose { setBackHandler(null) }
    }

    when (dialogUiState.activeDialog) {
        ActiveDialog.CHOOSER -> {
            val expanded = remember { mutableStateOf(true) }
            TsCurrencyPicker(
                currencies = currenciesUiState.availableCurrencies,
                expanded = expanded,
                onCurrencySelected = { viewModel.onIntent(CurrencyAdded(it)) },
                onDismissAddCurrencyDialog = { viewModel.onIntent(DismissCurrencyDialog) }
            )
        }

        ActiveDialog.USER_INPUT -> {
            TsUserInputDialog(
                tripParticipantsUiState = participantsUiState,
                onInputChange = { viewModel.onIntent(NewParticipantNameChanged(it)) },
                onMultiplicatorChange = { viewModel.onIntent(NewParticipantMultiplicatorChanged(it)) },
                onConfirm = { viewModel.onIntent(ParticipantInputSaved) },
                onDismiss = { viewModel.onIntent(DismissAddParticipantDialog) },
                titleRes = if (participantsUiState.isEditParticipant) R.string.edit_participant else R.string.add_participant,
                labelRes = R.string.participant_name_hint,
                positiveTextRes = if (participantsUiState.isEditParticipant) R.string.save else R.string.add,
                negativeTextRes = R.string.cancel
            )
        }

        ActiveDialog.CONFIRMATION -> {
            TsConfirmationDialog(
                onDismiss = { navController.popBackStack() },
                onConfirm = { viewModel.onIntent(SaveTripClicked) },
                titleRes = R.string.save_changes_dialog_title,
                questionRes = R.string.save_changes_dialog_question,
                positiveTextRes = R.string.save,
                negativeTextRes = R.string.discard
            )
        }

        ActiveDialog.WARNING -> {
            TsConfirmationDialog(
                onConfirm = { viewModel.onIntent(DuplicateNameDialogConfirmed) },
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
                    tripNameUiState = nameUiState,
                    tripStatusUiState = tripStatusUiState,
                    tripParticipants = participantsUiState.tripParticipants,
                    tripCurrencies = currenciesUiState.tripCurrencyCodes,
                    onTripNameChanged = { viewModel.onIntent(TripNameChanged(it)) },
                    onTripStatusChanged = { viewModel.onIntent(TripStatusChanged(it)) },
                    onAddParticipantButtonClick = { viewModel.onIntent(AddParticipantClicked) },
                    onEditParticipantButtonClick = { viewModel.onIntent(ParticipantEditRequested(it)) },
                    onDeleteParticipant = { viewModel.onIntent(ParticipantDeleted(it)) },
                    onAddCurrencyButtonClick = { viewModel.onIntent(AddCurrencyClicked) },
                    onDeleteCurrency = { viewModel.onIntent(CurrencyDeleted(it)) },
                    onSaveTrip = { viewModel.onIntent(SaveTripClicked) }
                )
            else
                AddTripLandscapeContent(
                    tripNameUiState = nameUiState,
                    tripStatusUiState = tripStatusUiState,
                    tripParticipants = participantsUiState.tripParticipants,
                    tripCurrencies = currenciesUiState.tripCurrencyCodes,
                    onTripNameChanged = { viewModel.onIntent(TripNameChanged(it)) },
                    onTripStatusChanged = { viewModel.onIntent(TripStatusChanged(it)) },
                    onAddParticipantButtonClick = { viewModel.onIntent(AddParticipantClicked) },
                    onEditParticipantButtonClick = { viewModel.onIntent(ParticipantEditRequested(it)) },
                    onDeleteParticipant = { viewModel.onIntent(ParticipantDeleted(it)) },
                    onAddCurrencyButtonClick = { viewModel.onIntent(AddCurrencyClicked) },
                    onDeleteCurrency = { viewModel.onIntent(CurrencyDeleted(it)) },
                    onSaveTrip = { viewModel.onIntent(SaveTripClicked) }
                )
        }
    }
}