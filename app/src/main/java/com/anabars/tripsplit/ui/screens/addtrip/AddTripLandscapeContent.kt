package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsMainButton
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun AddTripLandscapeContent(
    tripName: String,
    tripNameError: Boolean,
    tripNameErrorMessage: Int,
    onTripNameChanged: (String) -> Unit,
    participants: List<String>,
    onAddParticipantButtonClick: () -> Unit,
    onDeletedParticipant: (String) -> Unit,
    currencies: List<String>,
    onAddCurrencyButtonClick: () -> Unit,
    onDeleteCurrency: (String) -> Unit,
    onSaveTrip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(R.dimen.full_screen_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputSection(
            modifier = modifier.inputWidthModifier(),
            tripName = tripName,
            tripNameError = tripNameError,
            tripNameErrorMessage = tripNameErrorMessage,
            onTripNameChanged = onTripNameChanged
        )

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            TsContentCard(modifier = Modifier.weight(1f)) {
                ChipsSection(
                    labelRes = R.string.currencies_section_header,
                    items = currencies,
                    onAddItemButtonClick = onAddCurrencyButtonClick,
                    onDeleteItemButtonClick = onDeleteCurrency
                )
            }
            TsContentCard(modifier = Modifier.weight(1f)) {
                ChipsSection(
                    labelRes = R.string.participants_section_header,
                    items = participants,
                    onAddItemButtonClick = onAddParticipantButtonClick,
                    onDeleteItemButtonClick = onDeletedParticipant
                )
            }
        }

        TsMainButton(
            modifier = modifier.inputWidthModifier(),
            textRes = R.string.save
        ) { onSaveTrip() }
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
        participants = listOf("harry", "hermione", "ron", "draco"),
        onAddParticipantButtonClick = {},
        onDeletedParticipant = {},
        currencies = listOf("EUR", "BGN", "RON", "UAH"),
        onAddCurrencyButtonClick = {},
        onDeleteCurrency = {},
        onSaveTrip = {}
    )
}