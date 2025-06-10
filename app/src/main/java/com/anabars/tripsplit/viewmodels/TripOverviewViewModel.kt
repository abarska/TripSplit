package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.model.TripWithDetails
import com.anabars.tripsplit.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripOverviewViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _tripDetails = MutableStateFlow<TripWithDetails?>(null)
    val tripDetails: StateFlow<TripWithDetails?> = _tripDetails.asStateFlow()

    private val tripId: Long = savedStateHandle.get<Long>("id")
        ?: throw IllegalStateException("Trip ID is required for TripOverviewViewModel")

    init {
        viewModelScope.launch {
            tripRepository.getTripDetailsWithFlow(tripId)
                .collect { details ->
                    _tripDetails.value = details
                }
        }
    }
}