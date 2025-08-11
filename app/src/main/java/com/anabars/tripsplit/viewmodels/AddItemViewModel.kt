package com.anabars.tripsplit.viewmodels

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.repository.TripExpensesRepository
import com.anabars.tripsplit.ui.model.AddItemAmountCurrencyState
import com.anabars.tripsplit.ui.model.AddItemPayerParticipantsState
import com.anabars.tripsplit.ui.model.AddItemUiEffect
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.AmountChanged
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.CategoryChanged
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.CurrencySelected
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.DateSelected
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.OnBackPressed
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.ParticipantsSelected
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.PayerSelected
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.SaveItem
import com.anabars.tripsplit.ui.widgets.UseCase
import com.anabars.tripsplit.viewmodels.AddItemViewModel.AddItemError.*
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
class AddItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val tripExpensesRepository: TripExpensesRepository
) :
    ViewModel() {

    private val tripId: Long = savedStateHandle.get<Long>("tripId")
        ?: throw IllegalStateException("Trip ID is required for AddExpenseViewModel")

    private val _amountCurrencyState = MutableStateFlow(AddItemAmountCurrencyState())
    val amountCurrencyState: StateFlow<AddItemAmountCurrencyState> =
        _amountCurrencyState.asStateFlow()

    private val _payerParticipantsState = MutableStateFlow(AddItemPayerParticipantsState())
    val payerParticipantsState: StateFlow<AddItemPayerParticipantsState> =
        _payerParticipantsState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AddItemUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            try {
                val participants =
                    tripExpensesRepository.getActiveParticipantsByTripId(tripId).first()
                val currencies = tripExpensesRepository.getActiveCurrenciesByTripId(tripId).first()

                val initialSelectedParticipants = participants.toSet()
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
                    )
                }
            } catch (e: Exception) {
                Log.e("marysya", "Error loading initial data: $e")
            }
        }
    }

    fun onIntent(intent: AddItemIntent) {
        when (intent) {
            is DateSelected -> updateDate(intent.date)
            is CategoryChanged -> updateCategory(intent.category)
            is AmountChanged -> updateAmount(intent.amount)
            is CurrencySelected -> updateCurrencyCode(intent.code)
            is PayerSelected -> updatePayerId(intent.id)
            is ParticipantsSelected -> updateSelectedParticipants(intent.participants)
            is SaveItem -> saveItem(intent.useCase)
            is OnBackPressed -> viewModelScope.launch {
                _uiEffect.emit(AddItemUiEffect.NavigateBack)
            }
        }
    }

    private fun updateDate(date: LocalDate) =
        _amountCurrencyState.update { it.copy(selectedDate = date) }

    private fun updateCategory(cat: ExpenseCategory) =
        _amountCurrencyState.update { it.copy(selectedCategory = cat) }

    private fun updateAmount(amount: String) {
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

    private fun saveItem(useCase: UseCase) {
        viewModelScope.launch {
            if (!validateItem(useCase = useCase)) return@launch

            when (useCase) {
                UseCase.EXPENSE -> {
                    val expense = TripExpense.fromUiState(
                        amountCurrencyState = _amountCurrencyState.value,
                        payerParticipantsState = _payerParticipantsState.value,
                        tripId = tripId
                    )
                    val participants = _payerParticipantsState.value.selectedParticipants
                    tripExpensesRepository.saveExpenseWithParticipants(expense, participants)
                    viewModelScope.launch {
                        _uiEffect.emit(AddItemUiEffect.NavigateBack)
                    }
                }

                UseCase.PAYMENT -> {
                    Log.d("marysya", "saveItem: save payment")
                    // TODO: anabars implement 
                }
            }
        }
    }

    private suspend fun validateItem(useCase: UseCase): Boolean {
        val error: AddItemError? = getError(useCase)
        error?.let {
            highlightCardWithError(it)
            _uiEffect.emit(AddItemUiEffect.ShowSnackBar(it.errorMessageId))
        }
        return error == null
    }

    private fun getError(useCase: UseCase): AddItemError? {
        val amount = _amountCurrencyState.value.expenseAmount.toDoubleOrNull()
        val payer = _payerParticipantsState.value.expensePayerId
        val payees = _payerParticipantsState.value.selectedParticipants
        return when {
            amount == null || amount <= 0.0 -> INVALID_AMOUNT
            payer == null -> MISSING_PAYER
            payees.isEmpty() -> if (useCase == UseCase.EXPENSE) MISSING_PARTICIPANTS else MISSING_PAYEE
            else -> null
        }
    }

    private fun highlightCardWithError(error: AddItemError) {
        when (error) {
            INVALID_AMOUNT ->
                _amountCurrencyState.update { it.copy(isError = true) }

            MISSING_PAYER, MISSING_PARTICIPANTS, MISSING_PAYEE ->
                _payerParticipantsState.update { it.copy(isError = true) }
        }
    }

    enum class AddItemError(@StringRes val errorMessageId: Int) {
        INVALID_AMOUNT(R.string.error_amount_invalid),
        MISSING_PAYER(R.string.error_missing_payer),
        MISSING_PARTICIPANTS(R.string.error_missing_participants),
        MISSING_PAYEE(R.string.error_missing_payee)
    }
}