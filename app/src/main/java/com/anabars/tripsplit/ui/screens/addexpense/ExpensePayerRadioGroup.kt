package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.LayoutType
import com.anabars.tripsplit.ui.components.TsRadioGroup
import com.anabars.tripsplit.ui.utils.TsFontSize

@Composable
fun ExpensePayerRadioGroup(
    participants: List<TripParticipant>,
    paidBy: Long,
    onPayerSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    itemLabel: (TripParticipant) -> String,
) {
    if (participants.isEmpty()) return

    TsRadioGroup(
        modifier = modifier,
        items = participants,
        selectedItem = participants.find { it.id == paidBy } ?: participants.first(),
        onItemSelected = { onPayerSelected(it.id) },
        layout = LayoutType.Flow
    ) { participant ->
        TsInfoText(
            text = itemLabel(participant),
            fontSize = TsFontSize.MEDIUM,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}