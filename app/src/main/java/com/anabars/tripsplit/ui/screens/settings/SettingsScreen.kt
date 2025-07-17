package com.anabars.tripsplit.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onTabTitleChange: (String) -> Unit
) {

    val viewModel: SettingsViewModel = hiltViewModel()
    val localCurrency by viewModel.localCurrencyFlow.collectAsState()
    val currencies by viewModel.currencies.collectAsState()
    val onCurrencySelected: (String) -> Unit =
        { currency -> viewModel.saveCurrency(currency) }

    val screenTitle = stringResource(R.string.title_settings)
    LaunchedEffect(Unit) {
        onTabTitleChange(screenTitle)
    }

    Column(
        modifier = modifier.padding(dimensionResource(R.dimen.full_screen_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal))
    ) {

        TsContentCard {
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