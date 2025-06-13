package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.listitems.TripSplitRadioButton

@Composable
fun ExpenseCurrenciesRadioGroup(
    modifier: Modifier = Modifier,
    currencies: List<TripCurrency>,
    expenseCurrencyCode: String,
    onCurrencySelected: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        modifier = modifier
    ) {
        currencies.forEach { currency ->
            TripSplitRadioButton(
                value = currency.code,
                isSelected = currency.code == expenseCurrencyCode,
                onItemClick = { onCurrencySelected(it) },
            ) {
                InfoText(
                    text = currency.code,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}