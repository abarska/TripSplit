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
import androidx.compose.ui.Alignment
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
import com.anabars.tripsplit.ui.model.AddTripUiState
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeAddTripUiState
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun AddTripLandscapeContent(
    uiState: AddTripUiState,
    onTripNameChanged: (String) -> Unit,
    onTripStatusChanged: (TripStatus) -> Unit,
    onAddParticipantButtonClick: () -> Unit,
    onEditParticipantButtonClick: (TripParticipant) -> Unit,
    onDeleteParticipant: (TripParticipant) -> Unit,
    onAddCurrencyButtonClick: () -> Unit,
    onDeleteCurrency: (String) -> Unit,
    onSaveTrip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.full_screen_padding))
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        InputSection(
            modifier = Modifier.inputWidthModifier(),
            uiState = uiState,
            onTripNameChanged = onTripNameChanged
        )

        TsContentCard {
            TsRadioGroup(
                modifier = modifier
                    .inputWidthModifier()
                    .padding(16.dp),
                items = TripStatus.getInitialStatuses(),
                selectedItem = uiState.tripStatus,
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

        TsContentCard(modifier = Modifier.inputWidthModifier()) {
            ChipsSection(
                leadingIcon = Icons.Default.CurrencyExchange,
                items = uiState.tripCurrencyCodes,
                onAddItemButtonClick = onAddCurrencyButtonClick,
                onDeleteItemButtonClick = onDeleteCurrency,
                itemLabel = { it }
            )
        }

        TsContentCard(modifier = Modifier.inputWidthModifier()) {
            ChipsSection(
                leadingIcon = Icons.Outlined.People,
                items = uiState.tripParticipants,
                onAddItemButtonClick = onAddParticipantButtonClick,
                onItemClick = onEditParticipantButtonClick,
                onDeleteItemButtonClick = onDeleteParticipant,
                itemLabel = { it.chipDisplayLabelNameWithMultiplicator() }
            )
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
        uiState = getFakeAddTripUiState(),
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