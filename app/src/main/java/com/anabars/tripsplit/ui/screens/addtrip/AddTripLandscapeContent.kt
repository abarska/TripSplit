package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.MainButton
import com.anabars.tripsplit.ui.utils.fullScreenModifier

@Composable
fun AddTripLandscapeContent(
    tripName: String,
    tripNameError: Boolean,
    tripNameErrorMessage: Int,
    onTripNameChanged: (String) -> Unit,
    tripDescription: String,
    onTripDescriptionChanged: (String) -> Unit,
    participants: List<String>,
    onAddParticipantButtonClick: () -> Unit,
    onDeletedParticipant: (String) -> Unit,
    onSaveTrip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.then(Modifier.fullScreenModifier()),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputSection(
                tripName = tripName,
                tripNameError = tripNameError,
                tripNameErrorMessage = tripNameErrorMessage,
                onTripNameChanged = onTripNameChanged,
                tripDescription = tripDescription,
                onTripDescriptionChanged = onTripDescriptionChanged
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))
            MainButton(textRes = R.string.save) { onSaveTrip() }
        }

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ParticipantsSection(
                participants = participants,
                onAddParticipantButtonClick = onAddParticipantButtonClick,
                onDeletedParticipant = onDeletedParticipant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTripLandscapeContentPreview() {
    AddTripLandscapeContent(
        tripName = "trip name",
        tripNameError = false,
        tripNameErrorMessage = 0,
        onTripNameChanged = {},
        tripDescription = "trip description",
        onTripDescriptionChanged = {},
        participants = listOf("adam", "eve", "others"),
        onAddParticipantButtonClick = {},
        onSaveTrip = {},
        onDeletedParticipant = {}
    )
}