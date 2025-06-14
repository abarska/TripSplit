package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.widgets.LayoutType
import com.anabars.tripsplit.ui.widgets.TripSplitRadioGroup

@Composable
fun ExpensePayerRadioGroup(
    participants: List<TripParticipant>,
    paidBy: String,
    onPayerSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (participants.isEmpty()) return

    TripSplitRadioGroup(
        modifier = modifier,
        items = participants,
        selectedItem = participants.find { it.name == paidBy } ?: participants.first(),
        onItemSelected = { onPayerSelected(it.name) },
        layout = LayoutType.Flow
    ) { participant ->
        InfoText(
            text = participant.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}