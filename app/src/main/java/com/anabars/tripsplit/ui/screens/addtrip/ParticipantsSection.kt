package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.components.SecondaryButton
import com.anabars.tripsplit.ui.itemrows.ParticipantItemRow
import com.anabars.tripsplit.ui.model.ActionButton

@Composable
fun ParticipantsSection(
    participants: List<String>,
    onAddParticipantButtonClick: () -> Unit,
    onDeletedParticipant: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { InfoText(textRes = R.string.participants_section_header) }

        if (participants.isNotEmpty()) {
            item { Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_small))) }
            items(items = participants) { name ->
                ShowParticipant(name, participants, onDeletedParticipant)
            }
        }

        item { Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal))) }

        item { SecondaryButton(textRes = R.string.add_a_participant) { onAddParticipantButtonClick() } }
    }
}

@Composable
private fun ShowParticipant(
    name: String,
    participants: List<String>,
    onDeletedParticipant: (String) -> Unit
) {
    val buttons =
        if (name == participants.first()) emptyList()
        else listOf(
            ActionButton(
                icon = Icons.Default.Delete,
                contentDescriptionRes = R.string.delete_participant,
            ) { onDeletedParticipant(name) }
        )
    ParticipantItemRow(name = name, buttons = buttons)
    if (name != participants.last()) {
        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_small)))
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