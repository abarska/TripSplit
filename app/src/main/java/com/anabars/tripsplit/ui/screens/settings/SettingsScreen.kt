package com.anabars.tripsplit.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import com.anabars.tripsplit.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {

    val viewModel: SettingsViewModel = hiltViewModel()
    val localCurrency by viewModel.localCurrencyFlow.collectAsState()
    val currencies by viewModel.currencies.collectAsState()
    val onCurrencySelected: (String) -> Unit = { currency -> viewModel.saveCurrency(currency) }

    Column(
        modifier = modifier.padding(dimensionResource(R.dimen.full_screen_padding)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_normal))
    ) {

        TsContentCard(modifier = Modifier.inputWidthModifier()) {
            CurrencyPreferenceView(
                selectedCurrency = localCurrency.take(3),
                labelRes = R.string.local_currency,
                summaryRes = R.string.local_currency_summary,
                currencies = currencies,
                onCurrencySelected = onCurrencySelected
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(modifier = Modifier.inputWidthModifier())
}