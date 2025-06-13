package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.ShortInputTextField
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun InputSection(
    tripName: String,
    tripNameError: Boolean,
    tripNameErrorMessage: Int,
    onTripNameChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShortInputTextField(
            value = tripName,
            isError = tripNameError,
            labelRes = if (tripNameErrorMessage > 0 && tripNameError) tripNameErrorMessage else R.string.new_trip_title_hint,
            onValueChanged = onTripNameChanged,
            modifier = modifier.inputWidthModifier(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InputSectionPreview() {
    InputSection(
        tripName = "name",
        tripNameError = false,
        tripNameErrorMessage = 0,
        onTripNameChanged = {}
    )
}