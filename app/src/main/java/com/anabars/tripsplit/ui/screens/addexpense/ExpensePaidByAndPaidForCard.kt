package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.model.AddExpensePayerParticipantsState
import com.anabars.tripsplit.ui.utils.getFakeAddExpensePayerParticipantsState

@Composable
fun ExpensePaidByAndPaidForCard(
    addExpenseErrorRes: Int,
    payerParticipantsState: AddExpensePayerParticipantsState,
    onPayerSelected: (Long) -> Unit,
    onSelectionChanged: (Set<TripParticipant>) -> Unit,
    modifier: Modifier = Modifier
) {
    TsContentCard(
        modifier = modifier,
        isError = addExpenseErrorRes == R.string.error_participants_not_selected
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExpensePayerRadioGroup(
                modifier = Modifier.weight(1f),
                participants = payerParticipantsState.tripParticipants,
                paidBy = payerParticipantsState.expensePayerId,
                onPayerSelected = onPayerSelected,
                itemLabel = { it.chipDisplayLabel() }
            )

            ExpenseParticipantsCheckBoxGroup(
                modifier = Modifier.weight(1f),
                participants = payerParticipantsState.tripParticipants,
                selectedParticipants = payerParticipantsState.selectedParticipants,
                onSelectionChanged = onSelectionChanged
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpensePaidByAndPaidForCardPreview() {
    ExpensePaidByAndPaidForCard(
        addExpenseErrorRes = 0,
        payerParticipantsState = getFakeAddExpensePayerParticipantsState(),
        onPayerSelected = {},
        onSelectionChanged = {},
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun ExpensePaidByAndPaidForCardPreviewWithError() {
    ExpensePaidByAndPaidForCard(
        addExpenseErrorRes = R.string.error_participants_not_selected,
        payerParticipantsState = getFakeAddExpensePayerParticipantsState(),
        onPayerSelected = {},
        onSelectionChanged = {},
        modifier = Modifier.fillMaxWidth()
    )
}