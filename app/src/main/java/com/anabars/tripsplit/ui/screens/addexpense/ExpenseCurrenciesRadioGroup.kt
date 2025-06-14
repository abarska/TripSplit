package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.widgets.LayoutType
import com.anabars.tripsplit.ui.widgets.TripSplitRadioGroup

@Composable
fun ExpenseCurrenciesRadioGroup(
    modifier: Modifier = Modifier,
    currencies: List<TripCurrency>,
    expenseCurrencyCode: String,
    onCurrencySelected: (String) -> Unit
) {
    if (currencies.isEmpty()) return

    TripSplitRadioGroup(
        modifier = modifier,
        items = currencies,
        selectedItem = currencies.find { it.code == expenseCurrencyCode } ?: currencies.first(),
        onItemSelected = { onCurrencySelected(it.code) },
        layout = LayoutType.Flow
    ) { currency ->
        InfoText(
            text = currency.code,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}