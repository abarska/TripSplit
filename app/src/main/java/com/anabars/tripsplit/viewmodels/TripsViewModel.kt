package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripStatus
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

    private val _tripsGroupedByStatus = MutableStateFlow<Map<TripStatus, List<Trip>>>(emptyMap())
    val tripsGroupedByStatus = _tripsGroupedByStatus.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            tripRepository.getAllTrips()
                .distinctUntilChanged()
                .collect { trips ->
                    if (trips.isNotEmpty()) {
                        val groupedTrips: Map<TripStatus, List<Trip>> = trips.groupBy { it.status }
                        _tripsGroupedByStatus.value = groupedTrips.toSortedMap(TripStatus.comparator)
                    }
                }
        }
    }

    fun toggleSorting() {

    }
}