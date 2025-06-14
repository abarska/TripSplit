package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.widgets.TripSplitContentCard
import com.anabars.tripsplit.ui.widgets.ExpenseAmountInputField

@Composable
fun ExpenseAmountAndCurrencyCard(
    modifier: Modifier = Modifier,
    expenseAmount: String,
    expenseCurrencyCode: String,
    tripCurrencies: List<TripCurrency>,
    onExpenseAmountChanged: (String) -> Unit,
    onCurrencySelected: (String) -> Unit
) {
    TripSplitContentCard(modifier = modifier) {
        ExpenseAmountInputField(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = expenseAmount,
            currencyPrefix = expenseCurrencyCode,
            onValueChange = onExpenseAmountChanged
        )
        InfoText(textRes = R.string.currency)
        ExpenseCurrenciesRadioGroup(
            modifier = Modifier.padding(horizontal = 16.dp),
            currencies = tripCurrencies,
            expenseCurrencyCode = expenseCurrencyCode,
            onCurrencySelected = onCurrencySelected,
        )
    }
}