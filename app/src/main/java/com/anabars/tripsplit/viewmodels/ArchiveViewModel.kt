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
class ArchiveViewModel @Inject constructor(private val tripRepository: TripRepository) :
    ViewModel() {

    private val _archivedTrips = MutableStateFlow<List<Trip>>(emptyList())
    val archivedTrips = _archivedTrips.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            tripRepository.getTripsWithStatuses(listOf(TripStatus.ARCHIVED))
                .distinctUntilChanged()
                .collect { trips -> _archivedTrips.value = trips }
        }
    }
}