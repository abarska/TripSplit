package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.ui.components.DateInputSection
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsExpenseAmountInput
import com.anabars.tripsplit.ui.model.AddExpenseAmountCurrencyState
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.utils.getFakeAmountCurrencyUiState
import com.anabars.tripsplit.ui.utils.getFakeAmountCurrencyUiStateWithError
import java.time.LocalDate

@Composable
fun ExpenseAmountAndCurrencyCard(
    onDateSelected: (LocalDate) -> Unit,
    onCategoryChanged: (ExpenseCategory) -> Unit,
    amountCurrencyState: AddExpenseAmountCurrencyState,
    onExpenseAmountChanged: (String) -> Unit,
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TsContentCard(
        modifier = modifier,
        isError = amountCurrencyState.isError
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DateInputSection(
                modifier = Modifier.padding(horizontal = 16.dp),
                selectedDate = amountCurrencyState.selectedDate,
                onDateSelected = onDateSelected,
            )

            ExpenseCategoriesRadioGroup(
                modifier = Modifier.padding(horizontal = 16.dp),
                selectedCategory = amountCurrencyState.selectedCategory,
                onCategoryChanged = onCategoryChanged
            )

            TsExpenseAmountInput(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = amountCurrencyState.expenseAmount,
                currencyPrefix = amountCurrencyState.expenseCurrencyCode,
                onValueChange = onExpenseAmountChanged
            )
            ExpenseCurrenciesRadioGroup(
                currencies = amountCurrencyState.tripCurrencies,
                expenseCurrencyCode = amountCurrencyState.expenseCurrencyCode,
                onCurrencySelected = onCurrencySelected,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpenseAmountAndCurrencyCardPreview() {
    ExpenseAmountAndCurrencyCard(
        onDateSelected = {},
        onCategoryChanged = {},
        amountCurrencyState = getFakeAmountCurrencyUiState(),
        onExpenseAmountChanged = {},
        onCurrencySelected = {},
        modifier = Modifier.fillMaxWidth()
    )

}

@Preview(showBackground = true)
@Composable
private fun ExpenseAmountAndCurrencyCardPreviewWithError() {
    ExpenseAmountAndCurrencyCard(
        onDateSelected = {},
        onCategoryChanged = {},
        amountCurrencyState = getFakeAmountCurrencyUiStateWithError(),
        onExpenseAmountChanged = {},
        onCurrencySelected = {},
        modifier = Modifier.fillMaxWidth()
    )
}