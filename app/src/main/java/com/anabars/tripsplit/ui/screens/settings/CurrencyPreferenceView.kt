package com.anabars.tripsplit.ui.screens.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.TsCurrencyPicker

@Composable
fun CurrencyPreferenceView(
    currencies: List<String>,
    selectedCurrency: String,
    modifier: Modifier = Modifier,
    @StringRes labelRes: Int = 0,
    label: String = "",
    @StringRes summaryRes: Int = 0,
    summary: String = "",
    onCurrencySelected: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        val expanded = remember { mutableStateOf(false) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TsInfoText(textRes = labelRes, text = label)
            OutlinedButton(onClick = { expanded.value = true }) {
                Text(text = selectedCurrency)
            }
        }
        TsInfoText(
            modifier = Modifier.fillMaxWidth(),
            textRes = summaryRes,
            text = summary
        )
        TsCurrencyPicker(
            currencies = currencies,
            expanded = expanded,
            onCurrencySelected = onCurrencySelected
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
        onCurrencySelected = { _ -> }
    )
}