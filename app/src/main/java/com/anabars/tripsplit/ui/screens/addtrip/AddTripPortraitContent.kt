package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsMainButton
import com.anabars.tripsplit.ui.model.AddTripNameUiState
import com.anabars.tripsplit.ui.utils.getFakeTripCurrencies
import com.anabars.tripsplit.ui.utils.getFakeTripNameUiState
import com.anabars.tripsplit.ui.utils.getFakeTripParticipants

@Composable
fun AddTripPortraitContent(
    tripNameUiState: AddTripNameUiState,
    tripParticipants: List<TripParticipant>,
    tripCurrencies: List<String>,
    onTripNameChanged: (String) -> Unit,
    onAddParticipantButtonClick: () -> Unit,
    onEditParticipantButtonClick: (TripParticipant) -> Unit,
    onDeleteParticipant: (TripParticipant) -> Unit,
    onAddCurrencyButtonClick: () -> Unit,
    onDeleteCurrency: (String) -> Unit,
    onSaveTrip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(dimensionResource(R.dimen.full_screen_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal))
    ) {
        InputSection(
            tripNameUiState = tripNameUiState,
            onTripNameChanged = onTripNameChanged
        )

        TsContentCard {
            ChipsSection(
                labelRes = R.string.currencies_section_header,
                items = tripCurrencies,
                onAddItemButtonClick = onAddCurrencyButtonClick,
                onDeleteItemButtonClick = onDeleteCurrency,
                itemLabel = { it }
            )
        }

        TsContentCard {
            ChipsSection(
                labelRes = R.string.participants_section_header,
                items = tripParticipants,
                onAddItemButtonClick = onAddParticipantButtonClick,
                onItemClick = onEditParticipantButtonClick,
                onDeleteItemButtonClick = onDeleteParticipant,
                itemLabel = { it.chipDisplayLabel() }
            )
        }

        TsMainButton(textRes = R.string.save) { onSaveTrip() }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTripPortraitContentPreview() {
    AddTripPortraitContent(
        tripNameUiState = getFakeTripNameUiState(),
        tripParticipants = getFakeTripParticipants(),
        tripCurrencies = getFakeTripCurrencies().map{it.code},
        onTripNameChanged = {},
        onAddParticipantButtonClick = {},
        onEditParticipantButtonClick = {},
        onDeleteParticipant = {},
        onAddCurrencyButtonClick = {},
        onDeleteCurrency = {},
        onSaveTrip = {}
    )
}