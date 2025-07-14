package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.model.AddExpensePayerParticipantsState

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

        TsInfoText(textRes = R.string.expense_paid_by)

        ExpensePayerRadioGroup(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            participants = payerParticipantsState.tripParticipants,
            paidBy = payerParticipantsState.expensePayerId,
            onPayerSelected = onPayerSelected,
            itemLabel = { it.chipDisplayLabel() }
        )

        TsInfoText(textRes = R.string.expense_paid_for)

        ExpenseParticipantsCheckBoxGroup(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            participants = payerParticipantsState.tripParticipants,
            selectedParticipants = payerParticipantsState.selectedParticipants,
            onSelectionChanged = onSelectionChanged
        )
    }
}