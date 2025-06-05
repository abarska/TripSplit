package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.preferences.CurrencyPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val currencyPreference: CurrencyPreference) :
    ViewModel() {

    private val _currencies = MutableStateFlow<List<String>>(emptyList())
    val currencies: StateFlow<List<String>> = _currencies.asStateFlow()

    val localCurrencyFlow =
        currencyPreference.getCurrencyFlow(TripSplitConstants.PREF_KEY_LOCAL_CURRENCY).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Currency.getInstance(Locale.getDefault()).currencyCode
        )

    init {
        viewModelScope.launch {
            // TODO: load currencies from DB
            // _currencies.value = currentWorldCurrencies
        }
    }

    fun saveCurrency(key: String, code: String) {
        viewModelScope.launch {
            currencyPreference.saveCurrency(key, code)
        }
    }
}