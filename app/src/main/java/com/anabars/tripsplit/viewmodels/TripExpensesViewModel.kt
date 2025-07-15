package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
import com.anabars.tripsplit.repository.TripExpensesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TripExpensesViewModel @Inject constructor(
    tripExpensesRepository: TripExpensesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tripId: Long = savedStateHandle.get<Long>("id")
        ?: throw IllegalStateException("Trip ID is required for TripExpensesViewModel")

    val groupedExpenses: StateFlow<Map<LocalDate, List<ExpenseWithParticipants>>> =
        tripExpensesRepository.getExpensesWithParticipantsByTrip(tripId)
            .map { expenses ->
                expenses.groupBy {
                    Instant.ofEpochMilli(it.expense.timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                }.toSortedMap(compareByDescending { it })
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyMap()
            )

    val tripParticipants: StateFlow<List<TripParticipant>> =
        tripExpensesRepository.getParticipantsByTripId(tripId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}