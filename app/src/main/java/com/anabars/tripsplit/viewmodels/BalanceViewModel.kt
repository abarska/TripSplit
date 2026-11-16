package com.anabars.tripsplit.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.model.BalanceWithNameAndStatus
import com.anabars.tripsplit.repository.BalanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val balanceRepository: BalanceRepository,
    private val exchangeRateDao: ExchangeRateDao,
    private val dataStore: DataStore<Preferences>,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tripId: Long = savedStateHandle.get<Long>("tripId")
        ?: throw IllegalStateException("Trip ID is required for BalanceViewModel")

    @OptIn(ExperimentalCoroutinesApi::class)
    private val homeCurrencyAndRate = dataStore.data
        .map { prefs ->
            prefs[stringPreferencesKey(TripSplitConstants.PREF_KEY_LOCAL_CURRENCY)]
                ?: TripSplitConstants.BASE_CURRENCY
        }
        .distinctUntilChanged()
        .flatMapLatest { code ->
            exchangeRateDao.getRateFlowForCurrency(code).map { rate -> code to rate }
        }

    val uiState: StateFlow<BalanceUiState> = combine(
        balanceRepository.getBalancesAndCurrencies(tripId),
        homeCurrencyAndRate
    ) { balancesAndCurrencies, (homeCurrency, homeRate) ->
        val multiplier = homeRate?.let { BigDecimal(it) }
        BalanceUiState(
            balances = multiplier?.let { m ->
                balancesAndCurrencies.balances.map { balance ->
                    balance.copy(amount = balance.amount.multiply(m), amountCurrency = homeCurrency)
                }
            }.orEmpty(),
            currencies = balancesAndCurrencies.currencies,
            ratesAvailable = multiplier != null
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, BalanceUiState())
}

data class BalanceUiState(
    val balances: List<BalanceWithNameAndStatus> = emptyList(),
    val currencies: List<TripCurrency> = emptyList(),
    val ratesAvailable: Boolean = true
)
