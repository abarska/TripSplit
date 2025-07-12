package com.anabars.tripsplit.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.repository.TripExpensesRepository
import com.anabars.tripsplit.ui.model.AddExpenseAmountCurrencyState
import com.anabars.tripsplit.ui.model.AddExpenseDateCategoryState
import com.anabars.tripsplit.ui.model.AddExpenseErrorState
import com.anabars.tripsplit.ui.model.AddExpenseEvent
import com.anabars.tripsplit.ui.model.AddExpensePayerParticipantsState
import com.anabars.tripsplit.ui.model.ExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val tripExpensesRepository: TripExpensesRepository
) :
    ViewModel() {

    private val tripId: Long = savedStateHandle.get<Long>("tripId")
        ?: throw IllegalStateException("Trip ID is required for AddExpenseViewModel")

    private val _dateCategoryState = MutableStateFlow(AddExpenseDateCategoryState())
    val dateCategoryState: StateFlow<AddExpenseDateCategoryState> = _dateCategoryState.asStateFlow()

    private val _amountCurrencyState = MutableStateFlow(AddExpenseAmountCurrencyState())
    val amountCurrencyState: StateFlow<AddExpenseAmountCurrencyState> =
        _amountCurrencyState.asStateFlow()

    private val _payerParticipantsState = MutableStateFlow(AddExpensePayerParticipantsState())
    val payerParticipantsState: StateFlow<AddExpensePayerParticipantsState> =
        _payerParticipantsState.asStateFlow()

    private val _addExpenseErrorState = MutableStateFlow(AddExpenseErrorState.NONE)
    val addExpenseErrorState = _addExpenseErrorState.asStateFlow()

    private val _navigateBackState = MutableStateFlow(false)
    val navigateBackState = _navigateBackState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val participants = tripExpensesRepository.getParticipantsByTripId(tripId).first()
                val currencies = tripExpensesRepository.getCurrenciesByTripId(tripId).first()

                val initialSelectedParticipants = participants.toSet()
                val initialPayerId = participants.firstOrNull()?.id
                val initialCurrencyCode = currencies.firstOrNull()?.code

                _amountCurrencyState.update { currentState ->
                    currentState.copy(
                        tripCurrencies = currencies,
                        expenseCurrencyCode = initialCurrencyCode
                            ?: currentState.expenseCurrencyCode
                    )
                }

                _payerParticipantsState.update { currentState ->
                    currentState.copy(
                        tripParticipants = participants,
                        selectedParticipants = initialSelectedParticipants,
                        expensePayerId = initialPayerId ?: currentState.expensePayerId,
                    )
                }
            } catch (e: Exception) {
                Log.e("marysya", "Error loading initial data: $e")
            }
        }
    }

    fun onEvent(event: AddExpenseEvent) {
        when (event) {
            is AddExpenseEvent.DateSelected -> updateDate(event.date)
            is AddExpenseEvent.CategoryChanged -> updateCategory(event.category)
            is AddExpenseEvent.AmountChanged -> updateExpenseAmount(event.amount)
            is AddExpenseEvent.CurrencySelected -> updateCurrencyCode(event.code)
            is AddExpenseEvent.PayerSelected -> updatePayerId(event.id)
            is AddExpenseEvent.ParticipantsSelected -> updateSelectedParticipants(event.participants)
        }
    }

    private fun updateDate(date: LocalDate) =
        _dateCategoryState.update { it.copy(selectedDate = date) }

    private fun updateCategory(cat: ExpenseCategory) =
        _dateCategoryState.update { it.copy(selectedCategory = cat) }

    private fun updateExpenseAmount(amount: String) {
        _amountCurrencyState.update { it.copy(expenseAmount = amount) }
        if (_addExpenseErrorState.value == AddExpenseErrorState.EXPENSE_AMOUNT) {
            clearError()
        }
    }

    private fun updateCurrencyCode(code: String) =
        _amountCurrencyState.update { it.copy(expenseCurrencyCode = code) }

    private fun updatePayerId(id: Long) =
        _payerParticipantsState.update { it.copy(expensePayerId = id) }

    private fun updateSelectedParticipants(participants: Set<TripParticipant>) {
        _payerParticipantsState.update { it.copy(selectedParticipants = participants) }
        if (_addExpenseErrorState.value == AddExpenseErrorState.SELECTED_PARTICIPANTS) {
            clearError()
        }
    }

    private fun clearError() {
        _addExpenseErrorState.value = AddExpenseErrorState.NONE
    }

    fun saveExpense() {
        viewModelScope.launch {
            if (!validateExpense()) return@launch
            tripExpensesRepository.saveExpense(
                TripExpense.fromUiState(
                    dateCategoryState = _dateCategoryState.value,
                    amountCurrencyState = _amountCurrencyState.value,
                    payerParticipantsState = _payerParticipantsState.value,
                    tripId = tripId
                )
            )
            _navigateBackState.value = true
        }
    }

    fun validateExpense(): Boolean {
        val amount = _amountCurrencyState.value.expenseAmount.toDoubleOrNull()
        val hasAmountError = amount == null || amount <= 0.0
        val hasPayerError = _payerParticipantsState.value.selectedParticipants.isEmpty()
        _addExpenseErrorState.value = when {
            hasAmountError -> AddExpenseErrorState.EXPENSE_AMOUNT
            hasPayerError -> AddExpenseErrorState.SELECTED_PARTICIPANTS
            else -> AddExpenseErrorState.NONE
        }
        return !hasAmountError && !hasPayerError
    }

    fun onNavigatedBack() {
        _navigateBackState.value = false
    }
}