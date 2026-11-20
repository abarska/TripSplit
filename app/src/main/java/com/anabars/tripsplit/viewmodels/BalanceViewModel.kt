package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.preferences.CurrencyPreference
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.model.BalanceWithNameAndStatus
import com.anabars.tripsplit.repository.BalanceRepository
import com.anabars.tripsplit.utils.MoneyMath
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val balanceRepository: BalanceRepository,
    private val exchangeRateDao: ExchangeRateDao,
    private val currencyPreference: CurrencyPreference,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tripId: Long = savedStateHandle.get<Long>("tripId")
        ?: throw IllegalStateException("Trip ID is required for BalanceViewModel")

    private val balancesAndCurrencies = balanceRepository.getBalancesAndCurrencies(tripId)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BalanceUiState> = combine(
        balancesAndCurrencies,
        currencyPreference.getCurrencyFlow(TripSplitConstants.PREF_KEY_LOCAL_CURRENCY)
    ) { data, homeCurrency -> data to homeCurrency }
        .flatMapLatest { (data, homeCurrency) ->
            combine(
                exchangeRateDao.getRateFlowForCurrency(data.tripCurrency),
                exchangeRateDao.getRateFlowForCurrency(homeCurrency)
            ) { tripRate, homeRate ->
                toUiState(data.balances, data.currencies, data.tripCurrency, homeCurrency, tripRate, homeRate)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BalanceUiState())

    private fun toUiState(
        balances: List<BalanceWithNameAndStatus>,
        currencies: List<TripCurrency>,
        tripCurrency: String,
        homeCurrency: String,
        tripRate: BigDecimal?,
        homeRate: BigDecimal?
    ): BalanceUiState {
        val rate = if (homeCurrency != tripCurrency && tripRate != null && homeRate != null) {
            MoneyMath.crossRate(tripRate, homeRate)
        } else null
        val homeDigits = MoneyMath.fractionDigits(homeCurrency)
        return BalanceUiState(
            balances = balances.map { balance ->
                if (rate == null) balance
                else balance.copy(
                    approxAmount = balance.amount.multiply(rate).setScale(homeDigits, RoundingMode.HALF_EVEN),
                    approxCurrency = homeCurrency
                )
            },
            currencies = currencies
        )
    }
}

data class BalanceUiState(
    val balances: List<BalanceWithNameAndStatus> = emptyList(),
    val currencies: List<TripCurrency> = emptyList()
)
