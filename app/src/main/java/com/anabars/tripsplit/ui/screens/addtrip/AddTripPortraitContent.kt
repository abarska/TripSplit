package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.HorizontalSeparator
import com.anabars.tripsplit.ui.components.MainButton

@Composable
fun AddTripPortraitContent(
    tripName: String,
    tripNameError: Boolean,
    tripNameErrorMessage: Int,
    onTripNameChanged: (String) -> Unit,
    tripDescription: String,
    onTripDescriptionChanged: (String) -> Unit,
    participants: List<String>,
    onAddParticipantButtonClick: () -> Unit,
    onDeletedParticipant: (String) -> Unit,
    currencies: List<String>,
    onAddCurrencyButtonClick: () -> Unit,
    onDeleteCurrency: (String) -> Unit,
    onSaveTrip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
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

        HorizontalSeparator()

        CurrenciesSection(
            currencies = currencies,
            onAddCurrencyButtonClick = onAddCurrencyButtonClick,
            onDeleteCurrency = onDeleteCurrency
        )

        HorizontalSeparator()

        ParticipantsSection(
            modifier = Modifier.weight(1f),
            participants = participants,
            onAddParticipantButtonClick = onAddParticipantButtonClick,
            onDeletedParticipant = onDeletedParticipant
        )

        HorizontalSeparator()

        MainButton(textRes = R.string.save) { onSaveTrip() }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTripPortraitContentPreview() {
    AddTripPortraitContent(
        tripName = "trip name",
        tripNameError = false,
        tripNameErrorMessage = 0,
        onTripNameChanged = {},
        tripDescription = "trip description",
        onTripDescriptionChanged = {},
        participants = listOf("adam", "eve", "others"),
        onAddParticipantButtonClick = {},
        onDeletedParticipant = {},
        currencies = listOf("EUR", "BGN", "RON", "UAH"),
        onAddCurrencyButtonClick = {},
        onDeleteCurrency = {},
        onSaveTrip = {}
    )
}