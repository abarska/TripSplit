package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.R
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.preferences.CurrencyPreference
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.repository.TripRepository
import com.anabars.tripsplit.ui.dialogs.ActiveDialog
import com.anabars.tripsplit.ui.model.AddTripEvent
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
import com.anabars.tripsplit.ui.model.AddTripUiState
import com.anabars.tripsplit.utils.getCurrencyDisplayList
import com.anabars.tripsplit.utils.validCurrencyCodes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val _uiState = MutableStateFlow(AddTripUiState())
    val uiState: StateFlow<AddTripUiState> = _uiState.asStateFlow()

    private val _shouldNavigateHome = MutableStateFlow(false)
    val shouldNavigateHome: StateFlow<Boolean> = _shouldNavigateHome.asStateFlow()

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
        _uiState.value.tripParticipants.any { it.name == participant.name }

    fun addParticipant(participant: TripParticipant) =
        _uiState.update { it.copy(tripParticipants = it.tripParticipants + participant) }

    fun removeParticipant(participant: TripParticipant) =
        _uiState.update { it.copy(tripParticipants = it.tripParticipants - participant) }

    fun updateParticipant(index: Int, participant: TripParticipant) = {
        _uiState.update {
            val updatedList = it.tripParticipants.toMutableList()
            if (index in updatedList.indices) updatedList[index] = participant
            it.copy(tripParticipants = updatedList)
        }
    }

    private fun clearParticipants() = _uiState.update { it.copy(tripParticipants = emptyList()) }

    fun hasCurrency(code: String) = _uiState.value.tripCurrencies.contains(code)

    fun addCurrency(code: String) = _uiState.update {
        it.copy(tripCurrencies = it.tripCurrencies + code)
    }

    fun removeCurrency(code: String) = _uiState.update {
        it.copy(tripCurrencies = it.tripCurrencies - code)
    }

    private fun clearCurrencies() = _uiState.update {
        it.copy(tripCurrencies = emptyList())
    }

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
                _uiState.value.tripParticipants,
                _uiState.value.tripCurrencies
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
            is TripNameChanged -> {
                updateTripName(event.name)
                updateTripNameErrorMessage(0)
                updateTripNameError(false)
            }

            is NewParticipantNameChanged -> {
                updateNewParticipantName(event.name)
            }

            is NewParticipantMultiplicatorChanged -> {
                updateNewParticipantMultiplicator(event.multiplicator)
            }

            is ParticipantEditRequested -> {
                updateNewParticipantName(event.participant.name)
                updateNewParticipantMultiplicator(event.participant.multiplicator)
                updateParticipantIndex(_uiState.value.tripParticipants.indexOf(event.participant))
                updateActiveDialog(ActiveDialog.USER_INPUT)
            }

            is ParticipantDeleted -> {
                removeParticipant(event.participant)
            }

            is CurrencyAdded -> {
                val code = event.currency.take(3)
                if (!hasCurrency(code)) {
                    addCurrency(code)
                }
                updateActiveDialog(ActiveDialog.NONE)
            }

            is CurrencyDeleted -> {
                removeCurrency(event.code)
            }

            is AddCurrencyClicked -> {
                updateActiveDialog(ActiveDialog.CHOOSER)
            }

            is DismissCurrencyDialog -> {
                updateActiveDialog(ActiveDialog.NONE)
            }

            is DuplicateNameDialogConfirmed -> {
                resetParticipant()
                updateActiveDialog(ActiveDialog.USER_INPUT)
            }

            is AddParticipantClicked -> {
                updateActiveDialog(ActiveDialog.USER_INPUT)
            }

            is DismissAddParticipantDialog -> {
                updateActiveDialog(ActiveDialog.NONE)
                resetParticipant()
            }

            is SaveTripClicked -> {
                val tripNameTrimmed = _uiState.value.tripName.trim()
                if (fieldNotEmpty(value = tripNameTrimmed)) {
                    saveTrip(tripName = tripNameTrimmed)
                    _shouldNavigateHome.value = true
                } else {
                    updateActiveDialog(ActiveDialog.NONE)
                    updateTripNameError(true)
                    updateTripNameErrorMessage(R.string.error_mandatory_field)
                }
            }

            is NewParticipantSaveClicked -> {
                val nameTrimmed = _uiState.value.newParticipantName.trim()
                if (fieldNotEmpty(value = nameTrimmed)) {
                    val newParticipant =
                        TripParticipant(
                            name = nameTrimmed,
                            multiplicator = _uiState.value.newParticipantMultiplicator
                        )
                    if (nameAlreadyInUse(newParticipant)) {
                        updateActiveDialog(ActiveDialog.WARNING)
                    } else {
                        addParticipant(newParticipant)
                        updateActiveDialog(ActiveDialog.NONE)
                        resetParticipant()
                    }
                }
            }

            is ExistingParticipantEdited -> {
                val nameTrimmed = _uiState.value.newParticipantName.trim()
                if (fieldNotEmpty(nameTrimmed) && _uiState.value.updatedParticipantIndex >= 0) {
                    val updatedParticipant =
                        TripParticipant(
                            name = nameTrimmed,
                            multiplicator = _uiState.value.newParticipantMultiplicator
                        )
                    updateParticipant(
                        _uiState.value.updatedParticipantIndex,
                        updatedParticipant
                    )
                    updateActiveDialog(ActiveDialog.NONE)
                    resetParticipant()
                }
            }
        }
    }

    fun resetParticipant() {
        updateNewParticipantName("")
        updateNewParticipantMultiplicator(1)
        updateParticipantIndex(-1)
    }

    fun hasUnsavedInput() = _uiState.value.tripName.isNotBlank()
            || _uiState.value.tripParticipants.size > 1
            || _uiState.value.tripCurrencies.size > 1
}