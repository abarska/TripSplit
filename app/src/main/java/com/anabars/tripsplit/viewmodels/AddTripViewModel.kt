package com.anabars.tripsplit.viewmodels

import android.util.Log
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
import com.anabars.tripsplit.ui.model.AddTripEvent
import com.anabars.tripsplit.ui.model.AddTripEvent.AddCurrencyClicked
import com.anabars.tripsplit.ui.model.AddTripEvent.AddDefaultParticipant
import com.anabars.tripsplit.ui.model.AddTripEvent.AddParticipantClicked
import com.anabars.tripsplit.ui.model.AddTripEvent.CurrencyAdded
import com.anabars.tripsplit.ui.model.AddTripEvent.CurrencyDeleted
import com.anabars.tripsplit.ui.model.AddTripEvent.DismissAddParticipantDialog
import com.anabars.tripsplit.ui.model.AddTripEvent.DismissCurrencyDialog
import com.anabars.tripsplit.ui.model.AddTripEvent.DuplicateNameDialogConfirmed
import com.anabars.tripsplit.ui.model.AddTripEvent.NewParticipantMultiplicatorChanged
import com.anabars.tripsplit.ui.model.AddTripEvent.NewParticipantNameChanged
import com.anabars.tripsplit.ui.model.AddTripEvent.OnBackPressed
import com.anabars.tripsplit.ui.model.AddTripEvent.ParticipantDeleted
import com.anabars.tripsplit.ui.model.AddTripEvent.ParticipantEditRequested
import com.anabars.tripsplit.ui.model.AddTripEvent.ParticipantInputSaved
import com.anabars.tripsplit.ui.model.AddTripEvent.SaveTripClicked
import com.anabars.tripsplit.ui.model.AddTripEvent.TripNameChanged
import com.anabars.tripsplit.ui.model.AddTripEvent.TripStatusChanged
import com.anabars.tripsplit.ui.model.AddTripNameUiState
import com.anabars.tripsplit.ui.model.AddTripParticipantsUiState
import com.anabars.tripsplit.utils.getCurrencyDisplayList
import com.anabars.tripsplit.utils.validCurrencyCodes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTripViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository,
    private val currencyPreference: CurrencyPreference
) : ViewModel() {

    private val tripId: Long? = savedStateHandle.get<String>("tripId")?.toLongOrNull()
    val screenTitle = if (tripId == null) R.string.title_new_trip else R.string.title_edit_trip

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

    private val _shouldNavigateHome = MutableStateFlow(false)
    val shouldNavigateHome: StateFlow<Boolean> = _shouldNavigateHome.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            tripId?.let {
                populateState(tripRepository.getTripDetailsData(it))
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val currencies = getCurrencyDisplayList(validCurrencyCodes())
            setAvailableCurrencies(currencies)

            val localCurrencyCode = currencyPreference
                .getCurrencyFlow(TripSplitConstants.PREF_KEY_LOCAL_CURRENCY)
                .first()
            addCurrency(localCurrencyCode)
        }
    }

    private fun populateState(tripDetailsData: TripDetails?) {
        tripDetailsData?.let { data ->
            _nameUiState.value = _nameUiState.value.copy(data.trip.title)
            _statusUiState.value = data.trip.status
            _participantsUiState.value = _participantsUiState.value.copy(data.participants)
            _currenciesUiState.value =
                _currenciesUiState.value.copy(tripCurrencies = data.currencies.map { it.code })
        }
    }

    fun nameAlreadyInUse(participant: TripParticipant) =
        _participantsUiState.value.tripParticipants.any { it.name == participant.name }

    fun addParticipant(participant: TripParticipant) =
        _participantsUiState.update { it.copy(tripParticipants = it.tripParticipants + participant) }

    private fun removeParticipant(participant: TripParticipant) =
        _participantsUiState.update { it.copy(tripParticipants = it.tripParticipants - participant) }

    private fun updateParticipant(index: Int, participant: TripParticipant) {
        _participantsUiState.update {
            val updatedList = it.tripParticipants.toMutableList()
            if (index in updatedList.indices) updatedList[index] = participant
            it.copy(tripParticipants = updatedList)
        }
    }

    fun isEditingParticipant() = _participantsUiState.value.updatedParticipantIndex >= 0

    private fun clearParticipants() =
        _participantsUiState.update { it.copy(tripParticipants = emptyList()) }

    private fun hasCurrency(code: String) = _currenciesUiState.value.tripCurrencies.contains(code)

    private fun addCurrency(code: String) {
        if (code.isNotBlank() && !hasCurrency(code)) {
            _currenciesUiState.update {
                it.copy(tripCurrencies = it.tripCurrencies + code)
            }
        }
    }

    private fun removeCurrency(code: String) = _currenciesUiState.update {
        it.copy(tripCurrencies = it.tripCurrencies - code)
    }

    private fun clearCurrencies() = _currenciesUiState.update {
        it.copy(tripCurrencies = emptyList())
    }

    private fun saveTrip(tripName: String) {
        viewModelScope.launch {
            val trip = Trip(title = tripName, status = _statusUiState.value)
            tripRepository.saveTrip(
                tripId,
                trip,
                _participantsUiState.value.tripParticipants,
                _currenciesUiState.value.tripCurrencies
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

    fun onEvent(event: AddTripEvent) {
        when (event) {
            is TripNameChanged -> {
                updateTripName(event.name)
                updateTripNameErrorMessage(0)
                updateTripNameError(false)
            }

            is TripStatusChanged -> {
                updateTripStatus(event.status)
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
                updateParticipantIndex(_participantsUiState.value.tripParticipants.indexOf(event.participant))
                updateActiveDialog(ActiveDialog.USER_INPUT)
            }

            is ParticipantDeleted -> {
                removeParticipant(event.participant)
            }

            is CurrencyAdded -> {
                addCurrency(event.currency.take(3))
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
                val tripNameTrimmed = _nameUiState.value.tripName.trim()
                if (tripNameTrimmed.isNotEmpty()) {
                    saveTrip(tripName = tripNameTrimmed)
                    _shouldNavigateHome.value = true
                } else {
                    updateActiveDialog(ActiveDialog.NONE)
                    updateTripNameError(true)
                    updateTripNameErrorMessage(R.string.error_mandatory_field)
                }
            }

            is OnBackPressed -> {
                if (hasUnsavedInput())
                    updateActiveDialog(ActiveDialog.CONFIRMATION)
                else
                    _shouldNavigateHome.value = true
            }

            is ParticipantInputSaved -> {
                saveOrUpdateParticipant()
            }

            is AddDefaultParticipant -> {
                val defaultParticipant = TripParticipant(name = event.name, multiplicator = 1)
                if (!nameAlreadyInUse(defaultParticipant)) addParticipant(defaultParticipant)
            }
        }
    }

    private fun saveOrUpdateParticipant() {
        val nameTrimmed = _participantsUiState.value.newParticipantName.trim()
        if (nameTrimmed.isEmpty()) return

        val currentParticipant =
            TripParticipant.fromUiState(
                uiState = _participantsUiState.value,
                tripId = tripId ?: 0L
            )

        if (isEditingParticipant()) {
            Log.d("marysya", "edit")
            if (_participantsUiState.value.updatedParticipantIndex >= 0) {
                updateParticipant(
                    _participantsUiState.value.updatedParticipantIndex,
                    currentParticipant
                )
                updateActiveDialog(ActiveDialog.NONE)
                resetParticipant()
            }
        } else {
            Log.d("marysya", "add")
            if (nameAlreadyInUse(currentParticipant)) {
                updateActiveDialog(ActiveDialog.WARNING)
            } else {
                addParticipant(currentParticipant)
                updateActiveDialog(ActiveDialog.NONE)
                resetParticipant()
            }
        }
    }

    private fun resetParticipant() {
        updateNewParticipantName("")
        updateNewParticipantMultiplicator(1)
        updateParticipantIndex(-1)
    }

    private fun hasUnsavedInput() = _nameUiState.value.tripName.isNotBlank()
            || _participantsUiState.value.tripParticipants.size > 1
            || _currenciesUiState.value.tripCurrencies.size > 1

    private fun setAvailableCurrencies(currencies: List<String>) {
        _currenciesUiState.update { it.copy(availableCurrencies = currencies) }
    }
}