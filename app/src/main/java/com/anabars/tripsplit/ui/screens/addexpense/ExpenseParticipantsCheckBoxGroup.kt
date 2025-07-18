package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.TsCheckboxPill
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeTripParticipants
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun ExpenseParticipantsCheckBoxGroup(
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

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.vertical_spacer_small),
            Alignment.CenterHorizontally
        )
    ) {
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
private fun ExpenseParticipantsCheckBoxGroupPreview() {
    ExpenseParticipantsCheckBoxGroup(
        modifier = Modifier.inputWidthModifier(),
        participants = getFakeTripParticipants(),
        selectedParticipants = getFakeTripParticipants()
            .subList(0, getFakeTripParticipants().size - 1)
            .toSet(),
        onSelectionChanged = {}
    )
}