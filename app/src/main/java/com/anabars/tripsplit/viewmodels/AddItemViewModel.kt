package com.anabars.tripsplit.viewmodels

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.anabars.tripsplit.repository.TripItemRepository
import com.anabars.tripsplit.repository.TripItemResult
import com.anabars.tripsplit.ui.model.AddItemUiEffect
import com.anabars.tripsplit.ui.model.AmountCurrencyState
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.model.PayerParticipantsState
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.AmountChanged
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.CategoryChanged
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.CurrencySelected
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.DateSelected
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.OnBackPressed
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.ParticipantsSelected
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.PayerSelected
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.SaveItem
import com.anabars.tripsplit.viewmodels.AddItemViewModel.AddItemError.INVALID_AMOUNT
import com.anabars.tripsplit.viewmodels.AddItemViewModel.AddItemError.MISSING_PARTICIPANTS
import com.anabars.tripsplit.viewmodels.AddItemViewModel.AddItemError.MISSING_PAYEE
import com.anabars.tripsplit.viewmodels.AddItemViewModel.AddItemError.MISSING_PAYER
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
    private val tripItemRepository: TripItemRepository
) :
    ViewModel() {

    private val tripId: Long = savedStateHandle.get<Long>("tripId")
        ?: throw IllegalStateException("Trip ID is required for AddItemViewModel")

    val useCase: UseCase = savedStateHandle.get<String>("useCase")
        ?.let { UseCase.valueOf(it) }
        ?: throw IllegalStateException("UseCase is required for AddItemViewModel")

    private val _amountCurrencyState = MutableStateFlow(AmountCurrencyState())
    val amountCurrencyState: StateFlow<AmountCurrencyState> = _amountCurrencyState.asStateFlow()

    private val _payerParticipantsState = MutableStateFlow(PayerParticipantsState())
    val payerParticipantsState: StateFlow<PayerParticipantsState> =
        _payerParticipantsState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AddItemUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    // guards against a second Save tap while a save is in progress
    private var isSaving = false

    init {
        viewModelScope.launch {
            try {
                val participants =
                    tripItemRepository.getActiveParticipantsByTripId(tripId).first()
                val currencies = tripItemRepository.getActiveCurrenciesByTripId(tripId).first()

                val initialSelectedParticipants =
                    if (useCase == UseCase.EXPENSE) participants.toSet()
                    else emptySet()
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
            is SaveItem -> saveItem()
            is OnBackPressed -> viewModelScope.launch {
                if (hasUnsavedChanges()) _uiEffect.emit(AddItemUiEffect.NavigateBack(true))
                else _uiEffect.emit(AddItemUiEffect.NavigateBack(false))
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

    private fun saveItem() {
        if (isSaving) return
        isSaving = true
        viewModelScope.launch {
            val result = if (validateItem()) save() else null
            // stay locked after a successful save until navigation; otherwise allow a retry
            isSaving = result == TripItemResult.Success
            result?.let { handleSaveResult(it) }
        }
    }

    private suspend fun save(): TripItemResult =
        TODO("drift-free-balances 3.1/6.1: parse the amount to minor units and save through the repository")

    private suspend fun handleSaveResult(result: TripItemResult) {
        when (result) {
            TripItemResult.Success -> _uiEffect.emit(AddItemUiEffect.NavigateBack(false))
            TripItemResult.MissingExchangeRate ->
                _uiEffect.emit(AddItemUiEffect.ShowSnackBar(R.string.error_save_exchange_rates_unavailable))
        }
    }

    private suspend fun validateItem(): Boolean {
        val error: AddItemError? = getError()
        error?.let {
            highlightCardWithError(it)
            _uiEffect.emit(AddItemUiEffect.ShowSnackBar(it.errorMessageId))
        }
        return error == null
    }

    private fun getError(): AddItemError? {
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

    private fun hasUnsavedChanges(): Boolean {
        val commonPart =
            _amountCurrencyState.value.expenseAmount.isNotBlank() ||
                    _payerParticipantsState.value.expensePayerId != null

        return when (useCase) {
            UseCase.PAYMENT -> commonPart
                    || _payerParticipantsState.value.selectedParticipants.isNotEmpty()

            UseCase.EXPENSE -> commonPart
                    || _payerParticipantsState.value.selectedParticipants.size != _payerParticipantsState.value.tripParticipants.size
                    || _amountCurrencyState.value.selectedCategory != ExpenseCategory.Miscellaneous
        }
    }

    enum class AddItemError(@StringRes val errorMessageId: Int) {
        INVALID_AMOUNT(R.string.error_amount_invalid),
        MISSING_PAYER(R.string.error_missing_payer),
        MISSING_PARTICIPANTS(R.string.error_missing_participants),
        MISSING_PAYEE(R.string.error_missing_payee)
    }

    enum class UseCase { EXPENSE, PAYMENT }
}