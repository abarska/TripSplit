package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.LayoutType
import com.anabars.tripsplit.ui.components.TsRadioGroup

@Composable
fun ExpensePayerRadioGroup(
    participants: List<TripParticipant>,
    paidBy: String,
    onPayerSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (participants.isEmpty()) return

    TsRadioGroup(
        modifier = modifier,
        items = participants,
        selectedItem = participants.find { it.name == paidBy } ?: participants.first(),
        onItemSelected = { onPayerSelected(it.name) },
        layout = LayoutType.Flow
    ) { participant ->
        TsInfoText(
            text = participant.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}