package com.anabars.tripsplit.ui.screens.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsCurrencyPicker
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.TsOutlinedButton
import com.anabars.tripsplit.ui.utils.TsFontSize

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
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        val expanded = rememberSaveable { mutableStateOf(false) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TsInfoText(textRes = labelRes, text = label, fontSize = TsFontSize.MEDIUM)
            TsOutlinedButton(text = selectedCurrency) { expanded.value = true }
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