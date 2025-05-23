package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.components.SecondaryButton

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
            participants.forEachIndexed { index, name ->
                ParticipantRowItem(
                    name = name,
                    onDeleteClick = { onDeletedParticipant(name) },
                    hideChangeStatusIcon = true,
                    hideDeleteIcon = index == 0,
                )
                if (index < participants.lastIndex) {
                    Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_small)))
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