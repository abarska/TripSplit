package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.model.Trip
import com.anabars.tripsplit.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(private val tripRepository: TripRepository) : ViewModel() {
    private val _tripList = MutableStateFlow<List<Trip>>(emptyList())
    val tripList = _tripList.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            tripRepository.getAllTrips().distinctUntilChanged().collect { trips ->
                if (trips.isNotEmpty()) _tripList.value = trips
            }
        }
    }

    //    fun getAllTrips() = tripDao.getAllTrips().flowOn(Dispatchers.IO).conflate()
    fun deleteAllTrips() = viewModelScope.launch { tripRepository.deleteAllTrips() }

    fun getTrip(id: String) = viewModelScope.launch { tripRepository.getTrip(id) }
    fun addTrip(trip: Trip) = viewModelScope.launch { tripRepository.addTrip(trip) }
    fun updateTrip(trip: Trip) = viewModelScope.launch { tripRepository.updateTrip(trip) }
    fun deleteTrip(trip: Trip) = viewModelScope.launch { tripRepository.deleteTrip(trip) }

    fun fieldNotEmpty(value: String) = value.isNotEmpty()
}