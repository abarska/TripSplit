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

    // map can't be used because it doesn't trigger recomposition after sorting
    private val _tripsGroupedByStatus =
        MutableStateFlow<List<Pair<TripStatus, List<Trip>>>>(emptyList())
    val tripsGroupedByStatus = _tripsGroupedByStatus.asStateFlow()

    private val _ascendingOrder = MutableStateFlow(true)
    val ascendingOrder = _ascendingOrder.asStateFlow()

    private var cachedTrips: List<Trip> = emptyList()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            tripRepository.getTripsWithStatuses(TripStatus.getActiveStatuses())
                .distinctUntilChanged()
                .collect { trips ->
                    cachedTrips = trips
                    updateGroupedTrips(trips)
                }
        }
    }

    private fun updateGroupedTrips(trips: List<Trip>) {
        val grouped = trips.groupBy { it.status }
        val comparator =
            if (_ascendingOrder.value) TripStatus.comparator
            else TripStatus.comparator.reversed()
        val sortedList =
            grouped.entries
                .sortedWith { entry1, entry2 -> comparator.compare(entry1.key, entry2.key) }
                .map { it.key to it.value }
        _tripsGroupedByStatus.value = sortedList
    }

    fun toggleSorting() {
        _ascendingOrder.value = !_ascendingOrder.value
        updateGroupedTrips(cachedTrips)
    }

    fun updateTripStatus(id: Long, status: TripStatus) {
        viewModelScope.launch {
            tripRepository.updateTripStatus(id, status)
        }
    }
}