package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.model.TripDetails
import com.anabars.tripsplit.repository.TripRepository
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.utils.MoneyMath
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class TripOverviewViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tripId: Long = savedStateHandle.get<Long>("tripId")
        ?: throw IllegalStateException("Trip ID is required for TripOverviewViewModel")

    private val _tripDetails = MutableStateFlow<TripDetails?>(null)
    val tripDetails: StateFlow<TripDetails?> = _tripDetails.asStateFlow()

    val expenses: StateFlow<List<TripExpense>> =
        tripRepository.getExpensesByTripId(tripId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val expenseCategorizationResult: StateFlow<ExpenseCategorizationResult> =
        combine(expenses, tripDetails.filterNotNull()) { expensesList, details ->
            Pair(expensesList, details.trip.defaultCurrency)
        }
            .debounce(100) // reduce flicker
            .flatMapLatest { (expensesList, tripCurrency) ->
                flow {
                    emit(ExpenseCategorizationResult.Loading)
                    delay(100) // show spinner briefly
                    val digits = MoneyMath.fractionDigits(tripCurrency)
                    val grouped = expensesList.groupBy { it.category }
                        .mapValues { (_, expenses) ->
                            BigDecimal.valueOf(expenses.sumOf { it.tripAmountMinor }, digits).toDouble()
                        }
                    emit(ExpenseCategorizationResult.Success(grouped))
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                ExpenseCategorizationResult.Loading
            )

    init {
        viewModelScope.launch {
            tripRepository.getTripDetailsFlow(tripId)
                .collect { details ->
                    _tripDetails.value = details
                }
        }
    }
}

sealed class ExpenseCategorizationResult {
    object Loading : ExpenseCategorizationResult()
    data class Success(val data: Map<ExpenseCategory, Double>) :
        ExpenseCategorizationResult()
}