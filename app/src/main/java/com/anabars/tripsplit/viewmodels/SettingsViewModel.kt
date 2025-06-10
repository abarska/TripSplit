package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.preferences.CurrencyPreference
import com.anabars.tripsplit.utils.getCurrencyDisplayList
import com.anabars.tripsplit.utils.validCurrencyCodes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
        viewModelScope.launch(Dispatchers.Default) {
            _currencies.value = getCurrencyDisplayList(validCurrencyCodes())
        }
    }

    fun saveCurrency(currency: String) {
        viewModelScope.launch {
            currencyPreference.saveCurrency(TripSplitConstants.PREF_KEY_LOCAL_CURRENCY, currency.take(3))
        }
    }
}