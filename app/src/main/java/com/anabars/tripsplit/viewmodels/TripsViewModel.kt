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
class TripsViewModel @Inject constructor(private val tripRepository: TripRepository) : ViewModel() {

    private val _tripList = MutableStateFlow<List<Trip>>(emptyList())
    val tripList = _tripList.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            tripRepository.getAllTrips().distinctUntilChanged().collect { trips ->
                if (trips.isNotEmpty()) _tripList.value = trips
            }
        }
    }
}