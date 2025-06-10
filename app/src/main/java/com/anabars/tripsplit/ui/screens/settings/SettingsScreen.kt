package com.anabars.tripsplit.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.HorizontalSeparator
import com.anabars.tripsplit.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val localCurrency by viewModel.localCurrencyFlow.collectAsState()
    val currencies by viewModel.currencies.collectAsState()
    val onCurrencySelected: (String) -> Unit =
        { currency -> viewModel.saveCurrency(currency) }

    Column(modifier = modifier.padding(dimensionResource(R.dimen.full_screen_padding))) {

        CurrencyPreferenceView(
            selectedCurrency = localCurrency.take(3),
            labelRes = R.string.local_currency,
            summaryRes = R.string.local_currency_summary,
            currencies = currencies,
            onCurrencySelected = onCurrencySelected
        )

        HorizontalSeparator()
    }
}