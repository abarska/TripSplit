package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.LayoutType
import com.anabars.tripsplit.ui.components.TsRadioGroup
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeTripParticipants
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun ExpensePayerRadioGroup(
    participants: List<TripParticipant>,
    paidBy: Long,
    onPayerSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    itemLabel: (TripParticipant) -> String,
) {
    if (participants.isEmpty()) return
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TsInfoText(textRes = R.string.expense_paid_by, fontSize = TsFontSize.MEDIUM)
        TsRadioGroup(
            items = participants,
            selectedItem = participants.find { it.id == paidBy } ?: participants.first(),
            onItemSelected = { onPayerSelected(it.id) },
            layout = LayoutType.Column
        ) { participant ->
            TsInfoText(
                text = itemLabel(participant),
                fontSize = TsFontSize.MEDIUM,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpensePayerRadioGroupPreview() {
    ExpensePayerRadioGroup(
        participants = getFakeTripParticipants(),
        paidBy = getFakeTripParticipants().first().id,
        onPayerSelected = {},
        modifier = Modifier.inputWidthModifier(),
        itemLabel = { it.name }
    )
}