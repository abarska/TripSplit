package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsShortInput
import com.anabars.tripsplit.ui.model.AddTripNameUiState
import com.anabars.tripsplit.ui.utils.getFakeTripNameUiState
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun InputSection(
    tripNameUiState: AddTripNameUiState,
    onTripNameChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TsShortInput(
            value = tripNameUiState.tripName,
            isError = tripNameUiState.tripNameError,
            labelRes = if (tripNameUiState.tripNameErrorMessage > 0 && tripNameUiState.tripNameError) tripNameUiState.tripNameErrorMessage else R.string.new_trip_title_hint,
            onValueChanged = onTripNameChanged,
            modifier = modifier.inputWidthModifier(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InputSectionPreview() {
    InputSection(
        tripNameUiState = getFakeTripNameUiState(),
        onTripNameChanged = {}
    )
}