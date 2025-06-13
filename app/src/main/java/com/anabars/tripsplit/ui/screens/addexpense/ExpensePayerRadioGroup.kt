package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.listitems.TripSplitRadioButton

@Composable
fun ExpensePayerRadioGroup(
    participants: List<TripParticipant>,
    paidBy: String,
    onPayerSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        participants.forEach { participant ->
            TripSplitRadioButton(
                value = participant.name,
                isSelected = participant.name == paidBy,
                onItemClick = { onPayerSelected(it) },
            ) {
                InfoText(
                    text = participant.name,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}