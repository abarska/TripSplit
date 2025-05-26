package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.model.Trip
import com.anabars.tripsplit.repository.TripRepository
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

    private val _participants = MutableStateFlow<Set<String>>(emptySet())
    val participants: StateFlow<Set<String>> = _participants

    init {
        viewModelScope.launch(Dispatchers.IO) {
            tripRepository.getAllTrips().distinctUntilChanged().collect { trips ->
                if (trips.isNotEmpty()) _tripList.value = trips
            }
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
            tripRepository.saveTrip(trip, _participants.value.toList())
        }
    }

    fun getParticipantsByTripId(id: Long) =
        viewModelScope.launch { tripRepository.getParticipantsByTripId(id) }

    fun deleteParticipantsByTripId(id: Long) =
        viewModelScope.launch { tripRepository.deleteParticipantsByTripId(id) }

    fun addParticipant(name: String) = run { _participants.value += name }
    fun removeParticipant(name: String) = run { _participants.value -= name }
    fun clearParticipants() = run { _participants.value = emptySet() }

    fun fieldNotEmpty(value: String) = value.isNotEmpty()

    private var backHandler: (() -> Boolean)? = null

    fun setBackHandler(handler: (() -> Boolean)?) {
        backHandler = handler
    }

    fun handleBack(): Boolean {
        return backHandler?.invoke() ?: false
    }
}