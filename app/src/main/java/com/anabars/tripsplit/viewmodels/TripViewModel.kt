package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.model.Trip
import com.anabars.tripsplit.repository.TripRepository
import com.anabars.tripsplit.utils.getCurrencyDisplayList
import com.anabars.tripsplit.utils.validCurrencyCodes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(private val tripRepository: TripRepository) : ViewModel() {

    private val _tripList = MutableStateFlow<List<Trip>>(emptyList())
    val tripList = _tripList.asStateFlow()

    private val _currentTripParticipants = MutableStateFlow<List<String>>(emptyList())
    val currentTripParticipants: StateFlow<List<String>> = _currentTripParticipants

    private val _currentTripCurrencies = MutableStateFlow<List<String>>(emptyList())
    val currentTripCurrencies: StateFlow<List<String>> = _currentTripCurrencies

    private val _currencies = MutableStateFlow<List<String>>(emptyList())
    val currencies: StateFlow<List<String>> = _currencies.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            tripRepository.getAllTrips().distinctUntilChanged().collect { trips ->
                if (trips.isNotEmpty()) _tripList.value = trips
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            _currencies.value = getCurrencyDisplayList(validCurrencyCodes())
        }
    }

    //    fun getAllTrips() = tripDao.getAllTrips().flowOn(Dispatchers.IO).conflate()
    fun deleteAllTrips() = viewModelScope.launch { tripRepository.deleteAllTrips() }

    fun updateTrip(trip: Trip) = viewModelScope.launch { tripRepository.updateTrip(trip) }

    fun deleteTrip(trip: Trip) = viewModelScope.launch { tripRepository.deleteTrip(trip) }

    fun getTrip(id: String) = viewModelScope.launch { tripRepository.getTrip(id) }

    fun saveTrip(tripName: String, tripDescription: String) {
        viewModelScope.launch {
            val trip = Trip(title = tripName, description = tripDescription)
            tripRepository.saveTrip(trip, _currentTripParticipants.value, _currentTripCurrencies.value)
        }
    }

    fun getParticipantsByTripId(id: Long) =
        viewModelScope.launch { tripRepository.getParticipantsByTripId(id) }

    fun deleteParticipantsByTripId(id: Long) =
        viewModelScope.launch { tripRepository.deleteParticipantsByTripId(id) }

    fun hasParticipant(name: String) = _currentTripParticipants.value.contains(name)
    fun addParticipant(name: String) = run { _currentTripParticipants.value += name }
    fun removeParticipant(name: String) = run { _currentTripParticipants.value -= name }
    fun clearParticipants() = run { _currentTripParticipants.value = emptyList() }

    fun hasCurrency(code: String) = _currentTripCurrencies.value.contains(code)
    fun addCurrency(code: String) = run { _currentTripCurrencies.value += code }
    fun removeCurrency(code: String) = run { _currentTripCurrencies.value -= code }
    fun clearCurrencies() = run { _currentTripCurrencies.value = emptyList() }

    fun fieldNotEmpty(value: String) = value.isNotEmpty()

    private var backHandler: (() -> Boolean)? = null

    fun setBackHandler(handler: (() -> Boolean)?) {
        backHandler = handler
    }

    fun handleBack(): Boolean {
        return backHandler?.invoke() ?: false
    }
}