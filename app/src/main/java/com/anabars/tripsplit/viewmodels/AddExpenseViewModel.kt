package com.anabars.tripsplit.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.repository.TripExpensesRepository
import com.anabars.tripsplit.ui.model.AddExpenseAmountCurrencyState
import com.anabars.tripsplit.ui.model.AddExpensePayerParticipantsState
import com.anabars.tripsplit.ui.model.AddExpenseUiEffect
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.screens.addexpense.AddExpenseIntent
import com.anabars.tripsplit.ui.screens.addexpense.AddExpenseIntent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _amountCurrencyState = MutableStateFlow(AddExpenseAmountCurrencyState())
    val amountCurrencyState: StateFlow<AddExpenseAmountCurrencyState> =
        _amountCurrencyState.asStateFlow()

    private val _payerParticipantsState = MutableStateFlow(AddExpensePayerParticipantsState())
    val payerParticipantsState: StateFlow<AddExpensePayerParticipantsState> =
        _payerParticipantsState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AddExpenseUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            try {
                val participants =
                    tripExpensesRepository.getActiveParticipantsByTripId(tripId).first()
                val currencies = tripExpensesRepository.getActiveCurrenciesByTripId(tripId).first()

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

    fun onIntent(intent: AddExpenseIntent) {
        when (intent) {
            is DateSelected -> updateDate(intent.date)
            is CategoryChanged -> updateCategory(intent.category)
            is AmountChanged -> updateExpenseAmount(intent.amount)
            is CurrencySelected -> updateCurrencyCode(intent.code)
            is PayerSelected -> updatePayerId(intent.id)
            is ParticipantsSelected -> updateSelectedParticipants(intent.participants)
            is SaveExpense -> saveExpense()
            is OnBackPressed -> viewModelScope.launch {
                _uiEffect.emit(AddExpenseUiEffect.NavigateBack)
            }
        }
    }

    private fun updateDate(date: LocalDate) =
        _amountCurrencyState.update { it.copy(selectedDate = date) }

    private fun updateCategory(cat: ExpenseCategory) =
        _amountCurrencyState.update { it.copy(selectedCategory = cat) }

    private fun updateExpenseAmount(amount: String) {
        _amountCurrencyState.update {
            it.copy(expenseAmount = amount, isError = false)
        }
    }

    private fun updateCurrencyCode(code: String) =
        _amountCurrencyState.update { it.copy(expenseCurrencyCode = code) }

    private fun updatePayerId(id: Long) =
        _payerParticipantsState.update { it.copy(expensePayerId = id) }

    private fun updateSelectedParticipants(participants: Set<TripParticipant>) {
        _payerParticipantsState.update {
            it.copy(selectedParticipants = participants, isError = false)
        }
    }

    private fun saveExpense() {
        viewModelScope.launch {
            if (!validateExpense()) return@launch

            val expense = TripExpense.fromUiState(
                amountCurrencyState = _amountCurrencyState.value,
                payerParticipantsState = _payerParticipantsState.value,
                tripId = tripId
            )
            val participants = _payerParticipantsState.value.selectedParticipants
            tripExpensesRepository.saveExpenseWithParticipants(expense, participants)
            viewModelScope.launch {
                _uiEffect.emit(AddExpenseUiEffect.NavigateBack)
            }
        }
    }

    private fun validateExpense(): Boolean {
        val amount = _amountCurrencyState.value.expenseAmount.toDoubleOrNull()
        val wrongAmount = amount == null || amount <= 0.0
        val participantsMissingError = _payerParticipantsState.value.selectedParticipants.isEmpty()
        if (wrongAmount || participantsMissingError) {
            showError(
                wrongAmount = wrongAmount,
                participantsMissingError = participantsMissingError
            )
            highlightCardWithError(
                wrongAmount = wrongAmount,
                participantsMissingError = participantsMissingError
            )
        }
        return !wrongAmount && !participantsMissingError
    }

    private fun highlightCardWithError(wrongAmount: Boolean, participantsMissingError: Boolean) {
        if (wrongAmount)
            _amountCurrencyState.update { it.copy(isError = true) }
        if (participantsMissingError)
            _payerParticipantsState.update { it.copy(isError = true) }
    }

    private fun showError(wrongAmount: Boolean, participantsMissingError: Boolean) {
        if (wrongAmount) {
            viewModelScope.launch {
                _uiEffect.emit(AddExpenseUiEffect.ShowSnackBar(R.string.error_amount_invalid))
            }
        } else if (participantsMissingError) {
            viewModelScope.launch {
                _uiEffect.emit(AddExpenseUiEffect.ShowSnackBar(R.string.error_participants_not_selected))
            }
        }
    }
}