package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.data.room.entity.ExchangeRate
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.model.TripWithDetails
import com.anabars.tripsplit.repository.TripRepository
import com.anabars.tripsplit.ui.model.ExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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

    val exchangeRates: StateFlow<List<ExchangeRate>> =
        tripRepository.getExchangeRatesFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val expenseCategorizationResult: StateFlow<ExpenseCategorizationResult> =
        combine(expenses, exchangeRates) { expensesList, ratesList ->
            Pair(expensesList, ratesList)
        }
            .debounce(100) // reduce flicker
            .flatMapLatest { (expensesList, ratesList) ->
                flow {
                    emit(ExpenseCategorizationResult.Loading)
                    delay(100) // show spinner briefly
                    val result = if (ratesList.isEmpty()) {
                        ExpenseCategorizationResult.UnavailableData
                    } else {
                        val rateMap = ratesList.associateBy { it.currencyCode }
                        val missingCurrencies = expensesList
                            .map { it.currency }
                            .filter { it !in rateMap }
                            .distinct()
                        if (missingCurrencies.isNotEmpty()) {
                            ExpenseCategorizationResult.MissingCurrencies(missingCurrencies)
                        } else {
                            val grouped = expensesList.groupBy { it.category }
                                .mapValues { (_, expenses) ->
                                    expenses.sumOf { expense ->
                                        val rate = rateMap[expense.currency]!!.rate
                                        expense.amount / rate
                                    }
                                }
                            ExpenseCategorizationResult.Success(grouped)
                        }
                    }
                    emit(result)
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                ExpenseCategorizationResult.Loading
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

sealed class ExpenseCategorizationResult {
    object Loading : ExpenseCategorizationResult()
    object UnavailableData : ExpenseCategorizationResult()
    data class MissingCurrencies(val missingCurrencies: List<String>) :
        ExpenseCategorizationResult()
    data class Success(val data: Map<ExpenseCategory, Double>) :
        ExpenseCategorizationResult()
}