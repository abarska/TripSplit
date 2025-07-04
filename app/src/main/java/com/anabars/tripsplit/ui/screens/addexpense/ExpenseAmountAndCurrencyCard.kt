package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsExpenseAmountInput
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.model.AddExpenseUiState

@Composable
fun ExpenseAmountAndCurrencyCard(
    uiState: AddExpenseUiState,
    onExpenseAmountChanged: (String) -> Unit,
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TsContentCard(modifier = modifier) {
        TsExpenseAmountInput(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = uiState.expenseAmount,
            currencyPrefix = uiState.expenseCurrencyCode,
            onValueChange = onExpenseAmountChanged
        )
        TsInfoText(textRes = R.string.currency)
        ExpenseCurrenciesRadioGroup(
            modifier = Modifier.padding(horizontal = 16.dp),
            currencies = uiState.tripCurrencies,
            expenseCurrencyCode = uiState.expenseCurrencyCode,
            onCurrencySelected = onCurrencySelected,
        )
    }
}