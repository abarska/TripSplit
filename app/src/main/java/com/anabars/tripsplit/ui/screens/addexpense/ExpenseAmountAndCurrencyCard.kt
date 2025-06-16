package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsExpenseAmountInput

@Composable
fun ExpenseAmountAndCurrencyCard(
    modifier: Modifier = Modifier,
    expenseAmount: String,
    expenseCurrencyCode: String,
    tripCurrencies: List<TripCurrency>,
    onExpenseAmountChanged: (String) -> Unit,
    onCurrencySelected: (String) -> Unit
) {
    TsContentCard(modifier = modifier) {
        TsExpenseAmountInput(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = expenseAmount,
            currencyPrefix = expenseCurrencyCode,
            onValueChange = onExpenseAmountChanged
        )
        TsInfoText(textRes = R.string.currency)
        ExpenseCurrenciesRadioGroup(
            modifier = Modifier.padding(horizontal = 16.dp),
            currencies = tripCurrencies,
            expenseCurrencyCode = expenseCurrencyCode,
            onCurrencySelected = onCurrencySelected,
        )
    }
}