package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.R
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.preferences.CurrencyPreference
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.model.TripDetails
import com.anabars.tripsplit.repository.TripRepository
import com.anabars.tripsplit.ui.dialogs.ActiveDialog
import com.anabars.tripsplit.ui.model.AddTripUiState
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent.*
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

    private val _uiState = MutableStateFlow(AddTripUiState())
    val uiState: StateFlow<AddTripUiState> = _uiState.asStateFlow()

    private val _navigateBackWithWarning = MutableSharedFlow<Boolean>()
    val navigateBackWithWarning = _navigateBackWithWarning.asSharedFlow()

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
        _uiState.update { it.copy(availableCurrencies = currencyCodes) }
    }

    private suspend fun addHomeCurrency() {
        val localCurrencyCode = currencyPreference
            .getCurrencyFlow(TripSplitConstants.PREF_KEY_LOCAL_CURRENCY)
            .first()
        if (localCurrencyCode.isNotBlank() && !hasCurrency(localCurrencyCode)) {
            _uiState.update {
                it.copy(tripCurrencyCodes = it.tripCurrencyCodes + localCurrencyCode)
            }
        }
    }

    private fun populateState() {
        tripDetails?.let { data ->
            _uiState.update { state ->
                state.copy(
                    tripName = data.trip.title,
                    tripStatus = data.trip.status,
                    tripParticipants = data.activeParticipants,
                    tripCurrencyCodes = data.activeCurrencies.map { it.code }
                )
            }
        }
    }

    private fun nameAlreadyInUse(participant: TripParticipant) =
        _uiState.value.tripParticipants.any { it.name == participant.name }

    private fun hasCurrency(code: String) =
        _uiState.value.tripCurrencyCodes.contains(code)

    private fun saveTrip(tripName: String) {
        viewModelScope.launch {
            val trip = Trip(title = tripName, status = _uiState.value.tripStatus)
            tripRepository.saveTrip(
                tripId,
                trip,
                _uiState.value.tripParticipants,
                _uiState.value.tripCurrencyCodes
            )
        }
    }

    fun onIntent(intent: AddTripIntent) {
        when (intent) {
            is TripNameChanged -> {
                val newTripName = intent.name.capitalizeFirstChar()
                _uiState.update {
                    it.copy(
                        tripName = newTripName,
                        tripNameErrorMessage = 0,
                        tripNameError = false
                    )
                }
            }

            is TripStatusChanged -> {
                _uiState.update { it.copy(tripStatus = intent.status) }
            }

            is NewParticipantNameChanged -> {
                _uiState.update { it.copy(newParticipantName = intent.name.capitalizeFirstChar()) }
            }

            is NewParticipantMultiplicatorChanged -> {
                _uiState.update { it.copy(newParticipantMultiplicator = intent.multiplicator) }
            }

            is ParticipantEditRequested -> {
                _uiState.update {
                    it.copy(
                        newParticipantName = intent.participant.name.capitalizeFirstChar(),
                        newParticipantMultiplicator = intent.participant.multiplicator,
                        updatedParticipantIndex = _uiState.value.tripParticipants.indexOf(intent.participant),
                        activeDialog = ActiveDialog.USER_INPUT
                    )
                }
            }

            is ParticipantDeleted -> {
                _uiState.update { it.copy(tripParticipants = it.tripParticipants - intent.participant) }
            }

            is CurrencyAdded -> {
                val code = intent.currency.take(3)
                _uiState.update { state ->
                    val updatedCodes =
                        if (code.isNotBlank() && !hasCurrency(code)) state.tripCurrencyCodes + code
                        else state.tripCurrencyCodes
                    state.copy(
                        tripCurrencyCodes = updatedCodes,
                        activeDialog = ActiveDialog.NONE
                    )
                }
            }

            is CurrencyDeleted -> {
                _uiState.update { it.copy(tripCurrencyCodes = it.tripCurrencyCodes - intent.code) }
            }

            is AddCurrencyClicked -> {
                _uiState.update { it.copy(activeDialog = ActiveDialog.CHOOSER) }
            }

            is DismissCurrencyDialog -> {
                _uiState.update { it.copy(activeDialog = ActiveDialog.NONE) }
            }

            is DuplicateNameDialogConfirmed -> {
                _uiState.update { it.resetParticipantFields(ActiveDialog.USER_INPUT) }
            }

            is AddParticipantClicked -> {
                _uiState.update { it.copy(activeDialog = ActiveDialog.USER_INPUT) }
            }

            is DismissAddParticipantDialog -> {
                _uiState.update { it.resetParticipantFields(ActiveDialog.NONE) }
            }

            is SaveTripClicked -> {
                val tripNameTrimmed = _uiState.value.tripName.trim()
                if (tripNameTrimmed.isNotEmpty()) {
                    saveTrip(tripName = tripNameTrimmed)
                    viewModelScope.launch {
                        _navigateBackWithWarning.emit(false)
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            activeDialog = ActiveDialog.NONE,
                            tripNameError = true,
                            tripNameErrorMessage = R.string.error_mandatory_field
                        )
                    }
                }
            }

            is ParticipantInputSaved -> {
                val nameTrimmed = _uiState.value.newParticipantName.trim()
                if (nameTrimmed.isEmpty()) return
                if (nameTrimmed != _uiState.value.newParticipantName)
                    _uiState.update { it.copy(newParticipantName = nameTrimmed) }

                val participant =
                    TripParticipant.fromUiState(
                        uiState = _uiState.value,
                        tripId = tripId ?: 0L
                    )

                if (_uiState.value.isEditParticipant) {
                    val index = _uiState.value.updatedParticipantIndex
                    _uiState.update {
                        val updatedList = it.tripParticipants.toMutableList()
                        if (index in updatedList.indices) updatedList[index] = participant
                        it.resetParticipantFields(ActiveDialog.NONE)
                            .copy(tripParticipants = updatedList)
                    }
                } else {
                    if (nameAlreadyInUse(participant)) {
                        _uiState.update { it.resetParticipantFields(ActiveDialog.WARNING) }
                    } else {
                        _uiState.update {
                            it.resetParticipantFields(ActiveDialog.NONE)
                                .copy(tripParticipants = it.tripParticipants + participant)
                        }
                    }
                }
            }

            is AddDefaultParticipant -> {
                val participant = TripParticipant(name = intent.name, multiplicator = 1)
                if (nameAlreadyInUse(participant)) {
                    _uiState.update { it.resetParticipantFields(ActiveDialog.NONE) }
                } else {
                    _uiState.update {
                        it.resetParticipantFields(ActiveDialog.NONE)
                            .copy(tripParticipants = it.tripParticipants + participant)
                    }
                }
            }

            is BackPressed -> {
                viewModelScope.launch {
                    if (newTripHasUnsavedInput() || existingTripDataChanged()) {
                        _navigateBackWithWarning.emit(true)
                    } else {
                        _navigateBackWithWarning.emit(false)
                    }
                }
            }
        }
    }

    private fun existingTripDataChanged(): Boolean {
        return tripId != null &&
                (_uiState.value.tripName != tripDetails?.trip?.title ||
                        _uiState.value.tripStatus != tripDetails?.trip?.status ||
                        _uiState.value.tripParticipants != tripDetails?.activeParticipants ||
                        _uiState.value.tripCurrencyCodes != tripDetails?.activeCurrencies?.map { it.code })
    }

    private fun newTripHasUnsavedInput(): Boolean {
        return tripId == null &&
                (_uiState.value.tripName.isNotBlank() ||
                        _uiState.value.tripParticipants.size > 1 ||
                        _uiState.value.tripCurrencyCodes.size > 1)
    }

    private fun AddTripUiState.resetParticipantFields(activeDialog: ActiveDialog = ActiveDialog.NONE) =
        copy(
            newParticipantName = "",
            newParticipantMultiplicator = 1,
            updatedParticipantIndex = -1,
            activeDialog = activeDialog
        )

    private fun String.capitalizeFirstChar() = trimStart().replaceFirstChar { it.titlecase() }
}