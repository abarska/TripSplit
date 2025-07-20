package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.outlined.People
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripStatus
import com.anabars.tripsplit.ui.components.LayoutType
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.TsMainButton
import com.anabars.tripsplit.ui.components.TsRadioGroup
import com.anabars.tripsplit.ui.model.AddTripNameUiState
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeTripCurrencies
import com.anabars.tripsplit.ui.utils.getFakeTripNameUiState
import com.anabars.tripsplit.ui.utils.getFakeTripParticipants

@Composable
fun AddTripPortraitContent(
    tripNameUiState: AddTripNameUiState,
    tripStatusUiState: TripStatus,
    tripParticipants: List<TripParticipant>,
    tripCurrencies: List<String>,
    onTripNameChanged: (String) -> Unit,
    onTripStatusChanged: (TripStatus) -> Unit,
    onAddParticipantButtonClick: () -> Unit,
    onEditParticipantButtonClick: (TripParticipant) -> Unit,
    onDeleteParticipant: (TripParticipant) -> Unit,
    onAddCurrencyButtonClick: () -> Unit,
    onDeleteCurrency: (String) -> Unit,
    onSaveTrip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.full_screen_padding))
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal))
    ) {
        InputSection(
            tripNameUiState = tripNameUiState,
            onTripNameChanged = onTripNameChanged
        )

        TsContentCard {
            TsRadioGroup(
                modifier = modifier.padding(16.dp),
                items = TripStatus.getInitialTripStatuses(),
                selectedItem = tripStatusUiState,
                onItemSelected = onTripStatusChanged,
                layout = LayoutType.Row,
            ) { status ->
                TsInfoText(
                    text = stringResource(status.labelRes),
                    modifier = Modifier.padding(8.dp),
                    fontSize = TsFontSize.MEDIUM
                )
            }
        }

        TsContentCard {
            ChipsSection(
                leadingIcon = Icons.Default.CurrencyExchange,
                items = tripCurrencies,
                onAddItemButtonClick = onAddCurrencyButtonClick,
                onDeleteItemButtonClick = onDeleteCurrency,
                itemLabel = { it }
            )
        }

        TsContentCard {
            ChipsSection(
                leadingIcon = Icons.Outlined.People,
                items = tripParticipants,
                onAddItemButtonClick = onAddParticipantButtonClick,
                onItemClick = onEditParticipantButtonClick,
                onDeleteItemButtonClick = onDeleteParticipant,
                itemLabel = { it.chipDisplayLabelNameWithMultiplicator() }
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
        tripStatusUiState = TripStatus.PLANNED,
        tripParticipants = getFakeTripParticipants(),
        tripCurrencies = getFakeTripCurrencies().map { it.code },
        onTripNameChanged = {},
        onTripStatusChanged = {},
        onAddParticipantButtonClick = {},
        onEditParticipantButtonClick = {},
        onDeleteParticipant = {},
        onAddCurrencyButtonClick = {},
        onDeleteCurrency = {},
        onSaveTrip = {}
    )
}