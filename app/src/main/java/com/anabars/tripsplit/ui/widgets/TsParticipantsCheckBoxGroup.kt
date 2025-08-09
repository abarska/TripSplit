package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsCheckboxPill
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeTripParticipants
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun TsParticipantsCheckBoxGroup(
    modifier: Modifier = Modifier,
    participants: List<TripParticipant>,
    selectedParticipants: Set<TripParticipant>,
    onSelectionChanged: (Set<TripParticipant>) -> Unit,
) {
    val toggleItem: (TripParticipant) -> Unit = { item ->
        val newSelection =
            if (selectedParticipants.contains(item)) selectedParticipants - item
            else selectedParticipants + item
        onSelectionChanged(newSelection)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TsInfoText(textRes = R.string.expense_paid_for, fontSize = TsFontSize.MEDIUM)
        Spacer(modifier = Modifier.height(8.dp))
        participants.forEach { participant ->
            TsCheckboxPill(
                value = participant,
                isSelected = selectedParticipants.contains(participant),
                onItemClick = { toggleItem(participant) },
                content = {
                    TsInfoText(text = participant.name, fontSize = TsFontSize.MEDIUM)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TsParticipantsCheckBoxGroupPreview() {
    TsParticipantsCheckBoxGroup(
        modifier = Modifier.inputWidthModifier(),
        participants = getFakeTripParticipants(),
        selectedParticipants = getFakeTripParticipants()
            .subList(0, getFakeTripParticipants().size - 1)
            .toSet(),
        onSelectionChanged = {}
    )
}