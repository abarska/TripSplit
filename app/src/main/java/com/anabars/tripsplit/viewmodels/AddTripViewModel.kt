package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.preferences.CurrencyPreference
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.repository.TripRepository
import com.anabars.tripsplit.ui.dialogs.ActiveDialog
import com.anabars.tripsplit.ui.model.AddTripEvent
import com.anabars.tripsplit.ui.model.AddTripUiState
import com.anabars.tripsplit.utils.getCurrencyDisplayList
import com.anabars.tripsplit.utils.validCurrencyCodes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTripViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val currencyPreference: CurrencyPreference
) : ViewModel() {

    private val _localCurrency = MutableStateFlow("")
    val localCurrency: StateFlow<String> = _localCurrency.asStateFlow()

    private val _currencies = MutableStateFlow<List<String>>(emptyList())
    val currencies: StateFlow<List<String>> = _currencies.asStateFlow()

    private val _currentTripParticipants = MutableStateFlow<List<TripParticipant>>(emptyList())
    val currentTripParticipants: StateFlow<List<TripParticipant>> = _currentTripParticipants

    private val _currentTripCurrencies = MutableStateFlow<List<String>>(emptyList())
    val currentTripCurrencies: StateFlow<List<String>> = _currentTripCurrencies

    private val _uiState = MutableStateFlow(AddTripUiState())
    val uiState: StateFlow<AddTripUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _currencies.value = getCurrencyDisplayList(validCurrencyCodes())
            currencyPreference.getCurrencyFlow(TripSplitConstants.PREF_KEY_LOCAL_CURRENCY)
                .collect { currency ->
                    _localCurrency.value = currency
                }
        }
    }

    fun nameAlreadyInUse(participant: TripParticipant) =
        _currentTripParticipants.value.map { it.name }.contains(participant.name)

    fun addParticipant(participant: TripParticipant) =
        run { _currentTripParticipants.value += participant }

    fun removeParticipant(participant: TripParticipant) =
        run { _currentTripParticipants.value -= participant }

    fun updateParticipant(index: Int, participant: TripParticipant) = run {
        val updatedList = _currentTripParticipants.value.toMutableList().apply {
            this[index] = participant
        }
        _currentTripParticipants.value = updatedList
    }

    private fun clearParticipants() = run { _currentTripParticipants.value = emptyList() }

    fun hasCurrency(code: String) = _currentTripCurrencies.value.contains(code)
    fun addCurrency(code: String) = run { _currentTripCurrencies.value += code }
    fun removeCurrency(code: String) = run { _currentTripCurrencies.value -= code }
    private fun clearCurrencies() = run { _currentTripCurrencies.value = emptyList() }

    fun clearTempData() {
        clearParticipants()
        clearCurrencies()
    }

    fun fieldNotEmpty(value: String) = value.isNotEmpty()

    fun saveTrip(tripName: String) {
        viewModelScope.launch {
            val trip = Trip(title = tripName)
            tripRepository.saveTrip(
                trip,
                _currentTripParticipants.value,
                _currentTripCurrencies.value
            )
        }
    }

    fun updateTripName(input: String) {
        val newTripName = input.trimStart().replaceFirstChar { it.titlecase() }
        _uiState.value = _uiState.value.copy(tripName = newTripName)
    }

    fun updateTripNameErrorMessage(resId: Int) {
        _uiState.value = _uiState.value.copy(tripNameErrorMessage = resId)
    }

    fun updateTripNameError(isError: Boolean) {
        _uiState.value = _uiState.value.copy(tripNameError = isError)
    }

    fun updateNewParticipantName(input: String) {
        val newParticipantName = input.trimStart().replaceFirstChar { it.titlecase() }
        _uiState.value = _uiState.value.copy(newParticipantName = newParticipantName)
    }

    fun updateNewParticipantMultiplicator(multiplicator: Int) {
        _uiState.value = _uiState.value.copy(newParticipantMultiplicator = multiplicator)
    }

    fun updateParticipantIndex(index: Int) {
        _uiState.value = _uiState.value.copy(updatedParticipantIndex = index)
    }

    fun updateActiveDialog(dialog: ActiveDialog) {
        _uiState.value = _uiState.value.copy(activeDialog = dialog)
    }

    fun onEvent(event: AddTripEvent) {
        when (event) {
            is AddTripEvent.AddParticipantClicked -> TODO()
            is AddTripEvent.CurrencyAdded -> TODO()
            is AddTripEvent.CurrencyDeleted -> TODO()
            is AddTripEvent.NewParticipantMultiplicatorChanged -> TODO()
            is AddTripEvent.NewParticipantNameChanged -> TODO()
            is AddTripEvent.ParticipantDeleted -> TODO()
            is AddTripEvent.ParticipantEditRequested -> TODO()
            is AddTripEvent.TripNameChanged -> TODO()
            AddTripEvent.AddCurrencyClicked -> TODO()
            AddTripEvent.DismissAddParticipantDialog -> TODO()
            AddTripEvent.DismissCurrencyDialog -> TODO()
            AddTripEvent.ExistingParticipantEdited -> TODO()
            AddTripEvent.NewParticipantSaved -> TODO()
            AddTripEvent.SaveChangesDismissed -> TODO()
            AddTripEvent.SaveTripClicked -> TODO()
            AddTripEvent.WarningDialogConfirmed -> TODO()
        }
    }
}