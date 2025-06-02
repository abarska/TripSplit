package com.anabars.tripsplit.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.ui.components.HorizontalSeparator
import com.anabars.tripsplit.ui.utils.fullScreenModifier
import com.anabars.tripsplit.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val localCurrency by viewModel.localCurrencyFlow.collectAsState()
    val preferredCurrency by viewModel.preferredCurrencyFlow.collectAsState()
    val currencies by viewModel.currencies.collectAsState()
    val onCurrencySelected: (String, String) -> Unit =
        { key, currency -> viewModel.saveCurrency(key, currency) }

    Column(
        modifier = modifier.then(Modifier.fullScreenModifier())
    ) {

        CurrencyPreferenceView(
            key = TripSplitConstants.PREF_KEY_LOCAL_CURRENCY,
            selectedCurrency = localCurrency.substringBefore('-').trim(),
            labelRes = R.string.local_currency,
            summaryRes = R.string.local_currency_summary,
            currencies = currencies,
            action = onCurrencySelected
        )

        HorizontalSeparator()

        CurrencyPreferenceView(
            key = TripSplitConstants.PREF_KEY_PREFERRED_CURRENCY,
            selectedCurrency = preferredCurrency.substringBefore('-').trim(),
            labelRes = R.string.preferred_currency,
            summaryRes = R.string.preferred_currency_summary,
            currencies = currencies,
            action = onCurrencySelected
        )

        HorizontalSeparator()
    }
}