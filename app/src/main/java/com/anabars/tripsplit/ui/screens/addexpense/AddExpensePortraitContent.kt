package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsMainButton
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.utils.getFakeTripCurrencies
import com.anabars.tripsplit.ui.utils.getFakeTripParticipants
import java.time.LocalDate

@Composable
fun AddExpensePortraitContent(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    selectedCategory: ExpenseCategory,
    onCategoryChange: (ExpenseCategory) -> Unit,
    expenseAmount: String,
    expenseCurrencyCode: String,
    tripCurrencies: List<TripCurrency>,
    onExpenseAmountChanged: (String) -> Unit,
    onCurrencySelected: (String) -> Unit,
    tripParticipants: List<TripParticipant>,
    expensePayerId: Long,
    onPayerSelected: (Long) -> Unit,
    selectedParticipants: Set<TripParticipant>,
    onParticipantsSelected: (Set<TripParticipant>) -> Unit,
    onSaveExpense: () -> Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal))
    ) {

        ExpenseDateAndCategoryCard(
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange
        )

        ExpenseAmountAndCurrencyCard(
            expenseAmount = expenseAmount,
            expenseCurrencyCode = expenseCurrencyCode,
            tripCurrencies = tripCurrencies,
            onExpenseAmountChanged = onExpenseAmountChanged,
            onCurrencySelected = onCurrencySelected
        )

        ExpensePaidByAndPaidForCard(
            tripParticipants = tripParticipants,
            expensePayerId = expensePayerId,
            onPayerSelected = onPayerSelected,
            selectedParticipants = selectedParticipants,
            onSelectionChanged = onParticipantsSelected
        )

        TsMainButton(textRes = R.string.save) { onSaveExpense() }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddExpensePortraitContentPreview() {
    AddExpensePortraitContent(
        selectedDate = LocalDate.now(),
        onDateSelected = {},
        selectedCategory = ExpenseCategory.Miscellaneous,
        onCategoryChange = {},
        expenseAmount = "100.00",
        expenseCurrencyCode = getFakeTripCurrencies().first().code,
        tripCurrencies = getFakeTripCurrencies(),
        onExpenseAmountChanged = {},
        onCurrencySelected = {},
        tripParticipants = getFakeTripParticipants(),
        expensePayerId = -1,
        onPayerSelected = {},
        selectedParticipants = getFakeTripParticipants().take(2).toSet(),
        onParticipantsSelected = {},
        onSaveExpense = { 0 }
    )
}