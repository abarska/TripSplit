package com.anabars.tripsplit.ui.screens.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.widgets.CurrencyPicker

@Composable
fun CurrencyPreferenceView(
    currencies: List<String>,
    selectedCurrency: String,
    modifier: Modifier = Modifier,
    @StringRes labelRes: Int = 0,
    label: String = "",
    @StringRes summaryRes: Int = 0,
    summary: String = "",
    action: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoText(textRes = labelRes, text = label)
            CurrencyPicker(
                selectedCurrency = selectedCurrency,
                onCurrencySelected = action,
                currencies = currencies
            )
        }
        InfoText(
            modifier = Modifier.fillMaxWidth(),
            textRes = summaryRes,
            text = summary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyPreferencePreview() {
    CurrencyPreferenceView(
        selectedCurrency = "EUR",
        labelRes = R.string.local_currency,
        summaryRes = R.string.local_currency_summary,
        currencies = emptyList(),
        action = { _ -> }
    )
}