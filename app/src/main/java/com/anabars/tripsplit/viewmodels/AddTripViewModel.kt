package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.preferences.CurrencyPreference
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.repository.TripRepository
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

    private val _currentTripParticipants = MutableStateFlow<List<String>>(emptyList())
    val currentTripParticipants: StateFlow<List<String>> = _currentTripParticipants

    private val _currentTripCurrencies = MutableStateFlow<List<String>>(emptyList())
    val currentTripCurrencies: StateFlow<List<String>> = _currentTripCurrencies

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _currencies.value = getCurrencyDisplayList(validCurrencyCodes())
            currencyPreference.getCurrencyFlow(TripSplitConstants.PREF_KEY_LOCAL_CURRENCY)
                .collect { currency ->
                    _localCurrency.value = currency
                }
        }
    }

    fun hasParticipant(name: String) = _currentTripParticipants.value.contains(name)
    fun addParticipant(name: String) = run { _currentTripParticipants.value += name }
    fun removeParticipant(name: String) = run { _currentTripParticipants.value -= name }
    private fun clearParticipants() = run { _currentTripParticipants.value = emptyList() }

    fun hasCurrency(code: String) = _currentTripCurrencies.value.contains(code)
    fun addCurrency(code: String) = run { _currentTripCurrencies.value += code }
    fun removeCurrency(code: String) = run { _currentTripCurrencies.value -= code }
    private fun clearCurrencies() = run { _currentTripCurrencies.value = emptyList() }

    fun clearTempData(){
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
}