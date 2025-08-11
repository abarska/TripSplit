package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
import com.anabars.tripsplit.repository.TripItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TripExpensesViewModel @Inject constructor(
    val tripItemRepository: TripItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tripId: Long = savedStateHandle.get<Long>("id")
        ?: throw IllegalStateException("Trip ID is required for TripExpensesViewModel")

    val groupedExpensesResult: StateFlow<GroupedExpensesResult> =
        tripItemRepository.getExpensesWithParticipantsByTrip(tripId)
            .map { expenses ->
                if (expenses.isEmpty()) {
                    GroupedExpensesResult.Empty
                } else {
                    val grouped = expenses.groupBy {
                        Instant.ofEpochMilli(it.expense.timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }.toSortedMap(compareByDescending { it })
                    GroupedExpensesResult.Success(data = grouped)
                }
            }
            .onStart { emit(GroupedExpensesResult.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = GroupedExpensesResult.Loading
            )


    val tripParticipants: StateFlow<List<TripParticipant>> =
        tripItemRepository.getParticipantsByTripId(tripId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun deleteExpenseById(expenseId: Long) {
        viewModelScope.launch {
            tripItemRepository.deleteExpenseById(expenseId)
        }
    }
}

sealed class GroupedExpensesResult {
    object Loading : GroupedExpensesResult()
    object Empty : GroupedExpensesResult()
    data class Success(val data: Map<LocalDate, List<ExpenseWithParticipants>>) :
        GroupedExpensesResult()
}