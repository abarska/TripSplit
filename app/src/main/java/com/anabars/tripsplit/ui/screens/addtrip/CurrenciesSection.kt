package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.components.SecondaryButton
import com.anabars.tripsplit.ui.itemrows.CurrencyItemRow

@Composable
fun CurrenciesSection(
    currencies: List<String>,
    onAddCurrencyButtonClick: () -> Unit,
    onDeleteCurrency: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InfoText(textRes = R.string.currencies_section_header)

        if (currencies.isNotEmpty()) {
            Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_small)))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                currencies.forEach { code ->
                    CurrencyItemRow(code = code, onDeleteCurrency = onDeleteCurrency)
                }
            }
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))

        SecondaryButton(textRes = R.string.add_a_currency) { onAddCurrencyButtonClick() }
    }
}