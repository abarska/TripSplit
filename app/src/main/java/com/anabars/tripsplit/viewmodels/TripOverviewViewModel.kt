package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.model.TripWithDetails
import com.anabars.tripsplit.repository.TripRepository
import com.anabars.tripsplit.ui.model.ExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripOverviewViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tripId: Long = savedStateHandle.get<Long>("id")
        ?: throw IllegalStateException("Trip ID is required for TripOverviewViewModel")

    private val _tripDetails = MutableStateFlow<TripWithDetails?>(null)
    val tripDetails: StateFlow<TripWithDetails?> = _tripDetails.asStateFlow()

    val expenses: StateFlow<List<TripExpense>> =
        tripRepository.getExpensesByTripId(tripId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val categorizedExpenses: StateFlow<Map<ExpenseCategory, Double>> =
        expenses.map { list ->
            list.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    init {
        viewModelScope.launch {
            tripRepository.getTripDetailsWithFlow(tripId)
                .collect { details ->
                    _tripDetails.value = details
                }
        }
    }
}