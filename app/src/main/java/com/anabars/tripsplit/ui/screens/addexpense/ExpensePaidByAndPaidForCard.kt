package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.widgets.TripSplitContentCard

@Composable
fun ExpensePaidByAndPaidForCard(
    modifier: Modifier = Modifier,
    tripParticipants: List<TripParticipant>,
    expensePayer: String,
    onPayerSelected: (String) -> Unit,
) {
    TripSplitContentCard(modifier = modifier) {
        InfoText(textRes = R.string.expense_paid_by)
        ExpensePayerRadioGroup(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            participants = tripParticipants,
            paidBy = expensePayer,
            onPayerSelected = onPayerSelected
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_small)))

        InfoText(textRes = R.string.expense_paid_for)
    }
}