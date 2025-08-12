package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
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
class TripItemViewModel @Inject constructor(
    val tripItemRepository: TripItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tripId: Long = savedStateHandle.get<Long>("id")
        ?: throw IllegalStateException("Trip ID is required for TripExpensesViewModel")

    val groupedExpensesResult: StateFlow<GroupedResult<ExpenseWithParticipants>> =
        tripItemRepository.getExpensesWithParticipantsByTrip(tripId)
            .map { expenses ->
                if (expenses.isEmpty()) {
                    GroupedResult.Empty
                } else {
                    val grouped = expenses.groupBy {
                        Instant.ofEpochMilli(it.expense.timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }.toSortedMap(compareByDescending { it })
                    GroupedResult.Success(data = grouped)
                }
            }
            .onStart { emit(GroupedResult.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = GroupedResult.Loading
            )

    val groupedPaymentsResult: StateFlow<GroupedResult<TripPayment>> =
        tripItemRepository.getPaymentsByTripId(tripId)
            .map { payments ->
                if (payments.isEmpty()) {
                    GroupedResult.Empty
                } else {
                    val grouped = payments.groupBy {
                        Instant.ofEpochMilli(it.timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }.toSortedMap(compareByDescending { it })
                    GroupedResult.Success(data = grouped)
                }
            }
            .onStart { emit(GroupedResult.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = GroupedResult.Loading
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

    fun deletePaymentById(paymentId: Long) {
        viewModelScope.launch {
            tripItemRepository.deletePaymentById(paymentId)
        }
    }
}

// thank you to Advanced Kotlin by Marcin Moskala, I'm happy I can apply this knowledge
sealed class GroupedResult<out T> {
    object Loading : GroupedResult<Nothing>()
    object Empty : GroupedResult<Nothing>()
    data class Success<T>(val data: Map<LocalDate, List<T>>) : GroupedResult<T>()
}