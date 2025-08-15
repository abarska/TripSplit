package com.anabars.tripsplit.ui.widgets

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
import com.anabars.tripsplit.ui.model.AmountCurrencyState
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.screens.additem.ExpenseCategoriesRadioGroup
import com.anabars.tripsplit.ui.utils.getFakeAmountCurrencyUiState
import com.anabars.tripsplit.viewmodels.AddItemViewModel
import java.time.LocalDate

@Composable
fun TsDateAmountCurrencyCard(
    useCase: AddItemViewModel.UseCase,
    modifier: Modifier = Modifier,
    onDateSelected: (LocalDate) -> Unit,
    onCategoryChanged: (ExpenseCategory) -> Unit = {},
    amountCurrencyState: AmountCurrencyState,
    onExpenseAmountChanged: (String) -> Unit,
    onCurrencySelected: (String) -> Unit
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

            if (useCase == AddItemViewModel.UseCase.EXPENSE) {
                ExpenseCategoriesRadioGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    selectedCategory = amountCurrencyState.selectedCategory,
                    onCategoryChanged = onCategoryChanged
                )
            }

            TsExpenseAmountInput(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = amountCurrencyState.expenseAmount,
                currencyPrefix = amountCurrencyState.expenseCurrencyCode,
                onValueChange = onExpenseAmountChanged
            )
            TsCurrenciesRadioGroup(
                currencies = amountCurrencyState.tripCurrencies,
                expenseCurrencyCode = amountCurrencyState.expenseCurrencyCode,
                onCurrencySelected = onCurrencySelected,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TsDateAmountCurrencyCardPreviewExpense() {
    TsDateAmountCurrencyCard(
        useCase = AddItemViewModel.UseCase.EXPENSE,
        onDateSelected = {},
        amountCurrencyState = getFakeAmountCurrencyUiState(),
        onExpenseAmountChanged = {},
        onCurrencySelected = {},
        modifier = Modifier.fillMaxWidth()
    )

}

@Preview(showBackground = true)
@Composable
private fun TsDateAmountCurrencyCardPreviewPayment() {
    TsDateAmountCurrencyCard(
        useCase = AddItemViewModel.UseCase.PAYMENT,
        onDateSelected = {},
        amountCurrencyState = getFakeAmountCurrencyUiState(),
        onExpenseAmountChanged = {},
        onCurrencySelected = {},
        modifier = Modifier.fillMaxWidth()
    )

}