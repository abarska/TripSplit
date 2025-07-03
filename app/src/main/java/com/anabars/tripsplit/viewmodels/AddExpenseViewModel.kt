package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.repository.TripExpensesRepository
import com.anabars.tripsplit.ui.model.ExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val tripExpensesRepository: TripExpensesRepository
) :
    ViewModel() {
    fun saveExpense(
        expenseAmount: String,
        expenseCurrencyCode: String,
        selectedCategory: ExpenseCategory,
        selectedDate: LocalDate,
        expensePayerId: Long
    ) {
        viewModelScope.launch {
            val timestamp = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
            val expense = TripExpense(
                paidById = expensePayerId,
                amount = expenseAmount.toDouble(),
                currency = expenseCurrencyCode,
                category = selectedCategory,
                timestamp = timestamp,
                tripId = tripId
            )
            tripExpensesRepository.saveExpense(expense)
        }
    }

    private val tripId: Long = savedStateHandle.get<Long>("tripId")
        ?: throw IllegalStateException("Trip ID is required for AddExpenseViewModel")

    val tripCurrencies: StateFlow<List<TripCurrency>> =
        tripExpensesRepository.getCurrenciesByTrip(tripId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val tripParticipants: StateFlow<List<TripParticipant>> =
        tripExpensesRepository.getParticipantsByTrip(tripId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
}