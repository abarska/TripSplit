package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.components.SecondaryButton
import com.anabars.tripsplit.ui.itemrows.ShowParticipant

@Composable
fun ParticipantsSection(
    participants: List<String>,
    onAddParticipantButtonClick: () -> Unit,
    onDeletedParticipant: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InfoText(textRes = R.string.participants_section_header)

        if (participants.isNotEmpty()) {
            Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_small)))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                participants.forEach { participant ->
                    ShowParticipant(participant, participants, onDeletedParticipant)
                }
            }
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))

        SecondaryButton(textRes = R.string.add_a_participant) { onAddParticipantButtonClick() }
    }
}

@Preview(showBackground = true)
@Composable
private fun ParticipantsSectionPreview() {
    ParticipantsSection(
        participants = listOf("adam", "eve", "others"),
        onAddParticipantButtonClick = {},
        onDeletedParticipant = {}
    )
}