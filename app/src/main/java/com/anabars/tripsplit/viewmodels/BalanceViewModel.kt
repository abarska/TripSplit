package com.anabars.tripsplit.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.model.BalancesAndCurrencies
import com.anabars.tripsplit.repository.BalanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val balanceRepository: BalanceRepository,
    private val exchangeRateDao: ExchangeRateDao,
    private val dataStore: DataStore<Preferences>,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tripId: Long = savedStateHandle.get<Long>("id")
        ?: throw IllegalStateException("Trip ID is required for BalanceViewModel")

    private val _uiState = MutableStateFlow(
        BalancesAndCurrencies(emptyList(), emptyList())
    )
    val uiState: StateFlow<BalancesAndCurrencies> = _uiState

    init {
        loadBalances()
    }

    fun loadBalances() {
        viewModelScope.launch {
            val homeCurrency = dataStore.data
                .map { prefs ->
                    prefs[stringPreferencesKey(TripSplitConstants.PREF_KEY_LOCAL_CURRENCY)]
                        ?: TripSplitConstants.BASE_CURRENCY
                }
                .first()

            val homeRate = exchangeRateDao.getExchangeRateForCurrency(homeCurrency).rate

            balanceRepository.getBalancesAndCurrencies(tripId)
                .map { balancesAndCurrencies ->
                    val updatedBalances =
                        balancesAndCurrencies.balances.map { balance ->
                            balance.copy(
                                amount = balance.amount.multiply(BigDecimal(homeRate)),
                                amountCurrency = homeCurrency
                            )
                        }
                    balancesAndCurrencies.copy(balances = updatedBalances)
                }.collect { transformed ->
                    _uiState.value = transformed
                }
        }
    }
}
