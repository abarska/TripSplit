package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.R
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.preferences.CurrencyPreference
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripStatus
import com.anabars.tripsplit.data.room.model.TripDetails
import com.anabars.tripsplit.repository.TripRepository
import com.anabars.tripsplit.ui.dialogs.ActiveDialog
import com.anabars.tripsplit.ui.model.AddTripCurrenciesUiState
import com.anabars.tripsplit.ui.model.AddTripDialogState
import com.anabars.tripsplit.ui.model.AddTripNameUiState
import com.anabars.tripsplit.ui.model.AddTripParticipantsUiState
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.AddCurrencyClicked
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.AddDefaultParticipant
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.AddParticipantClicked
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.CurrencyAdded
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.CurrencyDeleted
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.DismissAddParticipantDialog
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.DismissCurrencyDialog
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.DuplicateNameDialogConfirmed
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.NewParticipantMultiplicatorChanged
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.NewParticipantNameChanged
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.ParticipantDeleted
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.ParticipantEditRequested
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.ParticipantInputSaved
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.SaveTripClicked
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.TripNameChanged
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.TripStatusChanged
import com.anabars.tripsplit.utils.getCurrencyDisplayList
import com.anabars.tripsplit.utils.validCurrencyCodesCached
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddTripViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository,
    private val currencyPreference: CurrencyPreference
) : ViewModel() {

    private val tripId: Long? = savedStateHandle.get<String>("tripId")?.toLongOrNull()
    private var tripDetails: TripDetails? = null

    private val _dialogUiState = MutableStateFlow(AddTripDialogState())
    val dialogUiState: StateFlow<AddTripDialogState> = _dialogUiState.asStateFlow()

    private val _nameUiState = MutableStateFlow(AddTripNameUiState())
    val nameUiState: StateFlow<AddTripNameUiState> = _nameUiState.asStateFlow()

    private val _statusUiState = MutableStateFlow(TripStatus.PLANNED)
    val statusUiState: StateFlow<TripStatus> = _statusUiState.asStateFlow()

    private val _participantsUiState = MutableStateFlow(AddTripParticipantsUiState())
    val participantsUiState: StateFlow<AddTripParticipantsUiState> =
        _participantsUiState.asStateFlow()

    private val _currenciesUiState = MutableStateFlow(AddTripCurrenciesUiState())
    val currenciesUiState: StateFlow<AddTripCurrenciesUiState> = _currenciesUiState.asStateFlow()

    private val _shouldNavigateBack = MutableSharedFlow<Boolean>()
    val shouldNavigateBack = _shouldNavigateBack.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadTripDetailsIfEditing()
            loadAvailableCurrencies()
            addHomeCurrency()
        }
    }

    private fun loadTripDetailsIfEditing() {
        tripId?.let {
            tripDetails = tripRepository.getTripDetailsData(it)
            populateState()
        }
    }

    private suspend fun loadAvailableCurrencies() {
        val currencyCodes = withContext(Dispatchers.Default) {
            getCurrencyDisplayList(validCurrencyCodesCached)
        }
        setAvailableCurrencies(currencyCodes)
    }

    private suspend fun addHomeCurrency() {
        val localCurrencyCode = currencyPreference
            .getCurrencyFlow(TripSplitConstants.PREF_KEY_LOCAL_CURRENCY)
            .first()
        addCurrency(localCurrencyCode)
    }

    private fun populateState() {
        tripDetails?.let { data ->
            _nameUiState.value = _nameUiState.value.copy(data.trip.title)
            _statusUiState.value = data.trip.status
            _participantsUiState.value = _participantsUiState.value.copy(data.activeParticipants)
            _currenciesUiState.value =
                _currenciesUiState.value.copy(tripCurrencyCodes = data.activeCurrencies.map { it.code })
        }
    }

    private fun nameAlreadyInUse(participant: TripParticipant) =
        _participantsUiState.value.tripParticipants.any { it.name == participant.name }

    private fun addParticipant(participant: TripParticipant, isDefault: Boolean) {
        if (nameAlreadyInUse(participant)) {
            resetParticipant(if (isDefault) ActiveDialog.NONE else ActiveDialog.WARNING)
        } else {
            _participantsUiState.update { it.copy(tripParticipants = it.tripParticipants + participant) }
            resetParticipant(ActiveDialog.NONE)
        }
    }

    private fun removeParticipant(participant: TripParticipant) =
        _participantsUiState.update { it.copy(tripParticipants = it.tripParticipants - participant) }

    private fun updateParticipant(index: Int, participant: TripParticipant) {
        _participantsUiState.update {
            val updatedList = it.tripParticipants.toMutableList()
            if (index in updatedList.indices) updatedList[index] = participant
            it.copy(tripParticipants = updatedList)
        }
        resetParticipant(ActiveDialog.NONE)
    }

    private fun clearParticipants() =
        _participantsUiState.update { it.copy(tripParticipants = emptyList()) }

    private fun hasCurrency(code: String) =
        _currenciesUiState.value.tripCurrencyCodes.contains(code)

    private fun addCurrency(code: String) {
        if (code.isNotBlank() && !hasCurrency(code)) {
            _currenciesUiState.update {
                it.copy(tripCurrencyCodes = it.tripCurrencyCodes + code)
            }
        }
    }

    private fun removeCurrency(code: String) = _currenciesUiState.update {
        it.copy(tripCurrencyCodes = it.tripCurrencyCodes - code)
    }

    private fun clearCurrencies() = _currenciesUiState.update {
        it.copy(tripCurrencyCodes = emptyList())
    }

    private fun saveTrip(tripName: String) {
        viewModelScope.launch {
            val trip = Trip(title = tripName, status = _statusUiState.value)
            tripRepository.saveTrip(
                tripId,
                trip,
                _participantsUiState.value.tripParticipants,
                _currenciesUiState.value.tripCurrencyCodes
            )
            clearParticipants()
            clearCurrencies()
        }
    }

    private fun updateTripStatus(status: TripStatus) {
        _statusUiState.value = status
    }

    private fun updateTripName(input: String) {
        val newTripName = input.trimStart().replaceFirstChar { it.titlecase() }
        _nameUiState.value = _nameUiState.value.copy(tripName = newTripName)
    }

    private fun updateTripNameErrorMessage(resId: Int) {
        _nameUiState.value = _nameUiState.value.copy(tripNameErrorMessage = resId)
    }

    private fun updateTripNameError(isError: Boolean) {
        _nameUiState.value = _nameUiState.value.copy(tripNameError = isError)
    }

    private fun updateNewParticipantName(input: String) {
        val newParticipantName = input.trimStart().replaceFirstChar { it.titlecase() }
        _participantsUiState.value =
            _participantsUiState.value.copy(newParticipantName = newParticipantName)
    }

    private fun updateNewParticipantMultiplicator(multiplicator: Int) {
        _participantsUiState.value =
            _participantsUiState.value.copy(newParticipantMultiplicator = multiplicator)
    }

    private fun updateParticipantIndex(index: Int) {
        _participantsUiState.value =
            _participantsUiState.value.copy(updatedParticipantIndex = index)
    }

    private fun updateActiveDialog(dialog: ActiveDialog) {
        _dialogUiState.value = _dialogUiState.value.copy(activeDialog = dialog)
    }

    fun onIntent(intent: AddTripIntent) {
        when (intent) {
            is TripNameChanged -> {
                updateTripName(intent.name)
                updateTripNameErrorMessage(0)
                updateTripNameError(false)
            }

            is TripStatusChanged -> {
                updateTripStatus(intent.status)
            }

            is NewParticipantNameChanged -> {
                updateNewParticipantName(intent.name)
            }

            is NewParticipantMultiplicatorChanged -> {
                updateNewParticipantMultiplicator(intent.multiplicator)
            }

            is ParticipantEditRequested -> {
                updateNewParticipantName(intent.participant.name)
                updateNewParticipantMultiplicator(intent.participant.multiplicator)
                updateParticipantIndex(_participantsUiState.value.tripParticipants.indexOf(intent.participant))
                updateActiveDialog(ActiveDialog.USER_INPUT)
            }

            is ParticipantDeleted -> {
                removeParticipant(intent.participant)
            }

            is CurrencyAdded -> {
                addCurrency(intent.currency.take(3))
                updateActiveDialog(ActiveDialog.NONE)
            }

            is CurrencyDeleted -> {
                removeCurrency(intent.code)
            }

            is AddCurrencyClicked -> {
                updateActiveDialog(ActiveDialog.CHOOSER)
            }

            is DismissCurrencyDialog -> {
                updateActiveDialog(ActiveDialog.NONE)
            }

            is DuplicateNameDialogConfirmed -> {
                resetParticipant(ActiveDialog.USER_INPUT)
            }

            is AddParticipantClicked -> {
                updateActiveDialog(ActiveDialog.USER_INPUT)
            }

            is DismissAddParticipantDialog -> {
                resetParticipant(ActiveDialog.NONE)
            }

            is SaveTripClicked -> {
                val tripNameTrimmed = _nameUiState.value.tripName.trim()
                if (tripNameTrimmed.isNotEmpty()) {
                    saveTrip(tripName = tripNameTrimmed)
                    viewModelScope.launch {
                        _shouldNavigateBack.emit(true)
                    }
                } else {
                    updateActiveDialog(ActiveDialog.NONE)
                    updateTripNameError(true)
                    updateTripNameErrorMessage(R.string.error_mandatory_field)
                }
            }

            is ParticipantInputSaved -> {
                saveOrUpdateParticipant()
            }

            is AddDefaultParticipant -> {
                addParticipant(
                    participant = TripParticipant(name = intent.name, multiplicator = 1),
                    isDefault = true
                )
            }
        }
    }

    private fun saveOrUpdateParticipant() {
        val nameTrimmed = _participantsUiState.value.newParticipantName.trim()
        if (nameTrimmed.isEmpty()) return
        if (nameTrimmed != _participantsUiState.value.newParticipantName)
            _participantsUiState.value =
                _participantsUiState.value.copy(newParticipantName = nameTrimmed)

        val participant =
            TripParticipant.fromUiState(
                uiState = _participantsUiState.value,
                tripId = tripId ?: 0L
            )

        if (_participantsUiState.value.isEditParticipant) {
            updateParticipant(_participantsUiState.value.updatedParticipantIndex, participant)
        } else {
            addParticipant(participant = participant, isDefault = false)
        }
    }

    private fun resetParticipant(dialog: ActiveDialog) {
        updateNewParticipantName("")
        updateNewParticipantMultiplicator(1)
        updateParticipantIndex(-1)
        updateActiveDialog(dialog)
    }

    fun existingTripDataChanged(): Boolean {
        return tripId != null &&
                (_nameUiState.value.tripName != tripDetails?.trip?.title ||
                        _statusUiState.value != tripDetails?.trip?.status ||
                        _participantsUiState.value.tripParticipants != tripDetails?.activeParticipants ||
                        _currenciesUiState.value.tripCurrencyCodes != tripDetails?.activeCurrencies?.map { it.code })
    }

    fun newTripHasUnsavedInput(): Boolean {
        return tripId == null &&
                (_nameUiState.value.tripName.isNotBlank() ||
                        _participantsUiState.value.tripParticipants.size > 1 ||
                        _currenciesUiState.value.tripCurrencyCodes.size > 1)
    }

    private fun setAvailableCurrencies(currencies: List<String>) {
        _currenciesUiState.update { it.copy(availableCurrencies = currencies) }
    }
}