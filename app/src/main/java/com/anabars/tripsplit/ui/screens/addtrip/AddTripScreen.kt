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
import com.anabars.tripsplit.ui.model.AddTripEvent
import com.anabars.tripsplit.ui.model.AddTripEvent.AddCurrencyClicked
import com.anabars.tripsplit.ui.model.AddTripEvent.AddParticipantClicked
import com.anabars.tripsplit.ui.model.AddTripEvent.CurrencyAdded
import com.anabars.tripsplit.ui.model.AddTripEvent.CurrencyDeleted
import com.anabars.tripsplit.ui.model.AddTripEvent.DismissAddParticipantDialog
import com.anabars.tripsplit.ui.model.AddTripEvent.DismissCurrencyDialog
import com.anabars.tripsplit.ui.model.AddTripEvent.DuplicateNameDialogConfirmed
import com.anabars.tripsplit.ui.model.AddTripEvent.NewParticipantMultiplicatorChanged
import com.anabars.tripsplit.ui.model.AddTripEvent.NewParticipantNameChanged
import com.anabars.tripsplit.ui.model.AddTripEvent.ParticipantDeleted
import com.anabars.tripsplit.ui.model.AddTripEvent.ParticipantEditRequested
import com.anabars.tripsplit.ui.model.AddTripEvent.SaveTripClicked
import com.anabars.tripsplit.ui.model.AddTripEvent.TripNameChanged
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
        viewModel.onEvent(AddTripEvent.OnBackPressed)
    }

    val defaultParticipantName = stringResource(R.string.you)
    val addScreenTitle = stringResource(R.string.title_new_trip)
    val editScreenTitle = stringResource(R.string.title_edit_trip)

    LaunchedEffect(Unit) {
        onTabTitleChange(if (viewModel.isEditModeFlow.first()) editScreenTitle else addScreenTitle)
        viewModel.onEvent(AddTripEvent.AddDefaultParticipant(defaultParticipantName))
        viewModel.shouldNavigateBack.collectLatest { navigateBack ->
            if (navigateBack) navController.popBackStack()
        }
        setBackHandler {
            viewModel.onEvent(AddTripEvent.OnBackPressed)
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
                onCurrencySelected = { viewModel.onEvent(CurrencyAdded(it)) },
                onDismissAddCurrencyDialog = { viewModel.onEvent(DismissCurrencyDialog) }
            )
        }

        ActiveDialog.USER_INPUT -> {
            TsUserInputDialog(
                tripParticipantsUiState = participantsUiState,
                onInputChange = { viewModel.onEvent(NewParticipantNameChanged(it)) },
                onMultiplicatorChange = { viewModel.onEvent(NewParticipantMultiplicatorChanged(it)) },
                onConfirm = { viewModel.onEvent(AddTripEvent.ParticipantInputSaved) },
                onDismiss = { viewModel.onEvent(DismissAddParticipantDialog) },
                titleRes = if (participantsUiState.isEditParticipant) R.string.edit_participant else R.string.add_participant,
                labelRes = R.string.participant_name_hint,
                positiveTextRes = if (participantsUiState.isEditParticipant) R.string.save else R.string.add,
                negativeTextRes = R.string.cancel
            )
        }

        ActiveDialog.CONFIRMATION -> {
            TsConfirmationDialog(
                onDismiss = { navController.popBackStack() },
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
                    tripNameUiState = nameUiState,
                    tripStatusUiState = tripStatusUiState,
                    tripParticipants = participantsUiState.tripParticipants,
                    tripCurrencies = currenciesUiState.tripCurrencyCodes,
                    onTripNameChanged = { viewModel.onEvent(TripNameChanged(it)) },
                    onTripStatusChanged = { viewModel.onEvent(AddTripEvent.TripStatusChanged(it)) },
                    onAddParticipantButtonClick = { viewModel.onEvent(AddParticipantClicked) },
                    onEditParticipantButtonClick = { viewModel.onEvent(ParticipantEditRequested(it)) },
                    onDeleteParticipant = { viewModel.onEvent(ParticipantDeleted(it)) },
                    onAddCurrencyButtonClick = { viewModel.onEvent(AddCurrencyClicked) },
                    onDeleteCurrency = { viewModel.onEvent(CurrencyDeleted(it)) },
                    onSaveTrip = { viewModel.onEvent(SaveTripClicked) }
                )
            else
                AddTripLandscapeContent(
                    tripNameUiState = nameUiState,
                    tripStatusUiState = tripStatusUiState,
                    tripParticipants = participantsUiState.tripParticipants,
                    tripCurrencies = currenciesUiState.tripCurrencyCodes,
                    onTripNameChanged = { viewModel.onEvent(TripNameChanged(it)) },
                    onTripStatusChanged = { viewModel.onEvent(AddTripEvent.TripStatusChanged(it)) },
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