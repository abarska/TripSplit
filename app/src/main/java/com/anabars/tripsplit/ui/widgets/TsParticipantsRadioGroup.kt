package com.anabars.tripsplit.ui.widgets

import androidx.annotation.StringRes
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
fun TsParticipantsRadioGroup(
    @StringRes label: Int,
    participants: List<TripParticipant>,
    paidBy: Long,
    onItemSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    itemLabel: (TripParticipant) -> String,
) {
    if (participants.isEmpty()) return
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TsInfoText(textRes = label, fontSize = TsFontSize.MEDIUM)
        TsRadioGroup(
            items = participants,
            selectedItem = participants.find { it.id == paidBy } ?: participants.first(),
            onItemSelected = { onItemSelected(it.id) },
            layout = LayoutType.Column
        ) { participant ->
            TsInfoText(
                text = itemLabel(participant),
                fontSize = TsFontSize.MEDIUM,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TsParticipantsRadioGroupPreview() {
    TsParticipantsRadioGroup(
        label = R.string.expense_paid_by,
        participants = getFakeTripParticipants(),
        paidBy = getFakeTripParticipants().first().id,
        onItemSelected = {},
        modifier = Modifier.inputWidthModifier(),
        itemLabel = { it.name }
    )
}