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

@Composable
fun ExpensePaidByAndPaidForCard(
    modifier: Modifier = Modifier,
    tripParticipants: List<TripParticipant>,
    expensePayerId: Long,
    onPayerSelected: (Long) -> Unit,
    selectedParticipants: Set<TripParticipant>,
    onSelectionChanged: (Set<TripParticipant>) -> Unit,
) {
    TsContentCard(modifier = modifier) {

        TsInfoText(textRes = R.string.expense_paid_by)

        ExpensePayerRadioGroup(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            participants = tripParticipants,
            paidBy = expensePayerId,
            onPayerSelected = onPayerSelected,
            itemLabel = { it.chipDisplayLabel() }
        )

        TsInfoText(textRes = R.string.expense_paid_for)

        ExpenseParticipantsCheckBoxGroup(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            participants = tripParticipants,
            selectedParticipants = selectedParticipants,
            onSelectionChanged = onSelectionChanged
        )
    }
}