package com.anabars.tripsplit.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
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

    private val _uiState = MutableStateFlow<List<BalanceUiState>>(emptyList())
    val uiState: StateFlow<List<BalanceUiState>> = _uiState

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

            balanceRepository.getBalancesWithNameAndStatus(tripId)
                .collect { balances ->
                    _uiState.value = balances.map {
                        BalanceUiState(
                            participantName = it.participantName,
                            amount = it.amountUsd.multiply(BigDecimal(homeRate)),
                            currency = homeCurrency
                        )
                    }
                }
        }
    }
}

data class BalanceUiState(
    val participantName: String,
    val amount: BigDecimal,
    val currency: String
)
