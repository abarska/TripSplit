package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.LayoutType
import com.anabars.tripsplit.ui.components.TsRadioGroup

@Composable
fun ExpenseCurrenciesRadioGroup(
    modifier: Modifier = Modifier,
    currencies: List<TripCurrency>,
    expenseCurrencyCode: String,
    onCurrencySelected: (String) -> Unit
) {
    if (currencies.isEmpty()) return

    TsRadioGroup(
        modifier = modifier,
        items = currencies,
        selectedItem = currencies.find { it.code == expenseCurrencyCode } ?: currencies.first(),
        onItemSelected = { onCurrencySelected(it.code) },
        layout = LayoutType.Flow
    ) { currency ->
        TsInfoText(
            text = currency.code,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}