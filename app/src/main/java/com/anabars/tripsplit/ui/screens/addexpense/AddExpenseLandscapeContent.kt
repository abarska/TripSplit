package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsMainButton
import com.anabars.tripsplit.ui.model.AddExpenseAmountCurrencyState
import com.anabars.tripsplit.ui.model.AddExpenseDateCategoryState
import com.anabars.tripsplit.ui.model.AddExpenseUiState
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.utils.getFakeAddExpenseUiState
import com.anabars.tripsplit.ui.utils.getFakeAmountCurrencyUiState
import java.time.LocalDate

@Composable
fun AddExpenseLandscapeContent(
    uiState: AddExpenseUiState,
    dateCategoryState: AddExpenseDateCategoryState,
    amountCurrencyState: AddExpenseAmountCurrencyState,
    onDateSelected: (LocalDate) -> Unit,
    onCategoryChange: (ExpenseCategory) -> Unit,
    onExpenseAmountChanged: (String) -> Unit,
    onCurrencySelected: (String) -> Unit,
    onPayerSelected: (Long) -> Unit,
    onParticipantsSelected: (Set<TripParticipant>) -> Unit,
    onSaveExpense: () -> Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal))
    ) {

        ExpenseDateAndCategoryCard(
            dateCategoryState = dateCategoryState,
            onDateSelected = onDateSelected,
            onCategoryChanged = onCategoryChange
        )

        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            ExpenseAmountAndCurrencyCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                amountCurrencyState = amountCurrencyState,
                onExpenseAmountChanged = onExpenseAmountChanged,
                onCurrencySelected = onCurrencySelected
            )
            ExpensePaidByAndPaidForCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                uiState = uiState,
                onPayerSelected = onPayerSelected,
                onSelectionChanged = onParticipantsSelected
            )
        }

        TsMainButton(textRes = R.string.save) { onSaveExpense() }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddExpenseLandscapeContentPreview() {
    AddExpenseLandscapeContent(
        uiState = getFakeAddExpenseUiState(),
        dateCategoryState = AddExpenseDateCategoryState(),
        amountCurrencyState = getFakeAmountCurrencyUiState(),
        onDateSelected = {},
        onCategoryChange = {},
        onExpenseAmountChanged = {},
        onCurrencySelected = {},
        onPayerSelected = {},
        onParticipantsSelected = {},
        onSaveExpense = { true }
    )
}