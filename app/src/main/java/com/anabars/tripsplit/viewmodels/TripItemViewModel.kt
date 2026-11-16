package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.model.GroupedResult
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
import com.anabars.tripsplit.repository.TripItemRepository
import com.anabars.tripsplit.repository.TripItemResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val tripItemRepository: TripItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tripId: Long = savedStateHandle.get<Long>("tripId")
        ?: throw IllegalStateException("Trip ID is required for TripExpensesViewModel")

    val groupedExpensesResult: StateFlow<GroupedResult<LocalDate, ExpenseWithParticipants>> =
        tripItemRepository.getExpensesWithParticipantsByTrip(tripId)
            .map { expenses ->
                if (expenses.isEmpty()) {
                    GroupedResult.Empty<LocalDate, ExpenseWithParticipants>()
                } else {
                    val grouped = expenses.groupBy {
                        Instant.ofEpochMilli(it.expense.timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }.toSortedMap(compareByDescending { it })
                    GroupedResult.Success(grouped)
                }
            }
            .onStart { emit(GroupedResult.Loading()) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = GroupedResult.Loading()
            )

    val groupedPaymentsResult: StateFlow<GroupedResult<LocalDate, TripPayment>> =
        tripItemRepository.getPaymentsByTripId(tripId)
            .map { payments ->
                if (payments.isEmpty()) {
                    GroupedResult.Empty<LocalDate, TripPayment>()
                } else {
                    val grouped = payments.groupBy {
                        Instant.ofEpochMilli(it.timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }.toSortedMap(compareByDescending { it })
                    GroupedResult.Success(grouped)
                }
            }
            .onStart { emit(GroupedResult.Loading()) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = GroupedResult.Loading()
            )

    val tripParticipants: StateFlow<List<TripParticipant>> =
        tripItemRepository.getParticipantsByTripId(tripId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _snackbarEffect = MutableSharedFlow<Int>()
    val snackbarEffect: SharedFlow<Int> = _snackbarEffect.asSharedFlow()

    private fun deleteExpenseById(expenseId: Long) {
        viewModelScope.launch {
            handleDeleteResult(tripItemRepository.deleteExpenseById(expenseId))
        }
    }

    private fun deletePaymentById(paymentId: Long) {
        viewModelScope.launch {
            handleDeleteResult(tripItemRepository.deletePaymentById(paymentId))
        }
    }

    private suspend fun handleDeleteResult(result: TripItemResult) {
        if (result == TripItemResult.MissingExchangeRate) {
            _snackbarEffect.emit(R.string.error_delete_exchange_rates_unavailable)
        }
    }

    fun onIntent(intent: DeleteItemIntent) {
        when (intent) {
            is DeleteItemIntent.DeleteExpenseItem -> deleteExpenseById(intent.id)
            is DeleteItemIntent.DeletePaymentItem -> deletePaymentById(intent.id)
        }
    }
}

sealed class DeleteItemIntent(open val id: Long) {
    data class DeletePaymentItem(override val id: Long) : DeleteItemIntent(id)
    data class DeleteExpenseItem(override val id: Long) : DeleteItemIntent(id)
}